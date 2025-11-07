package py.com.ecommerceventa.venta.service;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import py.com.ecommerceventa.config.RabbitMQConfig;
import py.com.ecommerceventa.pago.enums.PAGO_ESTADO;
import py.com.ecommerceventa.pago.enums.TIPO_MEDIO_PAGO;
import py.com.ecommerceventa.pago.entity.PagoEntity;
import py.com.ecommerceventa.pago.repository.PagoRepository;
import py.com.ecommerceventa.producto.entity.ProductoEntity;
import py.com.ecommerceventa.producto.enums.PRODUCTO_ESTADO;
import py.com.ecommerceventa.producto.repository.ProductoRepository;
import py.com.ecommerceventa.venta.DTO.VentaDetalleRequestDto;
import py.com.ecommerceventa.venta.DTO.VentaRequestDto;
import py.com.ecommerceventa.venta.DTO.VentaResponseDto;
import py.com.ecommerceventa.venta.entity.VentaDetalleEntity;
import py.com.ecommerceventa.venta.entity.VentaEntity;
import py.com.ecommerceventa.venta.enums.VENTA_ESTADO;
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
    PagoRepository pagoRepository;

    @Override
    public VentaResponseDto vender(VentaRequestDto ventaRequestDto) {
        BigDecimal totalCalculado = BigDecimal.ZERO;
        BigDecimal subtotal;

        log.info("Validamos el stock de los productos a vender, y procedemos a descontar. Tambien verificamos que es estado del producto sea {}", PRODUCTO_ESTADO.ACTIVO);
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
        venta.setEstado(VENTA_ESTADO.PENDIENTE.name());
        VentaEntity ventaGuardada = ventaRepository.save(venta);
        VentaResponseDto ventaResponseDto = new VentaResponseDto(ventaGuardada.getIdVenta(),ventaGuardada.getTotal());

        log.info("Venta guardada con exito, procedemos a guardar el detalle de la venta con id {}", ventaGuardada.getIdVenta());
        crearVentaDetalle(ventaRequestDto, ventaGuardada);

        log.info("Detalle venta guardada con exito. Procedemos a generar un pago en estado {} para la venta con id {}", PAGO_ESTADO.PENDIENTE,ventaResponseDto.getIdVenta());
        crearPagoPendiente(ventaGuardada, ventaResponseDto);

        return ventaResponseDto;
    }

    private void crearPagoPendiente(VentaEntity ventaGuardada, VentaResponseDto ventaResponseDto) {
        PagoEntity pagoEntity = new PagoEntity();
        pagoEntity.setVenta(ventaGuardada);
        pagoEntity.setMetodoPago(TIPO_MEDIO_PAGO.PENDIENTE.name());
        pagoEntity.setMonto(ventaGuardada.getTotal());
        pagoEntity.setEstado(PAGO_ESTADO.PENDIENTE.name());
        pagoRepository.save(pagoEntity);
        log.info("Pago en estado {} para la venta con id {} creado con exito",PAGO_ESTADO.PENDIENTE, ventaResponseDto.getIdVenta());
    }

    private void descontarStock(VentaRequestDto ventaRequestDto) {
        ProductoEntity productoEntity;
        Integer stock;
        for (VentaDetalleRequestDto detalle : ventaRequestDto.getCompraDetalle()) {
            productoEntity = productoRepository.findById(detalle.getIdProducto()).orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No pudimos encontrar el producto con id " + detalle.getIdProducto()
            ));

            if (!productoEntity.getEstado().equals(PRODUCTO_ESTADO.ACTIVO.name())) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"El producto con id " + productoEntity.getIdProducto() + " no esta en estado " + PRODUCTO_ESTADO.ACTIVO);

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
