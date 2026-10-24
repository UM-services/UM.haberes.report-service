package um.haberes.report.shared;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import um.haberes.report.shared.application.exception.ReportException;
import um.haberes.report.shared.domain.model.CategoriaResumen;
import um.haberes.report.shared.domain.model.ClaseResumen;
import um.haberes.report.shared.domain.model.CodigoResumen;
import um.haberes.report.shared.domain.model.DependenciaResumen;
import um.haberes.report.shared.domain.model.FacultadResumen;
import um.haberes.report.shared.domain.model.GeograficaResumen;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.infrastructure.client.haberes.core.adapter.FeignCargoTipoCatalogAdapter;
import um.haberes.report.shared.infrastructure.client.haberes.core.adapter.FeignCategoriaCatalogAdapter;
import um.haberes.report.shared.infrastructure.client.haberes.core.adapter.FeignClaseCatalogAdapter;
import um.haberes.report.shared.infrastructure.client.haberes.core.adapter.FeignCodigoCatalogAdapter;
import um.haberes.report.shared.infrastructure.client.haberes.core.adapter.FeignDependenciaCatalogAdapter;
import um.haberes.report.shared.infrastructure.client.haberes.core.adapter.FeignFacultadCatalogAdapter;
import um.haberes.report.shared.infrastructure.client.haberes.core.adapter.FeignGeograficaCatalogAdapter;
import um.haberes.report.shared.infrastructure.client.haberes.core.adapter.FeignPersonaCatalogAdapter;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CargoTipoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CategoriaClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.ClaseClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CodigoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.DependenciaClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.FacultadClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.GeograficaClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.PersonaClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoTipoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CategoriaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ClaseDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DependenciaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FacultadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.GeograficaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CargoTipoMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CategoriaMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.ClaseMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CodigoMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.DependenciaMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.FacultadMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.GeograficaMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.PersonaMapper;
import um.haberes.report.shared.infrastructure.file.ReportStorageAdapter;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Cubre los adaptadores del kernel de catalogos: mapeo wire -> dominio, tolerancia a null,
 * delegacion exacta al Feign y el almacenamiento en disco.
 */
class CatalogAdapterTest {

    private final PersonaClient personaClient = mock(PersonaClient.class);
    private final FacultadClient facultadClient = mock(FacultadClient.class);
    private final GeograficaClient geograficaClient = mock(GeograficaClient.class);
    private final DependenciaClient dependenciaClient = mock(DependenciaClient.class);
    private final CategoriaClient categoriaClient = mock(CategoriaClient.class);
    private final CargoTipoClient cargoTipoClient = mock(CargoTipoClient.class);
    private final ClaseClient claseClient = mock(ClaseClient.class);
    private final CodigoClient codigoClient = mock(CodigoClient.class);

    private final FeignPersonaCatalogAdapter personaAdapter =
            new FeignPersonaCatalogAdapter(personaClient, new PersonaMapper());
    private final FeignFacultadCatalogAdapter facultadAdapter =
            new FeignFacultadCatalogAdapter(facultadClient, new FacultadMapper());
    private final FeignGeograficaCatalogAdapter geograficaAdapter =
            new FeignGeograficaCatalogAdapter(geograficaClient, new GeograficaMapper());
    private final FeignDependenciaCatalogAdapter dependenciaAdapter =
            new FeignDependenciaCatalogAdapter(dependenciaClient, new DependenciaMapper());
    private final FeignCategoriaCatalogAdapter categoriaAdapter =
            new FeignCategoriaCatalogAdapter(categoriaClient, new CategoriaMapper());
    private final FeignCargoTipoCatalogAdapter cargoTipoAdapter =
            new FeignCargoTipoCatalogAdapter(cargoTipoClient, new CargoTipoMapper());
    private final FeignClaseCatalogAdapter claseAdapter =
            new FeignClaseCatalogAdapter(claseClient, new ClaseMapper());
    private final FeignCodigoCatalogAdapter codigoAdapter =
            new FeignCodigoCatalogAdapter(codigoClient, new CodigoMapper());

    @Test
    void personaMapeaCamposYNull() {
        PersonaDto dto = new PersonaDto();
        dto.setLegajoId(120L);
        dto.setDocumento(new BigDecimal("30111222"));
        dto.setApellido("Gómez");
        dto.setNombre("Ana");
        dto.setCuil("27-30111222-4");
        dto.setDependenciaId(4);
        dto.setDirectivoEtec((byte) 1);
        dto.setAltaAdministrativa(OffsetDateTime.parse("2015-03-01T00:00:00Z"));

        PersonaResumen persona = new PersonaMapper().toDomain(dto);

        assertThat(persona.getLegajoId()).isEqualTo(120L);
        assertThat(persona.getDocumento()).isEqualByComparingTo("30111222");
        assertThat(persona.apellidoNombre()).isEqualTo("Gómez, Ana");
        assertThat(persona.getCuil()).isEqualTo("27-30111222-4");
        assertThat(persona.getDependenciaId()).isEqualTo(4);
        assertThat(persona.getDirectivoEtec()).isEqualTo((byte) 1);
        assertThat(persona.getAltaAdministrativa()).isEqualTo(OffsetDateTime.parse("2015-03-01T00:00:00Z"));
        assertThat(new PersonaMapper().toDomain(null)).isNull();
    }

    @Test
    void personaAdapterDelegaYDevuelveOptional() {
        when(personaClient.findByLegajoId(120L)).thenReturn(null);
        assertThat(personaAdapter.findByLegajoId(120L)).isEmpty();

        when(personaClient.findAllByFacultad(2)).thenReturn(List.of(personaDto()));
        assertThat(personaAdapter.findAllByFacultad(2)).hasSize(1);
        verify(personaClient).findAllByFacultad(2);

        when(personaClient.findAllDocente(2026, 9)).thenReturn(null);
        assertThat(personaAdapter.findAllDocente(2026, 9)).isEmpty();

        when(personaClient.findAll()).thenReturn(null);
        assertThat(personaAdapter.findAll()).isEmpty();

        when(personaClient.findAllLegajos(List.of(120L))).thenReturn(List.of(personaDto()));
        assertThat(personaAdapter.findAllByLegajos(List.of(120L))).singleElement()
                .extracting(PersonaResumen::getLegajoId).isEqualTo(120L);
    }

    @Test
    void catalogosSimplesMapeanCampos() {
        FacultadDto facultadDto = new FacultadDto();
        facultadDto.setFacultadId(2);
        facultadDto.setNombre("Facultad de Ingeniería");
        FacultadResumen facultad = new FacultadMapper().toDomain(facultadDto);
        assertThat(facultad.getFacultadId()).isEqualTo(2);
        assertThat(facultad.getNombre()).isEqualTo("Facultad de Ingeniería");

        GeograficaDto geograficaDto = new GeograficaDto();
        geograficaDto.setGeograficaId(3);
        geograficaDto.setNombre("Sede San Rafael");
        GeograficaResumen geografica = new GeograficaMapper().toDomain(geograficaDto);
        assertThat(geografica.getNombre()).isEqualTo("Sede San Rafael");

        DependenciaDto dependenciaDto = new DependenciaDto();
        dependenciaDto.setDependenciaId(4);
        dependenciaDto.setNombre("Decanato");
        dependenciaDto.setAcronimo("FI");
        dependenciaDto.setFacultadId(2);
        dependenciaDto.setGeograficaId(3);
        DependenciaResumen dependencia = new DependenciaMapper().toDomain(dependenciaDto);
        assertThat(dependencia.getAcronimo()).isEqualTo("FI");
        assertThat(dependencia.getFacultadId()).isEqualTo(2);
        assertThat(dependencia.getGeograficaId()).isEqualTo(3);

        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setCategoriaId(9);
        categoriaDto.setNombre("Profesor Titular");
        categoriaDto.setBasico(new BigDecimal("1234567.89"));
        CategoriaResumen categoria = new CategoriaMapper().toDomain(categoriaDto);
        assertThat(categoria.getBasico()).isEqualByComparingTo("1234567.89");

        CargoTipoDto cargoTipoDto = new CargoTipoDto();
        cargoTipoDto.setCargoTipoId(1);
        cargoTipoDto.setNombre("Profesor");
        assertThat(new CargoTipoMapper().toDomain(cargoTipoDto).getNombre()).isEqualTo("Profesor");

        ClaseDto claseDto = new ClaseDto();
        claseDto.setClaseId(2);
        claseDto.setNombre("Segunda");
        claseDto.setValorHora(new BigDecimal("222.50"));
        ClaseResumen clase = new ClaseMapper().toDomain(claseDto);
        assertThat(clase.getClaseId()).isEqualTo(2);
        assertThat(clase.getValorHora()).isEqualByComparingTo("222.50");

        CodigoDto codigoDto = new CodigoDto();
        codigoDto.setCodigoId(981);
        codigoDto.setNombre("Sueldo Básico");
        CodigoResumen codigo = new CodigoMapper().toDomain(codigoDto);
        assertThat(codigo.getCodigoId()).isEqualTo(981);
        assertThat(new CodigoMapper().toDomain(null)).isNull();
    }

    @Test
    void adaptadoresSimplesDeleganYListasNulasSonVacias() {
        when(facultadClient.findAll()).thenReturn(null);
        when(facultadClient.findAllFacultades()).thenReturn(List.of());
        when(facultadClient.findByFacultadId(2)).thenReturn(null);
        assertThat(facultadAdapter.findAll()).isEmpty();
        assertThat(facultadAdapter.findAllFacultades()).isEmpty();
        assertThat(facultadAdapter.findById(2)).isEmpty();

        when(geograficaClient.findAll()).thenReturn(null);
        when(geograficaClient.findAllByGeograficaIdIn(List.of(1, 2))).thenReturn(null);
        when(geograficaClient.findByGeograficaId(3)).thenReturn(null);
        assertThat(geograficaAdapter.findAll()).isEmpty();
        assertThat(geograficaAdapter.findAllByIds(List.of(1, 2))).isEmpty();
        assertThat(geograficaAdapter.findById(3)).isEmpty();

        when(dependenciaClient.findAll()).thenReturn(null);
        when(dependenciaClient.findByDependenciaId(4)).thenReturn(null);
        assertThat(dependenciaAdapter.findAll()).isEmpty();
        assertThat(dependenciaAdapter.findById(4)).isEmpty();

        when(categoriaClient.findAll()).thenReturn(null);
        assertThat(categoriaAdapter.findAll()).isEmpty();

        when(cargoTipoClient.findAll()).thenReturn(null);
        when(cargoTipoClient.findByCargoTipoId(1)).thenReturn(null);
        assertThat(cargoTipoAdapter.findAll()).isEmpty();
        assertThat(cargoTipoAdapter.findById(1)).isEmpty();

        when(claseClient.findAll()).thenReturn(null);
        assertThat(claseAdapter.findAll()).isEmpty();

        when(codigoClient.findByCodigoId(981)).thenReturn(null);
        when(codigoClient.findAllByPeriodo(2026, 9)).thenReturn(null);
        assertThat(codigoAdapter.findById(981)).isEmpty();
        assertThat(codigoAdapter.findAllByPeriodo(2026, 9)).isEmpty();
    }

    @Test
    void codigoAdapterDelegaPeriodoYFiltraNulos() {
        CodigoDto dto = new CodigoDto();
        dto.setCodigoId(981);
        dto.setNombre("Sueldo Básico");
        when(codigoClient.findAllByPeriodo(2026, 9)).thenReturn(java.util.Arrays.asList(dto, null));

        List<CodigoResumen> codigos = codigoAdapter.findAllByPeriodo(2026, 9);

        assertThat(codigos).singleElement().extracting(CodigoResumen::getCodigoId).isEqualTo(981);
        verify(codigoClient).findAllByPeriodo(2026, 9);
    }

    @Test
    void storageResuelveCargaYFallaSiNoExiste() throws Exception {
        Path base = Path.of("target/test-output/storage-test");
        MockEnvironment environment = new MockEnvironment().withProperty("path.files", base.toString());
        ReportStorageAdapter storage = new ReportStorageAdapter(environment);

        Path target = storage.resolve("reporte.pdf");
        Files.writeString(target, "contenido");

        ReportFile file = storage.load(target, "reporte.pdf", ReportFile.Format.PDF);

        assertThat(file.path()).isEqualTo(target);
        assertThat(file.displayName()).isEqualTo("reporte.pdf");
        assertThat(file.format()).isEqualTo(ReportFile.Format.PDF);
        assertThat(file.size()).isEqualTo("contenido".length());

        Path missing = storage.resolve("no-existe.pdf");
        assertThatThrownBy(() -> storage.load(missing, "no-existe.pdf", ReportFile.Format.PDF))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining("no-existe.pdf");
    }

    private PersonaDto personaDto() {
        PersonaDto dto = new PersonaDto();
        dto.setLegajoId(120L);
        dto.setApellido("Gómez");
        dto.setNombre("Ana");
        return dto;
    }
}
