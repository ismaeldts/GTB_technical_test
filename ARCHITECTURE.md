# 🏗️ BTG Pactual – Technical Test: Arquitectura de la Solución

## 📋 Tabla de Contenido

1. [Resumen General](#resumen-general)
2. [Stack Tecnológico](#stack-tecnológico)
3. [Arquitectura Hexagonal (Ports & Adapters)](#arquitectura-hexagonal-ports--adapters)
4. [Estructura de Módulos](#estructura-de-módulos)
5. [Flujo de una Petición](#flujo-de-una-petición)
6. [Modelo de Datos](#modelo-de-datos)
7. [Seguridad (JWT + Spring Security)](#seguridad-jwt--spring-security)
8. [Manejo Global de Excepciones](#manejo-global-de-excepciones)
9. [Migraciones con Flyway](#migraciones-con-flyway)
10. [Estrategia de Testing](#estrategia-de-testing)
11. [Contenerización (Docker)](#contenerización-docker)
12. [¿Por qué PostgreSQL y no una base NoSQL?](#por-qué-postgresql-y-no-una-base-nosql)
13. [Endpoints de la API](#endpoints-de-la-api)
14. [Infraestructura AWS (IaC)](#infraestructura-aws-iac)
15. [Punto 2 – Consulta SQL](#punto-2--consulta-sql)

---

## Resumen General

Este proyecto implementa una **API REST** para la gestión de **Fondos de Inversión** de BTG Pactual, permitiendo a los clientes registrarse, autenticarse y suscribirse/cancelar fondos voluntarios de pensión (FPV) y fondos de inversión colectiva (FIC).

La aplicación fue construida con **Spring Boot 3.5** y **Java 21**, siguiendo los principios de la **Arquitectura Hexagonal (Ports & Adapters)** para lograr una separación clara entre la lógica de negocio y los detalles de infraestructura.

---

## Stack Tecnológico

| Capa | Tecnología | Propósito |
|---|---|---|
| **Lenguaje** | Java 21 | LTS con records, pattern matching, virtual threads |
| **Framework** | Spring Boot 3.5.11 | Autoconfiguración, inyección de dependencias |
| **Seguridad** | Spring Security + JWT (jjwt 0.12.6) | Autenticación stateless con tokens |
| **Persistencia** | Spring Data JPA + Hibernate | ORM sobre PostgreSQL |
| **Base de datos** | PostgreSQL 16 | Base relacional ACID |
| **Migraciones** | Flyway | Versionado de esquema DDL/DML |
| **Documentación** | SpringDoc OpenAPI 2.8.6 | Swagger UI automático |
| **Build** | Gradle 8+ | Gestión de dependencias y build |
| **Contenedores** | Docker + Docker Compose | Empaquetado y orquestación |
| **Testing** | JUnit 5, Mockito, AssertJ, MockMvc | Tests unitarios e integración web |

---

## Arquitectura Hexagonal (Ports & Adapters)

La arquitectura hexagonal separa la aplicación en **tres capas concéntricas**, donde la lógica de negocio (dominio) no depende de ningún framework ni tecnología externa:

```
┌──────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE                        │
│  ┌──────────────┐                    ┌────────────────┐  │
│  │  Adapter IN  │                    │  Adapter OUT   │  │
│  │  (Web/REST)  │                    │ (Persistence)  │  │
│  │              │    ┌──────────┐    │                │  │
│  │  Controller ─┼───►│  APP     │◄───┼─ Postgres      │  │
│  │  DTOs        │    │  SERVICE │    │  Adapter       │  │
│  │  Mapper      │    │          │    │                │  │
│  │              │    └─────┬────┘    │  JPA Repo      │  │
│  └──────────────┘          │         │  Entity        │  │
│                      ┌─────▼────┐    │  Mapper        │  │
│                      │  DOMAIN  │    └────────────────┘  │
│                      │  Model   │                        │
│                      │  Ports   │    ┌────────────────┐  │
│                      │  Except. │    │  Adapter OUT   │  │
│                      └──────────┘    │  (External)    │  │
│                                      │  Cross-module  │  │
│                                      └────────────────┘  │
└──────────────────────────────────────────────────────────┘
```

### Principios aplicados

| Principio | Cómo se aplica |
|---|---|
| **Inversión de Dependencias** | El dominio define interfaces (ports). La infraestructura las implementa (adapters). |
| **Independencia del framework** | Los modelos de dominio son POJOs puros sin anotaciones de JPA ni Spring. |
| **Testeabilidad** | Los services dependen de interfaces, fácilmente mockeables sin levantar contexto. |
| **Aislamiento de módulos** | Los módulos se comunican a través de puertos externos (`ClienteExternalPort`, `FondoExternalPort`), nunca accediendo directamente a servicios de otros módulos. |

---

## Estructura de Módulos

El proyecto se organiza en **3 módulos de negocio** + **1 módulo compartido**:

```
com.btgtechnicaltest.demo/
├── cliente/                          # 👤 Módulo de Clientes
│   ├── domain/
│   │   ├── model/
│   │   │   └── Cliente.java                  # Entidad de dominio
│   │   ├── ports/
│   │   │   ├── SaveClienteRepository.java    # Puerto de salida
│   │   │   ├── FindByIdClienteRepository.java
│   │   │   ├── FindByEmailClienteRepository.java
│   │   │   ├── FindAllClienteRepository.java
│   │   │   └── DeleteByIdClienteRepository.java
│   │   └── exception/
│   │       └── ClienteAlreadyExistsException.java
│   ├── application/
│   │   └── ClienteService.java               # Caso de uso
│   └── infrastructure/
│       └── adapter/
│           ├── in/web/                        # Adaptador de entrada (REST)
│           │   ├── ClienteController.java
│           │   ├── dto/
│           │   └── mapper/
│           └── out/persistence/               # Adaptador de salida (PostgreSQL)
│               ├── PostgresSaveClienteRepository.java
│               ├── PostgresFindByIdClienteRepository.java
│               ├── PostgresFindByEmailClienteRepository.java
│               ├── PostgresFindAllClienteRepository.java
│               ├── PostgresDeleteByIdClienteRepository.java
│               ├── JpaClienteRepository.java
│               ├── entity/ClienteEntity.java
│               └── mapper/ClienteMapper.java
│
├── fondo/                            # 💰 Módulo de Fondos
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Fondo.java
│   │   │   └── Moneda.java          # Enum: COP, USD, MXN
│   │   ├── ports/
│   │   │   ├── SaveFondoRepository.java
│   │   │   ├── FindByIdFondoRepository.java
│   │   │   ├── FindByNombreFondoRepository.java
│   │   │   ├── FindAllFondoRepository.java
│   │   │   └── DeleteByIdFondoRepository.java
│   │   └── exception/
│   │       └── FondoAlreadyExistsException.java
│   ├── application/
│   │   └── FondoService.java
│   └── infrastructure/
│       └── adapter/
│           ├── in/web/
│           │   ├── FondoController.java
│           │   └── dto/
│           └── out/persistence/
│               ├── PostgresSaveFondoRepository.java
│               ├── PostgresFindByIdFondoRepository.java
│               ├── PostgresFindByNombreFondoRepository.java
│               ├── PostgresFindAllFondoRepository.java
│               ├── PostgresDeleteByIdFondoRepository.java
│               ├── JpaFondoRepository.java
│               ├── entity/FondoEntity.java
│               └── mapper/FondoMapper.java
│
├── suscripcion/                      # 📝 Módulo de Suscripciones
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Suscripcion.java      # Lógica de validación de saldo
│   │   │   ├── Transaccion.java      # Factory methods: apertura/cancelación
│   │   │   ├── ClienteInfo.java      # Value Object (vista del cliente)
│   │   │   └── FondoInfo.java        # Value Object (vista del fondo)
│   │   ├── ports/
│   │   │   ├── SaveSuscripcionRepository.java
│   │   │   ├── FindByIdSuscripcionRepository.java
│   │   │   ├── FindAllSuscripcionRepository.java
│   │   │   ├── FindByClienteIdSuscripcionRepository.java
│   │   │   ├── DeleteByIdSuscripcionRepository.java
│   │   │   ├── SaveTransaccionRepository.java
│   │   │   ├── FindByClienteIdTransaccionRepository.java
│   │   │   ├── ClienteExternalPort.java   # Puerto externo cross-module
│   │   │   └── FondoExternalPort.java     # Puerto externo cross-module
│   │   └── exception/
│   │       ├── ClienteNotFoundException.java
│   │       ├── FondoNotFoundException.java
│   │       ├── SaldoInsuficienteException.java
│   │       ├── MontoInsuficienteException.java
│   │       ├── SuscripcionDuplicadaException.java
│   │       └── SuscripcionNotFoundException.java
│   ├── application/
│   │   └── SuscripcionService.java   # Orquesta suscribir/cancelar
│   └── infrastructure/
│       └── adapter/
│           ├── in/web/
│           │   ├── SuscripcionController.java
│           │   ├── dto/
│           │   └── mapper/
│           ├── out/persistence/
│           │   ├── PostgresSaveSuscripcionRepository.java
│           │   ├── PostgresFindByIdSuscripcionRepository.java
│           │   ├── PostgresFindAllSuscripcionRepository.java
│           │   ├── PostgresFindByClienteIdSuscripcionRepository.java
│           │   ├── PostgresDeleteByIdSuscripcionRepository.java  # Soft delete
│           │   ├── PostgresSaveTransaccionRepository.java
│           │   ├── PostgresFindByClienteIdTransaccionRepository.java
│           │   ├── JpaTransaccionRepository.java
│           │   ├── repository/JpaSuscripcionRepository.java
│           │   ├── entity/
│           │   └── mapper/
│           └── out/external/          # Adaptadores cross-module
│               ├── ClienteModuleAdapter.java
│               └── FondoModuleAdapter.java
│
└── shared/                           # ⚙️ Módulo Compartido
    ├── application/
    │   └── AuthService.java          # Registro + Login
    └── infrastructure/
        ├── adapter/in/web/
        │   ├── AuthController.java
        │   └── dto/
        └── config/
            ├── OpenApiConfig.java
            ├── security/
            │   ├── SecurityConfig.java
            │   ├── JwtAuthenticationFilter.java
            │   ├── JwtService.java
            │   └── CustomUserDetailsService.java
            └── exception/
                └── GlobalExceptionHandler.java
```

### Comunicación Cross-Module

Los módulos **nunca** se llaman directamente entre sí. El módulo de **Suscripciones** necesita consultar datos de clientes y fondos, y lo hace mediante **puertos externos**:

```
Suscripcion Module                          Cliente Module
┌──────────────────┐                   ┌───────────────────┐
│  SuscripcionSvc  │                   │  PostgresFindBy.. │
│        │         │                   │  PostgresSave..   │
│        ▼         │                   └─────────▲─────────┘
│ ClienteExternal  │ ── implementa ──►           │
│     Port         │                   ClienteModuleAdapter
└──────────────────┘
```

Esto garantiza que si el módulo de clientes cambia su implementación interna, el módulo de suscripciones no se ve afectado.

---

## Flujo de una Petición

Ejemplo: **Suscribirse a un fondo** (`POST /api/suscripciones`)

```
 Cliente HTTP
     │
     ▼
 ┌─────────────────────┐
 │ JwtAuthFilter        │  ➜ Valida token JWT
 └──────────┬──────────┘
            ▼
 ┌─────────────────────┐
 │ SuscripcionController│  ➜ Deserializa DTO, extrae email del Authentication
 └──────────┬──────────┘
            ▼
 ┌─────────────────────┐
 │ SuscripcionService   │  ➜ Orquesta lógica de negocio:
 │                      │     1. Buscar cliente por email (ClienteExternalPort)
 │                      │     2. Buscar fondo por id (FondoExternalPort)
 │                      │     3. Validar no duplicada
 │                      │     4. Suscripcion.crear() → valida saldo en dominio
 │                      │     5. Debitar saldo al cliente
 │                      │     6. Guardar suscripción
 │                      │     7. Registrar transacción de apertura
 └──────────┬──────────┘
            ▼
 ┌─────────────────────┐
 │ PostgresAdapters     │  ➜ Mapea domain↔entity, delega a JpaRepository
 └──────────┬──────────┘
            ▼
      PostgreSQL 16
```

---

## Modelo de Datos

```
┌─────────────┐       ┌──────────────────┐       ┌──────────────┐
│  clientes   │       │  suscripciones   │       │    fondos    │
├─────────────┤       ├──────────────────┤       ├──────────────┤
│ id (PK)     │◄──────│ cliente_id (FK)  │       │ id (PK)      │
│ nombre      │       │ fondo_id (FK)    │──────►│ nombre       │
│ email (UQ)  │       │ monto            │       │ monto_minimo │
│ apellidos   │       │ activo           │       │ moneda       │
│ ciudad      │       │ id (PK)          │       │ categoria    │
│ password    │       └──────────────────┘       └──────────────┘
│ role        │
│ saldo       │       ┌──────────────────┐
└─────────────┘       │  transacciones   │
        │             ├──────────────────┤
        │             │ id (PK, UUID)    │
        └────────────►│ cliente_id (FK)  │
                      │ fondo_id (FK)    │──────► fondos
                      │ tipo             │  (apertura/cancelacion)
                      │ monto            │
                      │ fecha            │
                      └──────────────────┘
```

### Decisiones de diseño en el esquema

- **Soft delete en suscripciones**: El campo `activo` (boolean) permite cancelar sin perder historial. Un índice parcial `WHERE activo = TRUE` garantiza que un cliente no pueda tener dos suscripciones activas al mismo fondo.
- **UUID en transacciones**: Las transacciones usan `UUID` como PK para evitar colisiones y no exponer secuencias.
- **Saldo del cliente en `clientes`**: Se mantiene como campo calculable para evitar consultas costosas sumando transacciones.

---

## Seguridad (JWT + Spring Security)

```
┌────────────┐    POST /api/auth/login     ┌──────────────┐
│  Cliente   │ ──────────────────────────► │ AuthController│
│            │ ◄─────────────────────────  │              │
│            │    { token: "eyJ..." }      └──────┬───────┘
│            │                                     │
│            │    GET /api/suscripciones            ▼
│            │    Authorization: Bearer eyJ...  ┌──────────────────┐
│            │ ──────────────────────────────► │JwtAuthFilter      │
└────────────┘                                 │ ➜ Extrae token    │
                                               │ ➜ Valida firma    │
                                               │ ➜ Carga UserDet.  │
                                               │ ➜ Set SecurityCtx │
                                               └──────────────────┘
```

| Característica | Implementación |
|---|---|
| **Algoritmo** | HMAC-SHA256 (`HS256`) |
| **Expiración** | Configurable vía `JWT_EXPIRATION` (default: 24h) |
| **Sesión** | `STATELESS` – sin cookies ni sesión en servidor |
| **Endpoints públicos** | `/api/auth/**`, `/actuator/**`, `/swagger-ui/**` |
| **Endpoints protegidos** | Todos los demás requieren `Authorization: Bearer <token>` |
| **Password encoding** | `BCryptPasswordEncoder` |

---

## Manejo Global de Excepciones

El `GlobalExceptionHandler` centraliza el mapeo de excepciones de dominio a respuestas HTTP:

| Excepción | HTTP Status | Código |
|---|---|---|
| `ClienteAlreadyExistsException` | `409 Conflict` | Registro duplicado |
| `FondoAlreadyExistsException` | `409 Conflict` | Fondo ya existe |
| `SuscripcionDuplicadaException` | `409 Conflict` | Ya suscrito al fondo |
| `ClienteNotFoundException` | `404 Not Found` | Cliente no encontrado |
| `FondoNotFoundException` | `404 Not Found` | Fondo no encontrado |
| `SuscripcionNotFoundException` | `404 Not Found` | Suscripción no encontrada |
| `MontoInsuficienteException` | `400 Bad Request` | Monto no válido |
| `SaldoInsuficienteException` | `400 Bad Request` | Saldo insuficiente |
| `BadCredentialsException` | `401 Unauthorized` | Login fallido |
| `RuntimeException` (catch-all) | `404 Not Found` | Error genérico |

Todas las respuestas de error siguen un formato uniforme:

```json
{
  "timestamp": "2026-03-12T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Fondo no encontrado con id: 99"
}
```

---

## Migraciones con Flyway

Las migraciones se ejecutan automáticamente al iniciar la aplicación:

| Archivo | Descripción |
|---|---|
| `V1__create_schema.sql` | Crea tablas `clientes`, `fondos`, `suscripciones`, `transacciones` con sus FK, índices y constraints |
| `V2__seed_fondos.sql` | Inserta los 5 fondos iniciales de BTG Pactual |

Hibernate se configura con `ddl-auto: validate` para que **solo valide** el esquema contra las entidades sin modificarlo; todo cambio DDL pasa por Flyway.

---

## Estrategia de Testing

```
                        Pirámide de Tests
                        ─────────────────
                             /  \
                            / E2E \          (Docker Compose)
                           /───────\
                          /  Integ. \        @WebMvcTest + MockMvc
                         /───────────\
                        /   Unitarios \      @ExtendWith(MockitoExtension)
                       /───────────────\
```

### Tests implementados

| Tipo | Archivos | Qué valida |
|---|---|---|
| **Unitarios – Services** | `ClienteServiceTest`, `FondoServiceTest`, `SuscripcionServiceTest` | Lógica de negocio con mocks de ports |
| **Unitarios – Postgres Adapters** | 12 archivos `Postgres*Test` | Delegación correcta al JPA repository y mapeo entity↔domain |
| **Unitarios – External Adapters** | `ClienteModuleAdapterTest`, `FondoModuleAdapterTest` | Comunicación cross-module |
| **Integración Web – Controllers** | `FondoControllerTest`, `ClienteControllerTest`, `SuscripcionControllerTest` | Endpoints HTTP con `@WebMvcTest`, serialización JSON, status codes |

### Configuración de testing

- **`TestSecurityConfig`**: Proporciona un `SecurityFilterChain` permisivo para los tests `@WebMvcTest`.
- **`@AutoConfigureMockMvc(addFilters = false)`**: Desactiva filtros de seguridad en tests de controlador.
- **Autenticación en tests**: Se usa `.principal(new UsernamePasswordAuthenticationToken(...))` en lugar de `@WithMockUser` para endpoints que reciben `Authentication` como parámetro.

---

## Contenerización (Docker)

### Dockerfile (Multi-stage)

```dockerfile
# Stage 1: Build con JDK 21 Alpine
FROM eclipse-temurin:21-jdk-alpine AS build
# → Compila con Gradle, genera bootJar

# Stage 2: Runtime con JRE 21 Alpine  
FROM eclipse-temurin:21-jre-alpine
# → Imagen final ~200MB, usuario no-root, health check
```

### Docker Compose

```yaml
services:
  postgres:    # PostgreSQL 16 Alpine con healthcheck
  app:         # Spring Boot conectado a postgres vía variables de entorno
```

| Variable | Default | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://postgres:5432/btg_db` | URL de conexión |
| `DB_USERNAME` | `postgres` | Usuario de BD |
| `DB_PASSWORD` | `postgres` | Contraseña de BD |
| `JWT_SECRET` | Base64 encoded key | Clave de firma JWT |
| `JWT_EXPIRATION` | `86400000` (24h) | Expiración del token en ms |

---

## ¿Por qué PostgreSQL y no una base NoSQL?

La elección de **PostgreSQL** sobre una base de datos NoSQL (como MongoDB o DynamoDB) se fundamenta en la **naturaleza del dominio** del problema:

### 1. 🔗 Relaciones fuertemente acopladas

El modelo de negocio tiene **relaciones claras entre entidades**: un cliente tiene muchas suscripciones, cada suscripción pertenece a un fondo, cada transacción referencia un cliente y un fondo. PostgreSQL maneja estas relaciones con **foreign keys**, garantizando integridad referencial que una base NoSQL no ofrece nativamente.

```
clientes ──< suscripciones >── fondos
clientes ──< transacciones >── fondos
```

### 2. 🛡️ Consistencia transaccional (ACID)

Las operaciones de suscripción son **transacciones financieras** que requieren atomicidad:

- Debitar saldo del cliente **Y** crear la suscripción **Y** registrar la transacción deben ocurrir **todas o ninguna**.
- PostgreSQL garantiza **ACID** (Atomicity, Consistency, Isolation, Durability) de forma nativa.
- En una base NoSQL, lograr consistencia transaccional entre múltiples documentos/colecciones requiere implementar patrones complejos (Saga, Outbox) que añaden complejidad innecesaria.

### 3. 📊 Consultas complejas con JOINs

El negocio requiere consultas como:

- Obtener suscripciones activas de un cliente con datos del fondo.
- Historial de transacciones ordenado por fecha.
- Validar que no exista suscripción duplicada activa (`idx_suscripcion_activa` con `WHERE activo = TRUE`).

PostgreSQL resuelve esto con **JOINs, índices parciales y queries SQL estándar**. En NoSQL, estas consultas requerirían desnormalización, duplicación de datos o múltiples roundtrips.

### 4. 🔒 Índices parciales y constraints avanzados

PostgreSQL permite crear un **índice único parcial**:

```sql
CREATE UNIQUE INDEX idx_suscripcion_activa
    ON suscripciones (cliente_id, fondo_id) WHERE activo = TRUE;
```

Esto garantiza **a nivel de base de datos** que un cliente no pueda tener dos suscripciones activas al mismo fondo, algo imposible de replicar en la mayoría de bases NoSQL.

### 5. 📈 Volumen y patrón de acceso

El volumen de datos esperado (clientes, fondos, suscripciones) es de **escala moderada** con patrones de acceso **predecibles** (CRUD + consultas por ID/email). No existe necesidad de escalamiento horizontal masivo ni de esquemas flexibles, que son las fortalezas de NoSQL.

### Cuándo sí elegiríamos NoSQL

| Escenario | Base recomendada |
|---|---|
| Logs de auditoría de alto volumen | MongoDB / Elasticsearch |
| Caché de sesiones o datos temporales | Redis |
| Catálogo de productos con esquema variable | MongoDB / DynamoDB |
| Datos de series temporales (métricas) | TimescaleDB / InfluxDB |

**En resumen**: para un sistema financiero con relaciones claras, transacciones ACID y consultas relacionales, PostgreSQL es la elección natural y óptima.

---

## Endpoints de la API

### 🔐 Autenticación (`/api/auth`)

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| `POST` | `/api/auth/register` | Registrar nuevo cliente | ❌ |
| `POST` | `/api/auth/login` | Iniciar sesión (retorna JWT) | ❌ |

### 👤 Clientes (`/api/clientes`)

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| `POST` | `/api/clientes` | Crear cliente | ❌ |
| `GET` | `/api/clientes/{id}` | Obtener cliente por ID | ✅ |
| `PATCH` | `/api/clientes/saldo` | Recargar saldo (cliente autenticado) | ✅ |

### 💰 Fondos (`/api/fondos`)

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| `POST` | `/api/fondos` | Crear fondo | ✅ |
| `GET` | `/api/fondos` | Listar todos los fondos | ✅ |
| `GET` | `/api/fondos/{id}` | Obtener fondo por ID | ✅ |
| `PUT` | `/api/fondos/{id}` | Actualizar fondo | ✅ |
| `DELETE` | `/api/fondos/{id}` | Eliminar fondo | ✅ |

### 📝 Suscripciones (`/api/suscripciones`)

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| `POST` | `/api/suscripciones` | Suscribirse a un fondo | ✅ |
| `DELETE` | `/api/suscripciones/{id}` | Cancelar suscripción | ✅ |
| `GET` | `/api/suscripciones/mis-suscripciones` | Mis suscripciones activas | ✅ |
| `GET` | `/api/suscripciones/historial` | Historial de transacciones | ✅ |
| `GET` | `/api/suscripciones/{id}` | Obtener suscripción por ID | ✅ |
| `GET` | `/api/suscripciones` | Listar todas las suscripciones | ✅ |

> 📖 Documentación interactiva disponible en: `http://localhost:8080/swagger-ui.html`

---

## Punto 2 – Consulta SQL

En la carpeta `punto2/` se encuentra el archivo `consulta_punto2.sql` que resuelve el segundo punto de la prueba técnica.

### Esquema propuesto

Se crea un esquema `GTB` con las tablas: `cliente`, `sucursal`, `producto`, `inscripcion`, `disponibilidad` y `visitan`, representando las relaciones N:M entre las entidades.

### Consulta final

La consulta retorna los **nombres de clientes que están inscritos a al menos un producto disponible en alguna sucursal que han visitado**:

```sql
SELECT DISTINCT c.nombre
FROM cliente c
WHERE EXISTS (
    SELECT 1
    FROM inscripcion i
    JOIN disponibilidad d ON i.idproducto = d.idproducto
    JOIN visitan v 
        ON v.idcliente = c.id 
       AND v.idsucursal = d.idsucursal
    WHERE i.idcliente = c.id
);
```

Se utilizó `EXISTS` con subconsulta correlacionada por rendimiento: el motor de BD puede detenerse al encontrar la primera coincidencia sin recorrer todos los registros, a diferencia de un `JOIN` + `DISTINCT` que procesaría todas las combinaciones.

---

## 🚀 Cómo ejecutar

```bash
# 1. Clonar el repositorio
git clone <repo-url> && cd demo

# 2. Levantar con Docker Compose
docker compose up -d

# 3. Verificar salud
curl http://localhost:8080/actuator/health

# 4. Abrir Swagger UI
open http://localhost:8080/swagger-ui.html
```

---

*Desarrollado por Ismael Trocha – Prueba Técnica BTG Pactual 2026*

