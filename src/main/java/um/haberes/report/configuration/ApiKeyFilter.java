package um.haberes.report.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Valida la API key que inyecta el gateway en el namespace exclusivo del frontend
 * ({@code /api/haberes/report/ui/**}), con el mismo esquema que {@code compras-service}.
 *
 * <p><b>Acotado por path a propósito:</b> las rutas legacy que consume {@code bonos.vb6}
 * ({@code /api/haberes/report/<slice>/...}) y el frontend ya migrado no llevan API key y no
 * deben romperse. El navegador nunca conoce la clave: la fija el gateway con
 * {@code SetRequestHeader}, así que tampoco puede inyectarla ni duplicarla.
 *
 * <p>Actuator y springdoc quedan fuera del filtro, igual que en compras.
 */
@Component
@Order(1)
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${app.api-key}")
    private String apiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestApiKey = request.getHeader("X-API-Key");

        if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    "{\"error\": \"Unauthorized\", \"message\": \"Missing or invalid X-API-Key header\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/haberes/report/ui/")
                || path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }
}
