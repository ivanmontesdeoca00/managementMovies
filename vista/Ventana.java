package vista;

import controlador.ControladorPeliculas;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import modelo.Pelicula;

public class Ventana extends Frame {

    // Campos del formulario
    private TextField txtId;
    private TextField txtNombre;
    private Choice choiceCategoria;
    private TextField txtPrecio;
    private TextField txtFechaEstreno;
    private Checkbox chkEnCartelera;

    // Botones CRUD
    private Button btnAgregar;
    private Button btnConsultar;
    private Button btnModificar;
    private Button btnEliminar;
    private Button btnLimpiar;

    // Tabla de despliegue
    private JTable tablaPeliculas;
    private DefaultTableModel modeloTabla;

    // Controlador
    private ControladorPeliculas controlador;

    public Ventana() {
        setTitle("MovieMaster - Gestión de Cartelera de Películas");
        setSize(820, 520);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        inicializarComponentes();
        controlador = new ControladorPeliculas(modeloTabla);
        configurarEventos();

        // Carga inicial desde MySQL al abrir la ventana
        try {
            controlador.cargarTabla();
        } catch (SQLException ex) {
            mostrarPopup("Error al conectar con la base de datos:\n" + ex.getMessage());
        }
    }

    private void inicializarComponentes() {
        // Panel superior: Formulario
        Panel panelFormulario = new Panel(new GridLayout(6, 2, 8, 8));

        panelFormulario.add(new Label("ID Película:"));
        txtId = new TextField();
        panelFormulario.add(txtId);

        panelFormulario.add(new Label("Nombre Película:"));
        txtNombre = new TextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new Label("Categoría:"));
        choiceCategoria = new Choice();
        choiceCategoria.add("Acción");
        choiceCategoria.add("Comedia");
        choiceCategoria.add("Drama");
        choiceCategoria.add("Terror");
        choiceCategoria.add("Ciencia Ficción");
        choiceCategoria.add("Animación");
        choiceCategoria.add("Suspenso");
        panelFormulario.add(choiceCategoria);

        panelFormulario.add(new Label("Precio Entrada:"));
        txtPrecio = new TextField();
        panelFormulario.add(txtPrecio);

        panelFormulario.add(new Label("Fecha Estreno (YYYY-MM-DD):"));
        txtFechaEstreno = new TextField();
        panelFormulario.add(txtFechaEstreno);

        panelFormulario.add(new Label("En Cartelera:"));
        chkEnCartelera = new Checkbox("Sí, en exhibición", true);
        panelFormulario.add(chkEnCartelera);

        // Panel de botones CRUD
        Panel panelBotones = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        btnAgregar = new Button("Agregar");
        btnConsultar = new Button("Consultar");
        btnModificar = new Button("Modificar");
        btnEliminar = new Button("Eliminar");
        btnLimpiar = new Button("Limpiar");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnConsultar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        Panel panelSuperior = new Panel(new BorderLayout(5, 5));
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);

        // Panel central: Tabla de películas
        String[] columnas = {
            "ID", "Nombre", "Categoría", "Precio Entrada", "Fecha Estreno", "En Cartelera"
        };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaPeliculas = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaPeliculas);

        add(scrollTabla, BorderLayout.CENTER);
    }

    private void configurarEventos() {
        // Cierre de ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        // CREATE - Agregar película
        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!validarCampos()) {
                    return;
                }
                try {
                    Pelicula p = construirPeliculaDesdeFormulario();
                    if (controlador.existeId(p.getId())) {
                        mostrarPopup("Ya existe una película registrada con el ID " + p.getId() + ".");
                        return;
                    }
                    if (controlador.agregarRegistro(p)) {
                        mostrarPopup("Película agregada correctamente.");
                        limpiarCampos();
                    }
                } catch (IllegalArgumentException ex) {
                    mostrarPopup("La fecha debe tener el formato YYYY-MM-DD (ejemplo: 2026-11-18).");
                } catch (SQLException ex) {
                    mostrarPopup("Error de base de datos al agregar:\n" + ex.getMessage());
                }
            }
        });

        // READ - Consultar película (por fila seleccionada o por ID ingresado)
        btnConsultar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int idBuscar;
                    int filaSeleccionada = tablaPeliculas.getSelectedRow();

                    if (filaSeleccionada != -1 && txtId.getText().trim().isEmpty()) {
                        idBuscar = Integer.parseInt(modeloTabla.getValueAt(filaSeleccionada, 0).toString());
                    } else if (!txtId.getText().trim().isEmpty()) {
                        idBuscar = Integer.parseInt(txtId.getText().trim());
                    } else {
                        mostrarPopup("Ingresa un ID o selecciona una fila de la tabla para consultar.");
                        return;
                    }

                    Pelicula p = controlador.buscarPelicula(idBuscar);
                    if (p != null) {
                        cargarPeliculaEnFormulario(p);
                    } else {
                        mostrarPopup("No se encontró ninguna película con el ID " + idBuscar + ".");
                    }
                } catch (NumberFormatException ex) {
                    mostrarPopup("El ID debe ser un número entero válido.");
                } catch (SQLException ex) {
                    mostrarPopup("Error de base de datos al consultar:\n" + ex.getMessage());
                }
            }
        });

        // UPDATE - Modificar película
        btnModificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!validarCampos()) {
                    return;
                }
                try {
                    Pelicula p = construirPeliculaDesdeFormulario();
                    if (!controlador.existeId(p.getId())) {
                        mostrarPopup("No existe una película con el ID " + p.getId() + " para modificar.");
                        return;
                    }
                    if (controlador.modificarRegistro(p)) {
                        mostrarPopup("Película modificada correctamente.");
                        limpiarCampos();
                    }
                } catch (IllegalArgumentException ex) {
                    mostrarPopup("La fecha debe tener el formato YYYY-MM-DD (ejemplo: 2026-11-18).");
                } catch (SQLException ex) {
                    mostrarPopup("Error de base de datos al modificar:\n" + ex.getMessage());
                }
            }
        });

        // DELETE - Eliminar película
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int idEliminar;
                    int filaSeleccionada = tablaPeliculas.getSelectedRow();

                    if (!txtId.getText().trim().isEmpty()) {
                        idEliminar = Integer.parseInt(txtId.getText().trim());
                    } else if (filaSeleccionada != -1) {
                        idEliminar = Integer.parseInt(modeloTabla.getValueAt(filaSeleccionada, 0).toString());
                    } else {
                        mostrarPopup("Ingresa el ID o selecciona una fila para eliminar.");
                        return;
                    }

                    int confirmacion = JOptionPane.showConfirmDialog(
                        Ventana.this,
                        "¿Seguro que deseas eliminar la película con ID " + idEliminar + "?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION
                    );

                    if (confirmacion == JOptionPane.YES_OPTION) {
                        if (controlador.eliminarRegistro(idEliminar)) {
                            mostrarPopup("Película eliminada correctamente.");
                            limpiarCampos();
                        } else {
                            mostrarPopup("No se encontró ninguna película con el ID " + idEliminar + ".");
                        }
                    }
                } catch (NumberFormatException ex) {
                    mostrarPopup("El ID debe ser un número entero válido.");
                } catch (SQLException ex) {
                    mostrarPopup("Error de base de datos al eliminar:\n" + ex.getMessage());
                }
            }
        });

        // Limpiar formulario
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });
    }

    private boolean validarCampos() {
        if (txtId.getText().trim().isEmpty()
                || txtNombre.getText().trim().isEmpty()
                || txtPrecio.getText().trim().isEmpty()
                || txtFechaEstreno.getText().trim().isEmpty()) {
            mostrarPopup("Todos los campos son obligatorios.");
            return false;
        }

        try {
            Integer.parseInt(txtId.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarPopup("El ID debe ser un número entero.");
            return false;
        }

        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            if (precio < 0) {
                mostrarPopup("El precio de entrada no puede ser negativo.");
                return false;
            }
        } catch (NumberFormatException ex) {
            mostrarPopup("El precio de entrada debe ser un número válido (ej: 5500 o 5500.50).");
            return false;
        }

        return true;
    }

    private Pelicula construirPeliculaDesdeFormulario() {
        int id = Integer.parseInt(txtId.getText().trim());
        String nombre = txtNombre.getText().trim();
        String categoria = choiceCategoria.getSelectedItem();
        double precio = Double.parseDouble(txtPrecio.getText().trim());
        String fecha = txtFechaEstreno.getText().trim();
        boolean enCartelera = chkEnCartelera.getState();

        return new Pelicula(id, nombre, categoria, precio, fecha, enCartelera);
    }

    private void cargarPeliculaEnFormulario(Pelicula p) {
        txtId.setText(String.valueOf(p.getId()));
        txtNombre.setText(p.getNombre());
        choiceCategoria.select(p.getCategoria());
        txtPrecio.setText(String.valueOf(p.getPrecioEntrada()));
        txtFechaEstreno.setText(p.getFechaEstreno());
        chkEnCartelera.setState(p.isEnCartelera());
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        choiceCategoria.select(0);
        txtPrecio.setText("");
        txtFechaEstreno.setText("");
        chkEnCartelera.setState(true);
        tablaPeliculas.clearSelection();
        txtId.requestFocus();
    }

    private void mostrarPopup(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}
