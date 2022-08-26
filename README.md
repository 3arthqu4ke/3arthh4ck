# 3arthh4ck

[![CodeFactor](https://www.codefactor.io/repository/github/3arthqu4ke/3arthh4ck/badge/main)](https://www.codefactor.io/repository/github/3arthqu4ke/3arthh4ck/overview/main)
[![GitHub All Releases](https://img.shields.io/github/downloads/3arthqu4ke/3arthh4ck/total.svg)](https://github.com/3arthqu4ke/3arthh4ck/releases)
[![Docker Image Size (latest by date)](https://img.shields.io/docker/image-size/3arthqu4ke/pingbypass?logo=docker)](https://hub.docker.com/r/3arthqu4ke/pingbypass)
[![Lines of code](docs/loc.svg)](https://tokei.rs/b1/github/3arthqu4ke/3arthh4ck?category=code)
![Repo size](https://img.shields.io/github/repo-size/3arthqu4ke/3arthh4ck.svg)
[![Build](https://github.com/3arthqu4ke/3arthh4ck/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/3arthqu4ke/3arthh4ck/actions)

3arthh4ck is Minecraft 1.12.2 utility mod for anarchy PvP. With the 1.7.0 release it also takes over the role
of the now outdated PingBypass [Server](https://github.com/3arthqu4ke/PingBypass) and 
[Client](https://github.com/3arthqu4ke/PingBypass-Client). To install it just drop the jar into your forge mods folder.
By default, any message prefixed with a `+` will be handled as command, e.g. `+toggle clickgui`, to open the gui. 
Because of bloat I decided to hide some of the more complicated Settings by default. You can find those by using the 
Settings module.

## Proxy/PingBypass
![Image of a PingBypass server](docs/pingbypass.png)  
3arthh4ck can be used as a Proxy server. With ping being such an important factor in crystal PvP this allows you to play
on servers far away from where you are without the disadvantage of high ping. This proxy can, opposed to the old
PingBypass, stay connected to a server, allowing to join through it at a later point. This can for example be used to 
wait out 2b2t's queue system.

To set up the 3arthh4ck proxy you need a server, I personally started out using [GCP's](https://cloud.google.com/) free
trial. The location of that server should be as close as possible to the one you want to play on. That server should 
have an Ip and port which are reachable from the outside. The game will run on that server. Keep in mind that no matter
which account you use on your client, the Minecraft account on the server will always be used when you play.

### Setup with docker
1.  Install [docker](https://docs.docker.com/engine/install/) on your server.

2.  Run `docker pull 3arthqu4ke/pingbypass`.

3.  Run `docker run -i -t -p <ip>:<port>:25565 3arthqu4ke/pingbypass`.

4.  You should now be in the shell of the docker container.

5.  Login to your Minecraft account via `hmc login <email>`, then enter your account password.

6.  Launch the PingBypass server with `hmc launch 1 -id --jvm -Dpb.password=<some password>`.

7.  You are now done with the server. Use the commands from the
    [HMC-Specifics](https://github.com/3arthqu4ke/HMC-Specifics) to stop the game. Or just stop the container.

8.  On your own PC just install 3arthh4ck by using its Installer or dropping it inside your mods folder.

9.  In the MultiPlayer Menus top right corner you will see a book and a PingBypass button. Use PingBypass button to
     toggle it on and off and the book to enter the server's connection details, also the password you used in step 6.

10. You can add the PingBypass server like a normal Minecraft server, this will make it look like in the picture above.
     When the PingBypass button is toggled on you will join any server you click through the PingBypass proxy.

11. There is two sets of modules, one accessible through the PB-Gui module. These modules have separate configs and 
     represent the ones on the proxy server.

### Manual Setup with HeadlessMc
This is just what the docker container already automates.

1.  Install Java 8 on the server

2.  Create a folder where your game will run.

3.  Inside that folder create two directories: `mods` and `earthhack`

4.  Put the 3arthh4ck jar and the [HMC-Specifics-1.12.2](https://github.com/3arthqu4ke/HMC-Specifics/releases/tag/1.0.3) 
    jar inside the mods folder.

5.  Inside the earthhack directory create a file called `pingbypass.properties` filled with the following:
    ```properties
    pb.server=true
    pb.password=<password for your pingbypass proxy>
    pb.ip=<the aforementioned ip (definitely not 127.0.0.1)>
    pb.port=<the aforementioned port>
    ```

6.  Download [HeadlessMc](https://github.com/3arthqu4ke/HeadlessMc) and run its jar once.

7.  This should create a file called `HeadlessMC/config.properties`. Edit that file and add:
    ```properties
    hmc.gamedir=<the directory created in step 2.>
    hmc.java.versions=<the directory where the java binary is located, e.g. /usr/bin/java>
    hmc.invert.jndi.flag=true
    hmc.invert.lookup.flag=true
    hmc.invert.lwjgl.flag=true
    hmc.invert.pauls.flag=true
    ```

8.  Run HeadlessMc again:
    * Login to your Microsoft account with `login <email>`, then enter your password.
    * Run `download 1.12.2`., then `forge 1.12.2`.
    * List the downloaded versions with `versions -refresh`.
    * Launch the game with `launch <id of the forge version> -id`.

9.  You are now done with the server. Just follow the steps after 7. in the docker setup.
