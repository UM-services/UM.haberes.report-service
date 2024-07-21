package um.haberes.report.kotlin.dto.haberes.core

data class CargoTipoDto(

    var cargoTipoId: Int? = null,
    var aCargo: Byte = 0,
    var nombre: String = "",
    var precedencia: Int = 0

)
