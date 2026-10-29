package um.haberes.report.reports.cursocargonovedad.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cursocargonovedad.domain.model.CursocargoNovedadContent;
import um.haberes.report.reports.cursocargonovedad.domain.model.CursocargoNovedadContent.Fila;
import um.haberes.report.reports.cursocargonovedad.domain.ports.in.GenerateCursocargoNovedadReportUseCase;
import um.haberes.report.reports.cursocargonovedad.domain.ports.out.CursocargoNovedadPdfRenderer;
import um.haberes.report.shared.domain.model.CursoCargoNovedadResumen;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.CursoCargoNovedadCatalogPort;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenerateCursocargoNovedadReportUseCaseImpl
        implements GenerateCursocargoNovedadReportUseCase {

    private static final String DISPLAY_NAME = "novedades-cursos";

    private final CursoCargoNovedadCatalogPort novedadCatalogPort;
    private final CursocargoNovedadPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes, boolean soloPendientes) {
        List<CursoCargoNovedadResumen> novedades = soloPendientes
                ? novedadCatalogPort.findAllPendientes(anho, mes)
                : novedadCatalogPort.findAllByPeriodo(anho, mes);
        List<Fila> filas = novedades.stream().map(this::toFila).toList();

        Path target = storage.resolve(fileName(anho, mes, soloPendientes));
        renderer.render(new CursocargoNovedadContent(anho, mes, soloPendientes, filas), target);
        return storage.load(target, displayName(anho, mes, soloPendientes), ReportFile.Format.PDF);
    }

    public String displayName(Integer anho, Integer mes, boolean soloPendientes) {
        return DISPLAY_NAME + (soloPendientes ? "-pendientes" : "") + "-" + mes + "-" + anho + ".pdf";
    }

    private Fila toFila(CursoCargoNovedadResumen novedad) {
        String facultad = novedad.getCurso() != null && novedad.getCurso().getFacultad() != null
                ? text(novedad.getCurso().getFacultad().getNombre()) : "";
        String sede = novedad.getCurso() != null && novedad.getCurso().getGeografica() != null
                ? text(novedad.getCurso().getGeografica().getNombre()) : "";
        return new Fila(facultad, sede, text(novedad.getLegajoId()),
                novedad.getPersona() == null ? "" : text(novedad.getPersona().apellidoNombre()),
                novedad.getCurso() == null ? "" : text(novedad.getCurso().getNombre()),
                novedad.getCargoTipo() == null ? "" : text(novedad.getCargoTipo().getNombre()),
                horas(novedad.getHorasSemanales()),
                novedad.getDesarraigo() != 0 ? "X" : "",
                tipo(novedad), estado(novedad), text(novedad.getSolicitud()));
    }

    private String tipo(CursoCargoNovedadResumen novedad) {
        if (novedad.getAlta() == 1 && novedad.getCambio() == 1) {
            return "Alta + cambio";
        }
        if (novedad.getAlta() == 1) {
            return "Alta";
        }
        if (novedad.getBaja() == 1) {
            return "Baja";
        }
        if (novedad.getCambio() == 1) {
            return "Cambio";
        }
        return "";
    }

    private String estado(CursoCargoNovedadResumen novedad) {
        if (novedad.getAutorizado() == 1) {
            return "Autorizada";
        }
        if (novedad.getRechazado() == 1) {
            return "Rechazada";
        }
        return "Pendiente";
    }

    private String horas(BigDecimal horas) {
        return horas == null ? "" : horas.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }

    private String fileName(Integer anho, Integer mes, boolean soloPendientes) {
        return "novedades-cursos." + (soloPendientes ? "pendientes." : "") + anho + "." + mes + ".pdf";
    }
}
