package um.haberes.report.kotlin.dto.haberes.core

data class LegajoControlDto(

    var legajoControlId: Long? = null,

    var legajoId: Long? = null,
    var anho: Int = 0,
    var mes: Int = 0,
    var liquidado: Byte = 0,
    var fusionado: Byte = 0,
    var bonoEnviado: Byte = 0,
    var persona: PersonaDto? = null

)
