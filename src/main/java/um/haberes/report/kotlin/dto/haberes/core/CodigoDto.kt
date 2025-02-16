package um.haberes.report.kotlin.dto.haberes.core

data class CodigoDto(

    var codigoId: Int? = null,
    var nombre: String = "",
    var docente: Byte = 0,
    var noDocente: Byte = 0,
    var transferible: Byte = 0,
    var incluidoEtec: Byte = 0,
    var afipConceptoSueldoIdPrimerSemestre: Long? = null,
    var afipConceptoSueldoIdSegundoSemestre: Long? = null,
    var afipConceptoSueldoPrimerSemestre: AfipConceptoSueldoDto? = null,
    var afipConceptoSueldoSegundoSemestre: AfipConceptoSueldoDto? = null,
    ) {
}
