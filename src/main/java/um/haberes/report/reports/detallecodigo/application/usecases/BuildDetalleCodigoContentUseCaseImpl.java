package um.haberes.report.reports.detallecodigo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.detallecodigo.domain.model.DetalleCodigoContent;
import um.haberes.report.reports.detallecodigo.domain.model.DetalleCodigoContent.Fila;
import um.haberes.report.reports.detallecodigo.domain.ports.in.BuildDetalleCodigoContentUseCase;
import um.haberes.report.shared.domain.model.Importes;
import um.haberes.report.shared.domain.model.ItemResumen;
import um.haberes.report.shared.domain.model.NovedadAcumuladoResumen;
import um.haberes.report.shared.domain.ports.out.CodigoCatalogPort;
import um.haberes.report.shared.domain.ports.out.ItemCatalogPort;
import um.haberes.report.shared.domain.ports.out.NovedadAcumuladoCatalogPort;
import um.haberes.report.shared.domain.ports.out.PersonaCatalogPort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Detalle por código (legacy: {@code SheetService.comparativoCodigo}).
 *
 * <p>Una consulta por fuente: ítems del código, acumulado de novedades del código y personas
 * con liquidación en el período (batch). La fila existe por persona liquidada, exista o no
 * movimiento para el código; en ese caso las columnas quedan vacías, igual que en el reporte.
 */
@Component
@RequiredArgsConstructor
public class BuildDetalleCodigoContentUseCaseImpl implements BuildDetalleCodigoContentUseCase {

    private final CodigoCatalogPort codigoCatalogPort;
    private final ItemCatalogPort itemCatalogPort;
    private final NovedadAcumuladoCatalogPort novedadAcumuladoCatalogPort;
    private final PersonaCatalogPort personaCatalogPort;

    @Override
    public DetalleCodigoContent build(Integer codigoId, Integer anho, Integer mes) {
        String codigoNombre = codigoCatalogPort.findById(codigoId)
                .map(codigo -> codigo.getNombre() == null ? "" : codigo.getNombre())
                .orElse("");
        Map<Long, BigDecimal> liquidados = itemCatalogPort.findAllByCodigo(codigoId, anho, mes).stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getLegajoId() != null)
                .collect(Collectors.toMap(ItemResumen::getLegajoId, ItemResumen::getImporte,
                        (a, b) -> a));
        Map<Long, NovedadAcumuladoResumen> novedades = novedadAcumuladoCatalogPort
                .findAllByCodigo(codigoId, anho, mes).stream()
                .filter(Objects::nonNull)
                .filter(novedad -> novedad.getLegajoId() != null)
                .collect(Collectors.toMap(NovedadAcumuladoResumen::getLegajoId, novedad -> novedad,
                        (a, b) -> a));

        List<Fila> filas = new ArrayList<>();
        for (var persona : personaCatalogPort.findAllByLiquidado(anho, mes)) {
            if (persona == null || persona.getLegajoId() == null) {
                continue;
            }
            BigDecimal liquidado = liquidados.get(persona.getLegajoId());
            NovedadAcumuladoResumen novedad = novedades.get(persona.getLegajoId());
            filas.add(new Fila(text(persona.getLegajoId()), persona.apellidoNombre(),
                    Importes.format(liquidado),
                    novedad == null ? "" : Importes.format(novedad.getImporte()),
                    novedad == null ? null : novedad.getCantidad()));
        }
        return new DetalleCodigoContent(codigoId, codigoNombre, anho, mes, filas);
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
