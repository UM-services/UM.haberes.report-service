package um.haberes.report.kotlin.dto.haberes.core

import java.math.BigDecimal

data class ContratadoPersonaDto(

    var contratadoId: Long? = null,
    var personaId: BigDecimal? = null,
    var documentoId: Int? = null,
    var apellido: String? = null,
    var nombre: String? = null,
    var cuit: String? = null

)
