package um.haberes.report.shared.infrastructure.client.haberes.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultadDto {

    private Integer facultadId;

    private String nombre = "";

    private String reducido = "";

    private String server = "";

    private String backendServer = "";

    private int backendPort = 0;

    private String dbName = "";

    private String dsn = "";

}
