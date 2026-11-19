package controlador;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import modelo.ConexionBD;
import modelo.Pelicula;

public class ControladorPeliculas {
    private DefaultTableModel modeloTabla;

    public ControladorPeliculas(DefaultTableModel modeloTabla) {
        this.modeloTabla = modeloTabla;
    }

    // READ: Obtiene todas las películas desde MySQL y las despliega en la tabla de la GUI
    public void cargarTabla() throws SQLException {
        if (modeloTabla == null) return;
        modeloTabla.setRowCount(0); // Limpia la tabla antes de recargar

        List<Pelicula> lista = listarPeliculas();
        for (Pelicula p : lista) {
            modeloTabla.addRow(new Object[]{
                p.getId(),
                p.getNombre(),
                p.getCategoria(),
                p.getPrecioEntrada(),
                p.getFechaEstreno(),
                p.isEnCartelera() ? "Sí" : "No"
            });
        }
    }

    public List<Pelicula> listarPeliculas() throws SQLException {
        List<Pelicula> peliculas = new ArrayList<>();
        String sql = "SELECT IdPelicula, NombrePelicula, Categoria, PrecioEntrada, FechaEstreno, EnCartelera FROM Pelicula";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pelicula p = new Pelicula(
                    rs.getInt("IdPelicula"),
                    rs.getString("NombrePelicula"),
                    rs.getString("Categoria"),
                    rs.getDouble("PrecioEntrada"),
                    rs.getDate("FechaEstreno").toString(), // Formato YYYY-MM-DD
                    rs.getBoolean("EnCartelera")
                );
                peliculas.add(p);
            }
        }
        return peliculas;
    }

    // CREATE: Inserta una película en MySQL
    public boolean agregarRegistro(Pelicula p) throws SQLException {
        String sql = "INSERT INTO Pelicula (IdPelicula, NombrePelicula, Categoria, PrecioEntrada, FechaEstreno, EnCartelera) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, p.getId());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getCategoria());
            ps.setDouble(4, p.getPrecioEntrada());
            ps.setDate(5, Date.valueOf(p.getFechaEstreno())); // Requiere YYYY-MM-DD
            ps.setBoolean(6, p.isEnCartelera());

            boolean insertado = ps.executeUpdate() > 0;
            if (insertado) {
                cargarTabla();
            }
            return insertado;
        }
    }

    // READ (por ID): Busca una película específica
    public Pelicula buscarPelicula(int id) throws SQLException {
        String sql = "SELECT IdPelicula, NombrePelicula, Categoria, PrecioEntrada, FechaEstreno, EnCartelera "
                   + "FROM Pelicula WHERE IdPelicula = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Pelicula(
                        rs.getInt("IdPelicula"),
                        rs.getString("NombrePelicula"),
                        rs.getString("Categoria"),
                        rs.getDouble("PrecioEntrada"),
                        rs.getDate("FechaEstreno").toString(),
                        rs.getBoolean("EnCartelera")
                    );
                }
            }
        }
        return null;
    }

    public boolean existeId(int id) throws SQLException {
        return buscarPelicula(id) != null;
    }

    // UPDATE: Modifica una película existente
    public boolean modificarRegistro(Pelicula p) throws SQLException {
        String sql = "UPDATE Pelicula SET NombrePelicula = ?, Categoria = ?, PrecioEntrada = ?, "
                   + "FechaEstreno = ?, EnCartelera = ? WHERE IdPelicula = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getCategoria());
            ps.setDouble(3, p.getPrecioEntrada());
            ps.setDate(4, Date.valueOf(p.getFechaEstreno()));
            ps.setBoolean(5, p.isEnCartelera());
            ps.setInt(6, p.getId());

            boolean actualizado = ps.executeUpdate() > 0;
            if (actualizado) {
                cargarTabla();
            }
            return actualizado;
        }
    }

    // DELETE: Elimina una película por su ID
    public boolean eliminarRegistro(int id) throws SQLException {
        String sql = "DELETE FROM Pelicula WHERE IdPelicula = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            boolean eliminado = ps.executeUpdate() > 0;
            if (eliminado) {
                cargarTabla();
            }
            return eliminado;
        }
    }
}
