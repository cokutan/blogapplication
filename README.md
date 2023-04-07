


# How to Run Blog App

1. Build the project with gradle task `bootJar`.

2. Just from the root directory of the project run command

```docker-compose up -d```

3. Then you can test the application using PostMan and/or using Swagger.
4. More information about API is avaliable at:
-  [for blog operations](https://github.com/cokutan/blogapplication/blob/develop/blogapp/index.html)
-  [for file operations](https://github.com/cokutan/blogapplication/blob/develop/blogapp-webflux/index.html)
4. Blog operations contain API for blog CRUD operations while file operations handle the file upload using `Spring WebFlux` and `R2DBC` connection for the same database. First API is in `blogapp` subproject and the second source is in `blogapp-webflux`.