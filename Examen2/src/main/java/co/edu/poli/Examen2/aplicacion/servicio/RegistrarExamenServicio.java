package co.edu.poli.Examen2.aplicacion.servicio;

import java.util.List;
import java.util.Objects;

import co.edu.poli.Examen2.aplicacion.puerto.entrada.RegistrarExamenCasoDeUso;
import co.edu.poli.Examen2.aplicacion.puerto.salida.ExamenRepository;
import co.edu.poli.Examen2.dominio.Examen;
import co.edu.poli.Examen2.dominio.FactorRh;
import co.edu.poli.Examen2.dominio.Paciente;
import co.edu.poli.Examen2.dominio.TipoSangre;
import co.edu.poli.Examen2.dominio.builder.ExamenBuilder;
import co.edu.poli.Examen2.dominio.chain.ExamenHandler;
import co.edu.poli.Examen2.dominio.chain.Resultado;

/**
 * Caso de uso de registro: construye el examen con el Builder, lo valida con
 * la cadena de responsabilidad y aplica las reglas que necesitan consultar lo
 * ya registrado (código repetido y paciente con datos inconsistentes).
 */
public class RegistrarExamenServicio implements RegistrarExamenCasoDeUso {

    private final ExamenRepository repositorio;
    private final ExamenHandler validadores;

    public RegistrarExamenServicio(ExamenRepository repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio es obligatorio");
        this.validadores = ExamenHandler.crearCadena();
    }

    @Override
    public void registrar(String codigo, double costo, String nombrePaciente,
                          TipoSangre tipoSangre, FactorRh factorRh) {
        Paciente ingresado = new Paciente(nombrePaciente, tipoSangre, factorRh);
        Examen candidato = new ExamenBuilder()
                .conCodigo(codigo)
                .conCosto(costo)
                .conPaciente(ingresado)
                .build();

        Resultado resultado = validadores.validar(candidato);
        if (!resultado.estaValido()) {
            throw new IllegalArgumentException(resultado.getMotivo());
        }

        List<Examen> existentes = repositorio.listar();
        if (existentes.contains(candidato)) {
            throw new IllegalArgumentException(
                    "Ya existe un examen con el código " + candidato.getCodigo() + ".");
        }

        Paciente paciente = unificarPaciente(ingresado, existentes);
        Examen examen = paciente == ingresado
                ? candidato
                : new ExamenBuilder()
                        .conCodigo(candidato.getCodigo())
                        .conCosto(candidato.getCosto())
                        .conPaciente(paciente)
                        .build();

        repositorio.guardar(examen);
    }

    /**
     * Si el paciente ya tiene exámenes, reutiliza el existente; si sus datos de
     * sangre no coinciden con los ingresados, rechaza el registro.
     */
    private Paciente unificarPaciente(Paciente nuevo, List<Examen> existentes) {
        for (Examen examen : existentes) {
            Paciente actual = examen.getPaciente();
            if (actual.equals(nuevo)) {
                if (actual.getTipoSangre() != nuevo.getTipoSangre()
                        || actual.getFactorRh() != nuevo.getFactorRh()) {
                    throw new IllegalArgumentException("El paciente " + actual.getNombre()
                            + " ya está registrado con sangre " + actual.getGrupoSanguineo()
                            + " y no coincide con " + nuevo.getGrupoSanguineo() + ".");
                }
                return actual;
            }
        }
        return nuevo;
    }
}
