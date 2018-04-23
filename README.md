[![Fetch Bot](docs/FullLogo.png)](https://github.com/cshadd/fetch-bot/)

[![Release](https://img.shields.io/github/release/cshadd/fetch-bot/all.svg)](https://github.com/cshadd/fetch-bot/releases)
[![License](https://img.shields.io/github/license/cshadd/fetch-bot.svg)](LICENSE)
[![Travis CI Build Status](https://www.travis-ci.org/cshadd/fetch-bot.svg)](https://www.travis-ci.org/cshadd/fetch-bot)

# Fetch Bot
This is a repository of an AI based robot that uses image processing and data structures to transverse an area.

## Getting Started
This repository may be used for your own robot.

### Items
* Setup:
    - x1 Computer
    - x1 Ethernet Cable
    - x1 Tool Kit
    - x1 Pack of Glue/Tape/Screws
* Recommended:
    - x1 Raspberry Pi 3 Model B
    - x1 JBtek Raspberry Pi Micro USB Cable with ON / OFF Switch - Easy Start / Reboot
    - x1 A-Male to B-Male USB
    - x1 Micro USB
    - x1 KUNCAN DC 5V to DC 12V Converter Step Up Voltage Converter 5ft Am to DC 5.5 x 2.1mm
    - x1 Logitech HD Webcam C525, Portable HD 720p Video Calling with Autofocus
    - x1 Male to Male HDMI Cable
    - x1 Elecrow RPA05010R HDMI 5-Inch 800x480 TFT LCD Display with Touch Screen Monitor for Raspberry Pi B+/2B/3B
    - x2 Adafruit Stepper Motor NEMA17 12V 350mA
    - x2 Adafruit TB6612 1.2A DC/Stepper Motor Driver Breakout Board
    - x2 Mounting Bracket for Nema 17 Stepper Motor (Geared Stepper) Hobby CNC/3D Printer
    - x2 RB-Nex-75 60mm Aluminum Omni Wheel
    - x2 5mm Aluminum Mounting Hub
    - "Back Wheel"
    - x1 Arduino Uno
    - x1 Keywish 5PCS HC-SR04 Ultrasonic Module Kit Distance Sensor for Arduino UNO, Mega, R3, Mega 2560, Nano, Duemilanove, Raspberry Pi 3
    - x1 Non-Soldering Breadboard
    - x1 Non-Soldering Jump Wires
    - x1 Internet Router
    - x1 Robot Chassis
    - x1 15000mAh Dual USB Output and 2A Input Battery Pack
* Optional:
    - x1 Raspberry Pi Case Kit

### Assembly
Great care is needed to assemble this robot.
We specifically chose these parts for our robot but it is up to you to decide which ones you will use.
```
1. Assemble the chassis with the...
```

### Why Raspberry Pi and Arduino Uno?
Let's face it. In our day and age of the 21st century, we want lightweight mobile systems for robots. The Pi and Uno is perfect for it.
We cannot stress enough about the power of the Pi and Uno. The features on the Pi contains preinstalled software such as ``git``.
It is easy to setup and use as it is virtually a computer itself.
We built this robot specifically on the Pi and Uno but made it as modular as possible to accomodate other systems.
If you want to use a full tower as a robot brain, go ahead. But the Pi and Uno is much simpler.
Proceeding forward, we will be specifically talking about the Pi and Uno unless otherwise. The system and Bash command lines will rely on the Pi.

### Prerequisites
* Recommended (Raspberry Pi/Computer):
    - Raspbian OS Stretch or higher
    - Chromium Browser
    - Java SE Development Kit 8.0 or higher
    - Java SE Runtime Environment 8.0 or higher
    - Apache Maven 3.5.3 or higher
    - OpenCV 3.4.1
    - Apache HTTP Server 2.4.25 or higher
    - PHP 7.0.27 or higher
    - Packaged Apps (apt-get):
        - arduino-core
        - arduino-mk
        - unclutter
    - Text Editor/IDE
* Optional (Computer):
    - Visual Studio 2017 or higher
    - Eclipse Oxygen or higher
    - Arduino IDE 1.8.5 or higher

### Installing
Clone/fork this repository and save it. Then use ``./bash-install.sh`` to install to your system (Bash).
``~/bin/StartFetchBot.sh`` is the launcher.
``/var/www/html/FetchBot/`` is the webserver.

### Further Considerations
You may want to:
* Configure remote SSH.
* Configure serial access.
* Check the serial ports when connecting the Arduino to the Raspberry Pi and change them if necessary in accordance with this program.
* Check the wiring when connecting the sensors and motors to the Arduino and change them if necessary in accordance with this program.

## Deployment

### Recommended Deployment
``~/bin/StartFetchBot.sh`` (Bash) is the launcher that you use to deploy the application.
To run or stop it you will need to use the web server.
``/var/www/html/FetchBot/`` is the web server that you can access at http://localhost/FetchBot. You may need to disable the cache.

### Visual Studio Solution (Optional)
You may use Visual Studio 2017 or higher as an editor. ``fetch-bot.sln`` is the solution file.

### Eclipse Oxygen (Optional)
You may use Eclipse Oxygen or higher as an editor. ``.project`` is the project file.

## Contributing
See [here](CONTRIBUTING.md).

## Versioning
We use [SemVer](http://semver.org/) for versioning. For the versions avalible, see the [tags on this repository](https://github.com/cshadd/fetch-bot/tags).

## Owners/Authors/Developers/Contributors
* Project Owner, Main Author, Leader Developer - [Christian Shadd](https://github.com/cshadd)
* Author, Hardware Developer, Documentation - [Maria Verna Aquino](https://github.com/anrev09)
* Author, Hardware Developer - [Thanh Vu](https://github.com/Vu-Thanh)
* Author, Hardware Developer, Software Developer - [Joseph Damian](https://github.com/walterk4)
* Author, Software Developer - [Giovanni Orozco](https://github.com/gio-oro)

And the [contributers](https://github.com/cshadd/fetch-bot/graphs/contributors).

## License
See [here](LICENSE).

## Development Standards
See [here](/docs/development-standards/DevelopmentStandards.pdf).

## Software Requirements Specification 
See [here](/docs/software-requirements-specification/SoftwareRequirementsSpecification.pdf).

## Acknowledgements
* [Raspberry Pi](https://www.raspberrypi.org/)
* [Arduino](https://www.arduino.cc/)
* [Ed's Blog](http://pblog.ebaker.me.uk/)
* [Adafruit](https://www.adafruit.com/)
* [Oracle](https://www.oracle.com/)
* [OpenCV](https://www.opencv.org/)
* [The Pi4J Project](http://pi4j.com/)
* [Apache](https://www.apache.org/)
* [JitPack](https://www.jitpack.io/)
* [ZenHub](https://www.zenhub.com/)
* [W3Schools](https://www.w3schools.com/)
* [The PHP Group](https://php.net/)
* [Microsoft](https://www.microsoft.com/)
* [Eclipse](https://www.eclipse.org/)
* [Travis CI](https://www.travis-ci.org/)

[![<3 Raspberry Pi](https://www.raspberrypi.org/app/uploads/2017/06/Powered-by-Raspberry-Pi-Logo_Outline-Colour-Screen-500x153.png)](https://www.raspberrypi.org/)

## CSUN AI-JAM 2018 Pre-Alpha Preview

[![CSUN AI-JAM 2018 Pre-Alpha Preview](https://img.youtube.com/vi/jIrqqUIsi4s/0.jpg)](https://www.youtube.com/watch?v=jIrqqUIsi4s)
