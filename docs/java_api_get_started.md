# Get started with the java API

Tyz's Skills provides a stable and flexible API, allowing developers to interact with the skill system, create addons, and extend the mod's features. 
This documentation will give you the keys to building powerful addons for Tyz's Skills.

## Dependencies
As mentioned in the main `README`, you need to add the mod as a dependency to your project.

Add this to your `build.gradle`:
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

Add this to your `neoforge.mods.toml` to ensure the mod loads correctly:
```toml
[[dependencies.your_mod_id]]
    modId="tyzs_skills"
    mandatory=true
    versionRange="[6.2.0,)"
    ordering="AFTER"
    side="BOTH"
```

After adding these, refresh your Gradle project to import the library.

## How it works
Skills and data are automatically loaded from JSON files located in the GAMEDIRECTORY/config/tyzs_skills folder.

Inside this directory, you will find:

- `skills/default` : This folder is wiped and recreated every time the game launches to serve as a reference.

- `skills/custom` : Contains user-modified skills. These JSONs act as an overwrite for the `default` folder.

The API handles **all the registration and parsing for you**. You don't need to manually write or parse JSON files to add your own default skills. 
You simply use the **provided registration** methods, and the mod will generate the necessary files in the default folder for the user.

## Summary
This documentation is divided into several parts to help you navigate the API:
1. Registering Skills: Learn how to register and update your own skills so they appear in the game and the default config folder.
2. The API Accessor: How to use the global entry point to access player data (SP, XP, Levels).
3. Custom Skill Types: Go beyond attributes by creating your own logic for skills (using the IMMUTABLE or CUSTOM types).
4. Events & Hooks: Listen to skill purchases, XP gains, or level-ups directly in Java to trigger your own mod's mechanics.

