package um.haberes.report.reports.bono.application.usecases;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.bono.domain.model.BonoContent;
import um.haberes.report.reports.bono.domain.model.BonoFuente;
import um.haberes.report.reports.bono.domain.model.DetalleCargosContent;
import um.haberes.report.shared.application.exception.ReportException;
import um.haberes.report.shared.domain.model.NumerosEnLetras;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/** Traduce los datos del core al contenido presentable del recibo y del detalle de cargos. */
@Component
public class BonoContentAssembler {

    private static final DecimalFormat MONTO = new DecimalFormat("#,##0.00");
    private static final DecimalFormat HORAS = new DecimalFormat("#0");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Integer CODIGO_ETEC = 29;

    public record Calculado(BonoContent content, BigDecimal neto, String cadena) {
    }

    public Calculado recibo(BonoFuente fuente, Integer anho, Integer mes) {
        validar(fuente);
        BonoFuente.Persona persona = fuente.persona();
        BonoFuente.Control control = fuente.control();
        int meses = Math.max(fuente.antiguedad().mesesDocentes(),
                fuente.antiguedad().mesesAdministrativos());
        Map<Integer, BigDecimal> items = items(fuente.items());
        BigDecimal item1 = items.getOrDefault(1, BigDecimal.ZERO);
        BigDecimal porcentajeEtec = fuente.porcentajeAntiguedadEtec() == null
                ? BigDecimal.ZERO : fuente.porcentajeAntiguedadEtec();

        BigDecimal horasEtec = BigDecimal.ZERO;
        BigDecimal basicoEtec = BigDecimal.ZERO;
        BigDecimal antiguedadEtec = BigDecimal.ZERO;
        BigDecimal presentismoEtec = BigDecimal.ZERO;
        BigDecimal adicionalEtec = BigDecimal.ZERO;
        if (items.containsKey(CODIGO_ETEC) && persona.directivoEtec() == 0) {
            for (BonoFuente.CursoCargo curso : fuente.cursosEtec()) {
                horasEtec = horasEtec.add(seguro(curso.horasSemanales()));
            }
            basicoEtec = horasEtec.multiply(seguro(control.horaReferenciaEtec()))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal indice = fuente.indices().isEmpty()
                    ? BigDecimal.ZERO : fuente.indices().getFirst();
            antiguedadEtec = basicoEtec.multiply(seguro(indice)).setScale(2, RoundingMode.HALF_UP);
            presentismoEtec = basicoEtec.multiply(porcentajeEtec).setScale(2, RoundingMode.HALF_UP);
            adicionalEtec = seguro(items.get(CODIGO_ETEC)).subtract(basicoEtec)
                    .subtract(antiguedadEtec).subtract(presentismoEtec)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        List<BonoContent.Curso> cursos = fuente.cursos().stream()
                .map(curso -> new BonoContent.Curso(text(curso.cargo()), text(curso.curso()),
                        marca(curso.desarraigo()), marca(curso.anual()), marca(curso.semestre1()),
                        marca(curso.semestre2()), HORAS.format(seguro(curso.horasSemanales()))))
                .toList();
        List<BonoContent.Cargo> docentes = cargos(fuente.docentes(), false);
        List<BonoContent.Cargo> noDocentes = cargos(fuente.noDocentes(), true);
        List<BonoContent.Cargo> adicionales = adicionales(fuente);
        List<BonoContent.Cargo> clases = fuente.clases().stream()
                .map(this::clase)
                .toList();

        BigDecimal[] totalRemunerativo = {BigDecimal.ZERO};
        BigDecimal[] totalNoRemunerativo = {BigDecimal.ZERO};
        BigDecimal[] totalDeduccion = {BigDecimal.ZERO};
        List<BonoContent.Concepto> remunerativos = conceptos(fuente.remunerativos(), items,
                persona, basicoEtec, antiguedadEtec, presentismoEtec, adicionalEtec,
                totalRemunerativo);
        List<BonoContent.Concepto> noRemunerativos = conceptos(fuente.noRemunerativos(), items,
                persona, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                totalNoRemunerativo);
        List<BonoContent.Concepto> deducciones = conceptos(fuente.deducciones(), items, persona,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, totalDeduccion);

        BigDecimal neto = seguro(items.get(99));
        String cadena = NumerosEnLetras.convertir(neto);
        BonoContent content = new BonoContent(anho, mes, persona.legajoId(),
                persona.apellido() + ", " + persona.nombre(), text(persona.documento()),
                text(persona.cuil()), text(persona.dependenciaAcronimo()),
                MONTO.format(item1.add(basicoEtec).setScale(2, RoundingMode.HALF_UP)),
                persona.altaAdministrativa() == null ? "" : FECHA.format(
                        persona.altaAdministrativa().withOffsetSameInstant(ZoneOffset.UTC)),
                meses / 12 + "." + meses % 12, text(fuente.banco() == null ? null : fuente.banco().cbu()),
                text(control.aporteJubilatorio()),
                control.fechaPago() == null ? "" : FECHA.format(
                        control.fechaPago().withOffsetSameInstant(ZoneOffset.UTC)),
                control.fechaDesde() == null ? "" : FECHA.format(
                        control.fechaDesde().withOffsetSameInstant(ZoneOffset.UTC)),
                control.fechaHasta() == null ? "" : FECHA.format(
                        control.fechaHasta().withOffsetSameInstant(ZoneOffset.UTC)),
                control.fechaDeposito() == null ? "" : FECHA.format(
                        control.fechaDeposito().withOffsetSameInstant(ZoneOffset.UTC)),
                cursos, docentes, noDocentes, adicionales, clases, remunerativos, noRemunerativos,
                deducciones, MONTO.format(totalRemunerativo[0].setScale(2, RoundingMode.HALF_UP)),
                MONTO.format(totalNoRemunerativo[0].setScale(2, RoundingMode.HALF_UP)),
                MONTO.format(totalDeduccion[0].setScale(2, RoundingMode.HALF_UP)),
                MONTO.format(neto), cadena);
        return new Calculado(content, neto, cadena);
    }

    public DetalleCargosContent detalle(BonoFuente fuente, Long legajoId, Integer facultadId) {
        if (fuente.persona() == null) {
            throw new ReportException("No se encontró la persona con legajo " + legajoId);
        }
        if (fuente.antiguedad() == null) {
            throw new ReportException("No se encontró la antigüedad del legajo " + legajoId);
        }
        int meses = Math.max(fuente.antiguedad().mesesDocentes(),
                fuente.antiguedad().mesesAdministrativos());
        List<BonoContent.Curso> cursos = fuente.cursos().stream()
                .map(curso -> new BonoContent.Curso(text(curso.cargo()), text(curso.curso()),
                        marca(curso.desarraigo()), marca(curso.anual()), marca(curso.semestre1()),
                        marca(curso.semestre2()), HORAS.format(seguro(curso.horasSemanales()))))
                .toList();
        return new DetalleCargosContent(legajoId,
                fuente.persona().apellido() + ", " + fuente.persona().nombre(),
                meses / 12 + "." + meses % 12, text(fuente.facultadNombre()), cursos,
                cargos(fuente.docentes(), false), cargos(fuente.noDocentes(), true),
                fuente.clases().stream().map(this::clase).toList());
    }

    private BonoContent.Cargo clase(BonoFuente.Clase clase) {
        String descripcion = text(clase.clase()) + " / " + text(clase.cargoClase()) + " / "
                + text(clase.descripcion()) + " / " + text(clase.geografica()) + " / "
                + clase.horas() + " horas";
        return new BonoContent.Cargo(text(clase.dependencia()), descripcion,
                MONTO.format(seguro(clase.valorHora()).multiply(new BigDecimal(clase.horas()))));
    }

    private List<BonoContent.Cargo> cargos(List<BonoFuente.CargoLiquidado> cargos,
                                           boolean noDocente) {
        return cargos.stream()
                .map(cargo -> {
                    BigDecimal multiplicador = BigDecimal.ONE;
                    if (noDocente) {
                        multiplicador = seguro(cargo.horasJornada()).compareTo(BigDecimal.ZERO) == 0
                                ? new BigDecimal(cargo.jornada()) : seguro(cargo.horasJornada());
                    }
                    return new BonoContent.Cargo(text(cargo.dependencia()), text(cargo.categoria()),
                            MONTO.format(seguro(cargo.basico()).multiply(multiplicador)
                                    .setScale(2, RoundingMode.HALF_UP)));
                })
                .toList();
    }

    private List<BonoContent.Cargo> adicionales(BonoFuente fuente) {
        Map<Integer, BigDecimal> acumulado = new LinkedHashMap<>();
        for (BonoFuente.Adicional adicional : fuente.adicionales()) {
            if (adicional == null || adicional.dependenciaId() == null) {
                continue;
            }
            acumulado.merge(adicional.dependenciaId(), seguro(adicional.adicional()),
                    (a, b) -> a.add(b).setScale(2, RoundingMode.HALF_UP));
        }
        Map<Integer, String> acronimos = fuente.dependencias().stream()
                .filter(Objects::nonNull)
                .filter(dependencia -> dependencia.dependenciaId() != null)
                .collect(Collectors.toMap(BonoFuente.Dependencia::dependenciaId,
                        dependencia -> text(dependencia.acronimo()), (a, b) -> a));
        String nombreCodigo = fuente.codigo981() == null ? "" : text(fuente.codigo981().nombre());
        return acumulado.entrySet().stream()
                .map(entry -> new BonoContent.Cargo(acronimos.getOrDefault(entry.getKey(), ""),
                        nombreCodigo, MONTO.format(entry.getValue())))
                .toList();
    }

    private List<BonoContent.Concepto> conceptos(List<BonoFuente.Grupo> grupos,
                                                 Map<Integer, BigDecimal> items,
                                                 BonoFuente.Persona persona,
                                                 BigDecimal basicoEtec, BigDecimal antiguedadEtec,
                                                 BigDecimal presentismoEtec, BigDecimal adicionalEtec,
                                                 BigDecimal[] total) {
        List<BonoContent.Concepto> conceptos = new ArrayList<>();
        for (BonoFuente.Grupo grupo : grupos) {
            if (grupo == null || grupo.codigoId() == null || !items.containsKey(grupo.codigoId())) {
                continue;
            }
            if (CODIGO_ETEC.equals(grupo.codigoId()) && persona.directivoEtec() == 0) {
                conceptos.add(concepto(grupo.codigoId(), "BASICO ETEC", basicoEtec, total));
                conceptos.add(concepto(grupo.codigoId(), "ANTIGUEDAD ETEC", antiguedadEtec, total));
                conceptos.add(concepto(grupo.codigoId(), "PRESENTISMO ETEC", presentismoEtec, total));
                conceptos.add(concepto(grupo.codigoId(), "ADICIONAL ETEC", adicionalEtec, total));
            } else {
                conceptos.add(concepto(grupo.codigoId(), text(grupo.nombre()),
                        items.get(grupo.codigoId()), total));
            }
        }
        return conceptos;
    }

    private BonoContent.Concepto concepto(Integer codigoId, String nombre, BigDecimal importe,
                                          BigDecimal[] total) {
        BigDecimal valor = seguro(importe);
        total[0] = total[0].add(valor).setScale(2, RoundingMode.HALF_UP);
        return new BonoContent.Concepto(String.valueOf(codigoId), nombre, MONTO.format(valor));
    }

    private Map<Integer, BigDecimal> items(List<BonoFuente.Item> items) {
        return items.stream()
                .filter(Objects::nonNull)
                .filter(item -> item.codigoId() != null)
                .collect(Collectors.toMap(BonoFuente.Item::codigoId,
                        item -> seguro(item.importe()), (a, b) -> a));
    }

    private void validar(BonoFuente fuente) {
        if (fuente == null || fuente.persona() == null) {
            throw new ReportException("No se encontró la persona para generar el bono");
        }
        if (fuente.control() == null) {
            throw new ReportException("No se encontró el control del período para el bono");
        }
        if (fuente.antiguedad() == null) {
            throw new ReportException("No se encontró la antigüedad para generar el bono");
        }
        if (fuente.control().fechaDesde() == null || fuente.control().fechaHasta() == null
                || fuente.control().fechaPago() == null || fuente.control().fechaDeposito() == null) {
            throw new ReportException("El control del período no tiene todas las fechas requeridas");
        }
    }

    private static BigDecimal seguro(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private String marca(byte activo) {
        return activo == 1 ? "*" : "";
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
