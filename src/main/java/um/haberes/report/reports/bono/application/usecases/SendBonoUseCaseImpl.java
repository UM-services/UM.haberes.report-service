package um.haberes.report.reports.bono.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.bono.domain.model.BonoFuente;
import um.haberes.report.reports.bono.domain.ports.in.GenerateBonoReportUseCase;
import um.haberes.report.reports.bono.domain.ports.in.SendBonoUseCase;
import um.haberes.report.reports.bono.domain.ports.out.BonoDataPort;
import um.haberes.report.reports.bono.domain.ports.out.BonoMailPort;
import um.haberes.report.shared.domain.model.ReportFile;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendBonoUseCaseImpl implements SendBonoUseCase {

    private final GenerateBonoReportUseCase generateBono;
    private final BonoDataPort dataPort;
    private final BonoMailPort mailPort;

    @Override
    public String enviar(Long legajoId, Integer anho, Integer mes, Long legajoIdSolicitud,
                         String ipAddress, boolean auditar) {
        ReportFile bono = generateBono.generar(legajoId, anho, mes, legajoIdSolicitud, ipAddress,
                auditar);
        if (bono == null || bono.path() == null) {
            return "ERROR: Sin Chequera para ENVIAR";
        }

        BonoFuente fuente = dataPort.cargar(legajoId, anho, mes, null);
        if (fuente.persona() == null) {
            return "ERROR: Sin correos para ENVIAR";
        }
        BonoFuente.Contacto contacto = fuente.contacto();
        if (contacto == null || contacto.mailInstitucional() == null
                || contacto.mailInstitucional().isBlank()) {
            return "ERROR: Sin correos para ENVIAR";
        }

        String cuerpo = "Estimad@ " + fuente.persona().apellido() + ", "
                + fuente.persona().nombre() + ": \n"
                + "\n"
                + "Le enviamos como archivo adjunto su bono de sueldo.\n"
                + "\n"
                + "Atentamente.\n"
                + "\n"
                + "Universidad de Mendoza\n"
                + "\n"
                + "\n"
                + "Por favor no responda este mail, fue generado automáticamente. "
                + "Su respuesta no será leída.\n";
        try {
            mailPort.enviar(contacto.mailInstitucional(),
                    "Envío Automático de Bono de Sueldo -> " + bono.path(), cuerpo, bono.path());
        } catch (Exception e) {
            log.error("No se pudo enviar el bono del legajo {}: {}", legajoId, e.getMessage());
            return "ERROR: No pudo ENVIARSE";
        }

        dataPort.marcarBonoEnviado(legajoId, anho, mes);
        return "Envío de Correo Ok!!";
    }
}
