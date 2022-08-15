# Docker needs ca. 4 GBs of RAM to build this image
# TODO: check if github actions can handle this
FROM 3arthqu4ke/headlessmc:latest

RUN hmc download 1.12.2
RUN hmc forge 1.12.2

COPY . /pingbypass
WORKDIR /pingbypass

RUN mkdir -p /headlessmc/headlessmc-scripts/HeadlessMC/run/earthhack
RUN cp /pingbypass/pingbypass.properties /headlessmc/headlessmc-scripts/HeadlessMC/run/earthhack

RUN mkdir /headlessmc/headlessmc-scripts/HeadlessMC/run/mods
RUN wget -O /headlessmc/headlessmc-scripts/HeadlessMC/run/mods/hmc-specifics-1.12.2.jar \
    https://github.com/3arthqu4ke/HMC-Specifics/releases/download/1.0.3/HMC-Specifics-1.12.2-b2-full.jar
RUN wget -O /headlessmc/headlessmc-scripts/HeadlessMC/run/mods/baritone-standalone-forge-1.2.15.jar \
    https://github.com/cabaletta/baritone/releases/download/v1.2.15/baritone-standalone-forge-1.2.15.jar

RUN chmod +x ./gradlew
# setupDecompWorkspace will put docker under heavy load and the build process might look frozen between these two steps:
# Download <lwjgl-platform-2.9.4-nightly>
# > Task :fixMcSources
# but it should be fine after a bit
RUN ./gradlew setupDecompWorkspace build -Dpb.jar.dir=/headlessmc/headlessmc-scripts/HeadlessMC/run/mods

WORKDIR /headlessmc/headlessmc-scripts
EXPOSE 25565
