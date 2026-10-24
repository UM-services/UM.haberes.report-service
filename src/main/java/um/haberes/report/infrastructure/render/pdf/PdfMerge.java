package um.haberes.report.infrastructure.render.pdf;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.pdf.PdfContentByte;
import org.openpdf.text.pdf.PdfReader;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Une varios PDF en uno solo, cerrando siempre lectores y streams. */
@Component
public class PdfMerge {

    public void merge(Path target, List<Path> sources) {
        Document document = new Document();
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter writer = PdfWriter.getInstance(document, output);
            document.open();
            PdfContentByte content = writer.getDirectContent();
            for (Path source : sources) {
                append(document, content, writer, source);
            }
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudieron unir los PDF de " + target, e);
        }
    }

    private void append(Document document, PdfContentByte content, PdfWriter writer, Path source) throws IOException {
        try (InputStream input = Files.newInputStream(source)) {
            PdfReader reader = new PdfReader(input);
            try {
                for (int page = 1; page <= reader.getNumberOfPages(); page++) {
                    document.newPage();
                    content.addTemplate(writer.getImportedPage(reader, page), 0, 0);
                }
            } finally {
                reader.close();
            }
        }
    }
}
