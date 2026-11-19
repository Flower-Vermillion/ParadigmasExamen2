package co.edu.poli.Examen2.infraestructura.persistencia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import co.edu.poli.Examen2.aplicacion.puerto.salida.ExamenRepository;
import co.edu.poli.Examen2.dominio.Examen;
import co.edu.poli.Examen2.dominio.FactorRh;
import co.edu.poli.Examen2.dominio.Paciente;
import co.edu.poli.Examen2.dominio.TipoSangre;

/**
 * Adaptador de salida que mantiene los exámenes en memoria y los serializa a
 * un archivo binario cuando se pide.
 * <p>
 * El dominio no es serializable: este adaptador convierte los objetos a DTO
 * propios (privados) antes de escribir y los reconstruye al leer.
 */
public class ArchivoExamenRepository implements ExamenRepository {

    /** Archivo que se usa cuando no se indica otra ruta. */
    private static final Path ARCHIVO_POR_DEFECTO = Path.of("examenes.ser");

    /** Archivo donde se escribe y desde donde se lee el estado serializado. */
    private final Path archivo;

    /** Exámenes registrados, mantenidos en memoria. */
    private final List<Examen> examenes = new ArrayList<>();

    /**
     * Crea el repositorio usando el archivo por defecto, {@code examenes.ser},
     * en el directorio de trabajo de la aplicación.
     */
    public ArchivoExamenRepository() {
        this(ARCHIVO_POR_DEFECTO);
    }

    /**
     * Crea el repositorio usando un archivo concreto.
     *
     * @param archivo ruta del archivo donde se serializan los exámenes
     */
    public ArchivoExamenRepository(Path archivo) {
        this.archivo = archivo;
    }

    /**
     * Agrega el examen a la lista en memoria. No escribe en disco: para
     * conservarlo hay que llamar a {@link #serializar()}.
     *
     * @param examen examen ya validado
     */
    @Override
    public void guardar(Examen examen) {
        examenes.add(examen);
    }

    /**
     * @return una copia inmutable de los exámenes que hay en memoria
     */
    @Override
    public List<Examen> listar() {
        return List.copyOf(examenes);
    }

    /**
     * Escribe en el archivo los exámenes que hay en memoria, convertidos a
     * DTO. Reemplaza el contenido anterior del archivo.
     *
     * @throws UncheckedIOException si no se puede escribir el archivo
     */
    @Override
    public void serializar() {
        ArrayList<ExamenDto> dtos = new ArrayList<>();
        for (Examen examen : examenes) {
            dtos.add(aDto(examen));
        }
        try (ObjectOutputStream salida = new ObjectOutputStream(Files.newOutputStream(archivo))) {
            salida.writeObject(dtos);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escribir " + archivo + ": " + e.getMessage(), e);
        }
    }

    /**
     * Lee el archivo, reconstruye los exámenes a partir de los DTO y los deja
     * como contenido actual del repositorio, reemplazando los que había en
     * memoria.
     *
     * @return una copia inmutable de los exámenes cargados
     * @throws IllegalStateException si el archivo no existe, no tiene el
     *                               formato esperado o no se puede leer
     */
    @Override
    public List<Examen> deserializar() {
        if (!Files.exists(archivo)) {
            throw new IllegalStateException("No existe el archivo " + archivo + ". Serialice primero.");
        }
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(archivo))) {
            Object leido = entrada.readObject();
            if (!(leido instanceof List<?> lista)) {
                throw new IllegalStateException("El archivo " + archivo + " no tiene el formato esperado.");
            }
            List<Examen> cargados = new ArrayList<>();
            for (Object elemento : lista) {
                if (elemento instanceof ExamenDto dto) {
                    cargados.add(aDominio(dto));
                }
            }
            examenes.clear();
            examenes.addAll(cargados);
            return List.copyOf(examenes);
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("No se pudo leer " + archivo + ": " + e.getMessage(), e);
        }
    }

    // ---------- Mapeo dominio <-> DTO ----------

    /**
     * Convierte un examen del dominio en su DTO de persistencia.
     *
     * @param examen examen del dominio
     * @return el DTO equivalente
     */
    private static ExamenDto aDto(Examen examen) {
        Paciente p = examen.getPaciente();
        PacienteDto pacienteDto = p == null ? null
                : new PacienteDto(p.getNombre(), p.getTipoSangre(), p.getFactorRh());
        return new ExamenDto(examen.getCodigo(), examen.getCosto(), pacienteDto);
    }

    /**
     * Reconstruye un examen del dominio a partir de su DTO de persistencia.
     *
     * @param dto DTO leído del archivo
     * @return el examen del dominio equivalente
     */
    private static Examen aDominio(ExamenDto dto) {
        PacienteDto p = dto.paciente();
        Paciente paciente = p == null ? null
                : new Paciente(p.nombre(), p.tipoSangre(), p.factorRh());
        return new Examen(dto.codigo(), dto.costo(), paciente);
    }

    // ---------- DTO de persistencia (solo existen aquí) ----------

    /**
     * Representación serializable de un {@link Paciente}.
     *
     * @param nombre     nombre del paciente
     * @param tipoSangre grupo sanguíneo
     * @param factorRh   factor Rh
     */
    private record PacienteDto(String nombre, TipoSangre tipoSangre, FactorRh factorRh)
            implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    /**
     * Representación serializable de un {@link Examen}.
     *
     * @param codigo   código del examen
     * @param costo    costo del examen
     * @param paciente DTO del paciente al que pertenece
     */
    private record ExamenDto(String codigo, double costo, PacienteDto paciente)
            implements Serializable {
        private static final long serialVersionUID = 1L;
    }
}