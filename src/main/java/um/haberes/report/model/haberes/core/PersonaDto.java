package um.haberes.report.model.haberes.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDto {

    private Long legajoId;

    private BigDecimal documento = BigDecimal.ZERO;

    private String apellido = "";

    private String nombre = "";

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime nacimiento;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime altaDocente;

    private int ajusteDocente = 0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime altaAdministrativa;

    private int ajusteAdministrativo = 0;

    private String estadoCivil = "";

    private Integer situacionId;

    private byte reemplazoDesarraigo = 0;

    private byte mitadDesarraigo = 0;

    private String cuil = "";

    private int posgrado = 0;

    private int estado = 0;

    private String liquida = "";

    private int estadoAfip = 0;

    private Integer dependenciaId;

    private String salida;

    private Long obraSocial;

    private Integer actividadAfip;

    private Integer localidadAfip;

    private int situacionAfip = 0;

    private Integer modeloContratacionAfip;

    private byte directivoEtec = 0;

    private DependenciaDto dependencia;

    private AfipSituacionDto afipSituacion;

    public String getApellidoNombre() {
        return MessageFormat.format("{0}, {1}", this.apellido, this.nombre);
    }

}
