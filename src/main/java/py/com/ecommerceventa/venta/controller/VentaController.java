package py.com.ecommerceventa.venta.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.com.ecommerceventa.venta.DTO.VentaRequestDto;
import py.com.ecommerceventa.venta.DTO.VentaResponseDto;
import py.com.ecommerceventa.venta.service.VentaService;

@RestController
@RequestMapping("/venta")
@AllArgsConstructor
public class VentaController {
    private final VentaService ventaService;

    @PostMapping("/sell")
    public VentaResponseDto sell(@RequestBody VentaRequestDto ventaRequestDto) {
        return ventaService.vender(ventaRequestDto);
    }
}
