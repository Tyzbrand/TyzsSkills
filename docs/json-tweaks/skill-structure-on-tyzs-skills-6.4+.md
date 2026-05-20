---
description: This page provides a detailed breakdown of a skill JSON file structure.
layout:
  width: wide
  title:
    visible: true
  description:
    visible: true
  tableOfContents:
    visible: true
  outline:
    visible: true
  pagination:
    visible: true
  metadata:
    visible: true
  tags:
    visible: true
tags:
  - json
---

# Skill Structure on Tyz's Skills 6.4+

## Main Properties

A skill file possesses numerous configurable properties.

{% hint style="danger" %}
**DO NOT** modify properties that are not explained below, as this could **corrupt** your data and **break** the skills.
{% endhint %}

_Configurable Properties:_

<table><thead><tr><th width="204">Property</th><th width="269" align="center">Possible Values</th><th width="340">Description</th><th width="340">Notes</th></tr></thead><tbody><tr><td><code>active</code></td><td align="center"><code>true</code>, <code>false</code></td><td>Defines if the skill is loaded.</td><td>This is the only way to disable a skill.</td></tr><tr><td><code>maximumLevel</code></td><td align="center">Values between <code>1</code> and <code>99</code></td><td>Determines the maximum level of the skill.</td><td></td></tr><tr><td><code>prices</code></td><td align="center">Positive integer values</td><td>Price for each level.</td><td>The list size must match the <code>maximumLevel</code> (e.g., [10, 20, 30]). The 1st value = price for level 1.</td></tr><tr><td><code>category</code></td><td align="center">Valid category ID</td><td>Determines the GUI tab where the skill appears.</td><td>Can be found in the config folder. Default are: abilities, combat, misc and nature.</td></tr><tr><td><code>icon</code></td><td align="center">Valid asset path</td><td>Determines the path to the skill's icon.</td><td>Must follow Minecraft asset formatting and come from a loaded Resource Pack or Mod.</td></tr><tr><td><code>displayName</code></td><td align="center">Translation key or plain text</td><td>Determines the skill's display name.</td><td></td></tr><tr><td><code>description</code></td><td align="center">Translation key or plain text</td><td>Determines the description to display.</td><td></td></tr><tr><td><code>modifiers</code></td><td align="center"><a href="README%20(1).md#modifier">Modifier</a>, empty <code>[]</code></td><td>Determines the data related to the attributes affected by the skill.</td><td>For <code>IMMUTABLE</code> or <code>TRAIT</code> type skills, this field must be left empty.</td></tr><tr><td><code>customValues</code></td><td align="center"><a href="README%20(1).md#custom-values">Custom value</a>, empty <code>{}</code></td><td>Determines the data related to the skill's specific values.</td><td>For non-<code>IMMUTABLE</code> type skills, this field can be left empty.</td></tr><tr><td><code>config</code></td><td align="center"><a href="README%20(1).md#behavior-settings">Behavior settings</a>, empty <code>{}</code></td><td>Optional settings defining the skill's in-game mechanics and restrictions.</td><td>This field can be left empty.</td></tr></tbody></table>

<details>

<summary>Example of an <code>IMMUTABLE</code> Skill Structure</summary>

```json
{
  "active": true,
  "id": "adrenaline",
  "maximumLevel": 3,
  "prices": [2, 4, 6],
  "type": "IMMUTABLE",
  "category": "combat",
  "icon": "tyzs_skills:textures/gui/skills/adrenaline.png",
  "displayName": "skill.tyzs_skills.adrenaline.displayName",
  "description": "skill.tyzs_skills.adrenaline.description",
  "modifiers": [],
  "customValues": {
    "effect_duration": {
      "values": [3.0, 5.0, 8.0],
      "unit": "skill.tyzs_skills.unit.seconds"
    }
  },
  "config": {}
}
```

</details>

<details>

<summary>Example of a <code>GENERIC</code> Skill Structure</summary>

```json
{
  "active": true,
  "id": "block_reach",
  "maximumLevel": 4,
  "prices": [4, 6, 8, 12],
  "type": "GENERIC",
  "category": "abilities",
  "icon": "tyzs_skills:textures/gui/skills/block_reach.png",
  "displayName": "skill.tyzs_skills.block_reach.displayName",
  "description": "skill.tyzs_skills.block_reach.description",
  "modifiers": [
    {
      "attribute": "minecraft:player.block_interaction_range",
      "operation": "ADD_VALUE",
      "values": [1.0, 2.0, 3.0, 4.0],
      "unit": "skill.tyzs_skills.unit.blocks"
    }
  ],
  "customValues": {},
  "config": {}
}
```

</details>

***

## Behavior Settings

You can add parameters inside the `config` object to modify how skills behave in-game.

These parameters are optional and will fall back to their default values if omitted.\
\
&#xNAN;_&#x41;vailable Settings:_

<table><thead><tr><th width="191">Property</th><th width="200">Possible Values</th><th width="146">Default Value</th><th width="340">Description</th><th width="340">Notes</th></tr></thead><tbody><tr><td><code>levelRequirement</code></td><td>Positive integer</td><td><code>0</code></td><td>Prevents the skill from being purchased if the player's level requirement is not met.</td><td>A value of <code>0</code> means no level requirement.</td></tr><tr><td><code>visible</code></td><td><code>true</code>, <code>false</code></td><td><code>true</code></td><td>Determines if the skill is visible in the Menu.</td><td>Only applies when the player's skill level is <code>0</code>.</td></tr><tr><td><code>purchasable</code></td><td><code>true</code>, <code>false</code></td><td><code>true</code></td><td>Determines if the skill can be purchased from the menu.</td><td>Only applies if the <code>PURCHASE_SYSTEM</code> common config is <code>true</code>.</td></tr><tr><td><code>refundable</code></td><td><code>true</code>, <code>false</code></td><td><code>true</code></td><td>Determines if the skill can be refunded from the menu.</td><td>Only applies if the <code>REFUND_SYSTEM</code> common config is <code>true</code>.</td></tr><tr><td><code>incompatibleSkills</code></td><td>Array of skill IDs</td><td>Empty list</td><td>Defines a list of skills that cannot be owned simultaneously with this one.</td><td></td></tr><tr><td><code>skillPrerequisites</code></td><td>Array of skill IDs</td><td>Empty list</td><td>Defines a list of skills the player must own before buying this one.</td><td></td></tr></tbody></table>

<details>

<summary>Example of a <code>config</code> Block</summary>

```json
"config": {
    "levelRequirement" : 15,
    "visible" : true,
    "purchasable" : false,
    "refundable" : true,
    "incompatibleSkills" : ["swift_learn", "excavation"],
    "skillPrerequisites" : ["twist_of_fate", "spare_parts"]
}
```

</details>

***

## Modifier

A modifier is an object that a skill can possess only if it is of type `GENERIC` or `CUSTOM`.

It contains all the information related to the modification of an attribute. A skill can have multiple modifiers for different attributes.

_A modifier consists of the following properties:_

`attribute`

* This is the player attribute you are going to modify [Full vanilla attribute list](https://minecraft.wiki/w/Attribute).
* It can come from another mod.
* e.g., **"minecraft:generic.movement\_speed"**

{% hint style="warning" %}
- If the modifier is missing, the skill will **not be loaded**.
- If the modifier is incorrect, the skill will have **no effect in game**.
{% endhint %}

`operation`

* This is how the skill is going to affect the attribute
* It can be :
  * **ADD\_VALUE** -> adds all of the modifiers' amounts to the base attribute (e.g., a value of 1.0 adds +1)
  * **ADD\_MULTIPLIED\_BASE** (treated as a percentage) -> multiplies the base attribute by (1 + value / 100) (e.g., a value of 10 adds +10%)
  * **ADD\_MULTIPLIED\_TOTAL** (treated as a percentage) -> multiplies the attribute value with all existing modifiers by (1 + value / 100) (e.g., a value of 10 adds +10%)
  * [More details](https://minecraft.wiki/w/Attribute)

`values`

* This is the list of values for the modifier.
* Values can be negative or positive.
* The size of the list must match the `maximumLevel` (1st value = attribute value at skill level 1).

`unit`

* This is a translation key for the unit to be displayed in the purchase/refund tooltip.
* This field can be empty, in which case no unit will be displayed.

<details>

<summary>Example of a <code>modifier</code> Block</summary>

```json
"modifiers": [
    {
      "attribute": "minecraft:player.block_interaction_range",
      "operation": "ADD_VALUE",
      "values": [1.0, 2.0, 3.0, 4.0],
      "unit": "skill.tyzs_skills.unit.blocks"
    }
  ],
```

</details>

***

## Custom Values

Custom values are an object that a skill can possess, only if it is of type `IMMUTABLE`. It contains all the information regarding the specific values the skill uses in gameplay.

{% hint style="danger" %}
You will **only need** to touch the values list. If you modify the value key, the skill will **break**.

Only change the key if **you know** exactly **what you are doing**.
{% endhint %}

_Custom values consist of the following properties:_

`values`

* This is the list of values that the skill processes.
* Values can be negative or positive.
* The size of the list must match the `maximumLevel` (1st value = effective value at skill level 1).

`unit`

* This is a translation key for the unit to be displayed in the purchase/refund tooltip.
* This field can be empty, in which case no unit will be displayed.

<details>

<summary>Example of a <code>customValues</code> Block</summary>

```json
"customValues": {
    "effect_duration": {
      "values": [3.0, 5.0, 8.0],
      "unit": "skill.tyzs_skills.unit.seconds"
    }
}
```

</details>
