package um.haberes.report.kotlin.dto

import java.math.BigDecimal

data class DesignacionTipoDto(

    var designacionTipoId: Int = 0,
    var nombre: String = "",
    var horasSemanales: BigDecimal = BigDecimal.ZERO,
    var horasTotales: BigDecimal = BigDecimal.ZERO,
    var simples: Int = 0

)
