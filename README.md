# Documentación de la API

## Descripción General

En este archivo se realizará la sustentación de las soluciones y mejoras que considero necesarias para el proyecto, así como otros temas de interés relacionados con la prueba.

## Estructura del Proyecto

En este caso, opté por mantener la arquitectura por capas que se plantea, pero le adicioné ciertas mejoras que considero importantes:

- Se añadió un manejo de paquetes más escalable y fácil de entender.
- Para el paquete **controller**, se realizó una separación clara entre los **handlers** para casos exitosos y los **handlers** para excepciones o respuestas diferentes a las de tipo 200'.
- Además, creé un paquete de excepciones y centralicé su manejo en un **handler** de excepciones, con un cuerpo de respuesta genérico para responder a los errores.
- En la capa de servicio, se añadió una **interfaz** para mantener el principio de segregación de interfaces y contrato (interfaz-implementación).
- También se añadió un paquete de **excepciones personalizadas** para evitar problemas derivados del uso de excepciones genéricas y tener un control más claro sobre ellas.
- Se agregó un paquete de **utilidades** con métodos estáticos útiles para el proyecto.
- se adiciono manejo de **lombook** para mejor manejo de codigo repetitivo en clases y que sea mas limpio el codigo.

## Identificación y Solución de Errores Básicos de Manejo de Beans en Spring Boot

Para esta clase se procedió a identificar errores básicos en el manejo de **beans** en Spring Boot.

### Problema Identificado:

La clase no tenía la anotación adecuada para ser gestionada por el contenedor de Spring. Es decir, no se estaba marcando como un **bean** de Spring, lo que impedía su inyección en otras partes del proyecto. Sin esta anotación, Spring no sabría cómo gestionar la clase y, como resultado, se generaría un error de compilación si intentamos inyectarla.

### Solución Propuesta:

Para solucionar este problema, identificamos que la clase debía ser un servicio. La solución fue marcar la clase con la anotación @Service. adicional se corrige error en la ruta de la clase  de controller.
para solucionar esto se corrigen rutas y adicional se aplica mejora en la respuesta del controlador, utilizando respuesta generica y manejo de exepciones para el controlador 
Esta anotación indica a Spring que esta clase es un servicio y debe ser gestionada como un bean dentro del contenedor de Spring. Al hacerlo, la clase puede ser inyectada correctamente en cualquier otra clase donde sea necesario.
Adicional, realizamos una implementación de HashMap para simular registros de base de datos y poder utilizar el servicio. Adicional, el método como estaba habría tenido problemas de NullPointerException o excepción no controlada si no encuentra registros, por lo cual se procede a implementar manejo de excepciones.
#### Código Ajustado:

```java
import org.springframework.stereotype.Service;


@Service
public class UserService {

    //seria nuestra data similar a un registro de base de datos
    private final Map<Long, User> userDatabase = new HashMap<>();

    @PostConstruct
    public void init() {
        userDatabase.put(1L, new User(1L, "Juan Pérez", "juan.perez@ejemplo.com"));
        userDatabase.put(2L, new User(2L, "María Gómez", "maria.gomez@ejemplo.com"));
        userDatabase.put(3L, new User(3L, "Carlos Ruiz", "carlos.ruiz@ejemplo.com"));
    }

    public User findById(Long id) {
        if (id == null) {
            throw new UserException("El ID no puede ser nulo");
        }

        User user = userDatabase.get(id);

        if (user == null) {
            throw new UserException("Usuario no encontrado para ID: " + id);
        }

        return user;
    }
}

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
        try {
            var user = userService.findById(id);
            ApiResponse<User> response = ApiResponse.success(user);
            return ResponseEntity.ok(response);
        } catch (UserException ex) {
            ApiResponse<User> response = ApiResponse.error("Usuario no encontrado", ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<User> response = ApiResponse.error("Error desconocido", ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
```
## Estructura de la Respuesta

Todas las respuestas de la API siguen la misma estructura:

- `status`: Indica el estado de la respuesta. Puede ser `"success"` o `"error"`.
- `data`: Contiene los datos reales si la solicitud fue exitosa.
- `error`: Contiene el mensaje de error si algo salió mal.
- `detail`: Proporciona detalles adicionales sobre el error.

### Ejemplo de Respuesta (Éxito):

```json
{
    "status": "success",
    "data": {
        "id": 1,
        "name": "Juan Pérez",
        "email": "juan.perez@ejemplo.com"
    },
    "error": null,
    "detail": null
}
```
## Correccion Compatibilidad de versiones en Gradle

### Solución al Conflicto de Versiones de Java y Spring Boot

Para resolver el problema entre las versiones de Java y la implementación de Spring Boot, se identificó que estábamos usando una versión antigua de Spring Boot que no era compatible con **Java 21**. La solución fue actualizar a una versión de Spring Boot compatible con Java 21.

### Cambios Realizados:

1. Se actualizó **Spring Boot** a una versión compatible con **Java 21**.
2. Se eliminaron dependencias **deprecadas** para evitar problemas de vulnerabilidades o conflictos con futuras implementaciones, especialmente si el proyecto se va a desplegar en plataformas como **Azure DevOps**.

### Fragmento de `build.gradle`:

```gradle
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.5.5'
	id 'io.spring.dependency-management' version '1.1.7'
}

group = 'com.prueba'
version = '0.0.1-SNAPSHOT'

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-web'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

	// Gestión de código rápido
	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'

	// Gestión de logs
	implementation 'org.springframework.boot:spring-boot-starter-logging'
}

tasks.named('test') {
	useJUnitPlatform()
}
```
 ##  Consumo de una API externa
En lugar de usar clases tradicionales para mapear la respuesta de la API, se utilizó un record de Java para representar los datos que se reciben. Los records son una forma sencilla y eficiente de trabajar con datos inmutables, y ayudan a mantener el código más limpio. Aquí se muestra cómo se mapea la respuesta de la API:

```java
public record ApiResponseDTO(
@JsonProperty("userId") String userId,
@JsonProperty("id") Integer id,
@JsonProperty("title") String title,
@JsonProperty("body") String body
) { }
```

Manejo de Errores

Se implementó un manejo de errores para asegurarnos de que la aplicación pueda responder adecuadamente en caso de fallos. Dependiendo del tipo de error, se maneja de distintas maneras:

Timeout: Si la API externa no responde dentro del tiempo esperado, lanzamos una excepción de tipo TimeoutException con un mensaje claro.

Errores del Cliente (4xx): Si la API devuelve un error como 400 Bad Request, lanzamos una excepción RestClientExeption indicando que la petición fue inválida.

Errores del Servidor (5xx): Si la API devuelve un error como 500 Internal Server Error, se intenta un reintento. Si el error persiste, lanzamos un RestClientExeption.

Reintentos Automáticos

Utilizamos RetryTemplate para realizar reintentos en caso de que la API externa devuelva un error del servidor (código 5xx). Si la API sigue fallando después de varios intentos, se lanza una excepción para que el consumidor del servicio sepa que algo ha ido mal.

Exposición del Endpoint

El servicio tiene un endpoint REST que consume la API externa y devuelve los datos. Si la llamada tiene éxito, la respuesta contiene los datos mapeados desde la API externa: