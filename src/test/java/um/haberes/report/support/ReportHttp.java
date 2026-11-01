package um.haberes.report.support;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Congela el contrato HTTP de descarga: status, content-type, content-disposition,
 * headers de cache y largo declarado. Es el mismo contrato que consumen
 * {@code bonos.vb6} (WebBrowser/Inet) y {@code haberes-frontend} ({@code responseType: blob}).
 */
public final class ReportHttp {

    private ReportHttp() {
    }

    public static byte[] assertAttachment(MvcResult result, String filename) {
        MockHttpServletResponse response = assertOkBinary(result);
        assertThat(response.getHeader("Content-Disposition")).isEqualTo("attachment; filename=" + filename);
        assertThat(response.getContentType()).isEqualTo("application/octet-stream");
        assertCacheHeaders(response);
        byte[] body = response.getContentAsByteArray();
        assertThat(body).isNotEmpty();
        assertThat(response.getContentLength()).isEqualTo(body.length);
        return body;
    }

    public static byte[] assertInlinePdf(MvcResult result, String filename) {
        MockHttpServletResponse response = assertOkBinary(result);
        assertThat(response.getHeader("Content-Disposition"))
                .isEqualTo("inline; filename=\"" + filename + "\"");
        assertThat(response.getContentType()).isEqualTo("application/pdf");
        assertCacheHeaders(response);
        byte[] body = response.getContentAsByteArray();
        assertThat(body).isNotEmpty();
        assertThat(new String(body, 0, Math.min(5, body.length))).startsWith("%PDF-");
        assertThat(response.getContentLength()).isEqualTo(body.length);
        return body;
    }

    private static MockHttpServletResponse assertOkBinary(MvcResult result) {
        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
        return response;
    }

    private static void assertCacheHeaders(MockHttpServletResponse response) {
        assertThat(response.getHeader("Cache-Control")).isEqualTo("no-cache, no-store, must-revalidate");
        assertThat(response.getHeader("Pragma")).isEqualTo("no-cache");
        assertThat(response.getHeader("Expires")).isEqualTo("0");
    }
}
