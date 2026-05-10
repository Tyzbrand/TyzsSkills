# Create your own skill for 6.2+

I will explain step by step how to properly create your own skill.

<br>

## Disclaimer

* I will not cover every detail for each property here, as that is already explained [here](../).
* If you intend to create skills using Java/JS code, you are in the wrong place. This guide only covers skill creation using JSON files.
* You can only create custom skills that modify player attributes.

\
<br>

## How it works

A quick reminder on how this works! Skill files are located in the `GAMEDIRECTORY/config/tyzs_skills/skills` folder. The folder we are interested in is `custom`. For every skill you place inside it, the game will attempt to load it.\
[More details here](../#how-it-works)

\
<br>

## Create a custom skill

### 1- Create the file

* Go to `GAMEDIRECTORY/config/tyzs_skills/skills/custom`.
* Create a file ending with `.json` (e.g., `my_custom_skill.json`).

<br>

### 2- Use the template

* Copy and paste the template below into your file:

```json
{
  "active": true,
  "id": "my_custom_skill",
  "maximumLevel": 4,
  "prices": [4, 6, 8, 12],
  "type": "CUSTOM",
  "category": "ABILITIES",
  "purchasable": true,
  "icon": "tyzs_skills:textures/gui/skills/my_custom_skill.png",
  "displayName": "skill.tyzs_skills.my_custom_skill.displayName",
  "description": "skill.tyzs_skills.my_custom_skill.description",
  "modifiers": [],
  "customValues": {}
}
```

* Now we are going to change all the properties

<br>

### 3- Choose a unique ID

This is the most important part, this is like your first name. It lets the mod recognize and manage it.

* Choose a unique id in lower case, without spaces and with only underscores (e.g., `my_custom_skill`)

<br>

### 4- Setup your skill properties

> **🛑 I will only cover specific properties. For details on how to set up general information, please refer to** [**Configure Skills**](../#configuration)

The JSON file contains a `modifiers` property. This property holds "modifiers", which we will use to apply in-game effects.

_A modifier follows this format:_

```json
{
      "attribute": "minecraft:generic.movement_speed",
      "operation": "ADD_VALUE",
      "values": [1.0, 2.0, 3.0, 4.0],
      "unit": "skill.tyzs_skills.unit.percentage"
}
```

* Copy and paste this template between the brackets `[]` of `modifiers`.
* We can now modify the data:

`attribute`

* This is the player attribute you are going to modify.
* It can come from another mod.
* In the template I use "minecraft:generic.movement\_speed", but you can use the one you want [Full vanilla attribute list](https://minecraft.wiki/w/Attribute).

> **🛑 If the modifier is missing, the skill will not be loaded.**\
> **🛑 If the modifier is incorrect, the skill will have no effect in game.**

`operation`

* This is how we are going to modify the attribute.
* You have to decide if it will add a value (ex: adding 1 heart) or increase by a percentage (ex: +10% respiration).\
  -> If it's the first case then write **"ADD\_VALUE".**\
  -> Otherwise, write **"ADD\_MULTIPLIED\_BASE".**

`values`

* This is the list of values for the modifier.
* Values can be negative or positive.
* The size of the list must match the maximumLevel (1st value = attribute value at skill level 1).

`unit`

* This is a translation key for the unit to be displayed in the purchase/refund tooltip.
* This field can be empty, in which case no unit will be displayed.

<br>

### 5- Finalizing

Before trying it in game, make sure these conditions are met:

* The skill ID is unique and without spaces.
* The price list size matches the maximum level.
* The field "type" is clearly defined on "CUSTOM".
* The modifier(s) exists.

Now you can try your skill and continue to add custom skills!

\
<br>

## Tips

**Values**

* You can use negative values to simulate a penalty or debuff (e.g., `"values": [-1.0, -2.0, -3.0, -4.0]`).
* If you want your skill to work on top of existing effects (potions or other modifiers), you can use "ADD\_MULTIPLIED\_TOTAL" as the operation.

<br>

**Design**

* You can use minecraft item/block textures as an icon (example with stick:`minecraft:textures/item/stick.png`).
* You can use icons already used (example with the one for Health Boost: `tyzs_skills:textures/gui/skills/health_boost.png`).
* I've added few unused icons you can use (e.g., `tyzs_skills:textures/gui/skills/unused_icon_set/FILENAME.png`).
* You are not forced to set an icon, it will display a default one.

<br>

**Texts**

* You can use `{value}` in your description to display skill effect intensity. If there are multiple modifiers, use numbers for the subsequent ones (e.g, `{value2}`).
* You can directly write plain text (description, displayName, units) instead of translation keys. You simply won't be able to translate them into other languages via resource packs.

<br>

**Other**

* Use `/skills reload` to apply changes you made while in game.
