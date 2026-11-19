# MovieMaster - Gestor de Cartelera de Películas

Aplicación de escritorio desarrollada en Java (AWT) bajo el patrón MVC para la gestión de una cartelera de películas. Permite realizar operaciones CRUD conectadas a una base de datos MySQL mediante el API JDBC.

## Funcionalidades

- **Crear:** Registro de nuevas películas con validación de campos y verificación de ID duplicado.
- **Leer:** Carga automática de los registros desde MySQL al iniciar la aplicación y consulta individual por ID o selección en tabla.
- **Actualizar:** Modificación de datos de películas existentes.
- **Eliminar:** Eliminación de registros con confirmación previa.
- **Pruebas de integración:** Validación de la conexión JDBC y del flujo CRUD completo mediante JUnit.

## Tecnologías

- Java (AWT / Swing `JTable`)
- MySQL 8
- JDBC (`mysql-connector-j-8.4.0.jar`)
- JUnit
- Docker (opcional para el servidor de base de datos)

## Estructura del proyecto

```text
├── controlador/
│   └── ControladorPeliculas.java
├── lib/
│   └── mysql-connector-j-8.4.0.jar
├── modelo/
│   ├── ConexionBD.java
│   └── Pelicula.java
├── test/
│   └── PruebasIntegracionPelicula.java
├── vista/
│   ├── Main.java
│   └── Ventana.java
├── .gitignore
└── db.properties.example
