
# How to Run Blog App

1. Build the project with gradle task `bootJar`.

2. Just from the root directory of the project run command

```docker-compose up -d```

3. Add `127.0.0.1 keycloak.docker.internal` and `127.0.0.1 mongodb.docker.internal` to `hosts` file

4. Then you can test the application using PostMan and/or using Swagger which is hosted at http://localhost:8082/swagger-ui/index.html.
