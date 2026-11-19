package modelo;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {
    private static final String RUTA_CONFIG = "db.properties";

    public static Connection obtenerConexion() throws SQLException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(RUTA_CONFIG)) {
            props.load(fis);
        } catch (IOException ex) {
            throw new SQLException("No se pudo leer el archivo " + RUTA_CONFIG + ". Copia db.properties.example", ex);
        }

        String url = props.getProperty("db.url");
        String usuario = props.getProperty("db.user");
        String contrasena = props.getProperty("db.password");

        return DriverManager.getConnection(url, usuario, contrasena);
    }
}
