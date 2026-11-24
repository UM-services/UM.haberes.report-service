package um.haberes.report.shared.infrastructure.client.haberes.core.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.json.JsonMapper;

/**
 * Regresión del bono individual: haberes-core devuelve la persona de
 * {@code /antiguedad/unique} sin {@code directivoEtec} y con campos extra
 * ({@code created}, {@code updated}, {@code apellidoNombre}).
 *
 * <p>Jackson 3 (Spring Boot 4) es estricto con los primitivos faltantes
 * ({@code FAIL_ON_NULL_FOR_PRIMITIVES} viene habilitado), por eso el contrato se fija
 * con el mapper por defecto de Jackson 3: si el DTO vuelve a declarar un primitivo que
 * core no envía, este test falla antes que la impresión real del recibo.
 */
class PersonaDtoTest {

    private final JsonMapper mapper = JsonMapper.builder().build();

    @Test
    void decodificaLaPersonaDeAntiguedadSinDirectivoEtec() {
        String json = """
                {
                  "antiguedadId": 355688,
                  "legajoId": 341,
                  "anho": 2026,
                  "mes": 2,
                  "mesesDocentes": 475,
                  "mesesAdministrativos": 474,
                  "persona": {
                    "legajoId": 341,
                    "documento": 12044551,
                    "apellido": "IGLESIAS",
                    "nombre": "ALFREDO ANTONIO",
                    "nacimiento": "1958-05-21T00:00:00Z",
                    "altaDocente": "1989-05-01T00:00:00Z",
                    "ajusteDocente": 34,
                    "altaAdministrativa": "1989-05-01T00:00:00Z",
                    "ajusteAdministrativo": 33,
                    "estadoCivil": "C",
                    "situacionId": 0,
                    "reemplazoDesarraigo": 0,
                    "mitadDesarraigo": 0,
                    "cuil": "20120445511",
                    "posgrado": 2,
                    "estado": 1,
                    "liquida": "S",
                    "estadoAfip": 1,
                    "dependenciaId": 40,
                    "salida": "A",
                    "obraSocial": 400800,
                    "actividadAfip": 93,
                    "localidadAfip": 48,
                    "situacionAfip": 1,
                    "modeloContratacionAfip": 8,
                    "dependencia": {
                      "dependenciaId": 40,
                      "nombre": "Facultad de Ingenieria - Sede Mendoza",
                      "acronimo": "FI-MZ",
                      "facultadId": 1,
                      "geograficaId": 1,
                      "facultad": {
                        "facultadId": 1,
                        "nombre": "Facultad de Ingeniería",
                        "reducido": "FI",
                        "server": "192.168.1.3",
                        "backendServer": "192.168.1.127",
                        "backendPort": 8101,
                        "dbName": "fiadm",
                        "dsn": "fi"
                      },
                      "geografica": {
                        "geograficaId": 1,
                        "nombre": "Mendoza",
                        "reducido": "MZ",
                        "desarraigo": 75101.00,
                        "geograficaIdReemplazo": null
                      }
                    },
                    "afipSituacion": {
                      "afipSituacionId": 1,
                      "descripcion": "Activo"
                    },
                    "apellidoNombre": "IGLESIAS, ALFREDO ANTONIO",
                    "created": "2021-12-17T10:27:59",
                    "updated": "2021-12-17T10:27:59"
                  },
                  "created": "2026-02-06T13:11:30",
                  "updated": "2026-02-27T14:03:31"
                }
                """;

        AntiguedadDto antiguedad = mapper.readValue(json, AntiguedadDto.class);

        assertThat(antiguedad.getLegajoId()).isEqualTo(341L);
        assertThat(antiguedad.getMesesDocentes()).isEqualTo(475);
        assertThat(antiguedad.getPersona().getApellido()).isEqualTo("IGLESIAS");
        assertThat(antiguedad.getPersona().getCuil()).isEqualTo("20120445511");
        assertThat(antiguedad.getPersona().getDependencia().getFacultad().getReducido()).isEqualTo("FI");
    }
}
