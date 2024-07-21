package um.haberes.report.kotlin.dto.haberes.core

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.OffsetDateTime

data class BonoImpresionDto(

    var bonoImpresionId: Long? = null,
    var legajoId: Long? = null,
    var anho: Int = 0,
    var mes: Int = 0,
    var legajoIdSolicitud: Long? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fecha: OffsetDateTime? = null,
    var ipAddress: String = ""

)
