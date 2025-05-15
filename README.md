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
1. Clone the repository
<br>

```bash
git clone https://github.com/JstnAngrendo/ewallet-api-demo.git
cd ewallet-api-demo
```
2. (Optional) Build the Docker Image Manually
Usually not needed when using Docker Compose.

```bash
docker build -t ewallet-demo .
```

3. Run Application with Docker Compose
```bash
docker-compose down -v   # Optional: Clean previous containers and volumes
docker-compose up --build
```


<h2 align="left">API Testing</h2>
You can test the API with:
<ul>
  <li>Postman</li>
  <li>Swagger UI at: </li>
</ul>

```
http://localhost:8080/swagger-ui/index.html
```
Note for Swagger Authorization:
When authorizing via Swagger, do not include the word Bearer. Just paste the token directly.

