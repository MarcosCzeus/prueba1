package com.inventario;

import com.inventario.service.ProductoService;
import com.inventario.service.VentaService;
import com.inventario.ui.ProductoPanel;
import com.inventario.ui.VentaPanel;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

@SpringBootApplication
public class InventarioApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(InventarioApplication.class)
                .headless(false)
                .run(args);

        ProductoService productoService = ctx.getBean(ProductoService.class);
        VentaService ventaService = ctx.getBean(VentaService.class);

        SwingUtilities.invokeLater(() -> {
            ProductoPanel productoPanel = new ProductoPanel(productoService);
            VentaPanel ventaPanel = new VentaPanel(ventaService, productoService);

            JTabbedPane pestanas = new JTabbedPane();
            pestanas.addTab("Productos", productoPanel);
            pestanas.addTab("Ventas", ventaPanel);
            pestanas.addChangeListener(e -> {
                productoPanel.cargar();
                ventaPanel.cargar();
            });

            JFrame ventana = new JFrame("Control de Inventario y Ventas");
            ventana.add(pestanas);
            ventana.setSize(950, 600);
            ventana.setLocationRelativeTo(null);
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setVisible(true);
        });
    }
}
