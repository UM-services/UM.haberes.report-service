package um.haberes.report.reports.bono.domain.ports.out;

import java.nio.file.Path;

/** Envio del recibo por correo. */
public interface BonoMailPort {

    void enviar(String destinatario, String asunto, String cuerpo, Path adjunto);
}
