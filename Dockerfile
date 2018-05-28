FROM openjdk:8u131-jre-alpine
LABEL maintainer="dimitrios@amaxilatis.com"

COPY docker-entrypoint.sh /usr/local/bin/
RUN ln -s usr/local/bin/docker-entrypoint.sh / # backwards compat
RUN chmod +x /usr/local/bin/docker-entrypoint.sh
COPY server.jar /server.jar
ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["java", "-Dspring.profiles.active=docr", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "/server.jar"]
