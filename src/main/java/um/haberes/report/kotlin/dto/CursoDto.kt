package um.haberes.report.kotlin.dto

data class CursoDto(

    var cursoId: Long? = null,

    var nombre: String = "",
    var facultadId: Int? = null,
    var geograficaId: Int? = null,
    var anual: Byte = 0,
    var semestre1: Byte = 0,
    var semestre2: Byte = 0,
    var nivelId: Int? = null,
    var adicionalCargaHoraria: Byte = 0,
    var facultad: FacultadDto? = null,
    var geografica: GeograficaDto? = null,
    var nivel: NivelDto? = null

)
