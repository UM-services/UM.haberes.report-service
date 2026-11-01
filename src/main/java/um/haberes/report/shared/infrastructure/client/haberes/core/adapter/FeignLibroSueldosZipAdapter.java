package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.ports.out.LibroSueldosZipPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LibroSueldoClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FeignLibroSueldosZipAdapter implements LibroSueldosZipPort {

    private final LibroSueldoClient libroSueldoClient;

    @Override
    public byte[] generate(Integer anho, Integer mes, List<Long> legajoIds) {
        byte[] zip = libroSueldoClient.generate(anho, mes, legajoIds);
        return zip == null ? new byte[0] : zip;
    }
}
