<h1 align="left">E-Wallet API Demo</h1>

###


###

###

<p align="left"><br><b></b>ewallet-api-demo is a simple API demonstration project built using Spring Boot, PostgreSQL, and Docker. This project aims to provide a mock e-wallet API for demo purposes, allowing you to simulate e-wallet transactions and manage user data. The project also includes Docker configurations for easy deployment and setup.<b></b></p>

###

<h2 align="left">I code with</h2>

###

<div align="left">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" height="40" alt="spring logo"  />
  <img width="12" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" height="40" alt="java logo"  />
  <img width="12" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/docker/docker-original.svg" height="40" alt="docker logo"  />
</div>

###
<h2 align="left">How to Run the Application</h2>
Clone the repository
<br>

```bash
git clone https://github.com/JstnAngrendo/ewallet-api-demo.git
cd ewallet-api-demo
```
Build the Docker Image (Optional if using Docker Compose): If you want to manually build the Docker image:
```bash
docker build -t ewallet-demo .
```
Run the Application: To start the container and expose port 8080, run:
```bash
docker run -p 8080:8080 ewallet-demo
```
or
```
docker-compose up --build
```
Access the Application: The application will now be available at http://localhost:8080 on your browser.<br>
You can also access it with Postman or Swagger UI


