---
description: This page lists all the in-game commands available in Tyz's Skills.
---

# Commands for Tyz's Skills

Tyz's Skills provides a wide range of <mark style="color:violet;">**commands**</mark> to manipulate <mark style="color:violet;">**player data**</mark>, <mark style="color:violet;">**test configurations**</mark>, or <mark style="color:violet;">**reset progress**</mark>.

{% hint style="info" %}
To execute these commands, a player must have **admin** permissions (**3** or higher).
{% endhint %}

***

#### `/skills reload`&#x20;

<i class="fa-arrow-turn-down-right" style="color:$primary;">:arrow-turn-down-right:</i>  Reloads all JSON configuration files from your 'custom' folder. This allows administrators to instantly <mark style="color:violet;">**apply**</mark> data or skill tweaks <mark style="color:violet;">**without needing**</mark> to <mark style="color:violet;">**relaunch**</mark> <mark style="color:$primary;">the game</mark>.

***

#### `/skills reset <category> <player>`

<i class="fa-arrow-turn-down-right">:arrow-turn-down-right:</i> Completely <mark style="color:violet;">**resets**</mark> specific mod-related data for the targeted player.

<i class="fa-arrow-turn-down-right">:arrow-turn-down-right:</i> Available Categories:

* `all`: Resets <mark style="color:violet;">**all data**</mark> (skills, levels, SP, XP, and stats).
* `skills`: Resets all of the player's <mark style="color:violet;">**skill levels**</mark> back to 0.
* `metadata`: Resets player's <mark style="color:violet;">**core progression**</mark> values (SP, XP and Level).
* `stats`: Resets all tracked player <mark style="color:violet;">**statistics**</mark>.
* `limits`: Resets all player <mark style="color:violet;">**limits**</mark> (XP, SP, and level).

{% hint style="danger" %}
This action is **irreversible**. All reset data will be **permanently lost.**
{% endhint %}

***

#### `/skills skill <player> <action> <skill_id> <amount>`

<i class="fa-arrow-turn-down-right">:arrow-turn-down-right:</i> Modifies the specific <mark style="color:violet;">**skill level**</mark> of a player.

<i class="fa-arrow-turn-down-right">:arrow-turn-down-right:</i> Available Actions:

* `set`: Sets the skill to the specified level.
* `add`: Adds the specified amount of levels to the skill.
* `remove`: Subtracts the specified amount of levels from the skill.

***

#### `/skills point <player> <action> <amount>`

<i class="fa-arrow-turn-down-right">:arrow-turn-down-right:</i> Modifies the <mark style="color:violet;">**Skill Points**</mark> (SP) balance of a player.

<i class="fa-arrow-turn-down-right">:arrow-turn-down-right:</i> Available Actions:

* `set`: Sets the player's SP to the specified amount.
* `add`: Adds the specified amount of SP to the player
* `remove`: Subtracts the specified amount of SP from the player.

***

#### `/skills level <player> <action> <amount>`

<i class="fa-arrow-turn-down-right">:arrow-turn-down-right:</i> Modifies the <mark style="color:violet;">**Level**</mark> of a player.

<i class="fa-arrow-turn-down-right">:arrow-turn-down-right:</i> Available Actions:

* `set`: Sets the player's level to the specied level.
* `add`: Adds the specified amount of levels to the player.
* `remove`: Subtracts the specified amount of levels from the player.

***

#### `/skills xp <player> <action> <amount>`

<i class="fa-arrow-turn-down-right">:arrow-turn-down-right:</i> Modifies the current <mark style="color:violet;">**XP**</mark> of a player.

<i class="fa-arrow-turn-down-right">:arrow-turn-down-right:</i> Available Actions:

* `set`: Sets the player's XP to the specified value.
* `add`: Adds the specified amount of XP to the player.
* `remove`: Subtracts the specified amount of XP from the player.
