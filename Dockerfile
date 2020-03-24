FROM gradle:6.2-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon

FROM openjdk:11-jre-slim

EXPOSE 8085

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar
COPY --from=build /home/gradle/src/build/resources/main/* /app/

CMD ["java","-jar","/app/spring-boot-application.jar"]