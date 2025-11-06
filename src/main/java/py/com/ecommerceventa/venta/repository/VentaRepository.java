package py.com.ecommerceventa.venta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.com.ecommerceventa.venta.entity.VentaEntity;

public interface VentaRepository extends JpaRepository<VentaEntity, Long> {

}
