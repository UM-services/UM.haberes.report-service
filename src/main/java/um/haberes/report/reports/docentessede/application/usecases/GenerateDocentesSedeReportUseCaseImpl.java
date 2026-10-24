package um.haberes.report.reports.docentessede.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.docentessede.domain.model.CursoCargoContratado;
import um.haberes.report.reports.docentessede.domain.model.DocentesSedeContent;
import um.haberes.report.reports.docentessede.domain.model.DocentesSedeContent.Curso;
import um.haberes.report.reports.docentessede.domain.model.DocentesSedeContent.Fila;
import um.haberes.report.reports.docentessede.domain.ports.in.GenerateDocentesSedeReportUseCase;
import um.haberes.report.reports.docentessede.domain.ports.out.CursoCargoContratadoCatalogPort;
import um.haberes.report.reports.docentessede.domain.ports.out.DocentesSedePdfRenderer;
import um.haberes.report.reports.docentessede.domain.ports.out.PersonaTesoreriaCatalogPort;
import um.haberes.report.shared.application.exception.ReportException;
import um.haberes.report.shared.domain.model.CargoTipoResumen;
import um.haberes.report.shared.domain.model.CursoCargoResumen;
import um.haberes.report.shared.domain.model.CursoResumen;
import um.haberes.report.shared.domain.model.FacultadResumen;
import um.haberes.report.shared.domain.model.GeograficaResumen;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.CargoTipoCatalogPort;
import um.haberes.report.shared.domain.ports.out.CursoCargoCatalogPort;
import um.haberes.report.shared.domain.ports.out.CursoCatalogPort;
import um.haberes.report.shared.domain.ports.out.FacultadCatalogPort;
import um.haberes.report.shared.domain.ports.out.GeograficaCatalogPort;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateDocentesSedeReportUseCaseImpl implements GenerateDocentesSedeReportUseCase {

    static final String DISPLAY_NAME = "docentesSede.pdf";

    private final FacultadCatalogPort facultadCatalogPort;
    private final GeograficaCatalogPort geograficaCatalogPort;
    private final CursoCatalogPort cursoCatalogPort;
    private final CursoCargoCatalogPort cursoCargoCatalogPort;
    private final CargoTipoCatalogPort cargoTipoCatalogPort;
    private final CursoCargoContratadoCatalogPort contratadoCatalogPort;
    private final PersonaTesoreriaCatalogPort personaTesoreriaCatalogPort;
    private final DocentesSedePdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer facultadId, Integer geograficaId, Integer anho, Integer mes) {
        FacultadResumen facultad = facultadCatalogPort.findById(facultadId)
                .orElseThrow(() -> new ReportException(
                        "No se encontró la facultad " + facultadId));
        GeograficaResumen geografica = geograficaCatalogPort.findById(geograficaId)
                .orElseThrow(() -> new ReportException(
                        "No se encontró la sede " + geograficaId));

        List<Curso> cursos = cursoCatalogPort
                .findAllByFacultadAndGeograficaAndPeriodo(facultadId, geograficaId, anho, mes)
                .stream()
                .filter(Objects::nonNull)
                .map(curso -> new Curso(text(curso.getNombre()), periodo(curso),
                        filas(curso, anho, mes)))
                .toList();

        Path target = storage.resolve(fileName(facultadId, geograficaId, anho, mes));
        renderer.render(new DocentesSedeContent(anho, mes, text(facultad.getNombre()),
                text(geografica.getNombre()), cursos), target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private List<Fila> filas(CursoResumen curso, Integer anho, Integer mes) {
        List<Fila> filas = new ArrayList<>();
        for (CursoCargoResumen cursoCargo : cursoCargoCatalogPort
                .findAllByCurso(curso.getCursoId(), anho, mes)) {
            if (cursoCargo == null) {
                continue;
            }
            String cargo = cursoCargo.getCargoTipo() == null
                    ? "" : text(cursoCargo.getCargoTipo().getNombre());
            String docente = cursoCargo.getPersona() == null
                    ? "" : text(cursoCargo.getPersona().apellidoNombre());
            if (cursoCargo.getCargoTipo() == null || cursoCargo.getPersona() == null) {
                log.warn("Datos incompletos en el curso {}: se emite la fila con celdas vacias",
                        curso.getCursoId());
            }
            filas.add(new Fila(cargo, docente,
                    cursoCargo.getDesignacionTipo() == null
                            ? "" : text(cursoCargo.getDesignacionTipo().getNombre()),
                    text(cursoCargo.getHorasSemanales()),
                    cursoCargo.getDesarraigo() == 1 ? "*" : ""));
        }
        for (CursoCargoContratado contratado : contratadoCatalogPort
                .findAllByCurso(curso.getCursoId(), anho, mes)) {
            if (contratado == null) {
                continue;
            }
            CargoTipoResumen cargoTipo = contratado.cargoTipoId() == null
                    ? null : cargoTipoCatalogPort.findById(contratado.cargoTipoId()).orElse(null);
            String docente = contratado.personaId() == null || contratado.documentoId() == null
                    ? null
                    : personaTesoreriaCatalogPort.findByUnique(contratado.personaId(),
                            contratado.documentoId()).map(p -> p.apellidoNombre()).orElse(null);
            if (cargoTipo == null || docente == null) {
                log.warn("Datos incompletos en el cargo contratado del curso {}: fila omitida",
                        curso.getCursoId());
                continue;
            }
            filas.add(new Fila(text(cargoTipo.getNombre()), docente, "Contratado",
                    text(contratado.horasSemanales()), ""));
        }
        return filas;
    }

    private String periodo(CursoResumen curso) {
        String periodo = "";
        if (curso.getAnual() == 1) {
            periodo = "Anual";
        }
        if (curso.getSemestre1() == 1) {
            periodo = "1er Semestre";
        }
        if (curso.getSemestre2() == 1) {
            periodo = "2do Semestre";
        }
        return periodo;
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }

    private String fileName(Integer facultadId, Integer geograficaId, Integer anho, Integer mes) {
        return "docentes." + facultadId + "." + geograficaId + "." + anho + "." + mes + ".pdf";
    }
}
