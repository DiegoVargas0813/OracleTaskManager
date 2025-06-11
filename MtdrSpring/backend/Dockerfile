FROM openjdk:17
#COPY pom.xml /tmp/
#COPY src /tmp/src/
WORKDIR /tmp/
EXPOSE 8080
COPY target/*.jar MyTodoList.jar
ENTRYPOINT ["java","-jar","MyTodoList.jar"]