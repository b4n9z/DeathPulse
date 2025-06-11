# DeathPulse Plugin

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.x-green.svg)
![Bukkit](https://img.shields.io/badge/Bukkit-1.21.x_Spigot--API-red.svg)
![Spigot](https://img.shields.io/badge/Spigot-1.21.x_Spigot--API-orange.svg)
![Paper](https://img.shields.io/badge/Paper-1.21.x_Spigot--API-blue.svg)
<!-- ![License](https://img.shields.io/badge/License-Apache--2.0-green.svg) -->

This `README.md` provides a comprehensive guide to using the DeathPulse plugin, including installation instructions, command usage, permissions, configuration details, and feature explanations. It also invites users to contribute and offers a constructive outlook on potential improvements.

## Table of Contents
- [Introduction](#introduction)
- [Installation](#installation)
- [Commands](#commands)
- [Permissions](#permissions)
- [Configuration](#configuration)
- [Features](#features)
- [Event Listeners](#event-listeners)
- [Contributing](#contributing)
<!-- - [License](#license) -->

## Introduction
DeathPulse is a Minecraft plugin designed to enhance gameplay by modifying player health mechanics upon death. The plugin allows server administrators to configure health gained or lost when players die, providing a unique twist to the game.

## Installation
1. Download the latest version of the DeathPulse plugin from the [releases page](https://github.com/b4n9z/DeathPulse/releases).
2. Place the downloaded JAR file into your server's `plugins` directory.
3. Start or restart your Minecraft server.
4. The plugin will generate a default configuration file in the `plugins/DeathPulse` directory.

## Commands
### Main Command
- `/DeathPulse` or `/dp`
    - **Aliases**: `dp`
    - **Usage**: `/dp <subcommand>`
    - **Description**: Main command for the DeathPulse plugin.
    - **Permission**: `dp.admin`

### Subcommands
- **reload**:
    - **Usage**: `/dp reload`
    - **Description**: Reloads the plugin configuration.
    - **Permission**: `dp.reload`

- **setHealth**:
    - **Usage**: `/dp setMaxHealth <player> <amount>`
    - **Description**: Sets the health of a specified player.
    - **Permission**: `dp.setHealth`

- **viewHealth**:
    - **Usage**: `/dp viewHealth <player>`
    - **Description**: Views the health of a specified player.
    - **Permission**: `dp.viewHealth`

- **resetHealth**:
    - **Usage**: `/dp resetHealth <player|allPlayer>`
    - **Description**: Resets the health of a specified player or all players to the starting health.
    - **Permission**: `dp.resetHealth`

- **matchHealth**:
    - **Usage**: `/dp matchHealth <player|allPlayer>`
    - **Description**: Matches the health of a specified player or all players based on their death data.
    - **Permission**: `dp.matchHealth`

- **removeDeathData**:
    - **Usage**: `/dp removeDeathData <player|allPlayer>`
    - **Description**: Removes the death data of a specified player or all players.
    - **Permission**: `dp.removeDeathData`

- **help**:
    - **Usage**: `/dp help`
    - **Description**: Shows this help message.
    - **Permission**: `dp.help`

## Permissions
- `dp.admin`: Access to all [DeathPulse](cci:2://file:///D:/b4n9z/Minecraft/Server/PluginBuild/DeathPulse/src/main/java/io/github/b4n9z/deathPulse/DeathPulse.java:8:0-57:1) commands.
- `dp.reload`: Permission to reload the plugin configuration.
- `dp.setMaxHealth`: Permission to set player health.
- `dp.viewHealth`: Permission to view player health.
- `dp.resetHealth`: Permission to reset player health.
- `dp.matchHealth`: Permission to match player health.
- `dp.removeDeathData`: Permission to remove player death data.
- `dp.help`: Permission to view the help message.

## Configuration
The configuration file (`config.yml`) allows you to customize various aspects of the plugin:

```yaml
# config.yml
firstTimeSetup: 0 # First day Plugin setup in this server, don't change this section if you don't know what you are doing

HP: # HP settings
  Start: 20 # Starting HP for players
  MaxHP: # Max HP player
    enabled: false # Enable max HP limit (true/false), when true, player has max HP limit
    amount: 140 # Amount of Max HP limit for players
  MinHP: # Min HP player
    enabled: false # (true/false) when true, player has min HP limit, when false, player with 0 HP getting ban
    amount: 2 # Amount of Min HP player limit
    banTime: 24 # Ban time in real life hours, set to 0 to ban permanently

ignore: # Ignore death
  enabled: false # (true/false) when true, player can ignore their death
  day: # Increase day settings
    enabled: false # true or false, when true, ignored day is active
    same_cause_required: false # (true/false) when true, player get ignore day just when they had died with ignored cause
    #when false, player death always ignored even not same with ignored cause in ignored day
    type: "minecraft" # "real" for real-world days, "minecraft" for minecraft days
    days: [ 19, 23, 29 ] # List of days (as multiples) when ignored day is active
  must_difference: false # (true/false) when true, player who had died with ignore cause before, cannot used ignore with same cause again
  # when false, player always ignore HP with ignore cause, even repeat death with same cause type, did not record their death data
  cause: # Ignored death type, most priority than decrease cause or increase cause
  # - all # Ignore all death type
  # - lava
  # - fall
  # - etc

increase: # Increase HP player when they die
  enabled: true # true or false, when true, player can increase their HP
  per_death: 2 # HP Increase per player death
  day: # Increase day settings
    enabled: false # true or false, when true, increase day is active
    same_cause_required: false # (true/false) when true, player get increase day just when they had died with increase cause
    #when false, player always increase HP even not same with increase cause in increase day
    type: "minecraft" # "real" for real-world days, "minecraft" for minecraft days
    days: [ 11, 13, 17 ] # List of days (as multiples) when increase day is active
    amount: 10 # HP Increase per death on increase day
  must_difference: true # (true/false) when true, player who had died with increase cause before, cannot used increase with same cause again
  # when false, player always increase HP with increase cause, even repeat death with same cause type, did not record their death data
  cause: # Increase death type, most priority than decrease cause but lower than ignore cause
    - all # Increase all death type
    # - lava
    # - fall
    # - etc

decrease: #decrease HP player when death with certain type
  enabled: false # true or false, when true, player can decrease their HP
  per_death: 2 # HP decrease per player death
  day: # Decrease day settings
    enabled: false # true or false, when true, decrease day is active
    same_cause_required: false # (true/false) when true, player get decrease day just when they had died with decrease cause
    #when false, player always decrease HP even not same with decrease cause in decrease day
    type: "minecraft" # "real" for real-world days, "minecraft" for minecraft days
    days: [ 5, 7 ] # List of days (as multiples) when decrease day is active
    amount: 10 # HP decrease per death on decrease day
  must_difference: false # (true/false) when true, player who had died with decrease cause before, cannot decrease with same cause again
  # when false, player always decrease HP with decrease cause, even repeat death with same cause type, did not record their death data
  cause: # Decrease death type, lowest priority than ignore cause and increase cause
  # - all # Decrease all death type
  # - lava
  # - fall
  # - etc

# cause ignored type always priority higher, after that death with increase cause, then decrease cause

notifications:
  defaultDeathMessage: false # true or false, when false, another player won't receive default death message
  player:
    maxHealth: "&bYou&f have reached the&c maximum health limit&f."
    minHealth: "&bYou&f have reached the&c minimum health limit&f."
    banReason: "&bYou&f have been&c banned&f due to low health"
    kicked: "&bYou&f have been&c kicked&f due to low health"

    ignored: "&fDied with &c{cause}&f now&c not increased&f HP"
    ignoredSameWay: "&fWe&c can't&f ignoring you again cause&c you died&f with&c same way"

    increased: "&bYou&a increased &d{increase}&f health cause: &c{cause}"
    increaseSameWay: "&bYou&c don't increased health&f with death same way"

    decreased: "&bYou&c decrease &d{decrease}&f health cause: &c{cause}"

  logServer:
    maxHealth: "&b{name}&f have reached the&c maximum health limit&f."
    minHealth: "&b{name}&f have reached the&c minimum health limit&f."

    increased: "&b{name}&a increase&d {increase}&f health by&b {cause}"
    decreased: "&b{name}&c decrease&d {decrease}&f health cause:&c {cause}"
    banReason: "&b{name}&f has been&c banned&f due to low health"
```
## Features
- **Health Increase**: Players increase health upon death.
- **Health Decrease**: Players can lose health upon death if configured.
- **Ignored, Increase, and Decrease Death Causes**: Specify certain death causes to ignore, increase, or decrease health.
- **Global Death Ignoring, Increasing, and Decreasing**: Use `all` to ignore, increase, or decrease health for all death types.
- **Day Ignored, Increase, and Decrease**: Allows configuration of specific days when death ignore, increase, and decrease is active, regardless of the cause of death with some settings. To activate this feature, you must enable the main `ignore`, `increase`, or `decrease` setting and day setting. If you only want to decrease health on specified days without affecting player health on other days, set `per_death` in the `increase` or `decrease` settings to 0.
- **Customizable Messages**: Tailor the messages players receive upon death.
- **First Join Health**: Set the health players start with on their first join.
- **Maximum Health Limit**: Set a maximum health limit that players can reach.
- **Minimum Health Limit/Ban**: Set a minimum health limit, or ban players when their health drops to zero.
- **Health Matching**: Match the health of a specified player or all players based on their death data with commands.
- **Death Data Management**: Remove death data of a specified player or all players with commands.
- **Configuration Reload**: Reload the plugin configuration without restarting the server.

## Event Listeners
- **PlayerJoinListener**: Sets the player's max health when they join the server for the first time.
- **PlayerDeathListener**: Handles health modification logic upon player death.

## Contributing
Contributions are welcome! <!-- Please read the [CONTRIBUTING.md](#) to get started. -->
<!--
## License
This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details. -->

---

While DeathPulse provides a unique twist on Minecraft gameplay, there is always room for improvement. Future enhancements could include more granular control over health modifications and additional configurable events. Your feedback and contributions would be invaluable in making this plugin even better!

Enjoy your time with DeathPulse!