package um.haberes.report.reports.bono.domain.model;

import java.util.List;

/**
 * Grilla del bono por dependencia: una fila por legajo del período con la persona, el mail
 * institucional y si el bono ya fue enviado. Es sólo lectura; el envío es una acción aparte.
 */
public record BonosDependenciaContent(Integer anho, Integer mes, Integer dependenciaId,
                                      String salida, List<Fila> filas) {

    public BonosDependenciaContent {
        filas = List.copyOf(filas);
    }

    public record Fila(Long legajoId, String apellidoNombre, String mailInstitucional,
                       boolean bonoEnviado) {
    }
}
