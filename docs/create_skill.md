# Create your own skill for 6.2+
I will explain step by step how to properly create your own skill

<br>

## Disclaimer
- Je ne vais pas traiter l'intégralité des details pour chaque propriété, car cela a déja été fait [ici](#docs/configure_skill).
- Si vous compter creer vos propres skills en utilisant du code java/js, vous n'etes pas au bon endroit. Ici cela ne concerne que la création de skills en uilisant seulment les json
- Vous ne pouvez creer des skills seulement en utilisant des attributs

<br>
<br>

## How it works
Petit rappel sur la maniere de gérer ça!
Les fichiers de skills se trouve dans le dossier `GAMEDIRECTORY/config/tyzs_skills/skills`. Ce qui nous interesse c'es tle dossier `custom`.
Pour chaque skill que vous mettez ici, le jeux tentera de le charger.<br>
[Details](#docs/configure_skill#how-it-works)

<br>
<br>

## Create a custom skill
### 1- Create the file
- Go to `GAMEDIRECTORY/config/tyzs_skills/skills/custom`.
- Create a file ending with `.json` (e.g., `my_custom_skill.json`).

<br>

### 2- Use the template
- Copy and paste the template below:
```
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
- Now we are going to change all the properties

<br>

### 3- Choose an unique ID
This is the most important part, this is like your first name. It let the mod recognize and manage it.

- Choose an unique id in lower case, without spaces and with only underscores (e.g., `my_custom_skill`)

<br>

### 4- Setup your skill properties
> **🛑 I will only deal with specific properties, for details on how to setup general information, please refeere to [Configure Skills](#docs/configure_skill#configuration)**

Le fichier json contient une propriété `modifiers`. cette propriété peux contenir des "modifers", c'est ce que nous allons utiliser pour avoir des effets en jeux.

_Un modifier suit le format suivant:_
```
{
      "attribute": "minecraft:generic.movement_speed",
      "operation": "ADD_VALUE",
      "values": [1.0, 2.0, 3.0, 4.0],
      "unit": "skill.tyzs_skills.unit.percentage"
}
```

- Copiez et coller ce template entre les crochets de `modifers`.
- Nous pouvons maintenant modifier les données:

`attribute`
- This is the player attribute you are going to modify.
- It can come from another mod.
- In the template I use "minecraft:generic.movement_speed", but you can use the one you want [Full vanilla attribute list](https://minecraft.wiki/w/Attribute).

> **🛑 If the modifier is missing, the skill will not be loaded.**<br>
> **🛑 If the modifier is incorrect, the skill will have no effect in game.**

`operation`
- This is how we are going to modify the attribute.
- You have to decide if it will add a value (ex: adding 1 heart) or increase by a percentage (ex: +10% respiration).<br>
-> If it's the first case then write **"ADD_VALUE".**<br>
-> Otherwise, write **"ADD_MULTIPLIED_BASE".**

`values`
- This is the list of values for the modifier.
- Values can be negative or positive.
- The size of the list must match the maximumLevel (1st value = attribute value at skill level 1).

`unit`
- This is a translation key for the unit to be displayed in the purchase/refund tooltip.
- This field can be empty, in which case no unit will be displayed.

<br>

### 5- Finalizing
Before trying it in game, make sure these condition are met:
- The skill ID is unique and without spaces.
- The price list size doesn't exceed the maximum level.
- The field "type" is clearly defined on "CUSTOM".
- The modifier(s) exists.

Now you can try your skill and continue to add custom skill!


<br>
<br>

## Tips

**Values**
- Vous pouvez utiliser des valeures négatives pour simuler un malus (e.g., `"values": [-1.0, -2.0, -3.0, -4.0]`).
- If you want your skill to work on top of existing effects (potions or other modifiers), you can use "ADD_MULTIPLIED_TOTAL" as operation.

<br>

**Design**
- You can use minecraft item/block textures as an icon (exemple with stick:` minecraft:textures/item/stick.png`).
- You can use icons already used (exemple with the one for Health Boost: `tyzs_skills:textures/gui/skills/health_boost.png`).
- I've added few unused icons you can use (e.g., `tyzs_skills:textures/gui/skills/unused_icon_set/FILENAME.png`).
- You are not forced to set an icon, it will display a default one.

<br>

**Texts**
- You can use `{value}` in your description to display skill effect intensity. Si il y a plusieurs modifers, utilisez des numéros pour tout ceux qui seront apres 1(e.g, `{value2}`).
- Vous pouvez directement ecrire les textes (description, displayName, units) directement a la place des clés de traduction. Vous ne pourrez simplement pas les traduires.

<br>

**Other**
- Use `/skills reload` to apply changes you made while in game.







