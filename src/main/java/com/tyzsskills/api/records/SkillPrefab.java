package com.tyzsskills.api.records;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.model.SkillConfiguration;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data container to register default JSON generation for custom skills
 */
public record SkillPrefab(boolean active, @NotNull String id,
                          @NotNull List<Integer> prices, @NotNull Enums.SkillType type, @NotNull String category,
                          @NotNull String icon, @NotNull String displayName, @NotNull String description,
                          @Nullable List<Modifier> modifiers, @Nullable Map<String, ValueSet> customValues, @Nullable SkillBehavior behavior,
                          @Nullable SkillConfiguration config) {

    public static Builder generic(@NotNull String skillId, @NotNull String category, @NotNull List<Integer> prices){
        return new Builder(Tyzsskills.MODID, skillId, category, prices, Enums.SkillType.GENERIC);
    }

    public static Builder custom(@NotNull String skillId, @NotNull String category, @NotNull List<Integer> prices){
        return new Builder(Tyzsskills.MODID, skillId, category, prices, Enums.SkillType.CUSTOM);
    }

    public static Builder immutable(@NotNull String skillId, @NotNull String category, @NotNull List<Integer> prices){
        return new Builder(Tyzsskills.MODID, skillId, category, prices, Enums.SkillType.IMMUTABLE);
    }

    public static Builder of(@NotNull String modId, @NotNull String skillId, @NotNull Enums.SkillType type, @NotNull String category, @NotNull List<Integer> prices){
        return new Builder(modId, skillId, category, prices, type);
    }

    public static class Builder{

        public Builder(@NotNull String modId, @NotNull String skillId, @NotNull String category, @NotNull List<Integer> prices, @NotNull Enums.SkillType type){
            this.skillId = skillId.toLowerCase();
            this.category = category;
            this.prices = prices;
            this.type = type;

            this.icon = modId + ":textures/gui/skills/" + this.skillId + ".png";
            this.displayName = "skill." + modId + "." + this.skillId + ".displayName";
            this.description = "skill." + modId + "." + this.skillId + ".description";
        }

        //Mandatories
        private final String skillId;
        private final String category;
        private final List<Integer> prices;
        private final Enums.SkillType type;

        //Defaults
        private boolean active = true;
        private String icon;
        private String displayName;
        private String description;
        private List<Modifier> modifiers;
        private Map<String, ValueSet> customValues;
        private SkillBehavior behavior;
        private SkillConfiguration config;

        //Fluents
        public Builder active(boolean isActive){active = isActive; return this;}
        public Builder withIcon(@NotNull String iconPath){icon = iconPath; return this;}
        public Builder withDisplayName(@NotNull String displayNameKey){displayName = displayNameKey; return this;}
        public Builder withDescription(@NotNull String descriptionKey){description = descriptionKey; return this;}
        public Builder withBehavior(@NotNull SkillBehavior behavior){this.behavior = behavior; return this;}
        public Builder withConfig(@NotNull SkillConfiguration config){this.config = config; return this;}

        public Builder withModifiers(@NotNull List<Modifier> modifiers){this.modifiers = new ArrayList<>(modifiers); return this;}
        public Builder addModifier(@NotNull Modifier modifier){
            if(modifiers == null) modifiers = new ArrayList<>();
            modifiers.add(modifier);
            return this;
        }

        public Builder withCustomValues(@NotNull Map<String, ValueSet> customValues){this.customValues = new HashMap<>(customValues); return this;}
        public Builder addCustomValue(@NotNull String key, @NotNull ValueSet values){
            if(customValues == null) customValues = new HashMap<>();
            customValues.put(key, values);
            return this;
        }

        public @NotNull SkillPrefab build(){
            return new SkillPrefab(active, skillId, prices, type, category, icon, displayName, description,
                    modifiers, customValues, behavior, config);
        }
    }


}




