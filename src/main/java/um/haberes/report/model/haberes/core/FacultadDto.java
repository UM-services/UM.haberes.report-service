package um.haberes.report.model.haberes.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.haberes.report.util.Jsonifyable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultadDto implements Jsonifyable {

    private Integer facultadId;

    private String nombre = "";

    private String reducido = "";

    private String server = "";

    private String backendServer = "";

    private int backendPort = 0;

    private String dbName = "";

    private String dsn = "";

}
