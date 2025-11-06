package py.com.ecommerceventa.venta.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VentaRequestDto {
    private long idCliente;
    private List<VentaDetalleRequestDto> compraDetalle ;
}
