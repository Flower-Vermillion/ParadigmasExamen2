# Registro de Exámenes de Sangre

Aplicación de escritorio (JavaFX) para registrar exámenes de sangre de pacientes. Es la refactorización de un proyecto base (fork de `WolframDev/ParadigmasExamen2`) hacia **Arquitectura Hexagonal** (puertos y adaptadores), incorporando tres patrones de diseño GoF: **Builder**, **Chain of Responsibility** y **Decorator**.

**Autor:** David Alvarez Cruz

## Funcionalidades

- **Registrar** un examen: código, costo, nombre del paciente, tipo de sangre (A, B, AB, O) y factor Rh.
- **Listar** los exámenes registrados.
- **Serializar** los exámenes a un archivo (`examenes.ser`) y **deserializarlos** de nuevo.

Reglas de negocio aplicadas al registrar:

- El código tiene el formato `LETRAS-NÚMEROS` (por ejemplo, `EXM-001`) y no puede repetirse.
- El costo debe ser mayor que cero.
- El nombre del paciente tiene al menos 3 letras; además se exige tipo de sangre y factor Rh.
- Un paciente puede tener varios exámenes. Se identifica por su nombre, y su tipo de sangre y factor Rh no pueden cambiar entre exámenes.

## Arquitectura

La aplicación sigue la Arquitectura Hexagonal: el dominio y los casos de uso no dependen de JavaFX ni del almacenamiento. Las dependencias apuntan siempre hacia el núcleo.

```
co.edu.poli.Examen2
├── vista                      App (raíz de composición: conecta todas las piezas)
├── infraestructura
│   ├── ui                     ControlFormExamen (adaptador de entrada, JavaFX)
│   └── persistencia           ArchivoExamenRepository, MySqlExamenRepository, ConexionBD
│       └── decorator          ExamenRepositoryDecorator, ...ConLog, ...ConCache
├── aplicacion
│   ├── puerto
│   │   ├── entrada            Registrar/Listar/Serializar/DeserializarExamenCasoDeUso
│   │   └── salida             ExamenRepository
│   └── servicio               Registrar/Listar/Serializar/DeserializarExamenServicio
└── dominio                    Examen, Paciente, TipoSangre, FactorRh
    ├── builder                ExamenBuilder
    └── chain                  ExamenHandler, Resultado, CodigoHandler, CostoHandler, PacienteHandler
```

Decisiones de diseño relevantes:

- **El dominio no depende de la persistencia.** `Examen` y `Paciente` no son serializables: `ArchivoExamenRepository` los convierte a DTO privados (`ExamenDto` y `PacienteDto`) antes de escribir el archivo y los reconstruye al leerlo.
- **El puerto de salida `ExamenRepository` tiene dos adaptadores intercambiables:** archivo (por defecto) y MySQL. Cambiar de uno a otro solo requiere modificar `App`.
- **`ConexionBD` mantiene una única conexión compartida** a MySQL (patrón Singleton) y lee su configuración de un archivo `.env`.

Los diagramas (`arquitectura2`, `dominio2` y `dominio3`) están en la **wiki** del repositorio.

## Patrones de diseño GoF

| Patrón | Categoría | Dónde | Qué resuelve |
|---|---|---|---|
| Builder | Creacional | `dominio.builder` | Arma el `Examen` paso a paso y evita crear exámenes incompletos. |
| Chain of Responsibility | Comportamiento | `dominio.chain` | Cada regla de validación (código, costo, paciente) es un eslabón independiente y se pueden agregar o quitar sin tocar el servicio. |
| Decorator | Estructural | `infraestructura.persistencia.decorator` | Agrega log y caché al repositorio sin modificar los adaptadores de persistencia. |

Adicionalmente, `ConexionBD` usa el patrón **Singleton** para compartir una sola conexión a la base de datos. Es un detalle de implementación de la capa de infraestructura y no forma parte de los tres patrones analizados.

El detalle de cada patrón (problema, justificación y beneficios) está en el informe `actividad3.pdf`, en la wiki.

## Tecnologías

- Java 21
- JavaFX 21
- Maven
- JUnit 5
- MySQL 8 y `mysql-connector-j` (opcional, solo para la persistencia en base de datos)
- `dotenv-java` (lectura del archivo `.env`)

## Requisitos

- JDK 21 instalado (no basta el JRE incluido en Eclipse: el proyecto usa el módulo `java.sql`).
- Maven 3.9 o superior.
- Eclipse IDE con soporte Maven (o cualquier IDE compatible).
- MySQL 8, solo si se quiere usar la persistencia en base de datos.

## Instalación

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/<tu-usuario>/ParadigmasExamen2.git
   ```
2. En Eclipse: `File > Import... > Maven > Existing Maven Projects` y elegir la carpeta `Examen2` del repositorio (la que contiene el `pom.xml`).
3. Verificar que el proyecto use el JDK 21: `Project > Properties > Java Build Path > Libraries > JRE System Library`.
4. Descargar las dependencias: clic derecho sobre el proyecto, `Maven > Update Project...`.

## Configuración

Por defecto la aplicación guarda los exámenes en memoria y los serializa al archivo `examenes.ser` (en la carpeta desde la que se ejecuta). No requiere configurar nada.

Para usar **MySQL**:

1. Ejecutar el script `esquema.sql` en el servidor MySQL.
2. Crear un archivo `.env` en la raíz del proyecto (la carpeta `Examen2`) con los datos de conexión:
   ```
   DB_URL=jdbc:mysql://localhost:3306/examenes?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   DB_USER=root
   DB_PASSWORD=su_clave
   ```
   Si el archivo o alguna variable no existe, se usan esos mismos valores por defecto, con la contraseña vacía. También se respetan las variables de entorno del sistema.
3. No subir el archivo `.env` al repositorio: agregarlo al `.gitignore`.
4. Verificar que el `pom.xml` incluya las dependencias `mysql-connector-j` y `dotenv-java`.
5. En `App.java`, cambiar `new ArchivoExamenRepository()` por `new MySqlExamenRepository()`.

Con MySQL, cada examen se guarda al registrarlo, por lo que los botones Serializar y Deserializar informan que no aplican. La conexión es compartida; para cerrarla al terminar la aplicación se puede llamar a `ConexionBD.getInstancia().cerrarConexion()`.

## Ejecución

Desde Eclipse: clic derecho sobre el proyecto, `Run As > Maven build...` y en *Goals* escribir `javafx:run`.

Desde la terminal, dentro de la carpeta `Examen2`:

```bash
mvn clean javafx:run
```

## Pruebas unitarias

```bash
mvn test
```

Las pruebas están en `src/test/java` y usan JUnit 5. Cubren, entre otras, las reglas de validación de la cadena de responsabilidad (por ejemplo, `PacienteHandlerTest`).

## Documentación JavaDoc

```bash
mvn javadoc:javadoc
```

La documentación se genera en `target/site/apidocs`. También se puede generar desde Eclipse con `Project > Generate Javadoc...`.

## Análisis con SonarQube

Con un servidor SonarQube en ejecución y un token de acceso:

```bash
mvn clean verify sonar:sonar -Dsonar.projectKey=Examen2 -Dsonar.host.url=http://localhost:9000 -Dsonar.token=<TOKEN>
```

La imagen con el resumen del análisis está en la wiki del repositorio.

## Documentos adicionales (wiki)

Los informes (`actividad1.pdf`, `actividad3.pdf`), los diagramas (`arquitectura2`, `dominio2`, `dominio3`), la imagen del análisis de SonarQube y el video de sustentación (`video5`) están en la sección Wiki de este repositorio.

## Declaración de uso de inteligencia artificial

Se utilizó **Claude** (Anthropic) como asistente durante el desarrollo, en las siguientes actividades:

- Orientación para importar el proyecto en Eclipse y configurar el entorno (JDK y módulos de JavaFX).
- Apoyo en el diagnóstico del diseño original (acoplamiento, cohesión, extensibilidad y mantenibilidad).
- Propuesta de la Arquitectura Hexagonal (paquetes, puertos y adaptadores) y revisión de los diagramas UML.
- Análisis y selección de los patrones GoF (Builder, Chain of Responsibility y Decorator).
- Generación de la base del código de dominio, aplicación e infraestructura, y de la documentación JavaDoc.
- Corrección de pruebas unitarias y redacción de este README.

El código y los diagramas generados con apoyo de IA fueron revisados e integrados por el autor.
