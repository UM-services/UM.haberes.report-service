package um.haberes.report.shared;

import org.junit.jupiter.api.Test;
import um.haberes.report.shared.domain.model.CargoLiquidacionResumen;
import um.haberes.report.shared.domain.model.CursoCargoNovedadResumen;
import um.haberes.report.shared.domain.model.CursoCargoResumen;
import um.haberes.report.shared.domain.model.CursoFusionResumen;
import um.haberes.report.shared.domain.model.CursoResumen;
import um.haberes.report.shared.domain.model.DocenteDesignacionResumen;
import um.haberes.report.shared.domain.model.LiquidacionResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.adapter.FeignCargoLiquidacionCatalogAdapter;
import um.haberes.report.shared.infrastructure.client.haberes.core.adapter.FeignCursoCargoCatalogAdapter;
import um.haberes.report.shared.infrastructure.client.haberes.core.adapter.FeignCursoCargoNovedadCatalogAdapter;
import um.haberes.report.shared.infrastructure.client.haberes.core.adapter.FeignCursoCatalogAdapter;
import um.haberes.report.shared.infrastructure.client.haberes.core.adapter.FeignCursoFusionCatalogAdapter;
import um.haberes.report.shared.infrastructure.client.haberes.core.adapter.FeignDocenteDesignacionCatalogAdapter;
import um.haberes.report.shared.infrastructure.client.haberes.core.adapter.FeignLiquidacionCatalogAdapter;
import um.haberes.report.shared.infrastructure.client.haberes.core.adapter.FeignTotalesPeriodoAdapter;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CargoLiquidacionClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CursoCargoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CursoCargoNovedadClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CursoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CursoFusionClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.DocenteDesignacionClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LiquidacionClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.TotalItemClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.TotalNovedadClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoLiquidacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoTipoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CategoriaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoCargoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoCargoNovedadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoFusionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DependenciaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DesignacionTipoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DocenteDesignacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FacultadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.GeograficaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.TotalItemDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.TotalNovedadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CargoLiquidacionMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CargoTipoMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CategoriaMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CursoCargoMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CursoCargoNovedadMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CursoFusionMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CursoMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.DependenciaMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.DesignacionTipoMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.DocenteDesignacionMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.FacultadMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.GeograficaMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.LiquidacionMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.PersonaMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.TotalItemMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.TotalNovedadMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Mapeo y delegacion de los agregados academicos y transaccionales del kernel. */
class AcademicCatalogAdapterTest {

    private final Factories factories = new Factories();

    private final CursoClient cursoClient = mock(CursoClient.class);
    private final CursoCargoClient cursoCargoClient = mock(CursoCargoClient.class);
    private final LiquidacionClient liquidacionClient = mock(LiquidacionClient.class);
    private final CargoLiquidacionClient cargoLiquidacionClient = mock(CargoLiquidacionClient.class);
    private final CursoFusionClient cursoFusionClient = mock(CursoFusionClient.class);
    private final CursoCargoNovedadClient cursoCargoNovedadClient = mock(CursoCargoNovedadClient.class);
    private final DocenteDesignacionClient docenteDesignacionClient = mock(DocenteDesignacionClient.class);
    private final TotalNovedadClient totalNovedadClient = mock(TotalNovedadClient.class);
    private final TotalItemClient totalItemClient = mock(TotalItemClient.class);

    @Test
    void cursoMapeaFacultadYGeograficaAnidadas() {
        CursoDto dto = new CursoDto();
        dto.setCursoId(301L);
        dto.setNombre("Análisis Matemático I");
        dto.setFacultadId(2);
        dto.setGeograficaId(3);
        dto.setAnual((byte) 1);
        dto.setFacultad(facultadDto(2, "Facultad de Ingeniería"));
        dto.setGeografica(geograficaDto(3, "Sede San Rafael"));

        CursoResumen curso = factories.cursoMapper.toDomain(dto);

        assertThat(curso.getCursoId()).isEqualTo(301L);
        assertThat(curso.getNombre()).isEqualTo("Análisis Matemático I");
        assertThat(curso.getFacultad().getNombre()).isEqualTo("Facultad de Ingeniería");
        assertThat(curso.getGeografica().getNombre()).isEqualTo("Sede San Rafael");
        assertThat(curso.getAnual()).isEqualTo((byte) 1);
        assertThat(factories.cursoMapper.toDomain(null)).isNull();

        CursoDto sinAnidados = new CursoDto();
        sinAnidados.setCursoId(302L);
        CursoResumen soloIds = factories.cursoMapper.toDomain(sinAnidados);
        assertThat(soloIds.getFacultad()).isNull();
        assertThat(soloIds.getGeografica()).isNull();
    }

    @Test
    void cursoCargoMapeaTodosLosAnidados() {
        CursoCargoDto dto = new CursoCargoDto();
        dto.setLegajoId(120L);
        dto.setCursoId(301L);
        dto.setCargoTipoId(1);
        dto.setCategoriaId(9);
        dto.setDesignacionTipoId(4);
        dto.setHorasSemanales(new BigDecimal("10.5"));
        dto.setDesarraigo((byte) 1);
        dto.setCurso(cursoDto(301L, 2, 3));
        dto.setCargoTipo(cargoTipoDto(1, "Profesor Titular"));
        dto.setCategoria(categoriaDto(9, "Profesor Titular", "1234567.89"));
        dto.setDesignacionTipo(designacionTipoDto(4, "Ordinaria"));
        dto.setPersona(personaDto(120L, "Gómez", "Ana"));

        CursoCargoResumen cursoCargo = factories.cursoCargoMapper.toDomain(dto);

        assertThat(cursoCargo.getLegajoId()).isEqualTo(120L);
        assertThat(cursoCargo.getHorasSemanales()).isEqualByComparingTo("10.5");
        assertThat(cursoCargo.getDesarraigo()).isEqualTo((byte) 1);
        assertThat(cursoCargo.getCurso().getCursoId()).isEqualTo(301L);
        assertThat(cursoCargo.getCargoTipo().getNombre()).isEqualTo("Profesor Titular");
        assertThat(cursoCargo.getCategoria().getBasico()).isEqualByComparingTo("1234567.89");
        assertThat(cursoCargo.getDesignacionTipo().getNombre()).isEqualTo("Ordinaria");
        assertThat(cursoCargo.getPersona().apellidoNombre()).isEqualTo("Gómez, Ana");
    }

    @Test
    void cursoCargoNovedadMapeaFlagsYRespuesta() {
        CursoCargoNovedadDto dto = new CursoCargoNovedadDto();
        dto.setLegajoId(121L);
        dto.setCursoId(301L);
        dto.setAnho(2026);
        dto.setMes(9);
        dto.setCargoTipoId(2);
        dto.setHorasSemanales(new BigDecimal("6.50"));
        dto.setDesarraigo((byte) 1);
        dto.setAlta((byte) 1);
        dto.setCambio((byte) 1);
        dto.setBaja((byte) 0);
        dto.setAutorizado((byte) 1);
        dto.setRechazado((byte) 0);
        dto.setSolicitud("ALTA-CAMBIO");
        dto.setRespuesta("OK");
        dto.setCurso(cursoDto(301L, 2, 3));
        dto.setPersona(personaDto(121L, "Díaz", "Carlos"));

        CursoCargoNovedadResumen novedad = factories.cursoCargoNovedadMapper.toDomain(dto);

        assertThat(novedad.getAlta()).isEqualTo((byte) 1);
        assertThat(novedad.getCambio()).isEqualTo((byte) 1);
        assertThat(novedad.getBaja()).isZero();
        assertThat(novedad.getAutorizado()).isEqualTo((byte) 1);
        assertThat(novedad.getSolicitud()).isEqualTo("ALTA-CAMBIO");
        assertThat(novedad.getRespuesta()).isEqualTo("OK");
        assertThat(novedad.getCurso().getGeograficaId()).isEqualTo(3);
        assertThat(novedad.getPersona().getApellido()).isEqualTo("Díaz");
        assertThat(factories.cursoCargoNovedadMapper.toDomain(null)).isNull();
    }

    @Test
    void liquidacionMapeaTotalesYCargoLiquidacionAnidados() {
        LiquidacionDto dto = new LiquidacionDto();
        dto.setLegajoId(120L);
        dto.setAnho(2026);
        dto.setMes(9);
        dto.setDependenciaId(4);
        dto.setTotalRemunerativo(new BigDecimal("1800000.00"));
        dto.setTotalNoRemunerativo(new BigDecimal("100000.00"));
        dto.setTotalDeduccion(new BigDecimal("250000.00"));
        dto.setTotalNeto(new BigDecimal("1650000.00"));

        LiquidacionResumen liquidacion = new LiquidacionMapper().toDomain(dto);

        assertThat(liquidacion.getLegajoId()).isEqualTo(120L);
        assertThat(liquidacion.getTotalRemunerativo()).isEqualByComparingTo("1800000.00");
        assertThat(liquidacion.getTotalNoRemunerativo()).isEqualByComparingTo("100000.00");
        assertThat(liquidacion.getTotalDeduccion()).isEqualByComparingTo("250000.00");
        assertThat(liquidacion.getTotalNeto()).isEqualByComparingTo("1650000.00");

        CargoLiquidacionDto cargoDto = new CargoLiquidacionDto();
        cargoDto.setLegajoId(120L);
        cargoDto.setAnho(2026);
        cargoDto.setMes(9);
        cargoDto.setDependenciaId(4);
        cargoDto.setCategoriaId(9);
        cargoDto.setCategoriaNombre("Profesor Titular");
        cargoDto.setCategoriaBasico(new BigDecimal("1500000.00"));
        cargoDto.setHorasJornada(new BigDecimal("10"));
        cargoDto.setJornada(1);
        cargoDto.setPresentismo(1);
        cargoDto.setSituacion("Activo");
        cargoDto.setDependencia(dependenciaDto(4, "Decanato", "FI"));
        cargoDto.setCategoria(categoriaDto(9, "Profesor Titular", "1500000.00"));

        CargoLiquidacionResumen cargo = factories.cargoLiquidacionMapper.toDomain(cargoDto);

        assertThat(cargo.getCategoriaNombre()).isEqualTo("Profesor Titular");
        assertThat(cargo.getCategoriaBasico()).isEqualByComparingTo("1500000.00");
        assertThat(cargo.getDependencia().getAcronimo()).isEqualTo("FI");
        assertThat(cargo.getCategoria().getNombre()).isEqualTo("Profesor Titular");
        assertThat(cargo.getJornada()).isEqualTo(1);
        assertThat(cargo.getPresentismo()).isEqualTo(1);
    }

    @Test
    void fusionYDesignacionMapeanCampos() {
        CursoFusionDto fusionDto = new CursoFusionDto();
        fusionDto.setCursoFusionId(77L);
        fusionDto.setLegajoId(120L);
        fusionDto.setAnho(2026);
        fusionDto.setMes(9);
        fusionDto.setFacultadId(2);
        fusionDto.setGeograficaId(3);
        fusionDto.setCategoriaId(9);
        fusionDto.setCargoTipoId(1);
        fusionDto.setDesignacionTipoId(4);
        fusionDto.setAnual((byte) 1);

        CursoFusionResumen fusion = new CursoFusionMapper().toDomain(fusionDto);

        assertThat(fusion.getCursoFusionId()).isEqualTo(77L);
        assertThat(fusion.getCategoriaId()).isEqualTo(9);
        assertThat(fusion.getDesignacionTipoId()).isEqualTo(4);
        assertThat(fusion.getAnual()).isEqualTo((byte) 1);

        DocenteDesignacionDto designacionDto = new DocenteDesignacionDto();
        designacionDto.setLegajoId(120L);
        designacionDto.setFacultadId(2);
        designacionDto.setGeograficaId(3);
        designacionDto.setEspacio("Análisis Matemático I");
        designacionDto.setCargo("Profesor Titular");
        designacionDto.setDesignacion("Ordinaria");
        designacionDto.setHorasSemanales(new BigDecimal("10"));
        designacionDto.setHorasDesignacion(new BigDecimal("10"));
        designacionDto.setAnual((byte) 1);
        designacionDto.setSemestre1((byte) 0);
        designacionDto.setSemestre2((byte) 0);

        DocenteDesignacionResumen designacion = new DocenteDesignacionMapper().toDomain(designacionDto);

        assertThat(designacion.getEspacio()).isEqualTo("Análisis Matemático I");
        assertThat(designacion.getCargo()).isEqualTo("Profesor Titular");
        assertThat(designacion.getHorasDesignacion()).isEqualByComparingTo("10");
        assertThat(designacion.getAnual()).isEqualTo((byte) 1);
        assertThat(designacion.getSemestre1()).isZero();
    }

    @Test
    void adaptadoresDeleganYListasNulasSonVacias() {
        when(cursoClient.findAll()).thenReturn(null);
        when(cursoClient.findByCursoId(301L)).thenReturn(null);
        when(cursoClient.findAllByFacultadIdAndGeograficaIdAndAnhoAndMes(2, 3, 2026, 9)).thenReturn(List.of());
        var cursoAdapter = new FeignCursoCatalogAdapter(cursoClient, factories.cursoMapper);
        assertThat(cursoAdapter.findAll()).isEmpty();
        assertThat(cursoAdapter.findById(301L)).isEmpty();
        assertThat(cursoAdapter.findAllByFacultadAndGeograficaAndPeriodo(2, 3, 2026, 9)).isEmpty();
        verify(cursoClient).findAllByFacultadIdAndGeograficaIdAndAnhoAndMes(2, 3, 2026, 9);

        when(cursoCargoClient.findAllByLegajo(120L, 2026, 9)).thenReturn(null);
        var cursoCargoAdapter = new FeignCursoCargoCatalogAdapter(cursoCargoClient, factories.cursoCargoMapper);
        assertThat(cursoCargoAdapter.findAllByLegajo(120L, 2026, 9)).isEmpty();
        verify(cursoCargoClient).findAllByLegajo(120L, 2026, 9);

        when(liquidacionClient.findAllByAcreditado(2026, 9)).thenReturn(null);
        var liquidacionAdapter = new FeignLiquidacionCatalogAdapter(liquidacionClient, new LiquidacionMapper());
        assertThat(liquidacionAdapter.findAllByAcreditado(2026, 9)).isEmpty();
        verify(liquidacionClient).findAllByAcreditado(2026, 9);

        when(cargoLiquidacionClient.findAllDocenteByLegajo(120L, 2026, 9)).thenReturn(null);
        when(cargoLiquidacionClient.findAllNoDocenteByLegajo(120L, 2026, 9)).thenReturn(List.of());
        var cargoAdapter = new FeignCargoLiquidacionCatalogAdapter(cargoLiquidacionClient, factories.cargoLiquidacionMapper);
        assertThat(cargoAdapter.findAllDocenteByLegajo(120L, 2026, 9)).isEmpty();
        assertThat(cargoAdapter.findAllNoDocenteByLegajo(120L, 2026, 9)).isEmpty();
        verify(cargoLiquidacionClient).findAllDocenteByLegajo(120L, 2026, 9);

        when(cursoFusionClient.findAllByLegajoId(120L, 2026, 9)).thenReturn(null);
        var fusionAdapter = new FeignCursoFusionCatalogAdapter(cursoFusionClient, new CursoFusionMapper());
        assertThat(fusionAdapter.findAllByLegajoId(120L, 2026, 9)).isEmpty();

        when(cursoCargoNovedadClient.findAllByPeriodo(2026, 9)).thenReturn(null);
        when(cursoCargoNovedadClient.findAllPendientes(2026, 9)).thenReturn(List.of());
        var novedadAdapter = new FeignCursoCargoNovedadCatalogAdapter(cursoCargoNovedadClient, factories.cursoCargoNovedadMapper);
        assertThat(novedadAdapter.findAllByPeriodo(2026, 9)).isEmpty();
        assertThat(novedadAdapter.findAllPendientes(2026, 9)).isEmpty();
        verify(cursoCargoNovedadClient).findAllByPeriodo(2026, 9);

        when(docenteDesignacionClient.findAllByPeriodo(2026, 9)).thenReturn(null);
        var designacionAdapter = new FeignDocenteDesignacionCatalogAdapter(docenteDesignacionClient, new DocenteDesignacionMapper());
        assertThat(designacionAdapter.findAllByPeriodo(2026, 9)).isEmpty();
    }

    @Test
    void totalesDePeriodoMapeanYDelegan() {
        TotalNovedadDto novedadDto = new TotalNovedadDto();
        novedadDto.setCodigoId(981);
        novedadDto.setTotal(new BigDecimal("1500000.50"));
        TotalItemDto itemDto = new TotalItemDto();
        itemDto.setCodigoId(981);
        itemDto.setTotal(new BigDecimal("1500000.50"));
        when(totalNovedadClient.findAllByPeriodo(2026, 9)).thenReturn(List.of(novedadDto));
        when(totalItemClient.findAllByPeriodo(2026, 9)).thenReturn(List.of(itemDto));

        var adapter = new FeignTotalesPeriodoAdapter(
                totalNovedadClient, totalItemClient, new TotalNovedadMapper(), new TotalItemMapper());

        assertThat(adapter.totalesNovedad(2026, 9)).singleElement()
                .satisfies(total -> {
                    assertThat(total.getCodigoId()).isEqualTo(981);
                    assertThat(total.getTotal()).isEqualByComparingTo("1500000.50");
                });
        assertThat(adapter.totalesItem(2026, 9)).singleElement()
                .extracting(total -> total.getTotal()).isEqualTo(new BigDecimal("1500000.50"));
        verify(totalNovedadClient).findAllByPeriodo(2026, 9);
        verify(totalItemClient).findAllByPeriodo(2026, 9);

        when(totalNovedadClient.findAllByPeriodo(2026, 8)).thenReturn(null);
        when(totalItemClient.findAllByPeriodo(2026, 8)).thenReturn(null);
        assertThat(adapter.totalesNovedad(2026, 8)).isEmpty();
        assertThat(adapter.totalesItem(2026, 8)).isEmpty();
    }

    private static PersonaDto personaDto(Long legajoId, String apellido, String nombre) {
        PersonaDto dto = new PersonaDto();
        dto.setLegajoId(legajoId);
        dto.setApellido(apellido);
        dto.setNombre(nombre);
        return dto;
    }

    private static FacultadDto facultadDto(Integer id, String nombre) {
        FacultadDto dto = new FacultadDto();
        dto.setFacultadId(id);
        dto.setNombre(nombre);
        return dto;
    }

    private static GeograficaDto geograficaDto(Integer id, String nombre) {
        GeograficaDto dto = new GeograficaDto();
        dto.setGeograficaId(id);
        dto.setNombre(nombre);
        return dto;
    }

    private static DependenciaDto dependenciaDto(Integer id, String nombre, String acronimo) {
        DependenciaDto dto = new DependenciaDto();
        dto.setDependenciaId(id);
        dto.setNombre(nombre);
        dto.setAcronimo(acronimo);
        return dto;
    }

    private static CategoriaDto categoriaDto(Integer id, String nombre, String basico) {
        CategoriaDto dto = new CategoriaDto();
        dto.setCategoriaId(id);
        dto.setNombre(nombre);
        dto.setBasico(new BigDecimal(basico));
        return dto;
    }

    private static CargoTipoDto cargoTipoDto(Integer id, String nombre) {
        CargoTipoDto dto = new CargoTipoDto();
        dto.setCargoTipoId(id);
        dto.setNombre(nombre);
        return dto;
    }

    private static DesignacionTipoDto designacionTipoDto(Integer id, String nombre) {
        DesignacionTipoDto dto = new DesignacionTipoDto();
        dto.setDesignacionTipoId(id);
        dto.setNombre(nombre);
        return dto;
    }

    private static CursoDto cursoDto(Long id, Integer facultadId, Integer geograficaId) {
        CursoDto dto = new CursoDto();
        dto.setCursoId(id);
        dto.setNombre("Curso " + id);
        dto.setFacultadId(facultadId);
        dto.setGeograficaId(geograficaId);
        return dto;
    }

    /** Mappers con sus dependencias reales. */
    private static final class Factories {
        private final FacultadMapper facultadMapper = new FacultadMapper();
        private final GeograficaMapper geograficaMapper = new GeograficaMapper();
        private final PersonaMapper personaMapper = new PersonaMapper();
        private final CargoTipoMapper cargoTipoMapper = new CargoTipoMapper();
        private final CategoriaMapper categoriaMapper = new CategoriaMapper();
        private final CursoMapper cursoMapper = new CursoMapper(facultadMapper, geograficaMapper);
        private final CursoCargoMapper cursoCargoMapper = new CursoCargoMapper(
                cursoMapper, cargoTipoMapper, categoriaMapper, new DesignacionTipoMapper(), personaMapper);
        private final CursoCargoNovedadMapper cursoCargoNovedadMapper =
                new CursoCargoNovedadMapper(cursoMapper, cargoTipoMapper, personaMapper);
        private final CargoLiquidacionMapper cargoLiquidacionMapper =
                new CargoLiquidacionMapper(new DependenciaMapper(), categoriaMapper);
    }
}
