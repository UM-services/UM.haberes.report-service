package um.haberes.report.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.AnotadorClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.AntiguedadClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.BonoImpresionClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CargoClaseClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CargoClaseDetalleClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CargoClasePeriodoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CargoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CargoLiquidacionClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CargoTipoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CategoriaClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.ClaseClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CodigoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CodigoGrupoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.ContactoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.ControlClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CursoCargoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CursoCargoNovedadClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CursoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CursoFusionClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.DependenciaClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.DesignacionTipoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.DesignacionToolClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.DocenteDesignacionClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.FacultadClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.GeograficaClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.ItemClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LegajoBancoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LegajoControlClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LetraClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LiquidacionAdicionalClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LiquidacionClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LiquidacionEtecClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.NovedadClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.PersonaClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.TotalItemClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.TotalMensualClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.TotalNovedadClient;
import um.haberes.report.shared.infrastructure.client.tesoreria.core.client.CursoCargoContratadoClient;

/**
 * Base de la suite de caracterizacion.
 *
 * <p>Levanta el contexto real y reemplaza <b>solo</b> los adaptadores salientes por mocks:
 * los 39 {@code @FeignClient} y el {@link JavaMailSender}. De este modo se ejercita el
 * controller, la orquestacion, el render y la escritura a disco tal como en produccion.
 *
 * <p>Los tests derivados no deben registrar mas beans: un unico contexto cacheado mantiene
 * la suite rapida.
 */
@SpringBootTest
@ActiveProfiles("test")
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
public abstract class AbstractReportIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @org.junit.jupiter.api.BeforeAll
    static void prepararEntorno() throws java.io.IOException {
        java.nio.file.Files.createDirectories(java.nio.file.Path.of("target/test-output"));
        // Los reportes cargan las imagenes por ruta relativa al basedir del modulo.
        org.assertj.core.api.Assertions.assertThat(java.nio.file.Path.of("marca_um.png")).exists();
        org.assertj.core.api.Assertions.assertThat(java.nio.file.Path.of("firma_new.png")).exists();
    }

    @MockitoBean
    protected AnotadorClient anotadorClient;
    @MockitoBean
    protected AntiguedadClient antiguedadClient;
    @MockitoBean
    protected BonoImpresionClient bonoImpresionClient;
    @MockitoBean
    protected CargoClaseClient cargoClaseClient;
    @MockitoBean
    protected CargoClaseDetalleClient cargoClaseDetalleClient;
    @MockitoBean
    protected CargoClasePeriodoClient cargoClasePeriodoClient;
    @MockitoBean
    protected CargoClient cargoClient;
    @MockitoBean
    protected CargoLiquidacionClient cargoLiquidacionClient;
    @MockitoBean
    protected CargoTipoClient cargoTipoClient;
    @MockitoBean
    protected CategoriaClient categoriaClient;
    @MockitoBean
    protected ClaseClient claseClient;
    @MockitoBean
    protected CodigoClient codigoClient;
    @MockitoBean
    protected CodigoGrupoClient codigoGrupoClient;
    @MockitoBean
    protected ContactoClient contactoClient;
    @MockitoBean
    protected ControlClient controlClient;
    @MockitoBean
    protected CursoCargoClient cursoCargoClient;
    @MockitoBean
    protected CursoCargoNovedadClient cursoCargoNovedadClient;
    @MockitoBean
    protected CursoClient cursoClient;
    @MockitoBean
    protected CursoFusionClient cursoFusionClient;
    @MockitoBean
    protected DependenciaClient dependenciaClient;
    @MockitoBean
    protected DesignacionTipoClient designacionTipoClient;
    @MockitoBean
    protected DesignacionToolClient designacionToolClient;
    @MockitoBean
    protected DocenteDesignacionClient docenteDesignacionClient;
    @MockitoBean
    protected FacultadClient facultadClient;
    @MockitoBean
    protected GeograficaClient geograficaClient;
    @MockitoBean
    protected ItemClient itemClient;
    @MockitoBean
    protected LegajoBancoClient legajoBancoClient;
    @MockitoBean
    protected LegajoControlClient legajoControlClient;
    @MockitoBean
    protected LetraClient letraClient;
    @MockitoBean
    protected LiquidacionAdicionalClient liquidacionAdicionalClient;
    @MockitoBean
    protected LiquidacionClient liquidacionClient;
    @MockitoBean
    protected LiquidacionEtecClient liquidacionEtecClient;
    @MockitoBean
    protected NovedadClient novedadClient;
    @MockitoBean
    protected PersonaClient personaClient;
    @MockitoBean
    protected TotalItemClient totalItemClient;
    @MockitoBean
    protected TotalMensualClient totalMensualClient;
    @MockitoBean
    protected TotalNovedadClient totalNovedadClient;
    @MockitoBean
    protected CursoCargoContratadoClient cursoCargoContratadoClient;
    @MockitoBean
    protected um.haberes.report.shared.infrastructure.client.tesoreria.core.client.PersonaClient personaTesoreriaClient;
    @MockitoBean
    protected JavaMailSender javaMailSender;
}
