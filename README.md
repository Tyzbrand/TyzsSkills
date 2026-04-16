<img width="2048" height="990" alt="TITLE_BASE" src="https://github.com/user-attachments/assets/38693032-fc5d-4bd0-a24a-5158dbad8b0e" />


**Tyz's Skills** is a skill mod for Minecraft. Increase your level passively to unlock a variety of skills and customize your gameplay in Minecraft. Enhance your abilities, become more powerful, and get extra features. All this with a deep and flexible skill system.


## Project Goal
The long-term vision is to provide a fully customizable progression system. I am currently working on an **API** to allow other developers to easily create addons without needing to modify the core mod.

## Documentation
Here the official documentation! It is still growing, but it will help you get started.<br>
_Compatible with: NeoForge 1.21.1 | Mod version: 6.2+_

### JSON Tweaks
- [Configure existing skills](docs/configure_skill.md)
- [Create custom skills](docs/create_skill.md)

### KubeJS
- []()


## Integration 
[![](https://jitpack.io/v/Tyzbrand/TyzsSkills.svg)](https://jitpack.io/#Tyzbrand/TyzsSkills)

You can easily integrate TyzsSkills into your repo.

Add this to your `build.gradle`
```gradle
repositories {
  maven { url 'https://jitpack.io' }
}
```
```gradle
dependencies {
    implementation 'com.github.Tyzbrand:TyzsSkills:6.2.0'
}
```

Add this to your `neoforge.mods.toml` for the dependency
```toml
[[dependencies.your_mod_id]]
    modId="tyzs_skills"
    mandatory=true
    versionRange="[6.2.0,)"
    ordering="AFTER"
    side="BOTH"
```


## Contributions & Pull Requests
Contributions and ideas are always welcome! 

**Please note:** To maintain perfect architectural consistency as I build an upcoming API, I won't "merge" Pull Requests directly. If you submit a fix or a feature, I will  reimplement/adapt it into the main branch. This ensures I stay 100% familiar with every line of code for future maintenance. 

This is my first major project, so the code might not be the cleanest or most optimized in some places. I am completely open to **tips, tricks, and advice**! If you see something that could be done better, feel free to let me know—I’m here to learn.

*Thanks!*

## License
This project is licensed under the **MIT License**. You are free to use, copy, and modify the code as long as the original author attribution is maintained.

*Find the mod on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/tyzs-skills) or [Modrinth](https://modrinth.com/mod/tyzs-skills).*
