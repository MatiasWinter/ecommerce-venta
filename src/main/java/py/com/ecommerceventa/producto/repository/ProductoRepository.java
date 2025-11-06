package py.com.ecommerceventa.producto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.com.ecommerceventa.producto.entity.ProductoEntity;


public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
}
