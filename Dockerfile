# ---------- Build étape ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -Dmaven.test.skip=true package



# ---------- Payara 6 étape ----------
FROM payara/server-full:6.2024.8-jdk17

ENV PAYARA_HOME=/opt/payara/appserver
ENV DOMAIN_HOME=${PAYARA_HOME}/glassfish/domains/domain1

# --- Driver MySQL ---
ADD https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.5.0/mysql-connector-j-9.5.0.jar \
    ${DOMAIN_HOME}/lib/ext/mysql-connector-j-9.5.0.jar

# --- Copier le WAR ---
COPY --from=build /app/target/bankease.war /tmp/bankease.war

# --- Copier le script ---
COPY payara-setup.asadmin ${PAYARA_HOME}/config/payara-setup.asadmin

# --- Exécuter la configuration JDBC + déploiement ---
RUN ${PAYARA_HOME}/bin/asadmin start-domain && \
    ${PAYARA_HOME}/bin/asadmin --user admin --passwordfile=${PAYARA_HOME}/passwordFile \
        multimode < ${PAYARA_HOME}/config/payara-setup.asadmin && \
    ${PAYARA_HOME}/bin/asadmin --user admin --passwordfile=${PAYARA_HOME}/passwordFile \
        deploy /tmp/bankease.war && \
    ${PAYARA_HOME}/bin/asadmin stop-domain

EXPOSE 8080 4848
