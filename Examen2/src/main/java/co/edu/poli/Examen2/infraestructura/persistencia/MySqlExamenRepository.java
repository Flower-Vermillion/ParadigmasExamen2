package co.edu.poli.Examen2.infraestructura.persistencia;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.poli.Examen2.aplicacion.puerto.salida.ExamenRepository;
import co.edu.poli.Examen2.dominio.Examen;
import co.edu.poli.Examen2.dominio.FactorRh;
import co.edu.poli.Examen2.dominio.Paciente;
import co.edu.poli.Examen2.dominio.TipoSangre;
import co.edu.poli.Examen2.dominio.builder.ExamenBuilder;

/**
 * Adaptador de salida que guarda los exámenes en MySQL (tablas {@code paciente}
 * y {@code examen}, ver esquema.sql). Cada examen se persiste al guardarlo, por
 * lo que serializar y deserializar no aplican.
 * <p>
 * Usa la conexión compartida de {@link ConexionBD}, por lo que nunca la cierra.
 */
public class MySqlExamenRepository implements ExamenRepository {

    /** Inserta el paciente; si ya existe (mismo nombre), no hace nada. */
    private static final String SQL_PACIENTE =
            "INSERT IGNORE INTO paciente (nombre, tipo_sangre, factor_rh) VALUES (?, ?, ?)";

    /** Inserta un examen asociado a un paciente existente. */
    private static final String SQL_EXAMEN =
            "INSERT INTO examen (codigo, costo, paciente_nombre) VALUES (?, ?, ?)";

    /** Consulta todos los exámenes junto con los datos de su paciente. */
    private static final String SQL_LISTAR =
            "SELECT e.codigo, e.costo, p.nombre, p.tipo_sangre, p.factor_rh "
            + "FROM examen e JOIN paciente p ON p.nombre = e.paciente_nombre ORDER BY e.codigo";

    /** Proveedor de la conexión compartida a la base de datos. */
    private final ConexionBD conexion;

    /**
     * Crea el repositorio con la instancia única de {@link ConexionBD}.
     */
    public MySqlExamenRepository() {
        this(ConexionBD.getInstancia());
    }

    /**
     * Crea el repositorio con un proveedor de conexión concreto.
     *
     * @param conexion proveedor de la conexión a la base de datos
     */
    public MySqlExamenRepository(ConexionBD conexion) {
        this.conexion = conexion;
    }

    /**
     * Guarda el paciente (si aún no existe) y el examen dentro de una misma
     * transacción: si algo falla, no se guarda ninguno de los dos.
     *
     * @param examen examen ya validado
     * @throws IllegalStateException si ocurre un error al escribir en MySQL
     */
    @Override
    public void guardar(Examen examen) {
        Paciente paciente = examen.getPaciente();
        try {
            Connection c = conexion.getConexion();
            c.setAutoCommit(false);
            try (PreparedStatement psPaciente = c.prepareStatement(SQL_PACIENTE);
                 PreparedStatement psExamen = c.prepareStatement(SQL_EXAMEN)) {
                psPaciente.setString(1, paciente.getNombre());
                psPaciente.setString(2, paciente.getTipoSangre().name());
                psPaciente.setString(3, paciente.getFactorRh().name());
                psPaciente.executeUpdate();

                psExamen.setString(1, examen.getCodigo());
                psExamen.setBigDecimal(2, BigDecimal.valueOf(examen.getCosto()));
                psExamen.setString(3, paciente.getNombre());
                psExamen.executeUpdate();

                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true); // la conexión es compartida: se deja como estaba
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar el examen en MySQL: " + e.getMessage(), e);
        }
    }

    /**
     * Consulta los exámenes guardados, ordenados por código, y los reconstruye
     * con su paciente.
     *
     * @return los exámenes almacenados en MySQL
     * @throws IllegalStateException si ocurre un error al leer de MySQL
     */
    @Override
    public List<Examen> listar() {
        List<Examen> resultado = new ArrayList<>();
        try {
            Connection c = conexion.getConexion();
            try (PreparedStatement ps = c.prepareStatement(SQL_LISTAR);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Paciente paciente = new Paciente(
                            rs.getString("nombre"),
                            TipoSangre.valueOf(rs.getString("tipo_sangre")),
                            FactorRh.valueOf(rs.getString("factor_rh")));
                    resultado.add(new ExamenBuilder()
                            .conCodigo(rs.getString("codigo"))
                            .conCosto(rs.getBigDecimal("costo").doubleValue())
                            .conPaciente(paciente)
                            .build());
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo listar los exámenes de MySQL: " + e.getMessage(), e);
        }
        return resultado;
    }

    /**
     * No aplica en este adaptador: MySQL ya guarda cada examen al registrarlo.
     *
     * @throws UnsupportedOperationException siempre
     */
    @Override
    public void serializar() {
        throw new UnsupportedOperationException(
                "MySQL guarda cada examen al registrarlo; no necesita serializar.");
    }

    /**
     * No aplica en este adaptador: MySQL ya guarda cada examen al registrarlo.
     *
     * @return nunca retorna normalmente
     * @throws UnsupportedOperationException siempre
     */
    @Override
    public List<Examen> deserializar() {
        throw new UnsupportedOperationException(
                "MySQL guarda cada examen al registrarlo; no necesita deserializar.");
    }
}