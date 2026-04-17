# Create a skill using java API

petit rappel : 
- Le point d'entré de l'API est : `TyzsSkillsAPI.class`
- Attention a n'appeler ses méthodes seulement coté server

## Register a Prefab
Afin de pouvoir enregistrer votre propre skill, vous deveez fournir au mod un "prefab" de ce skill. 

### 1- Creation du prefab
Un prefab est un record utilisé pour preInstancier un skill.

_Import:_
```java
import com.tyzsskills.api.records
```
_Example of instantiation:_
```java
SkillPrefab myCustomSkill = new SkillPrefab(
true,                                         // active
"my_custom_skill",                            // id
4,                                            // maximumLevel
List.of(),                                    // prices
Enums.SkillType.GENERIC,                      // type
Enums.CategoryType.MISC,                      // category
true,                                         // purchasable
"myaddon:textures/gui/skills/icon.png",       // icon
"skill.myaddon.custom.name",                  // displayName
"skill.myaddon.custom.desc",                  // description
List.of(),                                    // modifiers (null if none)
Map.of(),                                     // customValues (null if none)
false,                                        // isTrait (will be removed, set it false)
0                                             // powerWeight (will be removed, doesn't matter)
);
```

**Parameters Overview**<br>
Most of these parameters work exactly the same way as they do in the JSON configuration. 
Click [here](configure_skill.md) to see the detailed explanation of each property.

<br>

_Specific Java parameters to note:_
- `modifiers`/`customValues`: If your skill doesn't use these, you must pass `null`.
- `isTrait`/`powerWeight`: Will be removed next update. Pass `false`, it now doesn't matter.
- `type`: Your skill's type must be defined as `IMMUTABLE` or `GENERIC`. **Don't use** `CUSTOM`, this is exclusive to custom JSON skills.

<br>



