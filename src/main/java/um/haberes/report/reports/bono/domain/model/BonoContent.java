package um.haberes.report.reports.bono.domain.model;

import java.util.List;

/** Contenido ya calculado del recibo de haberes. */
public record BonoContent(Integer anho, Integer mes, Long legajoId, String apellidoNombre,
                          String documento, String cuil, String dependencia, String basico,
                          String ingreso, String antiguedad, String cbu, String aporteJubilatorio,
                          String fechaPago, String fechaDesde, String fechaHasta,
                          String fechaDeposito, List<Curso> cursos, List<Cargo> docentes,
                          List<Cargo> noDocentes, List<Cargo> adicionales, List<Cargo> clases,
                          List<Concepto> remunerativos, List<Concepto> noRemunerativos,
                          List<Concepto> deducciones, String totalRemunerativo,
                          String totalNoRemunerativo, String totalDeduccion, String neto,
                          String cadena) {

    public BonoContent {
        cursos = List.copyOf(cursos);
        docentes = List.copyOf(docentes);
        noDocentes = List.copyOf(noDocentes);
        adicionales = List.copyOf(adicionales);
        clases = List.copyOf(clases);
        remunerativos = List.copyOf(remunerativos);
        noRemunerativos = List.copyOf(noRemunerativos);
        deducciones = List.copyOf(deducciones);
    }

    public record Curso(String cargo, String curso, String desarraigo, String anual,
                        String semestre1, String semestre2, String horas) {
    }

    public record Cargo(String dependencia, String cargo, String basico) {
    }

    public record Concepto(String codigo, String nombre, String importe) {
    }
}
