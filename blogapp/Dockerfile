FROM openjdk:19-alpine

COPY ./build/libs/blogapp.jar blogapp-0.3.0.jar

CMD ["java","-jar","blogapp-0.3.0.jar"]

RUN apk --no-cache add curl