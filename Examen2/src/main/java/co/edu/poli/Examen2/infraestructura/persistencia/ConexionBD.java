package co.edu.poli.Examen2.infraestructura.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Gestiona la conexión a la base de datos MySQL mediante el patrón Singleton.
 * <p>
 * Los parámetros se leen de un archivo {@code .env} en la raíz del proyecto
 * (también se respetan las variables de entorno del sistema). Si falta el
 * archivo o alguna variable, se usan valores por defecto de desarrollo local:
 * </p>
 * <ul>
 *   <li>{@code DB_URL}: {@code jdbc:mysql://localhost:3306/examenes?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true}</li>
 *   <li>{@code DB_USER}: {@code root}</li>
 *   <li>{@code DB_PASSWORD}: cadena vacía</li>
 * </ul>
 * <p>
 * Pertenece a la capa de infraestructura y la usan los adaptadores de salida
 * que persisten en MySQL.
 * </p>
 */
public class ConexionBD {

    /** URL de conexión que se usa cuando no se define {@code DB_URL}. */
    private static final String URL_POR_DEFECTO =
            "jdbc:mysql://localhost:3306/examenes?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    /** Única instancia de la clase (Singleton). */
    private static ConexionBD instancia;

    /** Conexión JDBC compartida; se abre la primera vez que se pide. */
    private Connection conexion;

    /** URL JDBC de la base de datos. */
    private final String url;

    /** Usuario de la base de datos. */
    private final String usuario;

    /** Contraseña del usuario de la base de datos. */
    private final String password;

    /** Constructor privado: solo lee la configuración, no conecta. */
    private ConexionBD() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        url = dotenv.get("DB_URL", URL_POR_DEFECTO);
        usuario = dotenv.get("DB_USER", "root");
        password = dotenv.get("DB_PASSWORD", "");
    }

    /**
     * Devuelve la instancia única de la clase, creándola la primera vez que se pide.
     *
     * @return la única instancia de {@code ConexionBD}
     */
    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    /**
     * Retorna la conexión compartida, abriéndola si aún no existe o si se
     * cerró. <b>No cierre la conexión devuelta</b> (ni la use en un
     * try-with-resources): es la misma para toda la aplicación. Cierre solo
     * los {@code Statement} y {@code ResultSet}.
     *
     * @return la conexión activa
     * @throws SQLException si no se puede conectar
     */
    public synchronized Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(url, usuario, password);
        }
        return conexion;
    }

    /**
     * Cierra la conexión, si está abierta. Conviene llamarlo al cerrar la
     * aplicación (por ejemplo, en {@code Application.stop()}).
     */
    public synchronized void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            } finally {
                conexion = null;
            }
        }
    }
}