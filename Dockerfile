# Usa a imagem oficial do Tomcat com JDK 17
FROM tomcat:10.1-jdk21-temurin-noble

# Remove a aplicação ROOT padrão do tomcat
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copia o .war ja buildado para o ROOT do tomcat
COPY ./target/clima-api-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Expoe e porta 8080
EXPOSE 8080