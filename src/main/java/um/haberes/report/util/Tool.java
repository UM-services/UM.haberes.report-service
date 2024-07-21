package um.haberes.report.util;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Tool {

    public static OffsetDateTime hourAbsoluteArgentina() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(new Date());
        return calendar.getTime().toInstant().atOffset(ZoneOffset.UTC);
    }

    public static OffsetDateTime dateAbsoluteArgentina() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().toInstant().atOffset(ZoneOffset.UTC);
    }

    private static String left(String string, Integer count) {
        if (string.length() > count) {
            return string.substring(0, count);
        }
        return string;
    }

    private static String right(String string, Integer count) {
        if (string.length() > count) {
            return string.substring(string.length() - count);
        }
        return string;
    }

    public static String number_2_text(BigDecimal value) {
        value = value.setScale(2, RoundingMode.HALF_UP);
        String[] values = String.valueOf(value).split("\\.");
        String ultimo = number_2_text_entero(new BigDecimal(values[0]));
        String centavos = " con " + values[1] + "/100";
        return (ultimo + centavos).trim();
    }

    public static String number_2_text_entero(BigDecimal value) {
        String ultimo = "";
        long value_long = 0;
        long digitos = 0;
        switch (value.toString().length()) {
            case 1:
            case 2:
            case 3:
                ultimo = tres_ultimas(value);
                break;
            case 4:
            case 5:
            case 6:
                ultimo = tres_ultimas(value.divide(new BigDecimal(1000), 0, RoundingMode.DOWN));
                if (right(ultimo, 3).equals("uno")) {
                    ultimo = left(ultimo, ultimo.length() - 1);
                }
                ultimo = ultimo + " mil";
                value_long = value.longValue();
                digitos = value_long - (value_long / 1000) * 1000;
                if (digitos > 0) {
                    ultimo = ultimo + " " + tres_ultimas(new BigDecimal(digitos)).trim();
                }
                break;
            case 7:
            case 8:
            case 9:
                ultimo = tres_ultimas(value.divide(new BigDecimal(1000000), 0, RoundingMode.DOWN));
                if (right(ultimo, 3).equals("uno")) {
                    ultimo = left(ultimo, ultimo.length() - 1);
                }
                ultimo = ultimo + " millon";
                if (!ultimo.equals(" un millon")) {
                    ultimo = ultimo + "es";
                }
                value_long = value.longValue();
                digitos = value_long - (value_long / 1000000) * 1000000;
                if (digitos / 1000 > 0) {
                    ultimo = ultimo + " " + tres_ultimas(new BigDecimal(digitos / 1000)).trim();
                    if (right(ultimo, 3).equals("uno")) {
                        ultimo = left(ultimo, ultimo.length() - 1);
                    }
                    ultimo = ultimo + " mil";
                }
                if (digitos - (digitos / 1000) * 1000 > 0) {
                    ultimo = ultimo + " " + tres_ultimas(new BigDecimal(digitos - (digitos / 1000) * 1000)).trim();
                }
        }
        return ultimo.trim();
    }

    private static String tres_ultimas(BigDecimal value) {
        String[] centenas = {"cien", "doscientos", "trescientos", "cuatrocientos", "quinientos", "seiscientos",
                "setecientos", "ochocientos", "novecientos"};
        String centena = "";
        String ultimo = "";

        int numero = value.intValue();

        if (numero > 99) {
            centena = centenas[(numero / 100) - 1];
        }
        if (numero > 100 && numero < 200) {
            centena = centena + "to";
        }
        if (numero != (numero / 100) * 100) {
            centena = centena + " ";
        }
        ultimo = dos_ultimas(numero - (numero / 100) * 100);

        return centena + ultimo;
    }

    private static String dos_ultimas(Integer value) {
        int largo = value.toString().trim().length();

        String unidades[] = {"uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve"};
        String decena[] = {"diez", "once", "doce", "trece", "catorce", "quince", "dieciseis", "diecisiete",
                "dieciocho", "diecinueve"};
        String decenas[] = {"veint", "treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa"};
        String ultimo = "";

        switch (largo) {
            case 1:
                if (value > 0) {
                    ultimo = unidades[value - 1];
                }
                break;
            case 2:
                if (value > 9 && value < 20) {
                    ultimo = decena[value - 10];
                }
                if (value > 19 && value < 100) {
                    ultimo = decenas[(value / 10) - 2];
                    if (value == 20) {
                        ultimo = ultimo + "e";
                    }
                    if (value > 20 && value < 30) {
                        ultimo = ultimo + "i";
                    }
                    if (value > 30 && value - (value / 10) * 10 > 0) {
                        ultimo = ultimo + " y ";
                    }
                    if (value - (value / 10) * 10 > 0) {
                        ultimo = ultimo + unidades[value - (value / 10) * 10 - 1];
                    }
                }
                break;
        }
        return ultimo;
    }

    public static ResponseEntity<Resource> generateFile(String filename, String headerFilename) throws FileNotFoundException {
        File file = new File(filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + headerFilename);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity.ok().headers(headers).contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }

}
