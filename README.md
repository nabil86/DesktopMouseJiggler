# DesktopMouseJiggler
Move your mouse every 10 sec,there will be a tray icon that you can right click on for the option to run on startup, pause/resume, and exit. 
it can detect your presence and stop moving the mouse until you stop your activity
 
 Scrrenshot 
![image](https://user-images.githubusercontent.com/3588044/159699711-6f2ca006-aeef-4beb-86c5-56bce913eb0c.png)


# Requirement
* Java 17 or above
* OS: Linux / Windows

# Download 
[DesktopMouseJiggler.zip](https://github.com/nabil86/DesktopMouseJiggler/files/8332997/DesktopMouseJiggler.zip)
## Build / Génération
To generate the executable fat-jar and distribution start scripts (Unix `.sh` and Windows `.bat`), run:

```bash
./gradlew clean shadowJar installDist
# or to create a zip distribution containing the scripts:
./gradlew clean shadowJar distZip
```

- The fat JAR is produced in `build/libs/` (example: `build/libs/DesktopMouseJiggler-all.jar`).
- The install distribution is available under `build/install/DesktopMouseJiggler/`:
	- Start scripts: `build/install/DesktopMouseJiggler/bin/DesktopMouseJiggler` and `build/install/DesktopMouseJiggler/bin/DesktopMouseJiggler.bat`
	- Libraries: `build/install/DesktopMouseJiggler/lib/`
- Make the Unix script executable if needed:

```bash
chmod +x build/install/DesktopMouseJiggler/bin/DesktopMouseJiggler
```

If you prefer to run the fat JAR directly:

```bash
java -jar build/libs/*all*.jar
```

To launch app:
* windows: ./bin/DesktopMouseJiggler.bat
* Linux : ./bin/DesktopMouseJiggler

Enjoy !!!

