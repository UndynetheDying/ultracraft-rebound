## Major Changes
- Added `Versus` by [Aavanitro](https://www.youtube.com/@Aavanitro) to OST
- Added `Limbo Freeroam` by [ENNWAY](https://www.youtube.com/@ENNWAY) to OST
  - Played in Limbo Freeroam Dimension as long as you're not in creative mode
- Added Sawed-On Shotgun
- Added Jumpstart Nailgun
- Added new ████
- Added Hank
- Added new varians of Stained Glass Windows
- Removed "glitched" Blockstate from Fake Leaves
  - The Glitch visuals still appear, but it's random now
- Removed Proximity Blockstate from Cerberus Block
  - Proximity triggered Cerberi now use the nbt tag `proximity` instead
- Added `intro` Attribute to level music data
  - This allows setting an intro sound played before the actual track starts
- Added Flipped state to Cerberus Block
- Added `sacrificial` nbt tag to Pedestals
- Fixed Fall Damage not working correctly for Players
- Fixed Deadcoins not being shootable
- Added Hyacinth
- Added Hyacinth patches to Limbo worldgen
- Added Butteflies :3
- Style doesn't decay anymore while time is frozen
- Level Timer now pauses while time is frozen
- Fixed Level Timer Pausing not actually working at all lmao
- Added Item to place decorative Malicious Faces more easily
- Fixed sprint and sneak states tending to get stuck when enabling hivel mode
- Changed how slide and slam states are handled
- Changed how movement sounds are handled
- Reinforced Stained Glass windows don't need to be supported by blocks anymore once they've been placed successfully
## Settings & Config
## Commands
## Tweaks
## Minor Changes
- Fixed Cerberus Cracking Sounds not playing
- Fixed the modification suppression message in limbo showing up when it shouldn't
- Fixed fake trees being able to generate on any block
- Moved photosensitivity warning to Intro Sequence
- Fixed Style resetting when taking damage equal to or greater than half of the players Health
- Fixed Malicious Face not being immune to explosions anymore
- Opening the UltraCredits Screen now stops main menu ambience sounds
  - This fixes an issue where they'd stack when opening and closing the credits
- Fixed Nailgun alt-fire animation not being interrupted by rapid uses while not primary firing
- Fixed Weapons that weren't obtained yet, but aren't already equipped in the weapons loadout, being uncraftable
  - Normally all weapons are equipped by default, the unobtained ones just were skipped when switching weapons; newly added weapons weren't in previously saved loadouts though, revealing this issue
- Added `sprite-override` Attribute to UltraRecipes
- Fixed a minor issue with Malicious Faces rotation
- Kinda fixed transparency layering issues (only works with `Fabulous!` graphics enabled)
- Fixed an issue that caused the music in limbo1 to switch back to `Mirage of Paradise` when it shouldn't
- Added Hyacinth and//or Butterflies to old Limbo Levels
- Music Sounds now get streamed, meaning they cause less lag
## Edit Mode Changes
- Added `noAI` Attribute to spawn listeners
## Resource Changes
- Changed Shotgun UVs(moved smoke to make room for the chainsaw)
## API Changes
