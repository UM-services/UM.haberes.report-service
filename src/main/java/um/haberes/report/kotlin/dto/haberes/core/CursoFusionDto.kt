package um.haberes.report.kotlin.dto.haberes.core

data class CursoFusionDto (
    var cursoFusionId: Long? = null,

    var legajoId: Long? = null,
    var anho: Int = 0,
    var mes: Int = 0,
    var facultadId: Int? = null,
    var geograficaId: Int? = null,
    var cargoTipoId: Int? = null,
    var designacionTipoId: Int? = null,
    var anual: Byte = 0,
    var categoriaId: Int? = null,
){
}