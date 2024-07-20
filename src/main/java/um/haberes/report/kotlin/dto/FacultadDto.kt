package um.haberes.report.kotlin.dto

data class FacultadDto(

    var facultadId: Int? = null,
    var nombre: String = "",
    var reducido: String = "",
    var server: String = "",
    var backendServer: String = "",
    var backendPort: Int = 0,
    var dbName: String = "",
    var dsn: String = ""

)
