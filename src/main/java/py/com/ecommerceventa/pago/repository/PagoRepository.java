package py.com.ecommerceventa.pago.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.com.ecommerceventa.pago.entity.PagoEntity;
import py.com.ecommerceventa.venta.entity.VentaEntity;

import java.util.List;

public interface PagoRepository extends JpaRepository<PagoEntity, Long> {
    List<PagoEntity> findByVenta(VentaEntity venta);
}
