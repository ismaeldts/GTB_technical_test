# ── Stage 1: Build ──────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copiar archivos de Gradle primero para aprovechar caché de dependencias
COPY gradle/          gradle/
COPY gradlew          gradlew
COPY build.gradle     build.gradle
COPY settings.gradle  settings.gradle

# Dar permisos de ejecución y descargar dependencias (capa cacheada)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# Copiar código fuente
COPY src/ src/

# Construir JAR sin ejecutar tests (los tests se corren en CI aparte)
RUN ./gradlew bootJar --no-daemon -x test

# ── Stage 2: Runtime ───────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear usuario no-root por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar el JAR desde la etapa de build
COPY --from=build /app/build/libs/*.jar app.jar

# Cambiar a usuario no-root
USER appuser

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

