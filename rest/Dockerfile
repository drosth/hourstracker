FROM openjdk:8

RUN apt-get -q update \
    && apt-get install -qy --no-install-recommends \
        xfonts-base \
        xfonts-75dpi \
    && apt-get -q clean update

ADD third-party/wkhtmltox_0.12.5-1.stretch_amd64.deb /tmp

# Download and install wkhtmltopdf

# RUN wget -nv -O "/tmp/wkhtmltox.deb" "https://github.com/wkhtmltopdf/wkhtmltopdf/releases/download/0.12.5/wkhtmltox_0.12.5-1.stretch_amd64.deb"
RUN apt-get -qy install "/tmp/wkhtmltox_0.12.5-1.stretch_amd64.deb"
RUN apt-get install -f

LABEL MAINTAINER="h.drost@gmail.com"
WORKDIR /opt/docker
ADD --chown=daemon:daemon rest/target/docker/stage/opt /opt
USER daemon
ENTRYPOINT ["/opt/docker/bin/hourstracker-rest"]
CMD []
