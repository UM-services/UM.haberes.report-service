package um.haberes.report.kotlin.dto.haberes.core

data class AntiguedadDto(

    var antiguedadId: Long? = null,
    var legajoId: Long? = null,
    var anho: Int = 0,
    var mes: Int = 0,
    var mesesDocentes: Int = 0,
    var mesesAdministrativos: Int = 0,
    var persona: PersonaDto? = null

)
