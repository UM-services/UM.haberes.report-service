package um.haberes.report.reports.cursodocente.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cursodocente.domain.model.CursoDocenteContent;
import um.haberes.report.reports.cursodocente.domain.model.CursoDocenteContent.Docente;
import um.haberes.report.reports.cursodocente.domain.model.CursoDocenteContent.Fila;
import um.haberes.report.reports.cursodocente.domain.ports.in.GenerateCursoDocenteReportUseCase;
import um.haberes.report.reports.cursodocente.domain.ports.out.CursoDocentePdfRenderer;
import um.haberes.report.shared.domain.model.CategoriaResumen;
import um.haberes.report.shared.domain.model.CursoCargoResumen;
import um.haberes.report.shared.domain.model.CursoResumen;
import um.haberes.report.shared.domain.model.FacultadResumen;
import um.haberes.report.shared.domain.model.GeograficaResumen;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.CategoriaCatalogPort;
import um.haberes.report.shared.domain.ports.out.CursoCargoCatalogPort;
import um.haberes.report.shared.domain.ports.out.CursoCatalogPort;
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
public class GenerateCursoDocenteReportUseCaseImpl implements GenerateCursoDocenteReportUseCase {

    static final String DISPLAY_NAME = "curso-docente.pdf";

    private final PersonaCatalogPort personaCatalogPort;
    private final CursoCargoCatalogPort cursoCargoCatalogPort;
    private final CursoCatalogPort cursoCatalogPort;
    private final CategoriaCatalogPort categoriaCatalogPort;
    private final FacultadCatalogPort facultadCatalogPort;
    private final GeograficaCatalogPort geograficaCatalogPort;
    private final CursoDocentePdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        List<PersonaResumen> personas = personaCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(persona -> persona.getLegajoId() != null)
                .toList();

        Map<Long, List<CursoCargoResumen>> cursosPorLegajo = personas.stream()
                .collect(Collectors.toMap(PersonaResumen::getLegajoId,
                        persona -> cursoCargoCatalogPort.findAllByLegajo(persona.getLegajoId(), anho, mes),
                        (a, b) -> a));

        List<CursoCargoResumen> todos = cursosPorLegajo.values().stream()
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .toList();

        Map<Long, CursoResumen> cursos = cursoCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(curso -> curso.getCursoId() != null)
                .collect(Collectors.toMap(CursoResumen::getCursoId, c -> c, (a, b) -> a));
        Map<Integer, CategoriaResumen> categorias = categoriaCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(categoria -> categoria.getCategoriaId() != null)
                .collect(Collectors.toMap(CategoriaResumen::getCategoriaId, c -> c, (a, b) -> a));
        Map<Integer, FacultadResumen> facultades = facultadCatalogPort.findAllFacultades().stream()
                .filter(Objects::nonNull)
                .filter(facultad -> facultad.getFacultadId() != null)
                .collect(Collectors.toMap(FacultadResumen::getFacultadId, f -> f, (a, b) -> a));
        List<Integer> geograficaIds = todos.stream()
                .map(CursoCargoResumen::getCursoId)
                .map(cursos::get)
                .filter(Objects::nonNull)
                .map(CursoResumen::getGeograficaId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Integer, GeograficaResumen> geograficas = geograficaCatalogPort.findAllByIds(geograficaIds)
                .stream()
                .filter(Objects::nonNull)
                .filter(geografica -> geografica.getGeograficaId() != null)
                .collect(Collectors.toMap(GeograficaResumen::getGeograficaId, g -> g, (a, b) -> a));

        List<Docente> docentes = personas.stream()
                .filter(persona -> {
                    List<CursoCargoResumen> cursosCargo = cursosPorLegajo.get(persona.getLegajoId());
                    return cursosCargo != null && !cursosCargo.isEmpty();
                })
                .sorted(Comparator.comparing(PersonaResumen::getApellido,
                        Comparator.nullsLast(String::compareTo)))
                .map(persona -> new Docente(persona.getLegajoId(), persona.apellidoNombre(),
                        filas(cursosPorLegajo.get(persona.getLegajoId()), cursos, categorias,
                                facultades, geograficas)))
                .toList();

        Path target = storage.resolve(fileName(anho, mes));
        renderer.render(new CursoDocenteContent(anho, mes, docentes), target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private List<Fila> filas(List<CursoCargoResumen> cursosCargo, Map<Long, CursoResumen> cursos,
                             Map<Integer, CategoriaResumen> categorias,
                             Map<Integer, FacultadResumen> facultades,
                             Map<Integer, GeograficaResumen> geograficas) {
        List<Fila> filas = new ArrayList<>();
        for (CursoCargoResumen cursoCargo : cursosCargo) {
            if (cursoCargo == null || cursoCargo.getCursoId() == null) {
                continue;
            }
            CursoResumen curso = cursos.get(cursoCargo.getCursoId());
            if (curso == null) {
                log.warn("Curso con ID {} no encontrado", cursoCargo.getCursoId());
                continue;
            }
            FacultadResumen facultad = facultades.get(curso.getFacultadId());
            GeograficaResumen geografica = geograficas.get(curso.getGeograficaId());
            if (facultad == null || geografica == null) {
                log.warn("Datos incompletos para el curso {}", curso.getCursoId());
                continue;
            }
            CategoriaResumen categoria = categorias.get(cursoCargo.getCategoriaId());
            filas.add(new Fila(
                    text(facultad.getNombre()), text(geografica.getNombre()), text(curso.getNombre()),
                    text(cursoCargo.getHorasSemanales()),
                    categoria == null ? "" : text(categoria.getNombre()),
                    categoria == null ? "0" : text(categoria.getBasico()),
                    curso.getAnual() == 1 ? "1" : "",
                    curso.getSemestre1() == 1 ? "1" : "",
                    curso.getSemestre2() == 1 ? "1" : ""));
        }
        return filas;
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }

    private String fileName(Integer anho, Integer mes) {
        return "curso-docente.." + anho + "." + mes + ".pdf";
    }
}
