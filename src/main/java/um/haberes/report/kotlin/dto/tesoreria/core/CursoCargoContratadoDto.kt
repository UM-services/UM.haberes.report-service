package um.haberes.report.kotlin.dto.tesoreria.core

import um.haberes.report.kotlin.dto.haberes.core.ContratadoPersonaDto
import java.math.BigDecimal

data class CursoCargoContratadoDto(

    var cursoCargoContratadoId: Long? = null,
    var cursoId: Long? = null,
    var anho: Int = 0,
    var mes: Int = 0,
    var contratadoId: Long? = null,
    var contratoId: Long? = null,
    var cargoTipoId: Int? = null,
    var horasSemanales: BigDecimal = BigDecimal.ZERO,
    var horasTotales: BigDecimal = BigDecimal.ZERO,
    var designacionTipoId: Int? = null,
    var categoriaId: Int? = null,
    var cursoCargoNovedadId: Long? = null,
    var acreditado: Byte = 0,
    var contratadoPersona: ContratadoPersonaDto? = null

)
