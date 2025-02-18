package um.haberes.report.kotlin.dto.haberes.core

import java.math.BigDecimal

data class TotalItemDto (
    var uniqueId: String? = null,

    var anho: Int? = null,
    var mes: Int? = null,
    var codigoId: Int? = null,
    var total: BigDecimal = BigDecimal.ZERO
){
}