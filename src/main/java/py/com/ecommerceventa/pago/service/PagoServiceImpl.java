package py.com.ecommerceventa.pago.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import py.com.ecommerceventa.config.RabbitMQConfig;
import py.com.ecommerceventa.pago.DTO.PagoRequestDto;
import py.com.ecommerceventa.pago.entity.PagoEntity;
import py.com.ecommerceventa.pago.enums.PAGO_ESTADO;
import py.com.ecommerceventa.pago.repository.PagoRepository;
import py.com.ecommerceventa.venta.entity.VentaEntity;
import py.com.ecommerceventa.venta.enums.VENTA_ESTADO;
import py.com.ecommerceventa.venta.repository.VentaRepository;

import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PagoServiceImpl implements PagoService{
    PagoRepository pagoRepository;
    VentaRepository ventaRepository;

    @Override
    @RabbitListener(queues = RabbitMQConfig.QUEUE_PAGO)
    public void confirmarPago(PagoRequestDto pagoRequestDto) {
        log.info("Recibiendo confirmacion de pago para la venta con id {} ", pagoRequestDto);
        VentaEntity ventaEntity = ventaRepository.findById(pagoRequestDto.getIdVenta()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No pudimos encontrar la venta con ID: " + pagoRequestDto
        ));
        ventaEntity.setEstado(VENTA_ESTADO.CONFIRMADO.name());
        ventaRepository.save(ventaEntity);

        log.info("Procedemos a confirmar todos los pagos para la venta con id {} ", pagoRequestDto);
        List<PagoEntity> pagoEntityList = pagoRepository.findByVenta(ventaEntity);
        pagoEntityList.forEach(pago -> {pago.setEstado(PAGO_ESTADO.CONFIRMADO.name()); pago.setMetodoPago(pagoRequestDto.getTipoMedioPago().name());});
        pagoRepository.saveAll(pagoEntityList);

        log.info("Pago confirmado con exito para la venta con id {} ", pagoRequestDto);
    }

}
