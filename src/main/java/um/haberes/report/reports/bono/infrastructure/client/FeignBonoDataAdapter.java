package um.haberes.report.reports.bono.infrastructure.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.bono.domain.model.BonoFuente;
import um.haberes.report.reports.bono.domain.ports.out.BonoDataPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.AntiguedadClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.BonoImpresionClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CargoClaseDetalleClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CargoLiquidacionClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CodigoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CodigoGrupoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.ContactoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.ControlClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CursoCargoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.DependenciaClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.DesignacionToolClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.FacultadClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.ItemClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LegajoBancoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LegajoControlClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LetraClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LiquidacionAdicionalClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LiquidacionClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LiquidacionEtecClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.PersonaClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.AntiguedadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.BonoImpresionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClaseDetalleDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoLiquidacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoGrupoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ContactoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ControlDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoCargoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DependenciaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FacultadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ItemDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LegajoBancoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LegajoControlDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LetraDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionAdicionalDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeignBonoDataAdapter implements BonoDataPort {

    private final PersonaClient personaClient;
    private final FacultadClient facultadClient;
    private final CursoCargoClient cursoCargoClient;
    private final ControlClient controlClient;
    private final LiquidacionClient liquidacionClient;
    private final AntiguedadClient antiguedadClient;
    private final DesignacionToolClient designacionToolClient;
    private final CargoLiquidacionClient cargoLiquidacionClient;
    private final CargoClaseDetalleClient cargoClaseDetalleClient;
    private final LegajoBancoClient legajoBancoClient;
    private final DependenciaClient dependenciaClient;
    private final ItemClient itemClient;
    private final LiquidacionEtecClient liquidacionEtecClient;
    private final LiquidacionAdicionalClient liquidacionAdicionalClient;
    private final CodigoClient codigoClient;
    private final CodigoGrupoClient codigoGrupoClient;
    private final LetraClient letraClient;
    private final BonoImpresionClient bonoImpresionClient;
    private final ContactoClient contactoClient;
    private final LegajoControlClient legajoControlClient;

    @Override
    public BonoFuente cargar(Long legajoId, Integer anho, Integer mes, Integer facultadId) {
        PersonaDto persona = personaClient.findByLegajoId(legajoId);
        AntiguedadDto antiguedad = antiguedadClient.findByUnique(legajoId, anho, mes);
        ControlDto control = controlClient.findByPeriodo(anho, mes);
        List<BigDecimal> indices = safe(designacionToolClient.indiceAntiguedad(legajoId, anho, mes));
        LegajoBancoDto banco = null;
        try {
            banco = legajoBancoClient.findLegajoCbuPrincipal(legajoId, anho, mes);
        } catch (Exception e) {
            log.debug("Sin CBU principal para el legajo {}: {}", legajoId, e.getMessage());
        }
        Map<Integer, String> dependencias = safe(dependenciaClient.findAll()).stream()
                .filter(Objects::nonNull)
                .filter(dependencia -> dependencia.getDependenciaId() != null)
                .collect(Collectors.toMap(DependenciaDto::getDependenciaId,
                        DependenciaDto::getAcronimo, (a, b) -> a));

        List<CursoCargoDto> cursos = facultadId == null
                ? safe(cursoCargoClient.findAllByLegajo(legajoId, anho, mes))
                : safe(cursoCargoClient.findAllByFacultad(legajoId, anho, mes, facultadId));
        List<CargoLiquidacionDto> docentes = facultadId == null
                ? safe(cargoLiquidacionClient.findAllDocenteByLegajo(legajoId, anho, mes))
                : safe(cargoLiquidacionClient.findAllDocenteByLegajoAndFacultad(legajoId, anho, mes,
                        facultadId));
        List<CargoLiquidacionDto> noDocentes = facultadId == null
                ? safe(cargoLiquidacionClient.findAllNoDocenteByLegajo(legajoId, anho, mes))
                : safe(cargoLiquidacionClient.findAllNoDocenteByLegajoAndFacultad(legajoId, anho, mes,
                        facultadId));
        List<CargoClaseDetalleDto> clases = facultadId == null
                ? safe(cargoClaseDetalleClient.findAllByLegajo(legajoId, anho, mes))
                : safe(cargoClaseDetalleClient.findAllByLegajoAndFacultad(legajoId, anho, mes,
                        facultadId));

        LetraDto letra = null;
        try {
            letra = letraClient.findByUnique(legajoId, anho, mes);
        } catch (Exception e) {
            log.debug("Sin letra previa para el legajo {}: {}", legajoId, e.getMessage());
        }
        BigDecimal porcentajeEtec = BigDecimal.ZERO;
        try {
            porcentajeEtec = liquidacionEtecClient.calcularPorcentajeAntiguedad(legajoId, anho, mes,
                    15, 1);
        } catch (Exception e) {
            log.debug("Sin porcentaje ETEC para el legajo {}: {}", legajoId, e.getMessage());
        }
        ContactoDto contacto = null;
        try {
            contacto = contactoClient.findByLegajoId(legajoId);
        } catch (Exception e) {
            log.debug("Sin contacto para el legajo {}: {}", legajoId, e.getMessage());
        }
        LegajoControlDto legajoControl = null;
        try {
            legajoControl = legajoControlClient.findByUnique(legajoId, anho, mes);
        } catch (Exception e) {
            log.debug("Sin legajoControl para el legajo {}: {}", legajoId, e.getMessage());
        }
        String facultadNombre = null;
        if (facultadId != null) {
            FacultadDto facultad = facultadClient.findByFacultadId(facultadId);
            facultadNombre = facultad == null ? null : facultad.getNombre();
        }

        return new BonoFuente(
                persona(persona, dependencias),
                new BonoFuente.Control(control == null ? null : control.getFechaDesde(),
                        control == null ? null : control.getFechaHasta(),
                        control == null ? null : control.getFechaPago(),
                        control == null ? null : control.getFechaDeposito(),
                        control == null ? null : control.getAporteJubilatorio(),
                        control == null ? null : control.getHoraReferenciaEtec()),
                antiguedad == null ? null : new BonoFuente.Antiguedad(
                        antiguedad.getMesesDocentes(), antiguedad.getMesesAdministrativos()),
                indices,
                new BonoFuente.Banco(banco == null ? null : banco.getCbu()),
                safe(itemClient.findAllByLegajo(legajoId, anho, mes)).stream()
                        .map(item -> new BonoFuente.Item(item.getCodigoId(), item.getImporte()))
                        .toList(),
                cursos.stream().map(this::curso).toList(),
                safe(cursoCargoClient.findAllByLegajoAndNivel(legajoId, anho, mes, 2)).stream()
                        .map(this::curso).toList(),
                docentes.stream().map(this::cargo).toList(),
                noDocentes.stream().map(this::cargo).toList(),
                clases.stream().map(this::clase).toList(),
                safe(liquidacionAdicionalClient.findAllByLegajo(legajoId, anho, mes)).stream()
                        .map(adicional -> new BonoFuente.Adicional(adicional.getDependenciaId(),
                                adicional.getAdicional()))
                        .toList(),
                dependencias.entrySet().stream()
                        .map(entry -> new BonoFuente.Dependencia(entry.getKey(), entry.getValue()))
                        .toList(),
                codigo(),
                grupos(codigoGrupoClient.findAllByRemunerativo((byte) 1)),
                grupos(codigoGrupoClient.findAllByNoRemunerativo((byte) 1)),
                grupos(codigoGrupoClient.findAllByDeduccion((byte) 1)),
                letra == null ? null : new BonoFuente.Letra(letra.getLetraId(), letra.getNeto(),
                        letra.getCadena()),
                porcentajeEtec,
                contacto == null ? null : new BonoFuente.Contacto(
                        contacto.getMailInstitucional()),
                legajoControl == null ? null : legajoControl.getLegajoControlId(),
                legajoControl != null && legajoControl.getBonoEnviado() == 1,
                facultadNombre);
    }

    @Override
    public BonoFuente.Persona persona(Long legajoId) {
        PersonaDto persona = personaClient.findByLegajoId(legajoId);
        if (persona == null) {
            return null;
        }
        Map<Integer, String> dependencias = safe(dependenciaClient.findAll()).stream()
                .filter(Objects::nonNull)
                .filter(dependencia -> dependencia.getDependenciaId() != null)
                .collect(Collectors.toMap(DependenciaDto::getDependenciaId,
                        DependenciaDto::getAcronimo, (a, b) -> a));
        return persona(persona, dependencias);
    }

    @Override
    public void deleteItemsEnCero(Long legajoId, Integer anho, Integer mes) {
        itemClient.deleteAllByZero(legajoId, anho, mes);
    }

    @Override
    public BonoFuente.Letra guardarLetra(Long legajoId, Integer anho, Integer mes,
                                         BigDecimal neto, String cadena) {
        try {
            LetraDto existente = letraClient.findByUnique(legajoId, anho, mes);
            LetraDto letra = letraClient.update(
                    new LetraDto(existente.getLetraId(), legajoId, anho, mes, neto, cadena),
                    existente.getLetraId());
            return new BonoFuente.Letra(letra.getLetraId(), letra.getNeto(), letra.getCadena());
        } catch (Exception e) {
            LetraDto letra = letraClient.add(new LetraDto(null, legajoId, anho, mes, neto, cadena));
            return new BonoFuente.Letra(letra.getLetraId(), letra.getNeto(), letra.getCadena());
        }
    }

    @Override
    public void auditar(Long legajoId, Integer anho, Integer mes, Long legajoIdSolicitud,
                        String ipAddress, OffsetDateTime fecha) {
        bonoImpresionClient.add(new BonoImpresionDto(null, legajoId, anho, mes, legajoIdSolicitud,
                fecha, ipAddress));
    }

    @Override
    public void marcarBonoEnviado(Long legajoId, Integer anho, Integer mes) {
        try {
            LegajoControlDto control = legajoControlClient.findByUnique(legajoId, anho, mes);
            control.setBonoEnviado((byte) 1);
            legajoControlClient.update(control, control.getLegajoControlId());
        } catch (Exception e) {
            legajoControlClient.add(new LegajoControlDto(null, legajoId, anho, mes, (byte) 0,
                    (byte) 0, (byte) 1, null));
        }
    }

    @Override
    public List<BonoFuente.Liquidacion> liquidacionesDependencia(Integer dependenciaId,
                                                                 Integer anho, Integer mes,
                                                                 String salida) {
        return safe(liquidacionClient.findAllByDependencia(dependenciaId, anho, mes, salida)).stream()
                .filter(Objects::nonNull)
                .map(liquidacion -> new BonoFuente.Liquidacion(liquidacion.getLegajoId()))
                .toList();
    }

    private BonoFuente.Persona persona(PersonaDto persona, Map<Integer, String> dependencias) {
        if (persona == null) {
            return null;
        }
        return new BonoFuente.Persona(persona.getLegajoId(), persona.getApellido(),
                persona.getNombre(),
                persona.getDocumento() == null ? null : persona.getDocumento().toString(),
                persona.getCuil(), dependencias.get(persona.getDependenciaId()),
                persona.getAltaAdministrativa(), persona.getDirectivoEtec());
    }

    private BonoFuente.CursoCargo curso(CursoCargoDto dto) {
        return new BonoFuente.CursoCargo(
                dto.getCargoTipo() == null ? null : dto.getCargoTipo().getNombre(),
                dto.getCurso() == null ? null : dto.getCurso().getNombre(),
                dto.getDesarraigo(), dto.getCurso() == null ? 0 : dto.getCurso().getAnual(),
                dto.getCurso() == null ? 0 : dto.getCurso().getSemestre1(),
                dto.getCurso() == null ? 0 : dto.getCurso().getSemestre2(),
                dto.getHorasSemanales());
    }

    private BonoFuente.CargoLiquidado cargo(CargoLiquidacionDto dto) {
        return new BonoFuente.CargoLiquidado(
                dto.getDependencia() == null ? null : dto.getDependencia().getAcronimo(),
                dto.getCategoria() == null ? dto.getCategoriaNombre()
                        : dto.getCategoria().getNombre(),
                dto.getCategoriaBasico(), dto.getHorasJornada(), dto.getJornada());
    }

    private BonoFuente.Clase clase(CargoClaseDetalleDto dto) {
        return new BonoFuente.Clase(
                dto.getDependencia() == null ? null : dto.getDependencia().getAcronimo(),
                dto.getCargoClase() == null || dto.getCargoClase().getClase() == null
                        ? null : dto.getCargoClase().getClase().getNombre(),
                dto.getCargoClase() == null ? null : dto.getCargoClase().getNombre(),
                dto.getCargoClasePeriodo() == null ? null
                        : dto.getCargoClasePeriodo().getDescripcion(),
                dto.getCargoClasePeriodo() == null
                        || dto.getCargoClasePeriodo().getGeografica() == null ? null
                        : dto.getCargoClasePeriodo().getGeografica().getNombre(),
                dto.getCargoClasePeriodo() == null ? 0
                        : dto.getCargoClasePeriodo().getHoras(),
                dto.getValorHora());
    }

    private BonoFuente.Codigo codigo() {
        try {
            CodigoDto codigo = codigoClient.findByCodigoId(981);
            return codigo == null ? null : new BonoFuente.Codigo(codigo.getCodigoId(),
                    codigo.getNombre());
        } catch (Exception e) {
            return null;
        }
    }

    private List<BonoFuente.Grupo> grupos(List<CodigoGrupoDto> grupos) {
        return safe(grupos).stream()
                .filter(Objects::nonNull)
                .map(grupo -> new BonoFuente.Grupo(
                        grupo.getCodigoId() != null ? grupo.getCodigoId()
                                : grupo.getCodigo() == null ? null : grupo.getCodigo().getCodigoId(),
                        grupo.getCodigo() == null ? null : grupo.getCodigo().getNombre()))
                .toList();
    }

    private <T> List<T> safe(List<T> valores) {
        return valores == null ? List.of() : valores;
    }
}
