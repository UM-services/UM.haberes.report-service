/**
 *
 */
package um.haberes.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.service.BonoService;
import um.haberes.report.util.Tool;

import java.io.FileNotFoundException;

/**
 * @author daniel
 */
@RestController
@RequestMapping("/api/haberes/report/bono")
public class BonoController {

    private final BonoService service;

    @Autowired
    public BonoController(BonoService service) {
        this.service = service;
    }

    @GetMapping("/generatePdf/{legajoId}/{anho}/{mes}/{legajoIdSolicitud}/{ipAddress}")
    public ResponseEntity<Resource> generatePdf(@PathVariable Long legajoId, @PathVariable Integer anho,
                                                @PathVariable Integer mes, @PathVariable Long legajoIdSolicitud, @PathVariable String ipAddress)
            throws FileNotFoundException {
        return Tool.generateFile(service.generatePdf(legajoId, anho, mes, legajoIdSolicitud, ipAddress), "bono.pdf");
    }

    @GetMapping("/detalleCargos/{legajoId}/{anho}/{mes}/{facultadId}")
    public ResponseEntity<Resource> generateDetalleCargos(@PathVariable Long legajoId, @PathVariable Integer anho,
                                                @PathVariable Integer mes, @PathVariable Integer facultadId)
            throws FileNotFoundException {
        return Tool.generateFile(service.generateDetalleCargosPdf(legajoId, anho, mes, facultadId), "bono.pdf");
    }

    @GetMapping("/generatePdfDependencia/{anho}/{mes}/{dependenciaId}/{salida}/{legajoIdSolicitud}/{ipAddress}")
    public ResponseEntity<Resource> generatePdfDependencia(@PathVariable Integer anho, @PathVariable Integer mes,
                                                           @PathVariable Integer dependenciaId, @PathVariable String salida, @PathVariable Long legajoIdSolicitud,
                                                           @PathVariable String ipAddress) throws FileNotFoundException {
        return Tool.generateFile(service.generatePdfDependencia(anho, mes, dependenciaId, salida, legajoIdSolicitud,
                ipAddress), "bonos.pdf");
    }

//    @GetMapping("/sendBono/{legajoId}/{anho}/{mes}/{legajoIdSolicitud}/{ipAddress}")
//    public String sendBono(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes,
//                           @PathVariable Long legajoIdSolicitud, @PathVariable String ipAddress) throws MessagingException {
//        return service.sendBono(legajoId, anho, mes, legajoIdSolicitud, ipAddress);
//    }

}
