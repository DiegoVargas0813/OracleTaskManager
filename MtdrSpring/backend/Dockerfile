FROM openjdk:17
#COPY pom.xml /tmp/
#COPY src /tmp/src/
WORKDIR /tmp/
EXPOSE 8080
COPY target/*.jar MyTodoList.jar
ENTRYPOINT ["java", "-jar", "MyTodoList-0.0.1-SNAPSHOT.jar"]
