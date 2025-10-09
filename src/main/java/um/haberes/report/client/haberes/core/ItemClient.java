package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.ItemDto;

import java.util.List;

@FeignClient(name = "haberes-core-service", contextId = "itemClient", path = "/api/haberes/core/item")
public interface ItemClient {

    @GetMapping("/legajo/{legajoId}/{anho}/{mes}")
    List<ItemDto> findAllByLegajo(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/codigo/{codigoId}/{anho}/{mes}")
    List<ItemDto> findAllByCodigoId(@PathVariable Integer codigoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/periodo/{anho}/{mes}/{limit}")
    List<ItemDto> findAllByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer limit);

    @GetMapping("/periodolegajo/{anho}/{mes}/{legajoId}/{limit}")
    List<ItemDto> findAllByPeriodoAndLegajo(@PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Long legajoId, @PathVariable Integer limit);

    @GetMapping("/unique/{legajoId}/{anho}/{mes}/{codigoId}")
    ItemDto findByUnique(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer codigoId);

    @PostMapping("/")
    ItemDto add(@RequestBody ItemDto item);

    @PutMapping("/{itemId}")
    ItemDto update(@RequestBody ItemDto item, @PathVariable Long itemId);

    @PutMapping("/")
    List<ItemDto> saveAll(@RequestBody List<ItemDto> items);

    @DeleteMapping("/periodo/{anho}/{mes}")
    void deleteByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);

    @DeleteMapping("/legajo/{legajoId}/{anho}/{mes}/deleteallbyzero")
    void deleteAllByZero(@PathVariable("legajoId") Long legajoId,
                                         @PathVariable("anho") Integer anho,
                                         @PathVariable("mes") Integer mes);

    @GetMapping("/onlyETEC/{legajoId}/{anho}/{mes}")
    Boolean onlyETEC(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

}
