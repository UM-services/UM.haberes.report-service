package um.haberes.report.kotlin.dto.haberes.core

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.OffsetDateTime

data class ControlDto(

    var controlId: Long? = null,
    var anho: Int? = null,
    var mes: Int? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaDesde: OffsetDateTime? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaHasta: OffsetDateTime? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaPago: OffsetDateTime? = null,
    var aporteJubilatorio: String? = null,
    var depositoBanco: String? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaDeposito: OffsetDateTime? = null,
    var doctorado: BigDecimal = BigDecimal.ZERO,
    var maestria: BigDecimal = BigDecimal.ZERO,
    var especializacion: BigDecimal = BigDecimal.ZERO,
    var familiaNumerosa: BigDecimal = BigDecimal.ZERO,
    var escuelaPrimaria: BigDecimal = BigDecimal.ZERO,
    var escuelaSecundaria: BigDecimal = BigDecimal.ZERO,
    var escuelaPrimariaNumerosa: BigDecimal = BigDecimal.ZERO,
    var escuelaSecundariaNumerosa: BigDecimal = BigDecimal.ZERO,
    var prenatal: BigDecimal = BigDecimal.ZERO,
    var libre: BigDecimal = BigDecimal.ZERO,
    var ayudaEscolar: BigDecimal = BigDecimal.ZERO,
    var matrimonio: BigDecimal = BigDecimal.ZERO,
    var nacimiento: BigDecimal = BigDecimal.ZERO,
    var funcionDireccion: BigDecimal = BigDecimal.ZERO,
    var mayorResponsabilidadPatrimonial: BigDecimal = BigDecimal.ZERO,
    var polimedb: BigDecimal = BigDecimal.ZERO,
    var polimedo: BigDecimal = BigDecimal.ZERO,
    var montoeci: BigDecimal = BigDecimal.ZERO,
    var valampo: BigDecimal = BigDecimal.ZERO,
    var jubilaem: BigDecimal = BigDecimal.ZERO,
    var inssjpem: BigDecimal = BigDecimal.ZERO,
    var osociaem: BigDecimal = BigDecimal.ZERO,
    var jubilpat: BigDecimal = BigDecimal.ZERO,
    var inssjpat: BigDecimal = BigDecimal.ZERO,
    var osocipat: BigDecimal = BigDecimal.ZERO,
    var ansalpat: BigDecimal = BigDecimal.ZERO,
    var salfapat: BigDecimal = BigDecimal.ZERO,
    var minimoAporte: BigDecimal = BigDecimal.ZERO,
    var maximoAporte: BigDecimal = BigDecimal.ZERO,
    var mincontr: BigDecimal = BigDecimal.ZERO,
    var maximo1sijp: BigDecimal = BigDecimal.ZERO,
    var maximo2sijp: BigDecimal = BigDecimal.ZERO,
    var maximo3sijp: BigDecimal = BigDecimal.ZERO,
    var maximo4sijp: BigDecimal = BigDecimal.ZERO,
    var maximo5sijp: BigDecimal = BigDecimal.ZERO,
    var estadoDocenteTitular: BigDecimal = BigDecimal.ZERO,
    var estadoDocenteAdjunto: BigDecimal = BigDecimal.ZERO,
    var estadoDocenteAuxiliar: BigDecimal = BigDecimal.ZERO,
    var horaReferenciaEtec: BigDecimal = BigDecimal.ZERO,
    var modoLiquidacionId: Int? = null,
    var modoLiquidacion: ModoLiquidacionDto? = null

)
