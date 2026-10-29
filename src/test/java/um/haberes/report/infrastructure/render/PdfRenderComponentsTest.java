package um.haberes.report.infrastructure.render;

import org.junit.jupiter.api.Test;
import org.openpdf.text.Document;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfReader;
import org.openpdf.text.pdf.PdfWriter;
import um.haberes.report.infrastructure.render.pdf.PdfHeader;
import um.haberes.report.infrastructure.render.pdf.PdfHeaderFooter;
import um.haberes.report.infrastructure.render.pdf.PdfMerge;
import um.haberes.report.infrastructure.render.pdf.PdfTables;
import um.haberes.report.support.GoldenFiles;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Smoke tests de los componentes de render compartidos (F2). */
class PdfRenderComponentsTest {

    @Test
    void encabezadoYPieReproducenElBloqueInstitucional() throws Exception {
        byte[] pdf = render(PdfHeader.of("Cargos con Clase", "9/2026", List.of("Facultad de Ingeniería")), 3);
        String text = GoldenFiles.extractPdfText(pdf);

        assertThat(text).contains("UNIVERSIDAD DE MENDOZA");
        assertThat(text).contains("Cargos con Clase");
        assertThat(text).contains("Periodo: 9/2026");
        assertThat(text).contains("Facultad de Ingeniería");
        assertThat(text).contains("Página 1");
    }

    @Test
    void encabezadoSinLogoYVariasPaginasNoLanzaNpe() throws Exception {
        PdfHeader header = new PdfHeader("Novedades de Cursos", "9/2026 - Sólo pendientes",
                List.of(), null, "Autorizados", null, "UNIVERSIDAD DE MENDOZA", false);
        byte[] pdf = render(header, 80);
        String text = GoldenFiles.extractPdfText(pdf);

        assertThat(text).contains("Novedades de Cursos");
        assertThat(text).contains("Autorizados");
        assertThat(text).contains("Página 1");
        assertThat(text).contains("Página 2");
    }

    @Test
    void mergeUneLosPdfYConservaLasPaginas() throws Exception {
        Path dir = Path.of("target/test-output/pdf-merge");
        Files.createDirectories(dir);
        Path uno = dir.resolve("uno.pdf");
        Path dos = dir.resolve("dos.pdf");
        Files.write(uno, simplePdf("Documento uno"));
        Files.write(dos, simplePdf("Documento dos"));
        Path merged = dir.resolve("merged.pdf");

        new PdfMerge().merge(merged, List.of(uno, dos));

        assertThat(new PdfReader(merged.toString()).getNumberOfPages()).isEqualTo(2);
        String text = GoldenFiles.extractPdfText(Files.readAllBytes(merged));
        assertThat(text).contains("Documento uno").contains("Documento dos");
    }

    @Test
    void celdasComunesNoTienenBordeSalvoLaDeEncabezado() {
        assertThat(PdfTables.noBorder("x", PdfTables.font(8), PdfTables.center()).getBorder())
                .isZero();
        assertThat(PdfTables.bottomBorder("x", PdfTables.bold(8), PdfTables.center()).getBorder())
                .isEqualTo(org.openpdf.text.Rectangle.BOTTOM);
        assertThat(PdfTables.paragraph(new Paragraph("x"), PdfTables.center()).getBorder()).isZero();
    }

    private byte[] render(PdfHeader header, int paragraphs) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, output);
        PdfHeaderFooter event = new PdfHeaderFooter(header);
        writer.setPageEvent(event);
        document.open();
        event.setHeader(document);
        for (int i = 0; i < paragraphs; i++) {
            document.add(new Paragraph("Linea de contenido " + i));
        }
        document.close();
        return output.toByteArray();
    }

    private byte[] simplePdf(String text) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();
        document.add(new Paragraph(text));
        document.close();
        return output.toByteArray();
    }
}
