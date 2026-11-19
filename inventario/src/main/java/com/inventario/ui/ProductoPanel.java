package com.inventario.ui;

import com.inventario.model.Producto;
import com.inventario.service.ProductoService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;

public class ProductoPanel extends JPanel {

    private final ProductoService service;

    private final JTextField txtCodigo = new JTextField(8);
    private final JTextField txtNombre = new JTextField(18);
    private final JTextField txtPrecio = new JTextField(7);
    private final JTextField txtStock = new JTextField(5);
    private final JTextField txtBuscar = new JTextField(15);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Codigo", "Nombre", "Precio", "Stock"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    private Long idSeleccionado = null;

    public ProductoPanel(ProductoService service) {
        this.service = service;
        setLayout(new BorderLayout(5, 5));

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("Codigo:"));
        formulario.add(txtCodigo);
        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("Precio:"));
        formulario.add(txtPrecio);
        formulario.add(new JLabel("Stock:"));
        formulario.add(txtStock);

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnBuscar = new JButton("Buscar");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnEliminar);
        botones.add(new JLabel("   Buscar por nombre:"));
        botones.add(txtBuscar);
        botones.add(btnBuscar);

        JPanel arriba = new JPanel(new GridLayout(2, 1));
        arriba.add(formulario);
        arriba.add(botones);

        add(arriba, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnNuevo.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnBuscar.addActionListener(e -> cargar());
        tabla.getSelectionModel().addListSelectionListener(e -> seleccionar());

        cargar();
    }

    public void cargar() {
        modelo.setRowCount(0);
        for (Producto p : service.listar(txtBuscar.getText())) {
            modelo.addRow(new Object[]{p.getId(), p.getCodigo(), p.getNombre(), p.getPrecio(), p.getStock()});
        }
    }

    private void seleccionar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        idSeleccionado = (Long) modelo.getValueAt(fila, 0);
        txtCodigo.setText(modelo.getValueAt(fila, 1).toString());
        txtNombre.setText(modelo.getValueAt(fila, 2).toString());
        txtPrecio.setText(modelo.getValueAt(fila, 3).toString());
        txtStock.setText(modelo.getValueAt(fila, 4).toString());
    }

    private void guardar() {
        try {
            Producto p = new Producto();
            p.setId(idSeleccionado);
            p.setCodigo(txtCodigo.getText().trim());
            p.setNombre(txtNombre.getText().trim());
            p.setPrecio(new BigDecimal(txtPrecio.getText().trim()));
            p.setStock(Integer.parseInt(txtStock.getText().trim()));
            service.guardar(p);
            JOptionPane.showMessageDialog(this, "Producto guardado.");
            limpiar();
            cargar();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio y stock deben ser numeros.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this, "Eliminar el producto?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.eliminar(idSeleccionado);
            limpiar();
            cargar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        idSeleccionado = null;
        txtCodigo.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        tabla.clearSelection();
    }
}
