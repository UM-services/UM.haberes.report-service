package um.haberes.report.kotlin.dto.haberes.core

data class CodigoGrupoDto(

    var codigoId: Int? = null,
    var remunerativo: Byte = 0,
    var noRemunerativo: Byte = 0,
    var deduccion: Byte = 0,
    var total: Byte = 0,
    var codigo: CodigoDto? = null


)
