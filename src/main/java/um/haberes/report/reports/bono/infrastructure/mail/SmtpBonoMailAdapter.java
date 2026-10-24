package um.haberes.report.reports.bono.infrastructure.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.bono.domain.ports.out.BonoMailPort;
import um.haberes.report.shared.application.exception.ReportException;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class SmtpBonoMailAdapter implements BonoMailPort {

    private final JavaMailSender javaMailSender;

    @Override
    public void enviar(String destinatario, String asunto, String cuerpo, Path adjunto) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(destinatario);
            helper.setText(cuerpo);
            helper.setReplyTo("no-reply@um.edu.ar");
            helper.setSubject(asunto);
            helper.addAttachment(adjunto.getFileName().toString(),
                    new FileSystemResource(adjunto));
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new ReportException("No se pudo enviar el bono por correo", e);
        }
    }
}
