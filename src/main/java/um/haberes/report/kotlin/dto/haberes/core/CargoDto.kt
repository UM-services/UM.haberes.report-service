package um.haberes.report.kotlin.dto.haberes.core

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.OffsetDateTime

data class CargoDto (
    var cargoId: Long? = null,

    var legajoId: Long? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaAlta: OffsetDateTime? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaBaja: OffsetDateTime? = null,

    var dependenciaId: Int? = null,
    var categoriaId: Int? = null,
    var jornada: Int = 0,
    var presentismo: Int = 0,
    var horasJornada: BigDecimal = BigDecimal.ZERO,

    ){
}