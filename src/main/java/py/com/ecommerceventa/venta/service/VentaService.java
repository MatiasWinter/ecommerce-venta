package py.com.ecommerceventa.venta.service;

import py.com.ecommerceventa.venta.DTO.VentaRequestDto;
import py.com.ecommerceventa.venta.DTO.VentaResponseDto;

public interface VentaService {
    VentaResponseDto vender(VentaRequestDto ventaRequestDto);
}
