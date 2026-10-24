package um.haberes.report.reports.fusiondocente.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.fusiondocente.domain.model.FusionDocenteContent;
import um.haberes.report.reports.fusiondocente.domain.model.FusionDocenteContent.Fila;
import um.haberes.report.reports.fusiondocente.domain.ports.in.GenerateFusionDocenteReportUseCase;
import um.haberes.report.reports.fusiondocente.domain.ports.out.FusionDocentePdfRenderer;
import um.haberes.report.shared.domain.model.CategoriaResumen;
import um.haberes.report.shared.domain.model.CursoFusionResumen;
import um.haberes.report.shared.domain.model.FacultadResumen;
import um.haberes.report.shared.domain.model.GeograficaResumen;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.CategoriaCatalogPort;
import um.haberes.report.shared.domain.ports.out.CursoFusionCatalogPort;
import um.haberes.report.shared.domain.ports.out.FacultadCatalogPort;
import um.haberes.report.shared.domain.ports.out.GeograficaCatalogPort;
import um.haberes.report.shared.domain.ports.out.PersonaCatalogPort;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateFusionDocenteReportUseCaseImpl implements GenerateFusionDocenteReportUseCase {

    static final String DISPLAY_NAME = "fusion.docente.pdf";
    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(new Locale("en", "ES"));

    private final PersonaCatalogPort personaCatalogPort;
    private final CategoriaCatalogPort categoriaCatalogPort;
    private final FacultadCatalogPort facultadCatalogPort;
    private final GeograficaCatalogPort geograficaCatalogPort;
    private final CursoFusionCatalogPort cursoFusionCatalogPort;
    private final FusionDocentePdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        List<PersonaResumen> personas = personaCatalogPort.findAll().stream()
                .filter(Objects::nonNull).toList();
        Map<Integer, FacultadResumen> facultades = facultadCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(facultad -> facultad.getFacultadId() != null)
                .collect(Collectors.toMap(FacultadResumen::getFacultadId, f -> f, (a, b) -> a));
        Map<Integer, GeograficaResumen> geograficas = geograficaCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(geografica -> geografica.getGeograficaId() != null)
                .collect(Collectors.toMap(GeograficaResumen::getGeograficaId, g -> g, (a, b) -> a));
        Map<Integer, CategoriaResumen> categorias = categoriaCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(categoria -> categoria.getCategoriaId() != null)
                .collect(Collectors.toMap(CategoriaResumen::getCategoriaId, c -> c, (a, b) -> a));

        List<Fila> filas = new ArrayList<>();
        for (PersonaResumen persona : personas) {
            if (persona.getLegajoId() == null) {
                continue;
            }
            List<CursoFusionResumen> fusiones = cursoFusionCatalogPort
                    .findAllByLegajoId(persona.getLegajoId(), anho, mes).stream()
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(CursoFusionResumen::getLegajoId,
                            Comparator.nullsLast(Long::compareTo)))
                    .toList();
            if (fusiones.isEmpty()) {
                log.warn("No se encontraron cursos fusionados para el legajo {} en el periodo {}-{}",
                        persona.getLegajoId(), anho, mes);
                continue;
            }
            for (CursoFusionResumen fusion : fusiones) {
                FacultadResumen facultad = facultades.get(fusion.getFacultadId());
                GeograficaResumen geografica = geograficas.get(fusion.getGeograficaId());
                CategoriaResumen categoria = categorias.get(fusion.getCategoriaId());
                if (facultad == null || geografica == null || categoria == null) {
                    log.warn("Datos incompletos para la fusion del legajo {}",
                            persona.getLegajoId());
                    continue;
                }
                filas.add(new Fila(text(fusion.getLegajoId()), persona.apellidoNombre(),
                        text(facultad.getNombre()), text(geografica.getNombre()),
                        text(categoria.getNombre()),
                        categoria.getBasico() == null ? "0" : FORMAT.format(categoria.getBasico())));
            }
        }

        Path target = storage.resolve(fileName(anho, mes));
        renderer.render(new FusionDocenteContent(anho, mes, filas), target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }

    private String fileName(Integer anho, Integer mes) {
        return "fusion.docente.." + anho + "." + mes + ".pdf";
    }
}
