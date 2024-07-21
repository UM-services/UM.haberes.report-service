package um.haberes.report.kotlin.dto.haberes.core

import java.math.BigDecimal

data class CursoCargoNovedadDto(

    var cursoCargoNovedadId: Long? = null,
    var cursoId: Long? = null,
    var anho: Int = 0,
    var mes: Int = 0,
    var cargoTipoId: Int? = null,
    var legajoId: Long? = null,
    var horasSemanales: BigDecimal = BigDecimal.ZERO,
    var horasTotales: BigDecimal = BigDecimal.ZERO,
    var desarraigo: Byte = 0,
    var alta: Byte = 0,
    var baja: Byte = 0,
    var cambio: Byte = 0,
    var solicitud: String? = null,
    var autorizado: Byte = 0,
    var rechazado: Byte = 0,
    var respuesta: String? = null,
    var transferido: Byte = 0,
    var curso: CursoDto? = null,
    var cargoTipo: CargoTipoDto? = null,
    var persona: PersonaDto? = null

)
