package um.haberes.report.kotlin.dto.haberes.core

import um.haberes.report.util.Jsonifyable

data class FacultadDto(

    var facultadId: Int? = null,
    var nombre: String = "",
    var reducido: String = "",
    var server: String = "",
    var backendServer: String = "",
    var backendPort: Int = 0,
    var dbName: String = "",
    var dsn: String = ""

) : Jsonifyable
