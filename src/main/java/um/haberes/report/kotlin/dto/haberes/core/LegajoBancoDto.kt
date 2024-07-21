package um.haberes.report.kotlin.dto.haberes.core

import java.math.BigDecimal

data class LegajoBancoDto(

    var legajoBancoId: Long? = null,
    var legajoId: Long? = null,
    var anho: Int = 0,
    var mes: Int = 0,
    var cbu: String = "",
    var fijo: BigDecimal = BigDecimal.ZERO,
    var porcentaje: BigDecimal = BigDecimal.ZERO,
    var resto: Byte = 0,
    var acreditado: BigDecimal = BigDecimal.ZERO,
    var persona: PersonaDto? = null,
    var liquidacion: LiquidacionDto? = null

)
