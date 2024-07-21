package um.haberes.report.kotlin.dto.haberes.core

import java.math.BigDecimal

data class ItemDto(

    var itemId: Long? = null,
    var legajoId: Long? = null,
    var anho: Int = 0,
    var mes: Int = 0,
    var codigoId: Int? = null,
    var codigoNombre: String = "",
    var importe: BigDecimal = BigDecimal.ZERO,
    var persona: PersonaDto? = null,
    var codigo: CodigoDto? = null,

    )
