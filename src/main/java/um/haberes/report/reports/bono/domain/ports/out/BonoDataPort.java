package um.haberes.report.reports.bono.domain.ports.out;

import um.haberes.report.reports.bono.domain.model.BonoFuente;

import java.math.BigDecimal;
import java.util.List;

/**
 * Puerta unica al core para el modulo Bono. Agrupa las lecturas y las pocas escrituras
 * (items en cero, letra, auditoria y marca de envio) para no multiplicar adaptadores.
 */
public interface BonoDataPort {

    BonoFuente cargar(Long legajoId, Integer anho, Integer mes, Integer facultadId);

    BonoFuente.Persona persona(Long legajoId);

    void deleteItemsEnCero(Long legajoId, Integer anho, Integer mes);

    BonoFuente.Letra guardarLetra(Long legajoId, Integer anho, Integer mes, BigDecimal neto,
                                  String cadena);

    void auditar(Long legajoId, Integer anho, Integer mes, Long legajoIdSolicitud,
                 String ipAddress, java.time.OffsetDateTime fecha);

    void marcarBonoEnviado(Long legajoId, Integer anho, Integer mes);

    List<BonoFuente.Liquidacion> liquidacionesDependencia(Integer dependenciaId, Integer anho,
                                                          Integer mes, String salida);
}
