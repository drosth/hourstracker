FROM openjdk:8
# Download and install wkhtmltopdf
RUN apt-get update \
    && apt-get install -y \
        curl \
        xfonts-base \
        xfonts-75dpi \
    && apt-get clean

RUN curl "https://downloads.wkhtmltopdf.org/0.12/0.12.5/wkhtmltox_0.12.5-1.stretch_amd64.deb" -L -o "wkhtmltopdf.deb"
RUN dpkg -i ./wkhtmltopdf.deb
RUN apt-get install -f
RUN rm -rf wkhtmlto*

#FROM openjdk:8
LABEL MAINTAINER="h.drost@gmail.com"
# working dir
ARG TARGET_DIR

WORKDIR ${TARGET_DIR}
ADD --chown=daemon:daemon target/docker/stage/opt/docker ${TARGET_DIR}
USER daemon

ENTRYPOINT ["./bin/hourstracker-rest"]
CMD []
