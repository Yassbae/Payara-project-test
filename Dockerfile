# ---------- Build étape ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -Dmaven.test.skip=true package


# ---------- Payara étape ----------
FROM payara/server-full:6.2024.8-jdk17

ENV PAYARA_HOME=/opt/payara/appserver

# Ajouter driver MySQL (version 9.5.0)
ADD https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.5.0/mysql-connector-j-9.5.0.jar \
    ${PAYARA_HOME}/glassfish/domains/domain1/lib/ext/mysql-connector-j-9.5.0.jar

# Copier le WAR
COPY --from=build /app/target/bankease.war \
    ${PAYARA_HOME}/glassfish/domains/domain1/autodeploy/bankease.war

# Auto-config Payara JDBC pool & resource
COPY payara-setup.asadmin ${PAYARA_HOME}/config/payara-setup.asadmin

# Exécution auto des commandes Payara au boot
ENV AS_ADMIN_CMD_FILE=${PAYARA_HOME}/config/payara-setup.asadmin

EXPOSE 8080 4848
