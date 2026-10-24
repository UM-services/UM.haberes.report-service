package um.haberes.report.reports.bono.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Datos crudos que necesita el recibo/detalle de cargos, ya resueltos por el core. */
public record BonoFuente(Persona persona, Control control, Antiguedad antiguedad,
                         List<BigDecimal> indices, Banco banco, List<Item> items,
                         List<CursoCargo> cursos, List<CursoCargo> cursosEtec,
                         List<CargoLiquidado> docentes, List<CargoLiquidado> noDocentes,
                         List<Clase> clases, List<Adicional> adicionales,
                         List<Dependencia> dependencias, Codigo codigo981,
                         List<Grupo> remunerativos, List<Grupo> noRemunerativos,
                         List<Grupo> deducciones, Letra letra,
                         BigDecimal porcentajeAntiguedadEtec, Contacto contacto,
                         Long legajoControlId, boolean bonoEnviado, String facultadNombre) {

    public BonoFuente {
        indices = indices == null ? List.of() : List.copyOf(indices);
        items = List.copyOf(items);
        cursos = List.copyOf(cursos);
        cursosEtec = List.copyOf(cursosEtec);
        docentes = List.copyOf(docentes);
        noDocentes = List.copyOf(noDocentes);
        clases = List.copyOf(clases);
        adicionales = List.copyOf(adicionales);
        dependencias = List.copyOf(dependencias);
        remunerativos = List.copyOf(remunerativos);
        noRemunerativos = List.copyOf(noRemunerativos);
        deducciones = List.copyOf(deducciones);
    }

    public record Persona(Long legajoId, String apellido, String nombre, String documento,
                          String cuil, String dependenciaAcronimo, OffsetDateTime altaAdministrativa,
                          byte directivoEtec) {
    }

    public record Control(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta,
                          OffsetDateTime fechaPago, OffsetDateTime fechaDeposito,
                          String aporteJubilatorio, BigDecimal horaReferenciaEtec) {
    }

    public record Antiguedad(int mesesDocentes, int mesesAdministrativos) {
    }

    public record Banco(String cbu) {
    }

    public record Item(Integer codigoId, BigDecimal importe) {
    }

    public record CursoCargo(String cargo, String curso, byte desarraigo, byte anual,
                             byte semestre1, byte semestre2, BigDecimal horasSemanales) {
    }

    public record CargoLiquidado(String dependencia, String categoria, BigDecimal basico,
                                 BigDecimal horasJornada, int jornada) {
    }

    public record Clase(String dependencia, String clase, String cargoClase, String descripcion,
                        String geografica, int horas, BigDecimal valorHora) {
    }

    public record Adicional(Integer dependenciaId, BigDecimal adicional) {
    }

    public record Dependencia(Integer dependenciaId, String acronimo) {
    }

    public record Codigo(Integer codigoId, String nombre) {
    }

    public record Grupo(Integer codigoId, String nombre) {
    }

    public record Letra(Long letraId, BigDecimal neto, String cadena) {
    }

    public record Contacto(String mailInstitucional) {
    }

    public record Liquidacion(Long legajoId) {
    }
}
