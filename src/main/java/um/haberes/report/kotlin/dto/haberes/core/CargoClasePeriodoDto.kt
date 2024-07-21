package um.haberes.report.kotlin.dto.haberes.core

import java.math.BigDecimal

data class CargoClasePeriodoDto(

    var cargoClasePeriodoId: Long? = null,
    var legajoId: Long? = null,
    var cargoClaseId: Long? = null,
    var dependenciaId: Int? = null,
    var facultadId: Int? = null,
    var geograficaId: Int? = null,
    var periodoDesde: Long? = null,
    var periodoHasta: Long? = null,
    var horas: Int = 0,
    var valorHora: BigDecimal = BigDecimal.ZERO,
    var descripcion: String? = null,
    var persona: PersonaDto? = null,
    var cargoClase: CargoClaseDto? = null,
    var dependencia: DependenciaDto? = null,
    var facultad: FacultadDto? = null,
    var geografica: GeograficaDto? = null

)
