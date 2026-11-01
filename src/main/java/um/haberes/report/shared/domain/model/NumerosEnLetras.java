package um.haberes.report.shared.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Conversion de importes a letras. Es la version corregida del {@code Tool.number_2_text}
 * legacy: agrega el cero, soporta negativos y miles de millones (C6).
 */
public final class NumerosEnLetras {

    private static final BigDecimal LIMITE = new BigDecimal("1000000000000");
    private static final String[] UNIDADES = {"uno", "dos", "tres", "cuatro", "cinco", "seis",
            "siete", "ocho", "nueve"};
    private static final String[] DECENAS = {"diez", "once", "doce", "trece", "catorce", "quince",
            "dieciseis", "diecisiete", "dieciocho", "diecinueve"};
    private static final String[] DECENAS_MULTIPLOS = {"veint", "treinta", "cuarenta", "cincuenta",
            "sesenta", "setenta", "ochenta", "noventa"};
    private static final String[] CENTENAS = {"cien", "doscientos", "trescientos", "cuatrocientos",
            "quinientos", "seiscientos", "setecientos", "ochocientos", "novecientos"};

    private NumerosEnLetras() {
    }

    public static String convertir(BigDecimal valor) {
        BigDecimal seguro = valor == null ? BigDecimal.ZERO : valor.setScale(2, RoundingMode.HALF_UP);
        boolean negativo = seguro.signum() < 0;
        BigDecimal absoluto = seguro.abs();
        if (absoluto.compareTo(LIMITE) >= 0) {
            throw new IllegalArgumentException("Importe fuera del rango soportado: " + valor);
        }
        String[] partes = absoluto.toPlainString().split("\\.");
        String entero = entero(new BigDecimal(partes[0]));
        String centavos = partes.length > 1 ? partes[1] : "00";
        return (negativo ? "menos " : "") + entero + " con " + centavos + "/100";
    }

    private static String entero(BigDecimal valor) {
        long numero = valor.longValue();
        if (numero == 0) {
            return "cero";
        }
        StringBuilder salida = new StringBuilder();
        long milesMillones = numero / 1_000_000_000L;
        long millones = (numero / 1_000_000L) % 1000;
        long miles = (numero / 1000L) % 1000;
        long unidades = numero % 1000;

        if (milesMillones > 0) {
            if (milesMillones == 1) {
                salida.append("mil millones");
            } else {
                salida.append(tresUltimas(milesMillones)).append(" mil millones");
            }
        }
        if (millones > 0) {
            if (!salida.isEmpty()) {
                salida.append(' ');
            }
            if (millones == 1) {
                salida.append("un millon");
            } else {
                String texto = tresUltimas(millones);
                if (texto.endsWith("uno")) {
                    texto = texto.substring(0, texto.length() - 1);
                }
                salida.append(texto).append(" millones");
            }
        }
        if (miles > 0) {
            if (!salida.isEmpty()) {
                salida.append(' ');
            }
            if (miles == 1) {
                salida.append("un mil");
            } else {
                String texto = tresUltimas(miles);
                if (texto.endsWith("uno")) {
                    texto = texto.substring(0, texto.length() - 1);
                }
                salida.append(texto).append(" mil");
            }
        }
        if (unidades > 0) {
            if (!salida.isEmpty()) {
                salida.append(' ');
            }
            salida.append(tresUltimas(unidades));
        }
        return salida.toString().trim();
    }

    private static String tresUltimas(long valor) {
        int numero = (int) valor;
        StringBuilder salida = new StringBuilder();
        if (numero > 99) {
            salida.append(CENTENAS[(numero / 100) - 1]);
            if (numero > 100 && numero < 200) {
                salida.append("to");
            }
            if (numero != (numero / 100) * 100) {
                salida.append(' ');
            }
        }
        salida.append(dosUltimas(numero - (numero / 100) * 100));
        return salida.toString().trim();
    }

    private static String dosUltimas(int valor) {
        if (valor == 0) {
            return "";
        }
        if (valor < 10) {
            return UNIDADES[valor - 1];
        }
        if (valor < 20) {
            return DECENAS[valor - 10];
        }
        StringBuilder salida = new StringBuilder(DECENAS_MULTIPLOS[(valor / 10) - 2]);
        if (valor == 20) {
            return salida.append('e').toString();
        }
        if (valor < 30) {
            salida.append('i');
        }
        int unidad = valor - (valor / 10) * 10;
        if (unidad > 0) {
            if (valor >= 30) {
                salida.append(" y ");
            }
            salida.append(UNIDADES[unidad - 1]);
        }
        return salida.toString();
    }
}
