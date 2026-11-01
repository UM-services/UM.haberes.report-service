package um.haberes.report.reports.bono.domain.ports.out;

import um.haberes.report.reports.bono.domain.model.BonosDependenciaContent;

import java.util.List;

/** Datos de la grilla del bono por dependencia (una sola lectura al core). */
public interface BonoDependenciaDataPort {

    List<BonosDependenciaContent.Fila> legajos(Integer anho, Integer mes, Integer dependenciaId,
                                               String salida);
}
