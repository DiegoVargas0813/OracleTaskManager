# Oracle Task Manager
This is the backend component of the Oracle Task Manager project. It is a Spring Boot application that manages tasks, sprints, users, and bot integration using Oracle Cloud and Telegram. It is fully containerized with Docker and designed for maintainable local development and deployment.

![image](https://user-images.githubusercontent.com/7783295/116454396-cbfb7a00-a814-11eb-8196-ba2113858e8b.png)
  

## MyToDo React JS
The `mtdrworkshop` repository hosts the materiald (code, scripts and instructions) for building and deploying Cloud Native Application using a Java/Helidon backend


### Requirements
The lab executes scripts that require the following software to run properly: (These are already installed on and included with the OCI Cloud Shell)
* oci-cli
* python 2.7^
* terraform
* kubectl
* mvn (maven) 


🛠️ Technologies Used

Java 17

Spring Boot 2.6.4

Spring Web / JPA / Validation

Oracle JDBC Driver (ojdbc11)

Maven

Docker

JUnit & Mockito for testing

Telegram Bots API (Java SDK)

📁 Project Structure

MtdrSpring/
├── src/main/java/com/springboot/MyTodoList/
│   ├── controller/               # REST controllers
│   ├── model/                   # JPA entities
│   ├── repository/              # Data access layer
│   ├── service/                 # Business logic
│   ├── config/                  # Oracle DB configuration
│   ├── telegram/                # Telegram bot handlers
│   └── MyTodoListApplication.java
├── src/test/                    # JUnit + Mockito tests
├── Dockerfile
├── pom.xml

⚙️ Oracle Database Integration

The backend connects to an Oracle Autonomous Database using:

Oracle Wallet configuration via TNS_ADMIN

Custom OracleConfiguration.java class to define the datasource

Environment-based credentials

Make sure to set the TNS_ADMIN path and update application.properties:

spring.datasource.url=jdbc:oracle:thin:@javadev_high?TNS_ADMIN=/path/to/Wallet
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

🤖 Telegram Bot Integration

The TaskItemBotController class manages a Telegram bot using TelegramLongPollingBot. It supports:

Menu interaction

Creating and completing tasks

Querying sprints and users

The bot is automatically registered using:

@Bean
public TelegramBotsApi telegramBotsApi(TaskItemBotController controller) { ... }

Set your bot token in application.properties:

telegram.bot.token=YOUR_BOT_TOKEN

🧪 Testing

Unit tests use @WebMvcTest to isolate controller logic and avoid real DB writes. Example:

@WebMvcTest(TaskController.class)
class TaskControllerTest {
    ...
}

Run tests with:

mvn test

🐳 Docker Setup

Dockerfile (multi-stage)

FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/MyTodoList-0.0.1-SNAPSHOT.jar MyTodoList.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "MyTodoList.jar"]

Build & Run

# Build image
docker build -t oraclebackend-dev .

# Run container
docker run -p 8080:8080 --name oraclebackend-container oraclebackend-dev

The backend will be live at http://localhost:8080

🧩 Environment Notes

Make sure to include your Oracle wallet folder during container mounting if using Docker locally.

The Telegram bot can only run once at a time (error 409 if multiple sessions).

Use .env or pass secrets securely when deploying in production.

✅ Summary

This backend provides a complete API with Oracle DB integration and dynamic Telegram automation for task management. It is designed for scalable local development with Docker and is production-ready with secure configuration.

Feel free to contribute or report issues. Make sure to follow the README instructions and test before deploying.


