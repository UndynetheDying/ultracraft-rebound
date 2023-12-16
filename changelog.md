## Major Changes
- Added Minecart Parries
- Added Overheat Nailgun
  - Primary fire of all Nailgun variants now builds up "heat"
  - The higher the heat, the slower the fire rate (this one only applies to the Overheat Nailgun)
  - Alt fire uses a "heatsink", firing 5 nails and consuming 5 "heat" per tick until heat is used up.
- Removed cooldown caused by stopping to use the Flamethrower
- Added Slab Blocks
  - Work like redstone lamps with Numbers from 1-10
- Malicious Face can now be summoned as a non-decaying corpse using `{decorative: 1b}`
- Added Screenshake
- Fixed Keybind conflicts making only one action mapped to that key execute
  - Not even my fault, like I'm fixing an issue on Mojangs end here
- Rewrote Hivel Movement
  - no more hard coded values; you can customize everything in detail using a config file.
## Settings & Gamerules
- Removed almost all gamerules (only remaining is `ultra-startWithPiercer`)
- Added Option to disable Screenshake
## Commands
- ultrawhitelist can now be used by non-players
## Tweaks
- Buffed overpump damage towards others (10 Dmg -> 15 Dmg)
## Minor Changes
- Fixed water skimming not playing sounds
- Removed the "vents" MOTD from the non-essential resources
- Skyblocks no longer allow mobs to spawn on them naturally
- Stained Glass Windows can now be set to not drop an item when destroyed by giving them the NBT tag `{noDrop:1b}`
- Fixed World Join Message not displaying
- improved display of rapidly changing weapon overlays
- the cancerous Rodent glows now with LambDynamicLights installed
## Resource Changes
## API Changes
