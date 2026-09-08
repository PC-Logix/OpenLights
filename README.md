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

## OpenLights Controller

The controller solves the OpenComputers component limit: one controller component can manage up to 4,096 OpenLights without connecting each light to a cable network.

1. Place an OpenLights Controller and connect it to the computer network with OpenComputers cable.
2. Sneak-use the controller once. The chat message confirms the selected controller.
3. Sneak-use each OpenLight you want to enroll. Chat reports its stable numeric ID.
4. Keep sneak-using lights to enroll more; the selection stays active. Sneak-use a bound light without a selected controller to report its current ID.

The controller appears as the `openlights_controller` component on a connected computer. Its callbacks are:

- `getControllerId()` returns the controller UUID.
- `count()` returns the number of registered lights.
- `list()` returns ID-keyed records containing `x`, `y`, `z`, and `loaded`; loaded records also contain `color`, `colorHex`, and `brightness`.
- `getLight(id)` returns one record.
- `setColor(id, color)`, `setBrightness(id, brightness)`, and `setLight(id, color, brightness)` control one loaded light.
- `setAllColor(color)`, `setAllBrightness(brightness)`, and `setAll(color, brightness)` control every loaded registered light. Each returns changed and unavailable counts.
- `removeLight(id)` unregisters a light.

Colors are numeric RGB values such as `0xFF4000`; brightness is `0` through `15`. Registered lights in unloaded chunks remain in `list()` with `loaded=false` and are not force-loaded by callbacks.

Example:

```lua
local controller = component.openlights_controller
for id, light in pairs(controller.list()) do
  if light.loaded then controller.setColor(id, 0xFF4000) end
end
controller.setAllBrightness(12)
```
