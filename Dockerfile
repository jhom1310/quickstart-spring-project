FROM eclipse-temurin:17-jdk-alpine

# Diretório da app
WORKDIR /app

# Copiar JAR
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Porta exposta
EXPOSE 8080

# Comando para rodar
ENTRYPOINT ["java", "-jar", "app.jar"]
