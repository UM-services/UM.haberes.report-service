package um.haberes.report.kotlin.dto.haberes.core

import java.math.BigDecimal

data class ClaseDto(

    var claseId: Int? = null,
    var nombre: String = "",
    var valorHora: BigDecimal = BigDecimal.ZERO

)
