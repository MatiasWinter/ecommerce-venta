package py.com.ecommerceventa.venta.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class VentaResponseDto {
    private Long idVenta;
    private BigDecimal montoTotal;
}
