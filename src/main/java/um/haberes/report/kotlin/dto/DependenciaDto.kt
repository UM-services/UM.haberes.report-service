package um.haberes.report.kotlin.dto

data class DependenciaDto(

    var dependenciaId: Int? = null,
    var nombre: String = "",
    var acronimo: String = "",
    var facultadId: Int? = null,
    var geograficaId: Int? = null,
    var facultad: FacultadDto? = null,
    var geografica: GeograficaDto? = null

)
