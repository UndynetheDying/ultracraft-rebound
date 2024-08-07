## Major Changes
- Added fragile Skulls
  - Disappear when leaving a level to prevent keeping skulls from levels
- Level Changes
  - limbo1 `Illusionary Paradise`
    - Fixed an unprotected gap inbetween Room `Hall of Nails` and Room `Filthy Staircase`
    - Fixed a bunch of Spawner Listeners `yaw` Attribute
    - Replaced Skulls with fragile Skulls
  - limbo2 `Halls of the Blameless`
    - Fixed an unprotected gap in Room `Lava`
    - Fixed an unprotected gap in Room `Hideous Court`
    - Replaced Skulls with fragile Skulls
    - Added some more Stained Glass Windows
  - limbo3 `Clair de Lune`
    - Fixed an unprotected gap right outside the Entrance Elevator
    - Replaced Skulls with fragile Skulls
- Fixed Bucket Items being usable to both take and place fluids in protected Areas
- Fixed Flint and Steel being usable to place fire in protected Areas
- `Disable Flashy VFX` Setting is now disabled by default
  - It was enabled by accident
- Fixed `punch_breakable` and `knuckle_breakable` block tags not working properly
- Knuckleblaster punches and blasts can now break cracked stone bricks
- Ultracraft Explosion Knockback will now always go upwards
  - As in, it takes the absolute value of y; it still applies velocity on the x and z axis of course 
## Settings & Config
## Commands
- Fixed `/ultracraft progression level grant-all` not unlocking Level `clair de lune`
- Fixed `/ultracraft progression <unlocked//obtained> grant-all` not unlocking `Hivel Wings`, `Blood Healing` and `UltraHUD`
- Added `/ultracraft progression <list> revoke-all <target>`
  - Clears the given progression list on given targets (level unlocks are still global, so any target will affect everyone)
## Tweaks
- Buffed Knuckleblaster Explosion Damage (1.0 -> 2.0)
## Minor Changes
- Fixed Shotgun Pump Animation
- Fixed the Counterfeit Music Disc not being in the REI Music Disc Item Group
## Edit Mode Changes
## Resource Changes
- Reverted some unintended changes to the shotgun model in 2.2.0 (mainly animation related)
## API Changes
