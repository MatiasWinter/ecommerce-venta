package py.com.ecommerceventa.pago.service;

import py.com.ecommerceventa.pago.DTO.PagoRequestDto;

public interface PagoService {
    void confirmarPago(PagoRequestDto pagoRequestDto);
}
