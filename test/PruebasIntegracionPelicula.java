package test;

import static org.junit.Assert.*;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import controlador.ControladorPeliculas;
import modelo.ConexionBD;
import modelo.Pelicula;

public class PruebasIntegracionPelicula {

    private ControladorPeliculas controlador;
    private final int ID_PRUEBA = 9999;

    @Before
    public void setUp() throws SQLException {
        controlador = new ControladorPeliculas(null);
        // Asegura que no exista el registro de prueba antes de comenzar
        controlador.eliminarRegistro(ID_PRUEBA);
    }

    @After
    public void tearDown() throws SQLException {
        // Restaura la BD eliminando los datos de prueba
        controlador.eliminarRegistro(ID_PRUEBA);
    }

    @Test
    public void pruebaConexionABaseDeDatos() throws SQLException {
        try (Connection conn = ConexionBD.obtenerConexion()) {
            assertNotNull("La conexión no debe ser nula", conn);
            assertFalse("La conexión debe estar abierta", conn.isClosed());
        }
    }

    @Test
    public void pruebaFlujoCompletoCRUD() throws SQLException {
        
        Pelicula nueva = new Pelicula(ID_PRUEBA, "Matrix", "Ciencia Ficción", 5500.00, "1999-03-31", true);
        assertTrue("Debe insertar la película", controlador.agregarRegistro(nueva));

        Pelicula encontrada = controlador.buscarPelicula(ID_PRUEBA);
        assertNotNull("Debe encontrar la película insertada", encontrada);
        assertEquals("Matrix", encontrada.getNombre());

        encontrada.setPrecioEntrada(6200.00);
        encontrada.setEnCartelera(false);
        assertTrue("Debe modificar la película", controlador.modificarRegistro(encontrada));

        Pelicula modificada = controlador.buscarPelicula(ID_PRUEBA);
        assertEquals(6200.00, modificada.getPrecioEntrada(), 0.01);
        assertFalse(modificada.isEnCartelera());

        assertTrue("Debe eliminar la película", controlador.eliminarRegistro(ID_PRUEBA));
        assertNull("Ya no debe existir en la BD", controlador.buscarPelicula(ID_PRUEBA));
    }
}
