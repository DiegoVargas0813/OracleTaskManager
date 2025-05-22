Oracle Task Manager

A robust full-stack application powered by a Spring Boot backend and a React + Vite frontend. This project leverages Docker for seamless local development and deployment.

Project Structure

OracleTaskManager/
├── MtdrSpring/       # Backend (Spring Boot)
│   └── Dockerfile
├── OracleFront/      # Frontend (React + Vite)
│   └── Dockerfile

Prerequisites

Docker: https://docs.docker.com/get-docker/

(Optional) Maven: required only if building the backend manually

Running the Backend

Navigate to the backend directory:

cd MtdrSpring

Build the backend application:

./mvnw clean install  # Or use 'mvn clean install' if Maven is globally available

Build the backend Docker image:

docker build -t oraclebackend-dev .

Running the Frontend

Navigate to the frontend directory:

cd ../OracleFront

Build the frontend Docker image:

docker build -t oraclefront-dev .

Start the frontend container using Vite's dev server:

docker run -p 5173:5173 --name oraclefront-container oraclefront-dev

The application will be accessible at http://localhost:5173.

Stopping and Removing Containers

To stop and clean up the containers:

docker stop oraclebackend-container oraclefront-container
docker rm oraclebackend-container oraclefront-container

Notes

Ensure application.properties in the backend contains correct Oracle DB credentials.

If you encounter port conflicts, stop existing containers or update the exposed ports accordingly.

Technologies Used

Backend: Java 17, Spring Boot, Oracle JDBC

Frontend: React, Vite, TypeScript, TailwindCSS

Tooling: Docker, VS Code

