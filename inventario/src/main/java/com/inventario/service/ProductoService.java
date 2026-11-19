package com.inventario.service;

import com.inventario.model.Producto;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.VentaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;

    public ProductoService(ProductoRepository productoRepository, VentaRepository ventaRepository) {
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
    }

    public List<Producto> listar(String nombre) {
        String filtro = (nombre == null) ? "" : nombre.trim();
        return productoRepository.findByNombreContainingIgnoreCaseOrderByNombre(filtro);
    }

    public Producto guardar(Producto p) {
        if (p.getCodigo() == null || p.getCodigo().isBlank()) {
            throw new IllegalArgumentException("El codigo es obligatorio.");
        }
        if (p.getNombre() == null || p.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (p.getPrecio() == null || p.getPrecio().signum() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        if (p.getStock() == null || p.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        boolean codigoRepetido = (p.getId() == null)
                ? productoRepository.existsByCodigo(p.getCodigo())
                : productoRepository.existsByCodigoAndIdNot(p.getCodigo(), p.getId());
        if (codigoRepetido) {
            throw new IllegalArgumentException("Ya existe un producto con ese codigo.");
        }
        return productoRepository.save(p);
    }

    public void eliminar(Long id) {
        if (ventaRepository.existsByProductoId(id)) {
            throw new IllegalArgumentException("No se puede eliminar: el producto tiene ventas.");
        }
        productoRepository.deleteById(id);
    }
}
