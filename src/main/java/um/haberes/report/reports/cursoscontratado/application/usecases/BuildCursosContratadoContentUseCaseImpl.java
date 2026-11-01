package um.haberes.report.reports.cursoscontratado.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cursoscontratado.domain.model.CursosContratadoContent;
import um.haberes.report.reports.cursoscontratado.domain.model.CursosContratadoContent.Fila;
import um.haberes.report.reports.cursoscontratado.domain.ports.in.BuildCursosContratadoContentUseCase;
import um.haberes.report.shared.domain.model.CargoTipoResumen;
import um.haberes.report.shared.domain.model.ContratadoPersonaResumen;
import um.haberes.report.shared.domain.model.CursoCargoContratadoResumen;
import um.haberes.report.shared.domain.model.CursoResumen;
import um.haberes.report.shared.domain.model.FacultadResumen;
import um.haberes.report.shared.domain.model.GeograficaResumen;
import um.haberes.report.shared.domain.ports.out.CargoTipoCatalogPort;
import um.haberes.report.shared.domain.ports.out.ContratadoPersonaCatalogPort;
import um.haberes.report.shared.domain.ports.out.CursoCargoContratadoCatalogPort;
import um.haberes.report.shared.domain.ports.out.CursoCatalogPort;
import um.haberes.report.shared.domain.ports.out.FacultadCatalogPort;
import um.haberes.report.shared.domain.ports.out.GeograficaCatalogPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Cursos por contratado (legacy: {@code ContratadoService.generatePlanillaContratados}).
 *
 * <p>Antes se pedían los cursos de cada contratado (N+1 cruzando a tesoreria-core); ahora es
 * una sola consulta al período, agrupada en memoria por persona, y se mantiene el orden del
 * listado de contratados.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BuildCursosContratadoContentUseCaseImpl implements BuildCursosContratadoContentUseCase {

    private final ContratadoPersonaCatalogPort contratadoPersonaCatalogPort;
    private final CursoCargoContratadoCatalogPort cursoCargoContratadoCatalogPort;
    private final CursoCatalogPort cursoCatalogPort;
    private final FacultadCatalogPort facultadCatalogPort;
    private final GeograficaCatalogPort geograficaCatalogPort;
    private final CargoTipoCatalogPort cargoTipoCatalogPort;

    @Override
    public CursosContratadoContent build(Integer anho, Integer mes) {
        List<ContratadoPersonaResumen> contratados = contratadoPersonaCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .toList();
        if (contratados.isEmpty()) {
            return new CursosContratadoContent(anho, mes, List.of());
        }

        Map<String, List<CursoCargoContratadoResumen>> cursosPorPersona =
                cursoCargoContratadoCatalogPort.findAllByPeriodo(anho, mes).stream()
                        .filter(Objects::nonNull)
                        .filter(cursoCargo -> cursoCargo.getPersonaId() != null)
                        .collect(Collectors.groupingBy(cursoCargo -> key(
                                cursoCargo.getPersonaId(), cursoCargo.getDocumentoId())));
        Map<Long, CursoResumen> cursos = cursoCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(curso -> curso.getCursoId() != null)
                .collect(Collectors.toMap(CursoResumen::getCursoId, curso -> curso, (a, b) -> a));
        Map<Integer, FacultadResumen> facultades = facultadCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(facultad -> facultad.getFacultadId() != null)
                .collect(Collectors.toMap(FacultadResumen::getFacultadId, f -> f, (a, b) -> a));
        Map<Integer, GeograficaResumen> geograficas = geograficaCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(geografica -> geografica.getGeograficaId() != null)
                .collect(Collectors.toMap(GeograficaResumen::getGeograficaId, g -> g, (a, b) -> a));
        Map<Integer, CargoTipoResumen> cargos = cargoTipoCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(cargo -> cargo.getCargoTipoId() != null)
                .collect(Collectors.toMap(CargoTipoResumen::getCargoTipoId, c -> c, (a, b) -> a));

        List<Fila> filas = new ArrayList<>();
        for (ContratadoPersonaResumen contratado : contratados) {
            for (CursoCargoContratadoResumen cursoCargo : cursosPorPersona.getOrDefault(
                    key(contratado.getPersonaId(), contratado.getDocumentoId()), List.of())) {
                CursoResumen curso = cursos.get(cursoCargo.getCursoId());
                FacultadResumen facultad = curso == null ? null : facultades.get(curso.getFacultadId());
                GeograficaResumen geografica = curso == null ? null : geograficas.get(curso.getGeograficaId());
                CargoTipoResumen cargoTipo = cargos.get(cursoCargo.getCargoTipoId());
                if (curso == null || facultad == null || geografica == null || cargoTipo == null) {
                    log.warn("Datos incompletos para el curso {} del contratado {}: se emite la fila con celdas vacias",
                            cursoCargo.getCursoId(), contratado.getCuit());
                }
                filas.add(new Fila(text(contratado.getCuit()), contratado.apellidoNombre(),
                        mes + "/" + anho,
                        facultad == null ? "" : text(facultad.getNombre()),
                        geografica == null ? "" : text(geografica.getNombre()),
                        curso == null ? "" : text(curso.getNombre()),
                        cargoTipo == null ? "" : text(cargoTipo.getNombre()),
                        cursoCargo.getHorasSemanales() == null ? ""
                                : cursoCargo.getHorasSemanales().stripTrailingZeros().toPlainString(),
                        dictado(curso)));
            }
        }
        return new CursosContratadoContent(anho, mes, filas);
    }

    private String key(java.math.BigDecimal personaId, Integer documentoId) {
        return (personaId == null ? "" : personaId.toPlainString()) + "|"
                + (documentoId == null ? "" : documentoId);
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

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
