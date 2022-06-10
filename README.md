# Nitro
This project is a fork of applenick's [Nitro](https://github.com/applenick/Nitro), a Discord -> Minecraft bot which allows for Nitro users to claim an in-game rank or perks.

This plugin was originally created for use on [Overcast Community](https://oc.tc), while this specific fork has some changes to benefit [Warzone](https://github.com/Warzone). For OCC, its functionality has likely been adapted and implemented into Cloudy, a (sadly) closed source all-in-one bot with a variety of features (also developed by [applenick](https://github.com/applenick)).

**Disclaimer:** The changes present in this fork were implemented with beginner-level Java experience. I am almost sure that there are cleaner, more efficient ways of accomplishing what has been done. I apologize to those more knowledgeable in that regard. However, I am very open to receive constructive criticism and accept pull requests with better code from other developers.

## Features and functionality

- Nitro Boosting privileges can be claimed or redeemed by users to the Minecraft account/player of their choice. To do this, simply instruct the user to enter `!nitro <user>` into the designated redemption channel (specified in `config.yml` through `channel-main`).

- When the user does redeem Nitro privileges, the plugin will execute, through the console, the commands present in `redemption-command`. A similar process will take place once the user stops boosting the server or loses the "boosting" role (meaning the removal commands will be executed).

- This particular fork allows you to configure more than one redemption or removal command. To do this, you must separate each individual command by using a semicolon (`:`). For example:
```yml
redemption-command: say hello!:say welcome!   
remove-command: say goodbye!:say see you later!

# You may also use the %s placeholder to refer to the Minecraft username of the user.
# Useful for giving permissions, ranks, etc. The below example considers using LuckPerms.
redemption-command: lp user %s parent add nitro-boost
```

- Active Nitro Boosters will be stored in `config.yml` under `nitro-boosters`.
```yml

# List of nitro boosters
nitro-boosters:
  # Format
  # Discord username with discriminator : Discord User ID : Minecraft username : Minecraft UUID
  - Notch#0001:000000000123456789:Notch:069a79f4-44e9-4726-a5be-fca90e38aaf5
```

- Messages detailing the Discord user that has claimed Nitro perks and their target Minecraft username will be logged to both the server's console and the designated alerts channel (`channel-alerts`).
   - Server console
   ![image](https://user-images.githubusercontent.com/46306028/172479284-581a6950-d2b8-42de-b469-1948e8d10d98.png)
   - Discord alerts channel
   ![image](https://user-images.githubusercontent.com/46306028/172479405-7abfd61d-0646-4aec-a819-0700e8ccf056.png)

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
   - The ID of the channel in which redemption/removal logs will be sent.
   - The ID of the channel in which users can redeem Nitro Boosting privileges.
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

redemption-command: ""   # Commands to be executed when a user redeems Nitro privileges
remove-command: ""       # Commands to be executed when a user stops boosting the server 

# List of nitro boosters
nitro-boosters:
  - ""
```
