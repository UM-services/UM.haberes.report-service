package um.haberes.report.kotlin.dto.haberes.core

import java.math.BigDecimal

data class LetraDto(

    var letraId: Long? = null,
    var legajoId: Long? = null,
    var anho: Int = 0,
    var mes: Int = 0,
    var neto: BigDecimal = BigDecimal.ZERO,
    var cadena: String = ""

)
