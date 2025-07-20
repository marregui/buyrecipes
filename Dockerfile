FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src/ src/

RUN chmod +x gradlew
RUN ./gradlew build -x test
RUN mkdir -p /app/data

EXPOSE 8080

HEALTHCHECK --interval=30s \
  --timeout=10s \
  --start-period=30s \
  --retries=3 \
  CMD curl -f http://localhost:8080/recipes/test || exit 1

CMD ["./gradlew", "run"]