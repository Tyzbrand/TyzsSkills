# Use KubeJS events to react to the mod for 6.2+

You can catch events to cancel, modify or react to them by using `TyzsSkillsEvents`.
When an event is cancelable, you can use `event.cancel()`.

_Examples of usage:_
```js
// Example 1: Canceling an event
TyzsSkillsEvents.skill_purchase_pre(event => {
  // Prevent players from buying a specific skill
  if (event.getSkill().getId() == 'my_custom_skill') {
    event.cancel()
  }
})


// Example 2: Modifying a value dynamically
TyzsSkillsEvents.xp_change(event => {
  // Example: Double the XP gained by the player!
  let oldXp = event.getOldAmount()
  let newXp = event.getNewAmount()
  
  if (newXp > oldXp) {
    let gained = newXp - oldXp
    event.setNewAmount(oldXp + (gained * 2))
  }
})
```

<br>
<br>

## EVENTS
### Technical

- `skill_load_pre` -> At the start of the server, just before a skill is loaded. **[Cancelable]**
> Skill: `event.getSkill()`

<br>
<br>

- `skill_load_post` -> At the start of the server, just after a skill is loaded and added to the server list.
> Skill: `event.getSkill()`

<br>
<br>

- `skill_reload` -> When server skill list is reloaded (/skills reload).

<br>
<br>

- `player_reset` -> When a player gets their data reset (/skills reset).
> Player: `event.getPlayer()`

<br>
<br>

### Metadata

- `sp_change` -> When the SP amount of a player changes. **[Cancelable]**
> Player: `event.getPlayer()`<br>
> Amount: `event.getNewAmount()`,  `event.getOldAmount()`,  `event.setNewAmount(amount)`

<br>
<br>


- `power_change` -> When the power amount of a player changes. **[Cancelable]**
> Player: `event.getPlayer()`<br>
> Amount: `event.getNewPower()`,  `event.getOldPower()`,  `event.setNewPower(power)`

<br>
<br>

- `xp_change` -> When the XP amount of a player changes. **[Cancelable]**
> Player: `event.getPlayer()`<br>
> Amount: `event.getNewAmount()`,  `event.getOldAmount()`,  `event.setNewAmount(amount)`

<br>
<br>

- `skill_level_change` -> When a player's level changes.  **[Cancelable]**
> Player: `event.getPlayer()`<br>
> Amount: `event.getNewLevel()`,  `event.getOldLevel()`,  `event.setNewLevel(level)`

<br>
<br>

### Specific Actions

- `skill_purchase_pre` -> Just before a skill is bought by a player. **[Cancelable]**
> Player: `event.getPlayer()`<br>
> Skill: `event.getSkill()`

<br>
<br>

- `skill_purchase_post` ->  Just after a skill is bought by a player. 
> Player: `event.getPlayer()`<br>
> Skill: `event.getSkill()`

<br>
<br>

- `skill_refund_pre` -> Just before a skill is refunded by a player. **[Cancelable]**
> Player: `event.getPlayer()`<br>
> Skill: `event.getSkill()`

<br>
<br>

- `skill_refund_post` -> Just after a skill is refunded by a player. 
> Player: `event.getPlayer()`<br>
> Skill: `event.getSkill()`

<br>
<br>

- `skill_bookmark` -> When a player bookmarks a skill. 
> Player: `event.getPlayer()`<br>
> Skill: `event.getSkill()`

<br>
<br>
