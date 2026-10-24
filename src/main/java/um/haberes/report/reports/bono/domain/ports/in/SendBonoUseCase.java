package um.haberes.report.reports.bono.domain.ports.in;

public interface SendBonoUseCase {

    String enviar(Long legajoId, Integer anho, Integer mes, Long legajoIdSolicitud,
                  String ipAddress, boolean auditar);
}
