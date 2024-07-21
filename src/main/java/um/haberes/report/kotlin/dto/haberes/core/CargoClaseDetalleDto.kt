package um.haberes.report.kotlin.dto.haberes.core

import java.math.BigDecimal

data class CargoClaseDetalleDto(

    var cargoClaseDetalleId: Long? = null,
    var legajoId: Long? = null,
    var anho: Int? = 0,
    var mes: Int? = 0,
    var cargoClaseId: Long? = null,
    var dependenciaId: Int? = null,
    var facultadId: Int? = null,
    var geograficaId: Int? = null,
    var horas: Int = 0,
    var valorHora: BigDecimal = BigDecimal.ZERO,
    var cargoClasePeriodoId: Long? = null,
    var liquidado: Byte = 0,
    var persona: PersonaDto? = null,
    var cargoClase: CargoClaseDto? = null,
    var dependencia: DependenciaDto? = null,
    var facultad: FacultadDto? = null,
    var geografica: GeograficaDto? = null,
    var cargoClasePeriodo: CargoClasePeriodoDto? = null

)
