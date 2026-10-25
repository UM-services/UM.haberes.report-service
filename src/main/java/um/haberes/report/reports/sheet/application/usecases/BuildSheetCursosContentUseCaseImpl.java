package um.haberes.report.reports.sheet.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.sheet.domain.model.SheetCursosContent;
import um.haberes.report.reports.sheet.domain.model.SheetCursosContent.Fila;
import um.haberes.report.reports.sheet.domain.ports.in.BuildSheetCursosContentUseCase;
import um.haberes.report.shared.domain.model.CargoTipoResumen;
import um.haberes.report.shared.domain.model.CursoCargoResumen;
import um.haberes.report.shared.domain.model.CursoResumen;
import um.haberes.report.shared.domain.model.FacultadResumen;
import um.haberes.report.shared.domain.model.GeograficaResumen;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.domain.ports.out.CargoTipoCatalogPort;
import um.haberes.report.shared.domain.ports.out.CursoCargoCatalogPort;
import um.haberes.report.shared.domain.ports.out.CursoCatalogPort;
import um.haberes.report.shared.domain.ports.out.FacultadCatalogPort;
import um.haberes.report.shared.domain.ports.out.GeograficaCatalogPort;
import um.haberes.report.shared.domain.ports.out.PersonaCatalogPort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Cursos por período (legacy: {@code SheetService} → {@code report/sheet/generatecursos}).
 *
 * <p>Una única consulta de cursos-cargo por período (antes se pedía una por legajo) y el batch
 * de personas. Los catálogos se resuelven en memoria y el "Dictado" conserva la precedencia
 * original del sistema previo: Anual, luego 1er Semestre, luego 2do Semestre.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BuildSheetCursosContentUseCaseImpl implements BuildSheetCursosContentUseCase {

    private final FacultadCatalogPort facultadCatalogPort;
    private final GeograficaCatalogPort geograficaCatalogPort;
    private final CursoCatalogPort cursoCatalogPort;
    private final CargoTipoCatalogPort cargoTipoCatalogPort;
    private final CursoCargoCatalogPort cursoCargoCatalogPort;
    private final PersonaCatalogPort personaCatalogPort;

    @Override
    public SheetCursosContent build(Integer anho, Integer mes) {
        Map<Integer, FacultadResumen> facultades = facultadCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(facultad -> facultad.getFacultadId() != null)
                .collect(Collectors.toMap(FacultadResumen::getFacultadId, f -> f, (a, b) -> a));
        Map<Integer, GeograficaResumen> geograficas = geograficaCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(geografica -> geografica.getGeograficaId() != null)
                .collect(Collectors.toMap(GeograficaResumen::getGeograficaId, g -> g, (a, b) -> a));
        Map<Long, CursoResumen> cursos = cursoCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(curso -> curso.getCursoId() != null)
                .collect(Collectors.toMap(CursoResumen::getCursoId, c -> c, (a, b) -> a));
        Map<Integer, CargoTipoResumen> cargos = cargoTipoCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(cargo -> cargo.getCargoTipoId() != null)
                .collect(Collectors.toMap(CargoTipoResumen::getCargoTipoId, c -> c, (a, b) -> a));

        // Una sola consulta al período; los cursos de cada legajo se agrupan en memoria.
        Map<Long, List<CursoCargoResumen>> cursosPorLegajo = cursoCargoCatalogPort
                .findAllByPeriodo(anho, mes).stream()
                .filter(Objects::nonNull)
                .filter(cursoCargo -> cursoCargo.getLegajoId() != null)
                .collect(Collectors.groupingBy(CursoCargoResumen::getLegajoId));

        List<Long> legajoIds = cursosPorLegajo.keySet().stream().toList();
        List<PersonaResumen> personas = legajoIds.isEmpty() ? List.of()
                : personaCatalogPort.findAllByLegajos(legajoIds);

        // Mismo orden que usaba el reporte por legajo: facultad, sede, cargo y nombre del curso.
        Comparator<CursoCargoResumen> orden = Comparator
                .comparing((CursoCargoResumen cursoCargo) -> facultadId(cursos, cursoCargo),
                        Comparator.nullsLast(Integer::compareTo))
                .thenComparing(cursoCargo -> geograficaId(cursos, cursoCargo),
                        Comparator.nullsLast(Integer::compareTo))
                .thenComparing(CursoCargoResumen::getCargoTipoId,
                        Comparator.nullsLast(Integer::compareTo))
                .thenComparing(cursoCargo -> nombreCurso(cursos, cursoCargo),
                        Comparator.nullsLast(String::compareTo));

        List<Fila> filas = new ArrayList<>();
        for (PersonaResumen persona : personas) {
            if (persona == null || persona.getLegajoId() == null) {
                continue;
            }
            for (CursoCargoResumen cursoCargo : cursosPorLegajo
                    .getOrDefault(persona.getLegajoId(), List.of()).stream().sorted(orden).toList()) {
                CursoResumen curso = cursos.get(cursoCargo.getCursoId());
                FacultadResumen facultad = curso == null
                        ? null : facultades.get(curso.getFacultadId());
                GeograficaResumen geografica = curso == null
                        ? null : geograficas.get(curso.getGeograficaId());
                CargoTipoResumen cargoTipo = cargos.get(cursoCargo.getCargoTipoId());
                if (curso == null || facultad == null || geografica == null || cargoTipo == null) {
                    log.warn("Datos incompletos para el legajo {}: se emite la fila con celdas vacias",
                            persona.getLegajoId());
                }
                filas.add(new Fila(persona.getLegajoId(), persona.apellidoNombre(),
                        mes + "/" + anho,
                        curso == null ? null : curso.getFacultadId(),
                        facultad == null ? null : facultad.getNombre(),
                        curso == null ? null : curso.getGeograficaId(),
                        geografica == null ? null : geografica.getNombre(),
                        cursoCargo.getCursoId(),
                        curso == null ? null : curso.getNombre(),
                        cursoCargo.getCargoTipoId(),
                        cargoTipo == null ? null : cargoTipo.getNombre(),
                        cursoCargo.getHorasSemanales(), dictado(curso)));
            }
        }
        return new SheetCursosContent(anho, mes, filas);
    }

    private Integer facultadId(Map<Long, CursoResumen> cursos, CursoCargoResumen cursoCargo) {
        CursoResumen curso = cursos.get(cursoCargo.getCursoId());
        return curso == null ? null : curso.getFacultadId();
    }

    private Integer geograficaId(Map<Long, CursoResumen> cursos, CursoCargoResumen cursoCargo) {
        CursoResumen curso = cursos.get(cursoCargo.getCursoId());
        return curso == null ? null : curso.getGeograficaId();
    }

    private String nombreCurso(Map<Long, CursoResumen> cursos, CursoCargoResumen cursoCargo) {
        CursoResumen curso = cursos.get(cursoCargo.getCursoId());
        return curso == null ? null : curso.getNombre();
    }

    private String dictado(CursoResumen curso) {
        if (curso == null) {
            return "";
        }
        String dictado = "";
        if (curso.getAnual() == 1) {
            dictado = "Anual";
        }
        if (curso.getSemestre1() == 1) {
            dictado = "1er Semestre";
        }
        if (curso.getSemestre2() == 1) {
            dictado = "2do Semestre";
        }
        return dictado;
    }
}
