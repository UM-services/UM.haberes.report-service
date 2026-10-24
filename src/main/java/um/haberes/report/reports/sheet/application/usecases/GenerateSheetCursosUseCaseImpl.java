package um.haberes.report.reports.sheet.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.sheet.domain.model.SheetCursosContent;
import um.haberes.report.reports.sheet.domain.model.SheetCursosContent.Fila;
import um.haberes.report.reports.sheet.domain.ports.in.GenerateSheetCursosUseCase;
import um.haberes.report.reports.sheet.domain.ports.out.SheetCursosXlsxRenderer;
import um.haberes.report.shared.domain.model.CargoTipoResumen;
import um.haberes.report.shared.domain.model.CursoCargoResumen;
import um.haberes.report.shared.domain.model.CursoResumen;
import um.haberes.report.shared.domain.model.FacultadResumen;
import um.haberes.report.shared.domain.model.GeograficaResumen;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.CargoTipoCatalogPort;
import um.haberes.report.shared.domain.ports.out.CursoCargoCatalogPort;
import um.haberes.report.shared.domain.ports.out.CursoCatalogPort;
import um.haberes.report.shared.domain.ports.out.FacultadCatalogPort;
import um.haberes.report.shared.domain.ports.out.GeograficaCatalogPort;
import um.haberes.report.shared.domain.ports.out.PersonaCatalogPort;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateSheetCursosUseCaseImpl implements GenerateSheetCursosUseCase {

    static final String DISPLAY_NAME = "cursos.xlsx";

    private final FacultadCatalogPort facultadCatalogPort;
    private final GeograficaCatalogPort geograficaCatalogPort;
    private final CursoCatalogPort cursoCatalogPort;
    private final CargoTipoCatalogPort cargoTipoCatalogPort;
    private final CursoCargoCatalogPort cursoCargoCatalogPort;
    private final PersonaCatalogPort personaCatalogPort;
    private final SheetCursosXlsxRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        Map<Integer, FacultadResumen> facultades = facultadCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(facultad -> facultad.getFacultadId() != null)
                .collect(Collectors.toMap(FacultadResumen::getFacultadId, f -> f, (a, b) -> a));
        Map<Integer, GeograficaResumen> geograficas = geograficaCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(geografica -> geografica.getGeograficaId() != null)
                .collect(Collectors.toMap(GeograficaResumen::getGeograficaId, g -> g, (a, b) -> a));
        Map<Long, CursoResumen> cursos = cursoCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(curso -> curso.getCursoId() != null)
                .collect(Collectors.toMap(CursoResumen::getCursoId, c -> c, (a, b) -> a));
        Map<Integer, CargoTipoResumen> cargos = cargoTipoCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(cargo -> cargo.getCargoTipoId() != null)
                .collect(Collectors.toMap(CargoTipoResumen::getCargoTipoId, c -> c, (a, b) -> a));

        List<Long> legajoIds = cursoCargoCatalogPort.findAllByPeriodo(anho, mes).stream()
                .filter(Objects::nonNull)
                .map(CursoCargoResumen::getLegajoId)
                .filter(Objects::nonNull)
                .toList();

        List<Fila> filas = new ArrayList<>();
        for (PersonaResumen persona : personaCatalogPort.findAllByLegajos(legajoIds)) {
            if (persona == null || persona.getLegajoId() == null) {
                continue;
            }
            for (CursoCargoResumen cursoCargo : cursoCargoCatalogPort
                    .findAllByLegajo(persona.getLegajoId(), anho, mes)) {
                if (cursoCargo == null) {
                    continue;
                }
                CursoResumen curso = cursos.get(cursoCargo.getCursoId());
                FacultadResumen facultad = curso == null
                        ? null : facultades.get(curso.getFacultadId());
                GeograficaResumen geografica = curso == null
                        ? null : geograficas.get(curso.getGeograficaId());
                CargoTipoResumen cargoTipo = cargos.get(cursoCargo.getCargoTipoId());
                if (curso == null || facultad == null || geografica == null || cargoTipo == null) {
                    log.warn("Datos incompletos para el legajo {}: se emite la fila con celdas vacias",
                            persona.getLegajoId());
                }
                filas.add(new Fila(persona.getLegajoId(), persona.apellidoNombre(),
                        mes + "/" + anho,
                        curso == null ? null : curso.getFacultadId(),
                        facultad == null ? null : facultad.getNombre(),
                        curso == null ? null : curso.getGeograficaId(),
                        geografica == null ? null : geografica.getNombre(),
                        cursoCargo.getCursoId(),
                        curso == null ? null : curso.getNombre(),
                        cursoCargo.getCargoTipoId(),
                        cargoTipo == null ? null : cargoTipo.getNombre(),
                        cursoCargo.getHorasSemanales(), dictado(curso)));
            }
        }

        Path target = storage.resolve(fileName(anho, mes));
        renderer.render(new SheetCursosContent(anho, mes, filas), target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.XLSX);
    }

    private String dictado(CursoResumen curso) {
        if (curso == null) {
            return "";
        }
        String dictado = "";
        if (curso.getAnual() == 1) {
            dictado = "Anual";
        }
        if (curso.getSemestre1() == 1) {
            dictado = "1er Semestre";
        }
        if (curso.getSemestre2() == 1) {
            dictado = "2do Semestre";
        }
        return dictado;
    }

    private String fileName(Integer anho, Integer mes) {
        return "cursos" + anho + String.format("%02d", mes) + ".xlsx";
    }
}
