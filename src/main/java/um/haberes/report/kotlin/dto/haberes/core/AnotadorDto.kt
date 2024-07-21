package um.haberes.report.kotlin.dto.haberes.core

import java.time.LocalDateTime

data class AnotadorDto(

    var anotadorId: Long? = null,
    var legajoId: Long? = null,
    var anho: Int = 0,
    var mes: Int = 0,
    var facultadId: Int? = null,
    var anotacion: String? = null,
    var visado: Byte = 0,
    var ipVisado: String? = null,
    var user: String? = null,
    var respuesta: String? = null,
    var autorizado: Byte = 0,
    var rechazado: Byte = 0,
    var rectorado: Byte = 0,
    var transferido: Byte = 0,
    var persona: PersonaDto? = null,
    var facultad: FacultadDto? = null,
    var created: LocalDateTime? = null

)
