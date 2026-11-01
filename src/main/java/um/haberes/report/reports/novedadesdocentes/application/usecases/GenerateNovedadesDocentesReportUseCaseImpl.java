package um.haberes.report.reports.novedadesdocentes.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.novedadesdocentes.domain.model.NovedadesDocentesContent;
import um.haberes.report.reports.novedadesdocentes.domain.model.NovedadesDocentesContent.Novedad;
import um.haberes.report.reports.novedadesdocentes.domain.model.NovedadesDocentesContent.Seccion;
import um.haberes.report.reports.novedadesdocentes.domain.model.NovedadesDocentesContent.Sede;
import um.haberes.report.reports.novedadesdocentes.domain.ports.in.GenerateNovedadesDocentesReportUseCase;
import um.haberes.report.reports.novedadesdocentes.domain.ports.out.NovedadesDocentesPdfRenderer;
import um.haberes.report.shared.domain.model.CursoCargoNovedadResumen;
import um.haberes.report.shared.domain.model.FacultadResumen;
import um.haberes.report.shared.domain.model.GeograficaResumen;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.CursoCargoNovedadCatalogPort;
import um.haberes.report.shared.domain.ports.out.FacultadCatalogPort;
import um.haberes.report.shared.domain.ports.out.GeograficaCatalogPort;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateNovedadesDocentesReportUseCaseImpl
        implements GenerateNovedadesDocentesReportUseCase {

    static final String DISPLAY_NAME = "novedadesDocentes.pdf";

    private final FacultadCatalogPort facultadCatalogPort;
    private final GeograficaCatalogPort geograficaCatalogPort;
    private final CursoCargoNovedadCatalogPort novedadCatalogPort;
    private final NovedadesDocentesPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer facultadId, Integer anho, Integer mes) {
        String facultad = facultadCatalogPort.findById(facultadId)
                .map(FacultadResumen::getNombre)
                .orElse(null);
        if (facultad == null) {
            log.warn("No se encontro la facultad {}: el encabezado se emite sin nombre", facultadId);
        }

        List<Integer> geograficaIds = novedadCatalogPort.findAllByFacultad(facultadId, anho, mes)
                .stream()
                .filter(Objects::nonNull)
                .map(CursoCargoNovedadResumen::getCurso)
                .filter(Objects::nonNull)
                .map(curso -> curso.getGeograficaId())
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<Sede> sedes = new ArrayList<>();
        for (GeograficaResumen geografica : geograficaCatalogPort.findAllByIds(geograficaIds)) {
            if (geografica == null) {
                continue;
            }
            List<Seccion> secciones = new ArrayList<>();
            addSeccion(secciones, "ALTA", novedadCatalogPort
                    .findAllByFacultadAndGeograficaAndAlta(facultadId, geografica.getGeograficaId(),
                            anho, mes));
            addSeccion(secciones, "CAMBIO", novedadCatalogPort
                    .findAllByFacultadAndGeograficaAndCambio(facultadId,
                            geografica.getGeograficaId(), anho, mes));
            addSeccion(secciones, "BAJA", novedadCatalogPort
                    .findAllByFacultadAndGeograficaAndBaja(facultadId, geografica.getGeograficaId(),
                            anho, mes));
            sedes.add(new Sede(text(geografica.getNombre()), secciones));
        }

        Path target = storage.resolve(fileName(facultadId, anho, mes));
        renderer.render(new NovedadesDocentesContent(anho, mes, facultad, sedes), target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private void addSeccion(List<Seccion> secciones, String titulo,
                            List<CursoCargoNovedadResumen> novedades) {
        if (novedades.isEmpty()) {
            return;
        }
        secciones.add(new Seccion(titulo, novedades.stream()
                .filter(Objects::nonNull)
                .map(this::toNovedad)
                .toList()));
    }

    private Novedad toNovedad(CursoCargoNovedadResumen novedad) {
        String curso = novedad.getCurso() == null ? "" : text(novedad.getCurso().getNombre());
        if (novedad.getCurso() == null) {
            log.warn("Novedad sin curso para el legajo {}", novedad.getLegajoId());
        }
        return new Novedad(curso,
                novedad.getCargoTipo() == null ? "" : text(novedad.getCargoTipo().getNombre()),
                novedad.getPersona() == null ? "" : text(novedad.getPersona().apellidoNombre()),
                text(novedad.getHorasSemanales()),
                novedad.getDesarraigo() == 1 ? "*" : "",
                novedad.getAutorizado() == 1 ? "*" : "",
                novedad.getRechazado() == 1 ? "*" : "",
                text(novedad.getSolicitud()), text(novedad.getRespuesta()));
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }

    private String fileName(Integer facultadId, Integer anho, Integer mes) {
        return "novedades." + facultadId + "." + anho + "." + mes + ".pdf";
    }
}
