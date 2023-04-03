FROM openjdk:8-jre-alpine

# Create app directory
RUN mkdir -p /opt/logs
WORKDIR /opt

ADD *.jar /opt/

ENTRYPOINT [ "sh", "-c" ]
CMD ["exec java -Duser.timezone=America/Lima  \
                -jar *.jar"]

#docker build -f devops/Dockerfile  -t acreu2c011intedev01.azurecr.io/ayni-server2:1.0.0 .
