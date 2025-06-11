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
DeathPulse is a Minecraft plugin designed to enhance gameplay by modifying player health mechanics upon death. The plugin allows server administrators to configure health gained or lost when players die in certain types of deaths, providing a unique twist to the game.

## Installation
1. Download the latest version of the DeathPulse plugin from the [releases page](https://github.com/b4n9z/DeathPulse/releases).
2. Place the downloaded JAR file into your server's `plugins` directory.
3. Start or restart your Minecraft server.
4. The plugin will generate a default configuration file in the `plugins/DeathPulse` directory.
5. Edit the configuration file to customize the plugin's behavior. 
6. Reload the plugin using the `/dp reload` command.

## Commands
### Main Command
- `/DeathPulse` or `/dp`
    - **Aliases**: `dp`
    - **Usage**: `/dp <subcommand>`
    - **Description**: Main command for the DeathPulse plugin.
    - **Permission**: `dp.admin` or just set permissionsAllPlayer for specific Subcommands to true (keep in mind this is not recommended to all subcommands)

### Subcommands
- **reload**:
    - **Usage**: `/dp reload`
    - **Description**: Reloads the plugin configuration without restarting the server.
    - **Permission**: `dp.reload`

- **setConfig**:
    - **Usage**: `/dp setConfig <key> <value>`
    - **Description**: Sets a configuration value for the plugin just using command.
    - **Permission**: `dp.setConfig`

- **setMaxHealth**:
    - **Usage**: `/dp setMaxHealth <player> <amount>`
    - **Description**: Sets the maximum health of a specified player.
    - **Permission**: `dp.setMaxHealth`

- **viewHealth**:
    - **Usage**: `/dp viewHealth <player>`
    - **Description**: Views the health of a specified player their debt data and death data.
    - **Permission**: `dp.viewHealth`

- **viewDeathData**:
    - **Usage**: `/dp viewDeathData`
    - **Description**: Views the death data of players who run the command.
    - **Permission**: `dp.viewDeathData`

- **viewDebtData**:
    - **Usage**: `/dp viewDebtData`
    - **Description**: Views the debt data of players who run the command.
    - **Permission**: `dp.viewDebtData`

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

- **removeDebtData**:
    - **Usage**: `/dp removeDebtData <player|allPlayer>`
    - **Description**: Removes the debt data of a specified player or all players.
    - **Permission**: `dp.removeDebtData`

- **transferHealth**:
    - **Usage**: `/dp transferHealth <player> <amount>`
    - **Description**: Transfers health from one player(who run the command) to another.
    - **Permission**: `dp.transferHealth`

- **withdrawHealth**:
    - **Usage**: `/dp withdrawHealth <player> <amount>`
    - **Description**: Withdraws health from a player(who run the command) to make HealthItem.
    - **Permission**: `dp.withdrawHealth`

- **help**:
    - **Usage**: `/dp help`
    - **Description**: Shows this help message.
    - **Permission**: `dp.help`

## Permissions
- `dp.admin`: Access to all [DeathPulse](cci:2://file:///D:/b4n9z/Minecraft/Server/PluginBuild/DeathPulse/src/main/java/io/github/b4n9z/deathPulse/DeathPulse.java:8:0-57:1) commands.
- `dp.reload`: Permission to reload the plugin configuration.
- `dp.setConfig`: Permission to change config plugin.
- `dp.setMaxHealth`: Permission to set player health.
- `dp.viewHealth`: Permission to view player health.
- `dp.viewDeathData`: Permission to view death data player.
- `dp.viewDebtData`: Permission to view debt data player.
- `dp.resetHealth`: Permission to reset player health.
- `dp.matchHealth`: Permission to match player health.
- `dp.removeDeathData`: Permission to remove player death data.
- `dp.removeDebtData`: Permission to remove player debt data.
- `dp.transferHealth`: Permission to transfer health to another player.
- `dp.withdrawHealth`: Permission to withdraw health to make HealthItem.
- `dp.help`: Permission to view the help message.

## Configuration
<details>
<summary>config.yml</summary>
The configuration file (`config.yml`) allows you to customize various aspects of the plugin:

```yaml
############ CRUCIAL SETTINGS ############
firstTimeSetup: 0 # First day Plugin setup in this server, don't change this section if you don't know what you are doing
checkDayPeriod: 30 # Day check period in this server(in seconds), lower value is more accurate but may cause lag
############ WORLD SETTINGS ############
worldDaySettings:
  world: 24000
  # <world_name>: <ticks_per_day_in_world> # <world_name> is the name of the world, <ticks_per_day_in_world> is the ticks per day in that world
  #world_nether: 24000 # not use default because nether don't have day cycle
############ HP SETTINGS ############
hp: # HP settings
  start: 20 # Starting HP for players
  maxHP: # Max HP player
    enabled: false # Enable max HP limit (true/false), when true, player has max HP limit
    amount: 140 # Amount of Max HP limit for players
    dropHealthItem: true # Drop health item when player reach max HP when dead increase
  minHP: # Min HP player
    enabled: false # (true/false) when true, player has min HP limit, when false, player with 0 HP getting ban
    amount: 2 # Amount of Min HP player limit
    banTime: 24 # Ban time in real life hours, set to 0 to ban permanently
  afterBan: 2 # Health after player get ban and return back to server after ban
############ HEALTH ITEM SETTINGS ############
healthItem:
  material: "NETHER_STAR"  # Material item
  name: "&cHealth Token"  # Item Name
  lore:  # Item Description
    - "&7Right click to use"
    - ""
    - "&7Can be used to Increase your &7Max Health"
  healthPerItem: 2  # Health per item
  cannotToCraft: ["BEACON"] # List of items that cannot be crafted using health item

############ PRIORITY DEATH SETTINGS ############
priority: ["IGNORE", "INCREASE", "DECREASE"]
# Priority death, checking player die with higher priority first, when player die with that type, it will not check other type
# List of death type: IGNORE, INCREASE, DECREASE
############ IGNORE DEATH SETTINGS ############
ignore: # Ignore death
  enabled: false # (true/false) when true, player can ignore their death
  must_difference: false # (true/false) when true, player who had died with ignore cause before, cannot used ignore with same cause again
  # when false, player always ignore HP with ignore cause, even repeat death with same cause type, did not record their death data
  cause: # Ignored death type
    - ALL # Ignore all death type
    #- LAVA
    #- FALL
    #- etc
  day: # Ignored day settings
    enabled: false # true or false, when true, ignored day is active
    must_difference: false # (true/false) when true, player who had died with ignore day cause before, cannot ignore with same day cause again
    # when false, player always ignore HP with ignore day cause, even repeat death with same day cause type, did not record their death data
    deActiveIncrease: false # true or false, when true, increase death not active when ignore day
    deActiveDecrease: false # true or false, when true, decrease death not active when ignore day
    deActiveIncreaseDay: false # true or false, when true, increase day not active when ignore day
    deActiveDecreaseDay: false # true or false, when true, decrease day not active when ignore day
    type: "minecraft" # "real" for real-world days, "minecraft" for minecraft days
    days: [ 19, 23, 29 ] # List of days (as multiples) when ignored day is active
    cause: # Ignored day cause (death type)
      - ALL # Ignore all death type
      #- LAVA
      #- FALL
      #- etc
############ INCREASE DEATH SETTINGS ############
increase: # Increase HP player when they die
  enabled: true # true or false, when true, player can increase their HP
  must_difference: true # (true/false) when true, player who had died with increase cause before, cannot used increase with same cause again
  # when false, player always increase HP with increase cause, even repeat death with same cause type, did not record their death data
  cause: # Increase death type and Increase amount, (TYPE::AMOUNT)
    - ALL::2 # Increase all death type
    #- LAVA::4
    - FALL::1 # If using ALL but need custom increase amount for some death type, you can add below like this
    # - etc
  day: # Increase day settings
    enabled: false # true or false, when true, increase day is active
    must_difference: true # (true/false) when true, player who had died with increase day cause before, cannot increase with same day cause again
    # when false, player always increase HP with increase day cause, even repeat death with same day cause type, did not record their death data
    deActiveIgnore: false # true or false, when true, ignore death not active when increase day
    deActiveDecrease: false # true or false, when true, decrease death not active when increase day
    deActiveIgnoreDay: false # true or false, when true, ignore day not active when increase day is active
    deActiveDecreaseDay: false # true or false, when true, decrease day not active when increase day is active
    type: "minecraft" # "real" for real-world days, "minecraft" for minecraft days
    days: [ 11, 13, 17 ] # List of days (as multiples) when increase day is active
    cause: # Increase day cause (death type and increase amount), (TYPE::AMOUNT)
      - ALL::10 # Increase all death type
      - LAVA::14 # If using ALL but need custom increase day amount for some death type, you can add below like this
      #- FALL::4
      # - etc
############ DECREASE DEATH SETTINGS ############
decrease: #decrease HP player when death with certain type
  enabled: false # true or false, when true, player can decrease their HP
  must_difference: false # (true/false) when true, player who had died with decrease cause before, cannot decrease with same cause again
  # when false, player always decrease HP with decrease cause, even repeat death with same cause type, did not record their death data
  debt: true # (true/false) when true, player when got decrease but don't have enough HP, when they increase HP their HP will use to pay debt
  cause: # Decrease death type and Decrease amount, (TYPE::AMOUNT)
    - ALL::4 # Decrease all death type
    - PLAYER_ATTACK::6 # If using ALL but need custom decrease amount for some death type, you can add below like this
    #- FALL::10
    #- etc
  day: # Decrease day settings
    enabled: false # true or false, when true, decrease day is active
    must_difference: false # (true/false) when true, player who had died with decrease day cause before, cannot decrease with same day cause again
    # when false, player always decrease HP with decrease day cause, even repeat death with same day cause type, did not record their death data
    deActiveIgnore: false # true or false, when true, ignore death not active when decrease day
    deActiveIncrease: false # true or false, when true, increase death not active when decrease day
    deActiveIgnoreDay: false # true or false, when true, ignore day not active when decrease day is active
    deActiveIncreaseDay: false # true or false, when true, increase day not active when decrease day is active
    type: "minecraft" # "real" for real-world days, "minecraft" for minecraft days
    days: [ 5, 7 ] # List of days (as multiples) when decrease day is active
    cause: # Decrease day cause (death type and decrease amount), (TYPE::AMOUNT)
      - ALL::16 # Decrease all death type
      - PLAYER_ATTACK::18 # If using ALL but need custom decrease day amount for some death type, you can add below like this
      #- FALL::20
      #- etc

############ SEASON SETTINGS ############
season: # Season settings, add season days to death data player, so player can death with same cause again when season change
  enabled: true # true or false, when true, season is active
  type: "real" # "real" for real-world day per season, "minecraft" for minecraft day per season
  day: 30 # day (as multiples) when season is change
  resetWorldDay: false # true or false, when true, reset all world day and irl world setting day back to 0 when season is change
############ PERMISSION SETTINGS ############
permissionsAllPlayer: # Give permission to all player, OP can use command without permission
  reload: false # allow reload command to all player
  setConfig: false # allow setConfig command to all player
  setMaxHealth: false # allow setMaxHealth command to all player
  viewHealth: false # allow viewHealth command to all player
  viewDeathData: true # allow viewDeathData command to all player
  resetHealth: false # allow resetHealth command to all player
  matchHealth: false # allow matchHealth command to all player
  removeDeathData: false # allow removeDeathData command to all player
  removeDebtData: false # allow removeDebtData command to all player
  transferHealth: true # allow transferHealth command to all player
  withdrawHealth: true # allow withdrawHealth command to all player
  help: true # allow help command to all player
############ NOTIFICATION SETTINGS ############
notifications:
  defaultDeathMessage: false # true or false, when false, another player won't receive default death message
  player:
    maxHealth: "&bYou&f have reached the&c maximum health limit&f."
    minHealth: "&bYou&f have reached the&c minimum health limit&f."
    banReason: "&bYou&f have been&c banned&f due to low health"
    kicked: "&bYou&f have been&c kicked&f due to low health"

    ignored: "&fDied with &c{cause}&f now&c not increased&f HP"
    ignoredSameWay: "&cCan't&f ignoring again cause&c you died&f with&c same way"

    increased: "&bYou&a increased &d{increase}&f health cause: &c{cause}"
    increaseSameWay: "&bYou&c don't increased health&f with death same way"

    decreased: "&bYou&c decrease &d{decrease}&f health cause: &c{cause}"
    decreaseSameWay: "&bYou&c don't decreased health&f with death same way"

    debtPaidOff: "&bYou&f have paid off&c {debtPaid}&f health debt"
    debtReduced: "&bYou&f have reduced&c {debtPaid}&f health debt, now&c {debtLeft}&f health debt left"
    debtAdded: "&bYou&f have added&c {debtAdded}&f health debt, now&c {debtLeft}&f health debt you must pay"

    # Warning messages for special days
    warning:
      ignoredDay: "&6⚠ &eToday is an &6Ignored Day&e! Deaths won't affect your HP!"
      increaseDay: "&6⚠ &eToday is an &aIncrease Day&e! Get extra HP on death!"
      decreaseDay: "&6⚠ &eToday is a &cDecrease Day&e! Be careful, you'll lose more HP on death!"
      seasonChange: "&6⚠ &eSeason changed! Reset your death data!"

  logServer:
    maxHealth: "&b{name}&f have reached the&c maximum health limit&f."
    minHealth: "&b{name}&f have reached the&c minimum health limit&f."

    increased: "&b{name}&a increase&d {increase}&f health by&b {cause}"
    decreased: "&b{name}&c decrease&d {decrease}&f health cause:&c {cause}"
    banReason: "&b{name}&f has been&c banned&f due to low health"
```
</details>

## Features
- **Ignore Death**: Ignored death so player could not get Increase or Decrease.
- **Health Increase**: Players increase health upon death.
- **Health Decrease**: Players can lose health upon death if configured.
- **Ignore, Increase, and Decrease Death Types**: Specify certain death types to ignore, increase, or decrease health.
- **All Type Death Ignoring, Increasing, and Decreasing**: Use `ALL` to ignore, increase, or decrease health for all death causes.
- **Increase and Decrease Amount**: Specify Amount to increase or decrease health on certain death types.
- **Day Ignored, Increase, and Decrease**: Allows configuration of specific days when death ignore, increase, and decrease is active, regardless of the cause of death with some settings. If you only want to decrease health on specified days without affecting player health on other days, set `per_death` in the `increase` or `decrease` settings to 0.
- **DeActive Another Death In Certain Days**: Allow deactivating another death type in certain days.
- **Same Way Death**: If it must difference is false, and the death is the same as the last death, the player will not increase or decrease health again.
- **Customizable Priority**: Set a priority for different death types to determine the order of health modification.
- **Season Change**: Configure season settings to make players increase or decrease health with same cause again when season change.
- **Day Reset**: Configure day reset settings in when season change to make day back to 0. 
- **Customizable Messages**: Tailor the messages players receive upon death.
- **First Join Health**: Set the health players start with on their first join.
- **Maximum Health Limit**: Set a maximum health limit that players can reach.
- **Minimum Health Limit/Ban**: Set a minimum health limit, or ban players when their health drops to zero.
- **Debt Health**: Make players incur debt if their health is below the minimum or below 0 when it decreases.
- **Health Item**: Customize the health item settings, and make it so that the item cannot be used for crafting specific items.
- **Health Item Drop**: Drop a health item when a player reaches the maximum health limit upon got increase.
- **First Time Setup**: Set the first day Plugin setup in this server (`System.currentTimeMillis()`), don't change this section if you don't know what you are doing.
- **Day Check Period**: Set the day check period in this server(in seconds), lower value is more accurate but may cause lag.
- **World Day Settings**: Set the ticks per day in different worlds to used in ignored day, increase day, and decrease day.
- **Configuration Reload**: Reload the plugin configuration without restarting the server with commands.
- **Set Config Using Command**: Set a specific configuration value using a command.
- **Health Matching**: Match the health of a specified player or all players based on their death data with commands.
- **Transfer Health**: Transfer the health to another player using commands.
- **Withdraw Health**: Withdraw health item from using commands.
- **Permissions**: Set specific permissions for different commands to All Players, so players can run the commands without having the `dp.*` permission(at your own risk).

## Event Listeners
- **PlayerJoinListener**: Sets the player's start health when they join the server for the first time.
- **HealthItemListener**: Handles Health Item logic.
- **PlayerDeathListener**: Handles health modification logic upon player death.

## Contributing
Contributions are welcome! <!-- Please read the [CONTRIBUTING.md](#) to get started. -->
<!--
## License
This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details. -->

---

While DeathPulse provides a unique twist on Minecraft gameplay, there is always room for improvement. Future enhancements could include more granular control over health modifications and additional configurable events. Your feedback and contributions would be invaluable in making this plugin even better!

Enjoy your time with DeathPulse!