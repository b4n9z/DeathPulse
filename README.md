# DeathPulse Plugin

![Minecraft](https://img.shields.io/badge/Minecraft-1.21-green.svg)
![Bukkit](https://img.shields.io/badge/Bukkit-1.21_Spigot--API-red.svg)
![Spigot](https://img.shields.io/badge/Spigot-1.21_Spigot--API-orange.svg)
![Paper](https://img.shields.io/badge/Paper-1.21_Spigot--API-blue.svg)
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
1. Download the latest version of the DeathPulse plugin <!-- from the [releases page](#) -->.
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
    - **Usage**: `/dp setHealth <player> <amount>`
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

- **setStartHealth**:
    - **Usage**: `/dp setStartHealth <amount>`
    - **Description**: Sets the starting health for players in config.
    - **Permission**: `dp.setStartHealth`

- **setGainedPerDeath**:
    - **Usage**: `/dp setGainedPerDeath <amount>`
    - **Description**: Sets the health gained per death.
    - **Permission**: `dp.setGainedPerDeath`

- **setGainedMax**:
    - **Usage**: `/dp setGainedMax <true/false> <amount>`
    - **Description**: Sets the maximum health gain per death.
    - **Permission**: `dp.setGainedMax`

- **setDecrease**:
    - **Usage**: `/dp setDecrease <true/false> <perDeathAmount> <minHealthAmount>`
    - **Description**: Sets the health decrease per death.
    - **Permission**: `dp.setDecrease`

- **setDecreaseMin**:
    - **Usage**: `/dp setDecreaseMin <true/false> <minHealthAmount/banTime>`
    - **Description**: Sets the minimum health decrease or ban time. When set to true, it configures minimum health amount; when set to false, it configures ban time in hours.
    - **Permission**: `dp.setDecreaseMin`

- **help**:
    - **Usage**: `/dp help`
    - **Description**: Shows this help message.
    - **Permission**: `dp.help`

## Permissions
- `dp.admin`: Access to all [DeathPulse](cci:2://file:///D:/b4n9z/Minecraft/Server/PluginBuild/DeathPulse/src/main/java/io/github/b4n9z/deathPulse/DeathPulse.java:8:0-57:1) commands.
- `dp.reload`: Permission to reload the plugin configuration.
- `dp.setHealth`: Permission to set player health.
- `dp.viewHealth`: Permission to view player health.
- `dp.resetHealth`: Permission to reset player health.
- `dp.matchHealth`: Permission to match player health.
- `dp.removeDeathData`: Permission to remove player death data.
- `dp.setStartHealth`: Permission to set starting health.
- `dp.setGainedPerDeath`: Permission to set health gained per death.
- `dp.setGainedMax`: Permission to set maximum gained health.
- `dp.setDecrease`: Permission to set health decrease.
- `dp.setDecreaseMin`: Permission to set minimum health decrease or ban time.
- `dp.help`: Permission to view the help message.

## Configuration
The configuration file (`config.yml`) allows you to customize various aspects of the plugin:

```yaml
# config.yml
HP:
  start: 20 #Sart HP player
  gained:
    per_death: 2 #HP Gained per player death
    max:
      enabled: false #Max HP player, true or false, when true, player has max HP limit
      amount: 114 #Max HP player limit
  decrease: #decrease HP player when death with certain type
    enabled: false #true or false, when true, player can decrease their HP
    per_death: 2 #HP decrease per player death
    min: #HP minimum player when always death with decrease type
      enabled: false #true or false, when true, player has min HP limit, when false, player with 0 HP getting ban
      amount: 2 #Min HP player limit
      banTime: 24 #Ban time in hours

death:
  must_difference: true #true or false, when true, player must die with different way to gained HP
  ignored: #Ignored death type cause player not gain HP
  #  - lava
  #  - fall
  decrease: #decrease HP player when death with certain type
  #  - lava
  #  - fall

notifications:
  death_message:
    player:
      gained: "&fYou gained &9{gain}&f health cause : &c{cause}"
      ifSameWay: "&cYou don't gained health with death same way"
      ignored: "&fDied with &c{cause}&f not gained HP"
      decrease: "&fYou decrease &c{decrease}&f health cause : &c{cause}"
      maxHealth: "&fYou have reached the&c maximum health limit&f."
    logServer:
      gained: "{name} gained {gain} health by {cause}"
      decrease: "{name} decrease {decrease} health cause : {cause}"
```
## Features
- **Health Gain**: Players gain health upon death.
- **Health Decrease**: Players can lose health upon death if configured.
- **Customizable Messages**: Tailor the messages players receive upon death.
- **Ignored Death Causes**: Specify certain death causes to ignore.
- **First Join Health**: Set the health players start with on their first join.
- **Maximum Health Limit**: Set a maximum health limit that players can reach.
- **Minimum Health Limit/Ban**: Set a minimum health limit, or ban players when their health drops to zero.
- **Health Matching**: Match the health of a specified player or all players based on their death data.
- **Death Data Management**: Remove death data of a specified player or all players.
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