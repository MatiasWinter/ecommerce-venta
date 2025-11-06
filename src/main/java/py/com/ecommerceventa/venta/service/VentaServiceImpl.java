package py.com.ecommerceventa.venta.service;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import py.com.ecommerceventa.producto.entity.ProductoEntity;
import py.com.ecommerceventa.producto.repository.ProductoRepository;
import py.com.ecommerceventa.venta.DTO.VentaDetalleRequestDto;
import py.com.ecommerceventa.venta.DTO.VentaRequestDto;
import py.com.ecommerceventa.venta.DTO.VentaResponseDto;
import py.com.ecommerceventa.venta.entity.VentaDetalleEntity;
import py.com.ecommerceventa.venta.entity.VentaEntity;
import py.com.ecommerceventa.venta.repository.VentaDetalleRepository;
import py.com.ecommerceventa.venta.repository.VentaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class VentaServiceImpl implements VentaService{
    VentaRepository ventaRepository;
    VentaDetalleRepository ventaDetalleRepository;
    ProductoRepository productoRepository;

    @Override
    public VentaResponseDto vender(VentaRequestDto ventaRequestDto) {
        BigDecimal totalCalculado = BigDecimal.ZERO;
        BigDecimal subtotal;

        log.info("Validamos el stock de los productos a vender, y procedemos a descontar");
        descontarStock(ventaRequestDto);

        log.info("Procedemos a calcular el total a cobrar");
        for (VentaDetalleRequestDto detalle : ventaRequestDto.getCompraDetalle()) {
             subtotal = detalle.getPrecioUnitario()
                    .multiply(new BigDecimal(detalle.getCantidad()));
            totalCalculado = totalCalculado.add(subtotal);
        }

        log.info("Procedemos a guardar la venta para el cliente con id {} y con un total de {} ", ventaRequestDto.getIdCliente(),totalCalculado);
        VentaEntity venta = new VentaEntity();
        venta.setIdCliente(ventaRequestDto.getIdCliente());
        venta.setTotal(totalCalculado.setScale(2, RoundingMode.HALF_UP));
        VentaEntity ventaGuardada = ventaRepository.save(venta);
        VentaResponseDto ventaResponseDto = new VentaResponseDto(ventaGuardada.getIdVenta(),ventaGuardada.getTotal());

        log.info("Venta guardada con exito, procedemos a guardar el detalle de la venta con id {}", ventaGuardada.getIdVenta());
        crearVentaDetalle(ventaRequestDto, ventaGuardada);

        return ventaResponseDto;
    }

    private void descontarStock(VentaRequestDto ventaRequestDto) {
        ProductoEntity productoEntity;
        Integer stock;
        for (VentaDetalleRequestDto detalle : ventaRequestDto.getCompraDetalle()) {
            productoEntity = productoRepository.findById(detalle.getIdProducto()).orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No pudimos encontrar el producto con id " + detalle.getIdProducto()
            ));

            stock = productoEntity.getStock();
            stock = stock - detalle.getCantidad();
            if (stock < 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No podemos procesar la venta porque no hay suficiente stock para el producto con id " + detalle.getIdProducto());
            productoEntity.setStock(stock);
            productoRepository.save(productoEntity);
        }
    }

    private void crearVentaDetalle(VentaRequestDto ventaRequestDto, VentaEntity ventaGuardada) {
        for (VentaDetalleRequestDto dto : ventaRequestDto.getCompraDetalle()){
            VentaDetalleEntity detalle = new VentaDetalleEntity();
            detalle.setVenta(ventaGuardada);
            detalle.setIdProducto(dto.getIdProducto());
            detalle.setCantidad(dto.getCantidad());
            detalle.setPrecioUnitario(dto.getPrecioUnitario());
            ventaDetalleRepository.save(detalle);
        }
    }
}
