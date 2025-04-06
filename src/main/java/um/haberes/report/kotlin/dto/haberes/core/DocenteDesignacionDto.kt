package um.haberes.report.kotlin.dto.haberes.core

import java.math.BigDecimal

data class DocenteDesignacionDto
    (
    var legajoId: Long? = null,
    var anho: Int? = null,
    var mes: Int? = null,
    var facultadId: Int? = null,
    var geograficaId: Int? = null,
    var espacio: String? = null,
    var horasSemanales: BigDecimal? = null,
    var cargo: String? = null,
    var designacion: String? = null,
    var horasDesignacion: BigDecimal? = null,
    var anual: Byte? = null,
    var semestre1: Byte? = null,
    var semestre2: Byte? = null,
            ){
}