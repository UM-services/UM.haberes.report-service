package um.haberes.report.kotlin.dto.haberes.core

import java.math.BigDecimal

data class LiquidacionAdicionalDto(

    var liquidacionAdicionalId: Long? = null,
    var legajoId: Long? = null,
    var anho: Int? = null,
    var mes: Int? = null,
    var dependenciaId: Int? = null,
    var adicional: BigDecimal = BigDecimal.ZERO,
    var persona: PersonaDto? = null,
    var dependencia: DependenciaDto? = null

)
