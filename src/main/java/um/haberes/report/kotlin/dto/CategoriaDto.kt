package um.haberes.report.kotlin.dto

import java.math.BigDecimal

data class CategoriaDto(

    var categoriaId: Int? = null,
    var nombre: String = "",
    var basico: BigDecimal = BigDecimal.ZERO,
    var docente: Byte = 0,
    var noDocente: Byte = 0,
    var liquidaPorHora: Byte = 0

)
