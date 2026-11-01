package um.haberes.report.reports.bono.infrastructure.web.dto;

import java.util.List;

/** Contrato HTTP de la grilla del bono por dependencia. */
public record BonosDependenciaResponse(Integer anho, Integer mes, String dependenciaId, String salida,
                                       List<Fila> filas) {

    public BonosDependenciaResponse {
        filas = List.copyOf(filas);
    }

    public record Fila(String legajoId, String apellidoNombre, String mailInstitucional,
                       boolean bonoEnviado) {
    }
}
