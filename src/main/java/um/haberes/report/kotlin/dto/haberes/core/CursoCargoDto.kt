package um.haberes.report.kotlin.dto.haberes.core

import java.math.BigDecimal

data class CursoCargoDto(

    var cursoCargoId: Long? = null,
    var cursoId: Long? = null,
    var anho: Int = 0,
    var mes: Int = 0,
    var cargoTipoId: Int? = null,
    var legajoId: Long? = null,
    var horasSemanales: BigDecimal = BigDecimal.ZERO,
    var horasTotales: BigDecimal = BigDecimal.ZERO,
    var designacionTipoId: Int? = null,
    var categoriaId: Int? = null,
    var desarraigo: Byte = 0,
    var cursoCargoNovedadId: Long? = null,
    var curso: CursoDto? = null,
    var cargoTipo: CargoTipoDto? = null,
    var persona: PersonaDto? = null,
    var designacionTipo: DesignacionTipoDto? = null,
    var categoria: CategoriaDto? = null

)
