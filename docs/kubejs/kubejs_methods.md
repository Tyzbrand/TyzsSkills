# Use KubeJS binding to change and access data

You can also change and access data (sp amount, xp amount, skill lvl, skill list...) and trigger mechanics (purchase, refund...) by using the `TyzsSkills` global binding.

_Examples of usage:_
```js
// Basic manipulation (Adding a level using an item)
ItemEvents.rightClicked('minecraft:emerald', event => {
  let player = event.getPlayer()
  
  // Directly add 1 level to the skill without costing SP
  TyzsSkills.skills().addSkillLevel(player, 'my_custom_skill', 1)
  player.tell("You absorbed the emerald and leveled up!")
})
```
<br>

```js
//  Advanced logic with checks (Skill Altar)
BlockEvents.rightClicked('minecraft:enchanting_table', event => {
  let player = event.getPlayer()
  let skillId = 'magic_mastery'
  
  // 1. Verify the skill exists in the server files
  if (TyzsSkills.skills().isSkillLoaded(skillId)) {
    let currentLevel = TyzsSkills.skills().getSkillLevel(player, skillId)
    
    // 2. Check if the player is below level 3
    if (currentLevel < 3) {
      // 3. Attempt a normal purchase (costs SP and checks requirements automatically)
      let success = TyzsSkills.skills().tryBuySkill(player, skillId)
      
      if (success) {
        player.tell("The altar granted you a new skill level!")
      } else {
        player.tell("You need more SP to use this altar.")
      }
    }
  }
})
```

<br>

## Skill Points
Access these methods using `TyzsSkills.sp()`
- `getSP(player)` -> Returns the player SP amount.
- `addSP(player, amount)` -> Adds SP to the player. 
- `removeSP(player, amount)` -> Removes SP from the player.
- `setSP(player, amount)` -> Sets the SP to a specific amount.

## Skill XP
Access these methods using `TyzsSkills.xp()`
- `getXP(player)` -> Returns the player skill XP amount.
- `addXP(player, amount)` -> Adds skill XP to the player.
- `removeXP(player, amount)` -> Removes skill XP from the player.
- `setXP(player, amount)` -> Sets the skill XP to a specific amount.

## Level
Access these methods using `TyzsSkills.level()`
- `getLevel(player)` -> Returns the player skill level.
- `addLevel(player, level)` -> Adds skill level to the player.
- `removeLevel(player, level)` -> Removes skill level from the player.
- `setLevel(player, level)` -> Sets the skill level of a player.

## Power
Access these methods using `TyzsSkills.power()`
- `getPower(player)` -> Returns the player power.
- `addPower(player, power)` -> Adds power to the player.
- `removePower(player, power)` -> Removes power from the player.
- `setPower(player, power)` -> Sets the power amount of a player.

## Skills
Access these methods using `TyzsSkills.skills()`
- `isSkillLoaded(skillId)` -> Returns true if the skill is loaded in the server list.
- `getSkillLevel(player, skillId)` -> Returns the player's level for the specified skill.
- `addSkillLevel(player, skillId, amount)` -> Adds level to the player's skill.
- `removeSkillLevel(player, skillId, amount)` -> Removes level from the player's skill.
- `setSkillLevel(player, skillId, amount)` -> Sets level to the player's skill.
- `tryBuySkill(player, skillId)` -> Triggers a purchase for the player (handles sp checks and costs automatically).<br>
Returns true if the purchase succeeded, false otherwise.
- `tryRefundSkill(player, skillId)` -> Triggers a refund for the player (handles sp checks and returns points automatically).<br>
Returns true if the refund succeeded, false otherwise.
