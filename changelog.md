## Major Changes
- Fixed Overheat Nailgun being uncraftable
- Fixed rare client crash caused by style bonus nullref
- Fixed the last target of a piercing shot not actually taking the damage of all remaining pierces due to I-Frames
- Fixed Hell Bullets doing vanilla thrown projectile damage, which scales with difficulty
- Fixed Persistent Projectiles being parriable when stuck in ground
- Fixed Slam storage.. storage(?) issue
- Fixed alternate revolver hammer pull not working like in the original
- Hopefully fixed issue with enemies in levels not despawning properly
  - If it does work, that should also fix stained glass windows sometimes staying behind when a level instance gets destroyed
- Improved Drone parrying
- Improvements to Levels
  - All Levels Entrance Elevator door trigger has been expanded
  - All Level Unlock Triggers have been expanded
  - Prelude2 // Straight to Hell
    - Expanded first spawn trigger in `BigOutdoors` Room
    - Changed `mainRoom` wave 2 Schism placement to guide players towards the right door more naturally
  - Limbo1 // Illusionary Paradise
    - fixed softlock in `room-68`
    - moved spawn trigger in `room-69` slightly
    - fixed one of the doors in `room-69` not locking properly
  - Limbo2 // Halls of the Blameless
    - Nailgun Pedestal now gets removed after obtaining the Nailgun to reduce confusion
    - Made `Odd5ShapedRoom`s Spawn Trigger harder to skip accidentally
    - Reduced delay before second wave in `FirstRoomIEverMade`
- Added a safeguard that fixes an edit mode desync preventing focusing of blocks
- Hopefully fixed or at least improved door desyncing issue
- Fixed Coin Splitting
- Hitscans can no longer hit dead entities
- Fixed Mod Explosion damage boost towards non-mod entities also affecting Players
- Fixed Mod Explosion damage scaling with difficulty
- Fixed double//tripple//multi kill style bonuses not working correctly
- Fixed Style Bonus Weapon Staleness still being used when only one Weapon is held//equipped
- Fixed a rare crash involving enemies in levels dying
- Fixed alternative cybergrind loot tables for enemies being messed up, resulting in nothing being dropped at all
- Made getting locked out of the `destiny-chapel` Limbo Challenge Structure harder
- Hopefully fixed Structures and Levels being breakable when they shouldn't be
- Fixed typo in Triple Kill 
## Settings & Config
## Commands
## Tweaks
- Reduced Hell Bullet damage (6 -> 5)
- Made Interrupting Swordsmachines Melee Attacks easier
- Made Coin Splitting easier
## Minor Changes
- Finally removed the Console spam caused by datafixers missing from entities
- Updated Russian Translation
  - Thanks to closet748, it's no longer scrapped
- Added `debug` nbt tag to mod enemies
  - setting it to true does debug stuff with the enemy
  - currently it only makes melee interruptable enemies glow while they are interruptable
- Changed draw order of Style Bonuses
- Fixed a small bit of weirdness with weapon loadouts
- Added more Hell Mass to Demons
## Resource Changes
## API Changes
