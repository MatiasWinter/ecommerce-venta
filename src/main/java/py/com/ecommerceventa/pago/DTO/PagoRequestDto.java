package py.com.ecommerceventa.pago.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import py.com.ecommerceventa.pago.enums.TIPO_MEDIO_PAGO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PagoRequestDto {
    private Long idVenta;
    private TIPO_MEDIO_PAGO tipoMedioPago;
}
