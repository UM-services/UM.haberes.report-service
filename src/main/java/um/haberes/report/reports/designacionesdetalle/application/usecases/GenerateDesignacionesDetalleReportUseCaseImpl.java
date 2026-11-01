package um.haberes.report.reports.designacionesdetalle.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.designacionesdetalle.domain.model.DesignacionesDetalleContent;
import um.haberes.report.reports.designacionesdetalle.domain.model.DesignacionesDetalleContent.CursoFila;
import um.haberes.report.reports.designacionesdetalle.domain.model.DesignacionesDetalleContent.FusionFila;
import um.haberes.report.reports.designacionesdetalle.domain.model.DesignacionesDetalleContent.LiquidaFila;
import um.haberes.report.reports.designacionesdetalle.domain.model.DesignacionesDetalleContent.Periodo;
import um.haberes.report.reports.designacionesdetalle.domain.ports.in.GenerateDesignacionesDetalleReportUseCase;
import um.haberes.report.reports.designacionesdetalle.domain.ports.out.DesignacionTipoCatalogPort;
import um.haberes.report.reports.designacionesdetalle.domain.ports.out.DesignacionesDetallePdfRenderer;
import um.haberes.report.reports.designacionesdetalle.domain.ports.out.NovedadDiferenciaPort;
import um.haberes.report.shared.domain.model.CargoLiquidacionResumen;
import um.haberes.report.shared.domain.model.CargoTipoResumen;
import um.haberes.report.shared.domain.model.CategoriaResumen;
import um.haberes.report.shared.domain.model.CursoCargoResumen;
import um.haberes.report.shared.domain.model.CursoFusionResumen;
import um.haberes.report.shared.domain.model.DependenciaResumen;
import um.haberes.report.shared.domain.model.DesignacionTipoResumen;
import um.haberes.report.shared.domain.model.FacultadResumen;
import um.haberes.report.shared.domain.model.GeograficaResumen;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.CargoLiquidacionCatalogPort;
import um.haberes.report.shared.domain.ports.out.CargoTipoCatalogPort;
import um.haberes.report.shared.domain.ports.out.CategoriaCatalogPort;
import um.haberes.report.shared.domain.ports.out.CursoCargoCatalogPort;
import um.haberes.report.shared.domain.ports.out.CursoFusionCatalogPort;
import um.haberes.report.shared.domain.ports.out.DependenciaCatalogPort;
import um.haberes.report.shared.domain.ports.out.FacultadCatalogPort;
import um.haberes.report.shared.domain.ports.out.GeograficaCatalogPort;
import um.haberes.report.shared.domain.ports.out.PersonaCatalogPort;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateDesignacionesDetalleReportUseCaseImpl
        implements GenerateDesignacionesDetalleReportUseCase {

    static final String DISPLAY_NAME = "designaciones";
    private static final Integer CODIGO_DIFERENCIA = 27;
    private static final Locale LOCALE = Locale.forLanguageTag("es-AR");

    private final PersonaCatalogPort personaCatalogPort;
    private final CargoLiquidacionCatalogPort cargoLiquidacionCatalogPort;
    private final CursoCargoCatalogPort cursoCargoCatalogPort;
    private final CursoFusionCatalogPort cursoFusionCatalogPort;
    private final FacultadCatalogPort facultadCatalogPort;
    private final GeograficaCatalogPort geograficaCatalogPort;
    private final DependenciaCatalogPort dependenciaCatalogPort;
    private final CategoriaCatalogPort categoriaCatalogPort;
    private final CargoTipoCatalogPort cargoTipoCatalogPort;
    private final DesignacionTipoCatalogPort designacionTipoCatalogPort;
    private final NovedadDiferenciaPort novedadDiferenciaPort;
    private final DesignacionesDetallePdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Long legajoId, Integer anho, Integer mes) {
        Periodo anterior = periodoAnterior(anho, mes);
        PersonaResumen persona = personaCatalogPort.findByLegajoId(legajoId).orElse(null);
        String personaTexto = "Legajo " + (persona == null ? "" : text(persona.getLegajoId()))
                + (persona == null ? "" : " - " + persona.apellidoNombre());

        List<CargoLiquidacionResumen> liquidadas =
                cargoLiquidacionCatalogPort.findAllDocenteByLegajo(legajoId, anterior.anho(),
                        anterior.mes());
        List<CursoCargoResumen> cursos =
                cursoCargoCatalogPort.findAllByLegajo(legajoId, anho, mes);
        List<CursoFusionResumen> fusiones =
                cursoFusionCatalogPort.findAllByLegajoId(legajoId, anho, mes);

        Map<Integer, FacultadResumen> facultades = map(facultadCatalogPort.findAll(),
                FacultadResumen::getFacultadId);
        Map<Integer, GeograficaResumen> geograficas = map(geograficaCatalogPort.findAll(),
                GeograficaResumen::getGeograficaId);
        Map<Integer, DependenciaResumen> dependencias = map(dependenciaCatalogPort.findAll(),
                DependenciaResumen::getDependenciaId);
        Map<Integer, CategoriaResumen> categorias = map(categoriaCatalogPort.findAll(),
                CategoriaResumen::getCategoriaId);
        Map<Integer, CargoTipoResumen> cargoTipos = map(cargoTipoCatalogPort.findAll(),
                CargoTipoResumen::getCargoTipoId);
        Map<Integer, DesignacionTipoResumen> designacionTipos = map(
                designacionTipoCatalogPort.findAll(), DesignacionTipoResumen::getDesignacionTipoId);

        BigDecimal totalLiquidadas = BigDecimal.ZERO;
        List<LiquidaFila> liquidadasFilas = new java.util.ArrayList<>();
        for (CargoLiquidacionResumen cargo : liquidadas) {
            DependenciaResumen dependencia = dependencias.get(cargo.getDependenciaId());
            BigDecimal basico = basico(cargo.getCategoria(), cargo.getCategoriaBasico());
            liquidadasFilas.add(new LiquidaFila(
                    nombreFacultad(facultades, dependencia == null ? null : dependencia.getFacultadId()),
                    nombreGeografica(geograficas, dependencia == null ? null : dependencia.getGeograficaId()),
                    nombreCategoria(cargo, categorias), monto(basico)));
            totalLiquidadas = totalLiquidadas.add(basico);
        }

        BigDecimal totalCursos = BigDecimal.ZERO;
        List<CursoFila> cursosFilas = new java.util.ArrayList<>();
        for (CursoCargoResumen cargo : cursos) {
            BigDecimal basico = basico(cargo.getCategoria(), null);
            cursosFilas.add(new CursoFila(
                    nombreFacultad(facultades, cargo.getCurso() == null
                            ? null : cargo.getCurso().getFacultadId()),
                    nombreGeografica(geograficas, cargo.getCurso() == null
                            ? null : cargo.getCurso().getGeograficaId()),
                    cargo.getCurso() == null ? "" : text(cargo.getCurso().getNombre()),
                    numero(cargo.getHorasSemanales()),
                    nombreCargoTipo(cargoTipos, cargo.getCargoTipoId()),
                    nombreDesignacionTipo(designacionTipos, cargo.getDesignacionTipoId()),
                    monto(basico), marca(cargo.getDesarraigo() != 0),
                    marca(cargo.getCurso() != null && cargo.getCurso().getAnual() != 0),
                    marca(cargo.getCurso() != null && cargo.getCurso().getSemestre1() != 0),
                    marca(cargo.getCurso() != null && cargo.getCurso().getSemestre2() != 0)));
            totalCursos = totalCursos.add(basico);
        }

        BigDecimal totalFusion = BigDecimal.ZERO;
        List<FusionFila> fusionesFilas = new java.util.ArrayList<>();
        for (CursoFusionResumen fusion : fusiones) {
            CategoriaResumen categoria = categorias.get(fusion.getCategoriaId());
            BigDecimal basico = categoria != null && categoria.getBasico() != null
                    ? categoria.getBasico() : BigDecimal.ZERO;
            fusionesFilas.add(new FusionFila(
                    nombreFacultad(facultades, fusion.getFacultadId()),
                    nombreGeografica(geograficas, fusion.getGeograficaId()),
                    nombreCargoTipo(cargoTipos, fusion.getCargoTipoId()),
                    nombreDesignacionTipo(designacionTipos, fusion.getDesignacionTipoId()),
                    categoria != null ? text(categoria.getNombre()) : "", monto(basico)));
            totalFusion = totalFusion.add(basico);
        }

        BigDecimal diferencia = diferencia(legajoId, anho, mes);

        Path target = storage.resolve(fileName(legajoId, anho, mes));
        renderer.render(new DesignacionesDetalleContent(legajoId, personaTexto, anho, mes, anterior,
                liquidadasFilas, monto(totalLiquidadas), cursosFilas, monto(totalCursos),
                fusionesFilas, monto(totalFusion), monto(diferencia)), target);
        return storage.load(target, displayName(legajoId, anho, mes), ReportFile.Format.PDF);
    }

    public String displayName(Long legajoId, Integer anho, Integer mes) {
        return "designaciones-" + legajoId + "-" + mes + "-" + anho + ".pdf";
    }

    private BigDecimal diferencia(Long legajoId, Integer anho, Integer mes) {
        return novedadDiferenciaPort
                .findImporte(legajoId, anho, mes, CODIGO_DIFERENCIA, "null")
                .orElse(BigDecimal.ZERO);
    }

    private <T> Map<Integer, T> map(List<T> items, Function<T, Integer> key) {
        return items.stream()
                .filter(Objects::nonNull)
                .filter(item -> key.apply(item) != null)
                .collect(Collectors.toMap(key, item -> item, (a, b) -> a));
    }

    private Periodo periodoAnterior(Integer anho, Integer mes) {
        return mes == 1 ? new Periodo(anho - 1, 12) : new Periodo(anho, mes - 1);
    }

    private String nombreFacultad(Map<Integer, FacultadResumen> facultades, Integer id) {
        FacultadResumen facultad = facultades.get(id);
        return facultad != null ? text(facultad.getNombre()) : "";
    }

    private String nombreGeografica(Map<Integer, GeograficaResumen> geograficas, Integer id) {
        GeograficaResumen geografica = geograficas.get(id);
        return geografica != null ? text(geografica.getNombre()) : "";
    }

    private String nombreCategoria(CargoLiquidacionResumen cargo,
                                   Map<Integer, CategoriaResumen> categorias) {
        if (cargo.getCategoria() != null && cargo.getCategoria().getNombre() != null
                && !cargo.getCategoria().getNombre().isBlank()) {
            return cargo.getCategoria().getNombre();
        }
        CategoriaResumen categoria = categorias.get(cargo.getCategoriaId());
        if (categoria != null && categoria.getNombre() != null) {
            return categoria.getNombre();
        }
        return cargo.getCategoriaNombre() != null ? cargo.getCategoriaNombre() : "";
    }

    private String nombreCargoTipo(Map<Integer, CargoTipoResumen> cargoTipos, Integer id) {
        CargoTipoResumen cargoTipo = cargoTipos.get(id);
        return cargoTipo != null ? text(cargoTipo.getNombre()) : "";
    }

    private String nombreDesignacionTipo(Map<Integer, DesignacionTipoResumen> designacionTipos,
                                         Integer id) {
        DesignacionTipoResumen tipo = designacionTipos.get(id);
        return tipo != null ? text(tipo.getNombre()) : "";
    }

    private BigDecimal basico(CategoriaResumen categoria, BigDecimal alternativo) {
        if (categoria != null && categoria.getBasico() != null) {
            return categoria.getBasico();
        }
        return alternativo != null ? alternativo : BigDecimal.ZERO;
    }

    private String numero(BigDecimal valor) {
        return valor == null ? "" : valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String monto(BigDecimal valor) {
        BigDecimal seguro = valor == null ? BigDecimal.ZERO : valor.setScale(2, RoundingMode.HALF_UP);
        return String.format(LOCALE, "%,.2f", seguro);
    }

    private String marca(boolean activo) {
        return activo ? "X" : "";
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }

    private String fileName(Long legajoId, Integer anho, Integer mes) {
        return "designaciones-detalle." + legajoId + "." + anho + "." + mes + ".pdf";
    }
}
