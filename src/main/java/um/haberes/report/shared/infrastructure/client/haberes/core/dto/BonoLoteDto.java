package um.haberes.report.shared.infrastructure.client.haberes.core.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Contrato de {@code POST /api/haberes/core/bono/dependencia/{dep}/{anho}/{mes}/{salida}/lote}.
 *
 * <p>Devuelve el lote completo de una dependencia: los catálogos (dependencias, código 981 y
 * grupos) y el control del período viajan una sola vez, y cada legajo trae sólo sus datos.
 */
public record BonoLoteDto(Integer anho, Integer mes, Integer dependenciaId, String salida,
                          Control control, Catalogo catalogo, List<Legajo> legajos) {

    public record Control(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta,
                          OffsetDateTime fechaPago, OffsetDateTime fechaDeposito,
                          String aporteJubilatorio, BigDecimal horaReferenciaEtec) {
    }

    public record Catalogo(List<Dependencia> dependencias, Codigo codigo981,
                           List<Grupo> remunerativos, List<Grupo> noRemunerativos,
                           List<Grupo> deducciones) {
    }

    public record Dependencia(Integer dependenciaId, String acronimo) {
    }

    public record Codigo(Integer codigoId, String nombre) {
    }

    public record Grupo(Integer codigoId, String nombre) {
    }

    public record Legajo(Long legajoId, Persona persona, Antiguedad antiguedad,
                         List<BigDecimal> indices, String cbu, List<Item> items,
                         List<CursoCargo> cursos, List<CursoCargo> cursosEtec,
                         List<CargoLiquidado> docentes, List<CargoLiquidado> noDocentes,
                         List<Clase> clases, List<Adicional> adicionales,
                         BigDecimal porcentajeAntiguedadEtec, String mailInstitucional,
                         Long legajoControlId, boolean bonoEnviado) {
    }

    public record Persona(Long legajoId, String apellido, String nombre, String documento,
                          String cuil, Integer dependenciaId, OffsetDateTime altaAdministrativa,
                          byte directivoEtec) {
    }

    public record Antiguedad(int mesesDocentes, int mesesAdministrativos) {
    }

    public record Item(Integer codigoId, BigDecimal importe) {
    }

    public record CursoCargo(String cargo, String curso, byte desarraigo, byte anual, byte semestre1,
                             byte semestre2, BigDecimal horasSemanales) {
    }

    public record CargoLiquidado(String dependencia, String categoria, BigDecimal basico,
                                 BigDecimal horasJornada, int jornada) {
    }

    public record Clase(String dependencia, String clase, String cargoClase, String descripcion,
                        String geografica, int horas, BigDecimal valorHora) {
    }

    public record Adicional(Integer dependenciaId, BigDecimal adicional) {
    }
}
