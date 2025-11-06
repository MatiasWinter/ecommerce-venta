package py.com.ecommerceventa.venta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.com.ecommerceventa.venta.entity.VentaDetalleEntity;

@Repository
public interface VentaDetalleRepository extends JpaRepository<VentaDetalleEntity,Long> {

}
