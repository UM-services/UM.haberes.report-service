package um.haberes.report.kotlin.dto.haberes.core

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.OffsetDateTime

data class LiquidacionDto(

    var liquidacionId: Long? = null,
    var legajoId: Long? = null,
    var anho: Int = 0,
    var mes: Int = 0,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaLiquidacion: OffsetDateTime? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaAcreditacion: OffsetDateTime? = null,
    var dependenciaId: Int? = null,
    var salida: String? = null,
    var totalRemunerativo: BigDecimal = BigDecimal.ZERO,
    var totalNoRemunerativo: BigDecimal = BigDecimal.ZERO,
    var totalDeduccion: BigDecimal = BigDecimal.ZERO,
    var totalNeto: BigDecimal = BigDecimal.ZERO,
    var bloqueado: Byte = 0,
    var estado: Int = 0,
    var liquida: String = "",
    var persona: PersonaDto? = null,
    var dependencia: DependenciaDto? = null

)
