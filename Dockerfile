FROM gradle:8.5.0-jdk21

WORKDIR /

COPY / .

RUN gradle installDist

CMD ./build/install/app/bin/app --spring.profiles.active=prod