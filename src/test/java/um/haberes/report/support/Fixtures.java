package um.haberes.report.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fixtures JSON deterministicas, una por reporte y escenario.
 *
 * <p>El archivo es un "sobre" cuyas claves son los agregados que consume el reporte
 * ({@code personas}, {@code facultades}, {@code cursos}, ...). Los tests convierten cada
 * clave al DTO de wire real, de modo que el mismo fixture sirve antes y despues del refactor
 * (los DTO cambian de paquete, no de forma).
 */
public final class Fixtures {

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private Fixtures() {
    }

    public static JsonNode node(String report, String scenario) {
        String resource = "/fixtures/" + report + "/" + scenario + ".json";
        try (InputStream stream = Fixtures.class.getResourceAsStream(resource)) {
            if (stream == null) {
                throw new IllegalArgumentException("No existe el fixture " + resource);
            }
            return MAPPER.readTree(stream);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el fixture " + resource, e);
        }
    }

    public static <T> List<T> list(JsonNode root, String field, Class<T> type) {
        JsonNode node = root.get(field);
        if (node == null || node.isNull()) {
            return List.of();
        }
        if (!node.isArray()) {
            throw new IllegalArgumentException("El campo '" + field + "' del fixture debe ser un arreglo");
        }
        List<T> values = new ArrayList<>();
        for (JsonNode item : node) {
            values.add(MAPPER.convertValue(item, type));
        }
        return values;
    }

    public static <T> T one(JsonNode root, String field, Class<T> type) {
        JsonNode node = root.get(field);
        if (node == null || node.isNull()) {
            return null;
        }
        return MAPPER.convertValue(node, type);
    }

    public static Integer integer(JsonNode root, String field) {
        JsonNode node = root.get(field);
        return node == null || node.isNull() ? null : node.asInt();
    }

    public static String text(JsonNode root, String field) {
        JsonNode node = root.get(field);
        return node == null || node.isNull() ? null : node.asText();
    }

    public static Long longValue(JsonNode root, String field) {
        JsonNode node = root.get(field);
        return node == null || node.isNull() ? null : node.asLong();
    }
}
