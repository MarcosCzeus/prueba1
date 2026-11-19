package com.inventario.service;

import com.inventario.model.Producto;
import com.inventario.model.Venta;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    public List<Venta> listar() {
        return ventaRepository.findAllByOrderByFechaDesc();
    }

    @Transactional
    public Venta registrar(String cliente, Long productoId, int cantidad) {
        if (cliente == null || cliente.isBlank()) {
            throw new IllegalArgumentException("El cliente es obligatorio.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));
        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + producto.getStock());
        }

        // Descuenta el inventario
        producto.setStock(producto.getStock() - cantidad);

        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setCliente(cliente.trim());
        venta.setProducto(producto);
        venta.setCantidad(cantidad);
        venta.setPrecio(producto.getPrecio());
        venta.setTotal(producto.getPrecio().multiply(BigDecimal.valueOf(cantidad)));
        return ventaRepository.save(venta);
    }

    @Transactional
    public void anular(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada."));

        // Devuelve el stock al producto
        Producto producto = venta.getProducto();
        producto.setStock(producto.getStock() + venta.getCantidad());

        ventaRepository.delete(venta);
    }
}
