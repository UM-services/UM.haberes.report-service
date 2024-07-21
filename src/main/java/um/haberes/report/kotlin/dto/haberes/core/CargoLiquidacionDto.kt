package um.haberes.report.kotlin.dto.haberes.core

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.OffsetDateTime

data class CargoLiquidacionDto(

    var cargoLiquidacionId: Long? = null,
    var legajoId: Long? = null,
    var anho: Int? = null,
    var mes: Int? = null,
    var dependenciaId: Int? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaDesde: OffsetDateTime? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaHasta: OffsetDateTime? = null,
    var categoriaId: Int? = null,
    var categoriaNombre: String = "",
    var categoriaBasico: BigDecimal = BigDecimal.ZERO,
    var horasJornada: BigDecimal = BigDecimal.ZERO,
    var jornada: Int = 0,
    var presentismo: Int = 0,
    var situacion: String? = null,
    var persona: PersonaDto? = null,
    var dependencia: DependenciaDto? = null,
    var categoria: CategoriaDto? = null

)
