# OpenLights

OpenLights is an OpenComputers: Rebooted addon that adds a programmable light block. Computers can set its RGB color and vanilla light level from 0 to 15.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.233 or newer
- OpenComputers: Rebooted 1.9.4 or newer
- Java 21

Veil 2.0 or newer is optional. When present, OpenLights uses Veil point lights so the programmed RGB color also illuminates the world. Without Veil, the block remains color-tinted and emits the programmed vanilla light level.

## Building

Run `gradlew.bat build` on Windows or `./gradlew build` on Linux and macOS. The built mod is written to `build/libs`.

## Component API

An OpenLight is exposed as an `openlight` component with the original API:

- `greet()`
- `setColor(color)`
- `setBrightness(brightness)`
- `getColor()`
- `getBrightness()`
