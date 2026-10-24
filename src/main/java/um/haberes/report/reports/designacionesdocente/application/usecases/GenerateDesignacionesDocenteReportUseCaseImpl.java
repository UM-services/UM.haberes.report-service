package um.haberes.report.reports.designacionesdocente.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.designacionesdocente.domain.model.DesignacionesDocenteContent;
import um.haberes.report.reports.designacionesdocente.domain.model.DesignacionesDocenteContent.Docente;
import um.haberes.report.reports.designacionesdocente.domain.model.DesignacionesDocenteContent.Fila;
import um.haberes.report.reports.designacionesdocente.domain.ports.in.GenerateDesignacionesDocenteReportUseCase;
import um.haberes.report.reports.designacionesdocente.domain.ports.out.DesignacionesDocentePdfRenderer;
import um.haberes.report.shared.domain.model.DocenteDesignacionResumen;
import um.haberes.report.shared.domain.model.FacultadResumen;
import um.haberes.report.shared.domain.model.GeograficaResumen;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.DocenteDesignacionCatalogPort;
import um.haberes.report.shared.domain.ports.out.FacultadCatalogPort;
import um.haberes.report.shared.domain.ports.out.GeograficaCatalogPort;
import um.haberes.report.shared.domain.ports.out.PersonaCatalogPort;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateDesignacionesDocenteReportUseCaseImpl
        implements GenerateDesignacionesDocenteReportUseCase {

    static final String DISPLAY_NAME = "designaciones-docente.pdf";

    private final PersonaCatalogPort personaCatalogPort;
    private final DocenteDesignacionCatalogPort docenteDesignacionCatalogPort;
    private final FacultadCatalogPort facultadCatalogPort;
    private final GeograficaCatalogPort geograficaCatalogPort;
    private final DesignacionesDocentePdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        List<PersonaResumen> personas = personaCatalogPort.findAllDocente(anho, mes).stream()
                .filter(Objects::nonNull)
                .filter(persona -> persona.getLegajoId() != null)
                .toList();
        List<DocenteDesignacionResumen> designaciones = docenteDesignacionCatalogPort
                .findAllByPeriodo(anho, mes).stream()
                .filter(Objects::nonNull)
                .toList();

        Map<Long, List<DocenteDesignacionResumen>> porLegajo = designaciones.stream()
                .filter(designacion -> designacion.getLegajoId() != null)
                .collect(Collectors.groupingBy(DocenteDesignacionResumen::getLegajoId));
        Map<Integer, FacultadResumen> facultades = facultadCatalogPort.findAllFacultades().stream()
                .filter(Objects::nonNull)
                .filter(facultad -> facultad.getFacultadId() != null)
                .collect(Collectors.toMap(FacultadResumen::getFacultadId, f -> f, (a, b) -> a));
        List<Integer> geograficaIds = designaciones.stream()
                .map(DocenteDesignacionResumen::getGeograficaId)
                .filter(Objects::nonNull).distinct().toList();
        Map<Integer, GeograficaResumen> geograficas = geograficaCatalogPort
                .findAllByIds(geograficaIds).stream()
                .filter(Objects::nonNull)
                .filter(geografica -> geografica.getGeograficaId() != null)
                .collect(Collectors.toMap(GeograficaResumen::getGeograficaId, g -> g, (a, b) -> a));

        List<Docente> docentes = personas.stream()
                .filter(persona -> porLegajo.containsKey(persona.getLegajoId()))
                .sorted(Comparator.comparing(PersonaResumen::getApellido,
                        Comparator.nullsLast(String::compareTo)))
                .map(persona -> new Docente(persona.getLegajoId(), persona.apellidoNombre(),
                        filas(porLegajo.get(persona.getLegajoId()), facultades, geograficas)))
                .toList();

        Path target = storage.resolve(fileName(anho, mes));
        renderer.render(new DesignacionesDocenteContent(anho, mes, docentes), target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private List<Fila> filas(List<DocenteDesignacionResumen> designaciones,
                             Map<Integer, FacultadResumen> facultades,
                             Map<Integer, GeograficaResumen> geograficas) {
        List<Fila> filas = new ArrayList<>();
        designaciones.stream()
                .sorted(Comparator.comparing(DocenteDesignacionResumen::getEspacio,
                        Comparator.nullsLast(String::compareTo)))
                .forEach(designacion -> {
                    FacultadResumen facultad = facultades.get(designacion.getFacultadId());
                    GeograficaResumen geografica = geograficas.get(designacion.getGeograficaId());
                    if (facultad == null || geografica == null) {
                        log.warn("Datos incompletos para la designacion del legajo {}",
                                designacion.getLegajoId());
                        return;
                    }
                    filas.add(new Fila(text(facultad.getNombre()), text(geografica.getNombre()),
                            text(designacion.getEspacio()), text(designacion.getHorasSemanales()),
                            text(designacion.getCargo()), text(designacion.getDesignacion()),
                            text(designacion.getHorasDesignacion()), dictado(designacion)));
                });
        return filas;
    }

    private String dictado(DocenteDesignacionResumen designacion) {
        boolean anual = designacion.getAnual() != null && designacion.getAnual() != 0;
        boolean semestre1 = designacion.getSemestre1() != null && designacion.getSemestre1() != 0;
        boolean semestre2 = designacion.getSemestre2() != null && designacion.getSemestre2() != 0;
        if (anual) {
            return "Anual";
        }
        if (semestre1 && semestre2) {
            return "Semestral";
        }
        if (semestre1) {
            return "Semestre 1";
        }
        if (semestre2) {
            return "Semestre 2";
        }
        return "No especificado";
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }

    private String fileName(Integer anho, Integer mes) {
        return "designaciones-docente." + anho + "." + mes + ".pdf";
    }
}
