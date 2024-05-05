# Use an official OpenJDK runtime as a parent image
FROM maven:3.8.5-openjdk-17 AS build

# Set the Data Volume
VOLUME /root/.m2

# Set the working directory in the container
WORKDIR /home/app

# Expose port 8080
EXPOSE 8080

# Copy the Application source files and Maven project files (pom.xml)
COPY src ./src
COPY pom.xml ./

# Copy Root CA certificate
COPY rootCA.crt $JAVA_HOME/lib/security/

# Command to add Root CA certificate to default truststore of the JVM
RUN $JAVA_HOME/bin/keytool -import -trustcacerts -noprompt -file $JAVA_HOME/lib/security/rootCA.crt \
-alias shdCA -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit

# Command to build the application JAR
RUN mvn -f ./pom.xml clean package -DskipTests

# Copy the application JAR
COPY ./target/*.jar app.jar

# Command to run the application
CMD ["java", "-jar", "app.jar"]
