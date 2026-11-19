package modelo;

public class Pelicula {
    private int id;
    private String nombre;
    private String categoria;
    private double precioEntrada;
    private String fechaEstreno;
    private boolean enCartelera;

    public Pelicula(int id, String nombre, String categoria, double precioEntrada, String fechaEstreno, boolean enCartelera) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioEntrada = precioEntrada;
        this.fechaEstreno = fechaEstreno;
        this.enCartelera = enCartelera;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecioEntrada() {
        return precioEntrada;
    }

    public void setPrecioEntrada(double precioEntrada) {
        this.precioEntrada = precioEntrada;
    }

    public String getFechaEstreno() {
        return fechaEstreno;
    }

    public void setFechaEstreno(String fechaEstreno) {
        this.fechaEstreno = fechaEstreno;
    }

    public boolean isEnCartelera() {
        return enCartelera;
    }

    public void setEnCartelera(boolean enCartelera) {
        this.enCartelera = enCartelera;
    }
}
