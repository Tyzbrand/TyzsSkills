---
description: Start with Tyz's Skills 6.4+
---

# Get Started

## Introduction

Welcome to <mark style="color:violet;">**Tyz's Skills**</mark>!&#x20;

This page will guide you through the <mark style="color:violet;">**core concepts**</mark> of the mod and how you can <mark style="color:violet;">**customize**</mark> it to fit your needs.

Tyz's Skills is designed as a <mark style="color:violet;">**plug-and-play**</mark> mod. This means you can drop it into your game and play immediately <mark style="color:violet;">**without changing**</mark> a single setting.&#x20;

Everything has been carefully tuned to provide the <mark style="color:violet;">**most balanced experience**</mark> possible while maintaining a <mark style="color:violet;">**vanilla Minecraft feel**</mark>.

However, if you are a <mark style="color:violet;">**modpack creator**</mark>, a <mark style="color:violet;">**server owner**</mark>, or simply a <mark style="color:violet;">**player**</mark> looking to tailor your experience, you are in the right place!&#x20;

The mod is <mark style="color:violet;">**data-driven**</mark>, making <mark style="color:violet;">**99%**</mark> of its features fully <mark style="color:violet;">**customizable**</mark>.

***

## Neoforge Config

You can access and modify the <mark style="color:violet;">**general parameters**</mark> of the mod directly or through the native <mark style="color:violet;">**NeoForge configuration file**</mark>:



* <mark style="color:purple;">**COMMON Config**</mark><mark style="color:$primary;">:</mark> Change refund values, set XP or SP caps, configure death penalties, and toggle global mechanics (like disabling the entire purchase/refund system).
* <mark style="color:purple;">**CLIENT Config**</mark>: Change the positions, colors, and scaling of overlays, reposition or toggle the inventory menu button, and customize other user interface elements.

***

## JSON <mark style="color:$primary;">Customization</mark>

You can <mark style="color:violet;">**modify all**</mark> the major configurations of the mod by editing <mark style="color:violet;">**JSON files**</mark> directly within the mod's <mark style="color:violet;">**config folder**</mark>.<br>

What you can configure via JSON:

* <mark style="color:purple;">**Default Skills**</mark>: Modify any existing skill data (display names, prices, icons, effects). You can also disable skills or change their behavior.
* <mark style="color:purple;">**Custom Skills**</mark>: Create your own skills from scratch using any available player attributes.
* **XP Gains**: Fine-tune the exact XP rewards for breaking blocks, killing entities, or eating food by adding or removing IDs and tags.
* <mark style="color:purple;">**Leveling & Scaling**</mark>: Balance how much XP is required per level and customize the rewards gained upon leveling up.
* <mark style="color:purple;">**Categories**</mark>: Create new GUI tabs or modify existing ones to structure your experience your way.

<p align="right"><a href="/broken/pages/xz7QxY9rDt2akUuDjPwm" class="button primary" data-icon="file-brackets-curly">Start with JSON</a></p>

***

## KubeJS Integration

If you want to implement <mark style="color:violet;">**more advanced**</mark> and dynamic logic, you can write custom <mark style="color:violet;">**JavaScript scripts**</mark> to interact with the mod.&#x20;

Tyz's Skills provides a wide range of custom <mark style="color:violet;">**events**</mark> and <mark style="color:violet;">**methods**</mark>.

What you can achieve via KubeJS:

* Manipulate <mark style="color:violet;">**skill data**</mark> on the <mark style="color:violet;">**server**</mark> side.
* Read and modify <mark style="color:violet;">**player data**</mark> (SP, XP, levels) on both the <mark style="color:violet;">**client**</mark> and <mark style="color:violet;">**server**</mark> side.
* Listen and react to specific <mark style="color:violet;">**mod events**</mark> (such as <mark style="color:violet;">**purchasing**</mark>, <mark style="color:violet;">**refunding**</mark>, <mark style="color:violet;">**earning XP**</mark><mark style="color:$primary;">**...**</mark>) to trigger custom code.

<p align="right"><a href="/broken/pages/lq0LTUZHBsw6jmqgsifw" class="button primary" data-icon="square-js">Start with KubeJS</a></p>

***

## Java API

Tyz's Skills also features a <mark style="color:violet;">**native Java API**</mark>, allowing developers to build <mark style="color:violet;">**official addons**</mark> for the mod!

* **Documentation Coming Soon...**

{% hint style="warning" %}
The Java API is currently under **heavy development**. While the **Javadoc** is up to date and **functional**, we strongly **advise against** using it for production addons.



Once fully released and stable, it will serve as a secure, **long-term** platform for your **addons**. However, during this **development** phase:

* The API will undergo **drastic changes** that are highly likely to **break** your addons in future updates.
* We cannot guarantee long-term stability or unintended side effects just yet.
{% endhint %}

