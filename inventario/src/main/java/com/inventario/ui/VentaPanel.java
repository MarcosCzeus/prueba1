package com.inventario.ui;

import com.inventario.model.Producto;
import com.inventario.model.Venta;
import com.inventario.service.ProductoService;
import com.inventario.service.VentaService;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class VentaPanel extends JPanel {

    private final VentaService ventaService;
    private final ProductoService productoService;

    private final JTextField txtCliente = new JTextField(15);
    private final JComboBox<Producto> cboProducto = new JComboBox<>();
    private final JTextField txtCantidad = new JTextField(5);
    private final JLabel lblTotal = new JLabel("Total vendido: 0.00");

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Fecha", "Cliente", "Producto", "Cantidad", "Precio", "Total"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    private final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public VentaPanel(VentaService ventaService, ProductoService productoService) {
        this.ventaService = ventaService;
        this.productoService = productoService;
        setLayout(new BorderLayout(5, 5));

        JButton btnRegistrar = new JButton("Registrar venta");
        JButton btnAnular = new JButton("Anular venta");

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("Cliente:"));
        formulario.add(txtCliente);
        formulario.add(new JLabel("Producto:"));
        formulario.add(cboProducto);
        formulario.add(new JLabel("Cantidad:"));
        formulario.add(txtCantidad);
        formulario.add(btnRegistrar);
        formulario.add(btnAnular);

        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(lblTotal, BorderLayout.SOUTH);

        btnRegistrar.addActionListener(e -> registrar());
        btnAnular.addActionListener(e -> anular());

        cargar();
    }

    public void cargar() {
        cboProducto.removeAllItems();
        for (Producto p : productoService.listar("")) {
            cboProducto.addItem(p);
        }

        modelo.setRowCount(0);
        BigDecimal totalVendido = BigDecimal.ZERO;
        for (Venta v : ventaService.listar()) {
            modelo.addRow(new Object[]{v.getId(), v.getFecha().format(formato), v.getCliente(),
                    v.getProducto().getNombre(), v.getCantidad(), v.getPrecio(), v.getTotal()});
            totalVendido = totalVendido.add(v.getTotal());
        }
        lblTotal.setText("Total vendido: " + totalVendido);
    }

    private void registrar() {
        try {
            Producto producto = (Producto) cboProducto.getSelectedItem();
            if (producto == null) {
                throw new IllegalArgumentException("Seleccione un producto.");
            }
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            Venta venta = ventaService.registrar(txtCliente.getText(), producto.getId(), cantidad);
            JOptionPane.showMessageDialog(this, "Venta registrada. Total: " + venta.getTotal());
            txtCliente.setText("");
            txtCantidad.setText("");
            cargar();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un numero entero.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void anular() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta de la tabla.");
            return;
        }
        Long id = (Long) modelo.getValueAt(fila, 0);
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Anular la venta " + id + "? El stock se devuelve al producto.", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            ventaService.anular(id);
            cargar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
