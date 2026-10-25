package um.haberes.report.shared.domain.ports.out;

import java.util.List;

/** Obtiene el ZIP del Libro de Sueldos Digital que genera haberes-core. */
public interface LibroSueldosZipPort {

    byte[] generate(Integer anho, Integer mes, List<Long> legajoIds);
}
