![PVPGear](http://s019.radikal.ru/i630/1204/20/c4ff8d0ea7ff.png)

## PVPGear ##

PVPGear is a simple plugin for Bukkit Minecraft Server that makes some items like golden armor and tools (useless before) useful again – in PVP!

Items are fully configurable through config so you can make any item to hit foes like thousand swords (or harder!).

Current plugin state: beta. I haven't throughly tested this on productions server so use at your own risk.

## Configuration ##

Open config.yml and edit values. That's just that simple.

In **weapons** section you can modify items that do damage. Swords and axes go here.     
In **armor** section you can place gear that reduce damage. Helmets and chestplates go here.

Each item in both section has two parameters:

* **name** – that's a name of item, just not to get lost.     
* **damage** - this is a MULTIPLIER of damage. By default it is 1, so no changes to game mechanics are made because X * 1 = X (school maths!). If you want you sword to hit harder - change this to something above 1. If you place here a 2 it will hit harder 2 times. Over-wise, if you want item to be more gentle place here something below 1 (0.5 f.e.).

And the last parameter, **Debug** brings on debug mode. It is for debug.

[Russian discussion thread on rubukkit.org](http://rubukkit.org/threads/thread.11756/)

Isn't this simple?..:)