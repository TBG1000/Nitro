# Nitro
This project is a fork of applenick's [Nitro](https://github.com/applenick/Nitro), a Discord -> Minecraft bot which allows for Nitro users to claim an in-game rank or perks.

This plugin was originally created for use on [Overcast Community](https://oc.tc), while this specific fork has some changes to benefit [Warzone](https://github.com/Warzone). For OCC, its functionality has likely been adapted and implemented into Cloudy, a (sadly) closed source all-in-one bot with a variety of features (also developed by [applenick](https://github.com/applenick)).

**Disclaimer:** The changes present in this fork were implemented with beginner-level Java experience. I am almost sure that there are cleaner, more efficient ways of accomplishing what has been done. I apologize to those more knowledgeable in that regard. However, I am very open to receive constructive criticism and accept pull requests with better code from other developers.

## Features and functionality

- Using `!nitro help` will show the user a list of available commands.

- Nitro Boosting privileges can be claimed or redeemed by users to the Minecraft account/player of their choice. To do this, simply instruct the user to enter `!nitro-redeem <minecraft username>` into the designated redemption channel (specified in `config.yml` through `channel-main`).

- Users may also remove their own privileges by using `!nitro-remove`.

- When the user does redeem Nitro privileges, the plugin will execute, through the console, the commands present in `redemption-commands`. A similar process will take place once the user stops boosting the server or loses the "boosting" role (meaning the removal commands will be executed).

- This particular fork allows you to configure more than one redemption or removal command. To do this, you must simply create a list of the commands you would like to execute. For example:
```yml
redemption-commands:
  - say Welcome!
  - say Thank you for boosting the server!
removal-commands:
  - say We're sorry to see you go!
  - say We hope you enjoyed your time on the server!
```
- You may also use the %s placeholder to refer to the Minecraft username of the Nitro Booster. This may be useful for rank or permission management.
```yml
# Using LuckPerms
redemption-commands:
  - lp user %s parent add NitroBooster
removal-commands:
  - lp user %s parent remove NitroBooster
```
- Active Nitro Boosters will be stored in `config.yml` under `nitro-boosters`.
```yml

# List of nitro boosters
# Format
# Discord username with discriminator : Discord User ID : Minecraft username : Minecraft UUID
nitro-boosters:
  - Notch#0001:000000000123456789:Notch:069a79f4-44e9-4726-a5be-fca90e38aaf5
```

- Messages detailing the Discord user that has claimed Nitro perks and their target Minecraft username will be logged to both the server's console and the designated alerts channel (`channel-alerts`).
   - Server console
   ![image](https://user-images.githubusercontent.com/46306028/172479284-581a6950-d2b8-42de-b469-1948e8d10d98.png)
   - Discord alerts channel
   ![image](https://user-images.githubusercontent.com/46306028/172479405-7abfd61d-0646-4aec-a819-0700e8ccf056.png)

## Management and staff commands

A number of commands are only available to staff in the configured staff channel of the configuration (`channel-staff`).

- `!nitro-list <boosters|bans|commands>`
  - `boosters`: Lists all Nitro boosters that have redeemed Nitro privileges.
  - `bans`: Lists all users that have been banned from redeemed Nitro privileges.
  - `commands`: Lists the redemption and removal commands present in the configuration.
- `!nitro-force-remove`: Forcefully removes an active Nitro booster from the `nitro-boosters` list in the configuration. This command will execute the available removal commands for the targeted user.
- `!nitro-ban <discriminated username> <discord id>`: Bans a user from redeeming Nitro privileges.
  - Banning a user will prevent them from using `!nitro-redeem` or `!nitro-remove` again until they are unbanned.
  - If the user was present in the `nitro-boosters` list, they will also be removed from it and removal commands will be executed.
- `!nitro-unban <discriminated username> <discord id>`: Unbans a user from redeeming Nitro privileges.
  - Unbanning a user will allow them to use `!nitro-redeem` or `!nitro-remove` again.
- `!nitro-reload`: Reloads the configuration file.
## Building

1. First, [clone](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository) or download the project's source code.
2. Optionally, make your desired changes.
3. Run the code formatter, following Google's [code style.](https://google.github.io/styleguide/javaguide.html)
```bash
mvn com.coveo:fmt-maven-plugin:format
```
5. Compile the project.
```bash
mvn package
```

You'll find the bot's `.jar` file inside the `target` folder of the project's root directory.

You may also find a pre-built `.jar` [here](https://github.com/TBG1000/Nitro/actions/workflows/main.yml).

## Installing
When creating the bot that will be linked to Nitro's plugin `.jar`, be sure to toggle on the "Server Members Intent" (`GUILD_MEMBERS`) option. If this setting is left off, the bot will not be able to properly function. It will fail to remove privileges from users that were previously Nitro Boosters but have since stopped boosting the server.

1. Drop the plugin's `.jar` in your server's `plugins` folder.
2. Restart the server to automatically generate the bot's required files (`config.yml`, `plugin.yml`).
3. Fill in the blanks of the configuration file (`config.yml`). To do this, you'll need the following:
   - A token for your Discord bot which you can get at the [Discord Developer Portal](https://discord.com/developers/docs)
   - The ID of the server in which the bot will be functioning.
   - The ID of the Nitro Booster role.
     - This role can be any role, not necessarily the legitimate "Nitro Booster" role.
   - The ID of the channel in which logs will be sent.
   - The ID of the channel in which users can redeem/remove Nitro Boosting privileges.
   - The ID of the channel in which staff members can use the management commands.
   - The command(s) to be executed on the Minecraft server once a user redeems privileges.
   - The command(s) to be executed on the Minecraft server once a user stops boosting the Discord server.
4. Restart the server once again for the changes to take place. Once your bot goes online, users may start redeeming their privileges in the designated channel.

You may look at a sample of the configuration file [below](https://github.com/TBG1000/Nitro/#config).
You can also find out how to get server, role or channel IDs [here](https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID). 

## Config
```yml
# Discord Config stuff
enabled: true # Enable discord bot?

token: ""       # ID of Discord bot token
server: ""      # ID of discord server
nitro-role: ""  # ID of the nitro role

channel-alerts: ""   # ID of channel where logs from bot are sent
channel-main: ""     # ID of channel where command can be used
channel-staff: ""   # ID of channel where staff can use management commands

# List of redemption commands, executed when the user boosts the server
redemption-commands:
  - ""
# List of removal commands, executed when the user stops boosting the server
removal-commands:
  - ""

# List of nitro boosters
# Format
# Discriminated Discord username : User Discord ID : Minecraft username : Minecraft username
nitro-boosters:
  - ""

# List of users banned from redemption privileges
# Format
# Discriminated Discord username : User Discord ID
banned-boosters:
  - ""
```
