package um.haberes.report.shared.infrastructure.web;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.application.exception.ReportException;
import um.haberes.report.shared.domain.model.ReportFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/** Construye la descarga HTTP con los mismos headers que el legacy (Tool.generateFile*). */
@Component
public class ReportResponseBuilder {

    public ResponseEntity<Resource> attachment(ReportFile file) {
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file.path().toFile()));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.displayName());
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            return ResponseEntity.ok().headers(headers).contentLength(file.size())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
        } catch (FileNotFoundException e) {
            throw new ReportException("No se pudo leer el reporte " + file.displayName(), e);
        }
    }

    public ResponseEntity<Resource> inlinePdf(ReportFile file) {
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file.path().toFile()));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.displayName() + "\"");
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            return ResponseEntity.ok().headers(headers).contentLength(file.size())
                    .contentType(MediaType.APPLICATION_PDF).body(resource);
        } catch (FileNotFoundException e) {
            throw new ReportException("No se pudo leer el reporte " + file.displayName(), e);
        }
    }
}
