package um.haberes.report.model.haberes.core;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnotadorDto {

    private Long anotadorId;

    private Long legajoId;

    private int anho = 0;

    private int mes = 0;

    private Integer facultadId;

    private String anotacion;

    private byte visado = 0;

    private String ipVisado;

    private String user;

    private String respuesta;

    private byte autorizado = 0;

    private byte rechazado = 0;

    private byte rectorado = 0;

    private byte transferido = 0;

    private PersonaDto persona;

    private FacultadDto facultad;

    private LocalDateTime created;

}
