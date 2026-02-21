FROM amazoncorretto:21-alpine

EXPOSE 8080
VOLUME /tmp

ADD target/paylock-0.0.1-SNAPSHOT.jar paylock.jar
RUN /bin/sh -c "touch /paylock.jar"

ENV TZ=Africa/Nairobi
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENTRYPOINT ["java","-Xmx256m","-XX:+UseG1GC","-Djava.security.egd=file:/dev/./urandom","-jar","/paylock.jar"]
