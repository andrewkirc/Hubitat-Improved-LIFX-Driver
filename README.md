# LIFX Scene Device Driver for Hubitat

## Overview

This Hubitat driver allows you to control LIFX scenes and effects directly from your Hubitat hub. With this driver, you can activate LIFX scenes, turn them off, and even apply special effects like the morph effect.

## Features

- Activate LIFX scenes
- Turn off lights associated with a scene
- Activate the morph effect on scene lights
- Adjust brightness of effects
- Turn off all effects

## Installation

1. Log in to your Hubitat hub's admin interface.
2. Go to the "Drivers Code" page.
3. Click on the "+ New Driver" button.
4. Paste the entire contents of the `LIFX Scene Device by @andrewkirc` driver code.
5. Click on "Save".

## Setup

1. Go to the "Devices" page in your Hubitat admin interface.
2. Click on "+ Add Virtual Device".
3. Give your device a name (e.g., "Living Room LIFX Scene").
4. For the Type, select "LIFX Scene Device by @andrewkirc".
5. Click "Save Device".

## Configuration

After creating the device, you need to configure it with your LIFX API details:

1. Click on your newly created device.
2. In the device page, click on the "Preferences" section.
3. Fill in the following fields:
   - LIFX API Token: Your LIFX API token (obtain from https://cloud.lifx.com/settings)
   - LIFX Scene ID: The ID of the scene you want to control
   - LIFX Selector: Specify which lights to control (e.g., `all`, `label:Bedroom,label:Kitchen`, or `id:d3b2f2d97452`)
   - Effect Brightness Percentage: Set the brightness for effects (1-100)
4. Click "Save Preferences".

## Usage

Once configured, you can use the following commands:

- `on()`: Activates the specified LIFX scene
- `off()`: Turns off the lights associated with the scene
- `activateScene()`: Same as `on()`
- `activateMorphEffect()`: Applies the morph effect to the specified lights
- `effectsOff()`: Turns off all effects on the specified lights

You can use these commands in Hubitat apps, rules, or dashboard tiles.

## Troubleshooting

- If you encounter any issues, check the Hubitat logs for error messages.
- Ensure your LIFX API token, scene ID, and selector are correct.
- Make sure your Hubitat hub has internet access to communicate with the LIFX API.

## Support

For support, please open an issue on the GitHub repository or contact the author, [https://andrewkirc.com](Andrew Kirchofer).

## License

MIT

## Acknowledgments

Thanks to the Hubitat community and LIFX for making this integration possible.
