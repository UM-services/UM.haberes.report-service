package um.haberes.report.kotlin.dto

import java.math.BigDecimal

data class GeograficaDto(

    var geograficaId: Int? = null,
    var nombre: String = "",
    var reducido: String = "",
    var desarraigo: BigDecimal = BigDecimal.ZERO,
    var geograficaIdReemplazo: Int? = null

)
