package um.haberes.report.shared.domain.model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Formato de importes de las planillas: mismo agrupamiento y separador decimal que usaba el
 * sistema previo, con dos decimales fijos para que un importe redondo no pierda los centavos.
 *
 * <p>{@link NumberFormat} no es thread-safe, por eso se crea una instancia por llamada.
 */
public final class Importes {

    private Importes() {
    }

    public static String format(BigDecimal value) {
        if (value == null) {
            return "";
        }
        NumberFormat format = NumberFormat.getNumberInstance(new Locale("en", "ES"));
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        return format.format(value);
    }

    public static String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
