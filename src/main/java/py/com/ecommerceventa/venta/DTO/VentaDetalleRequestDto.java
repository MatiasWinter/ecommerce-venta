package py.com.ecommerceventa.venta.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaDetalleRequestDto {
    private Long idProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}
