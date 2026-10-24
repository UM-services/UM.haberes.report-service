package um.haberes.report.reports.cargosclaseperiodo.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargoClasePeriodoDefinicion;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargoClasePeriodoDetalle;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargoClasePeriodoVigencia;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargosClasePeriodoContent;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargosClasePeriodoContent.Fila;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargosClasePeriodoContent.Sede;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.in.GenerateCargosClasePeriodoReportUseCase;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.out.CargoClaseCatalogPort;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.out.CargoClaseDetalleCatalogPort;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.out.CargoClasePeriodoCatalogPort;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.out.CargosClasePeriodoPdfRenderer;
import um.haberes.report.shared.domain.model.ClaseResumen;
import um.haberes.report.shared.domain.model.FacultadResumen;
import um.haberes.report.shared.domain.model.GeograficaResumen;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ClaseCatalogPort;
import um.haberes.report.shared.domain.ports.out.FacultadCatalogPort;
import um.haberes.report.shared.domain.ports.out.GeograficaCatalogPort;
import um.haberes.report.shared.domain.ports.out.PersonaCatalogPort;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateCargosClasePeriodoReportUseCaseImpl
        implements GenerateCargosClasePeriodoReportUseCase {

    static final String DISPLAY_NAME = "cargos-clase-periodo.pdf";
    private static final String NO_SEDE = "Sin Sede";

    private final PersonaCatalogPort personaCatalogPort;
    private final FacultadCatalogPort facultadCatalogPort;
    private final GeograficaCatalogPort geograficaCatalogPort;
    private final ClaseCatalogPort claseCatalogPort;
    private final CargoClaseDetalleCatalogPort detalleCatalogPort;
    private final CargoClasePeriodoCatalogPort vigenciaCatalogPort;
    private final CargoClaseCatalogPort cargoClaseCatalogPort;
    private final CargosClasePeriodoPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer facultadId, Integer anho, Integer mes) {
        String facultad = facultadCatalogPort.findById(facultadId)
                .map(FacultadResumen::getNombre)
                .orElse(null);
        if (facultad == null) {
            log.warn("No se encontro la facultad {}: el encabezado se emite sin nombre", facultadId);
        }

        Map<Integer, GeograficaResumen> geograficas = geograficaCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(geografica -> geografica.getGeograficaId() != null)
                .collect(Collectors.toMap(GeograficaResumen::getGeograficaId, g -> g, (a, b) -> a));

        Map<Integer, ClaseResumen> clases = claseCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(clase -> clase.getClaseId() != null)
                .collect(Collectors.toMap(ClaseResumen::getClaseId, c -> c, (a, b) -> a));

        Map<String, List<Fila>> filasPorSede = new LinkedHashMap<>();
        for (PersonaResumen persona : personaCatalogPort.findAllByFacultad(facultadId)) {
            if (persona == null || persona.getLegajoId() == null) {
                continue;
            }
            List<CargoClasePeriodoDetalle> detalles =
                    detalleCatalogPort.findByLegajo(persona.getLegajoId(), anho, mes);
            if (detalles.isEmpty()) {
                log.warn("No se encontraron detalles de cargos para el legajo {} en el periodo {}-{}",
                        persona.getLegajoId(), anho, mes);
                continue;
            }
            for (CargoClasePeriodoDetalle detalle : detalles) {
                if (detalle == null || detalle.cargoClaseId() == null) {
                    continue;
                }
                Fila fila = buildFila(persona, detalle, geograficas, clases);
                if (fila == null) {
                    continue;
                }
                GeograficaResumen sede = detalle.geograficaId() == null
                        ? null : geograficas.get(detalle.geograficaId());
                String sedeNombre = sede == null || sede.getNombre() == null
                        ? NO_SEDE : sede.getNombre();
                filasPorSede.computeIfAbsent(sedeNombre, key -> new ArrayList<>()).add(fila);
            }
        }

        List<Sede> sedes = filasPorSede.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.nullsLast(String::compareTo)))
                .map(entry -> new Sede(entry.getKey(), entry.getValue()))
                .toList();

        Path target = storage.resolve(fileName(facultadId, anho, mes));
        renderer.render(new CargosClasePeriodoContent(anho, mes, facultad, sedes), target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private Fila buildFila(PersonaResumen persona, CargoClasePeriodoDetalle detalle,
                           Map<Integer, GeograficaResumen> geograficas,
                           Map<Integer, ClaseResumen> clases) {
        Map<Long, CargoClasePeriodoVigencia> vigencias = vigenciaCatalogPort
                .findAllByLegajo(persona.getLegajoId()).stream()
                .filter(Objects::nonNull)
                .filter(vigencia -> vigencia.cargoClaseId() != null)
                .collect(Collectors.toMap(CargoClasePeriodoVigencia::cargoClaseId,
                        vigencia -> vigencia, (a, b) -> a));
        CargoClasePeriodoVigencia vigencia = vigencias.get(detalle.cargoClaseId());
        if (vigencia == null) {
            log.warn("No se encontro periodo para cargoClaseId {}", detalle.cargoClaseId());
            return null;
        }

        CargoClasePeriodoDefinicion definicion = cargoClaseCatalogPort
                .findByCargoClaseId(detalle.cargoClaseId())
                .orElse(null);
        if (definicion == null) {
            log.warn("No se encontro cargo clase para cargoClaseId {}", detalle.cargoClaseId());
            return null;
        }

        ClaseResumen clase = definicion.claseId() == null ? null : clases.get(definicion.claseId());
        GeograficaResumen sede = detalle.geograficaId() == null
                ? null : geograficas.get(detalle.geograficaId());
        if (clase == null || sede == null) {
            log.warn("Datos incompletos para cargoClaseId {}", detalle.cargoClaseId());
            return null;
        }

        return new Fila(persona.getLegajoId(), persona.apellidoNombre(), definicion.nombre(),
                vigencia.descripcion(), vigencia.periodoDesde(), vigencia.periodoHasta(),
                vigencia.horas(), clase.getNombre());
    }

    private String fileName(Integer facultadId, Integer anho, Integer mes) {
        return "cargos-clase-periodo.." + facultadId + "." + anho + mes + "..pdf";
    }
}
