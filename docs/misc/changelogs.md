# Changelogs

This page displays the official <mark style="color:violet;">**changelogs**</mark> for the <mark style="color:violet;">**latest versions**</mark> of Tyz's Skills.

{% hint style="info" %}
To keep this documentation relevant and concise, older versions have been omitted.&#x20;

The history below covers all updates starting from version **6.4.0** up to the **most recent release**.
{% endhint %}

{% updates format="full" %}
{% update date="2026-05-20" %}
## v6.4.2

#### **\[Hotfix]**

* <mark style="color:blue;">**Fixed #20**</mark>: Fixed an issue where skill refunds would guarantee a minimum return of 1 SP.
* <mark style="color:blue;">**Fixed #21**</mark>: Fixed a bug where the purchase/refund tooltip would be hidden if the player couldn't afford the skill.
* <mark style="color:blue;">**Emergency Rework**</mark>: Overhauled "Spare Parts" logic for better balance. The refund probability is now rolled per ingredient rather than globally per craft. Added a new configurable cap to limit the maximum number of materials that can be recovered per craft.
{% endupdate %}

{% update date="2026-05-18" %}
## v6.4.1

#### **\[Hotfix]**

* Fixed an issue where the skill list would appear empty upon opening the menu for the first time. The "ALL" tab is now selected by default.
* Fixed an issue where players could gain XP by consuming non-food items under certain conditions.
* <mark style="color:blue;">**Fixed #19**</mark>: Resolved a bug in singleplayer where JSON files were deleted when switching worlds.
* <mark style="color:blue;">**Fixed #18**</mark>: Fixed an issue that prevented GENERIC and CUSTOM skill modifiers from being correctly removed under level 1.
{% endupdate %}

{% update date="2026-05-13" %}
## v6.4.0

#### **\[Changes]**

* <mark style="color:blue;">**Custom Categories**</mark>: You can now create and modify your own categories without limits via JSON.
* <mark style="color:blue;">**JSON Unified Loading**</mark>: Centralized the JSON loading method. All modifications must now be performed via overrides within the Custom folder.
* <mark style="color:blue;">**Tooltips**</mark>: New tooltip styles (cleaner and unique visuals).
* <mark style="color:blue;">**Skill Configuration**</mark>: Added an optional "configuration" property to skills to define specific behaviors:
  * Purchasable
  * Refundable
  * Incompatibilities
  * Prerequisites
  * Level requirement
* <mark style="color:blue;">**New Category**</mark>: Added "Nature".
* <mark style="color:blue;">**UI**</mark>: Redesigned category tab layout in the menu.
* <mark style="color:blue;">**Skill Reassignment**</mark>: Updated categories for:
  * Excavation: Misc -> Abilities
  * Sheperd's Blessing: Misc -> Nature
  * Green Thumb: Misc -> Nature
* <mark style="color:blue;">**Config**</mark>: New config setting (Purchasable Skills).
* <mark style="color:blue;">**Bulk Purchase/Refund**</mark>: Buttons now change texture only on hover.
* <mark style="color:blue;">**Skill Requirements**</mark>: Skill requirements are now displayed below the description in the tooltip.
* <mark style="color:blue;">**Localization**</mark>: Added Spanish translation (Thanks to Estatrix).

#### **\[Bug Fixes]**

* Fixed flickering issues caused by stats decimal calculations.
* Fixed some exploits with "Spare Parts". It no longer functions on recipes where all ingredients (4 or 9) are identical.
* Fixed various issues with UI and Textures.

#### **\[API]**&#x20;

<mark style="color:blue;">**Breaking Changes**</mark>: The API is currently under heavy reconstruction. It remains accessible, but usage is highly discouraged at this stage as further breaking changes are guaranteed.
{% endupdate %}
{% endupdates %}
