package com.inventario.repository;

import com.inventario.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findAllByOrderByFechaDesc();

    boolean existsByProductoId(Long productoId);
}
