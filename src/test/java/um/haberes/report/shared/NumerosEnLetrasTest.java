package um.haberes.report.shared;

import org.junit.jupiter.api.Test;
import um.haberes.report.shared.domain.model.NumerosEnLetras;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Tabla de congelamiento de C6 (cero, negativos, miles de millones y decimales). */
class NumerosEnLetrasTest {

    @Test
    void convierteLosCasosDeLaTablaAprobada() {
        assertThat(NumerosEnLetras.convertir(new BigDecimal("1.00")))
                .isEqualTo("uno con 00/100");
        assertThat(NumerosEnLetras.convertir(new BigDecimal("15.00")))
                .isEqualTo("quince con 00/100");
        assertThat(NumerosEnLetras.convertir(new BigDecimal("21.00")))
                .isEqualTo("veintiuno con 00/100");
        assertThat(NumerosEnLetras.convertir(new BigDecimal("100.00")))
                .isEqualTo("cien con 00/100");
        assertThat(NumerosEnLetras.convertir(new BigDecimal("101.00")))
                .isEqualTo("ciento uno con 00/100");
        assertThat(NumerosEnLetras.convertir(new BigDecimal("999.00")))
                .isEqualTo("novecientos noventa y nueve con 00/100");
        assertThat(NumerosEnLetras.convertir(new BigDecimal("1000.00")))
                .isEqualTo("un mil con 00/100");
        assertThat(NumerosEnLetras.convertir(new BigDecimal("1001.00")))
                .isEqualTo("un mil uno con 00/100");
        assertThat(NumerosEnLetras.convertir(new BigDecimal("21000.00")))
                .isEqualTo("veintiun mil con 00/100");
        assertThat(NumerosEnLetras.convertir(new BigDecimal("1000000.00")))
                .isEqualTo("un millon con 00/100");
        assertThat(NumerosEnLetras.convertir(new BigDecimal("2000000.00")))
                .isEqualTo("dos millones con 00/100");
        assertThat(NumerosEnLetras.convertir(new BigDecimal("1650000.00")))
                .isEqualTo("un millon seiscientos cincuenta mil con 00/100");
    }

    @Test
    void agregaElCeroYLosDecimales() {
        assertThat(NumerosEnLetras.convertir(BigDecimal.ZERO)).isEqualTo("cero con 00/100");
        assertThat(NumerosEnLetras.convertir(new BigDecimal("10.5")))
                .isEqualTo("diez con 50/100");
    }

    @Test
    void soportaNegativosYMilesDeMillones() {
        assertThat(NumerosEnLetras.convertir(new BigDecimal("-5.00")))
                .isEqualTo("menos cinco con 00/100");
        assertThat(NumerosEnLetras.convertir(new BigDecimal("1000000000.00")))
                .isEqualTo("mil millones con 00/100");
        assertThat(NumerosEnLetras.convertir(new BigDecimal("2000000000.00")))
                .isEqualTo("dos mil millones con 00/100");
    }

    @Test
    void fueraDeRangoFalla() {
        assertThatThrownBy(() -> NumerosEnLetras.convertir(new BigDecimal("1000000000000.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fuera del rango");
    }
}
