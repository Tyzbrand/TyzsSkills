package com.tyzsskills.api.records;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.model.SpellBehavior;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record SpellPrefab(boolean active, @NotNull String id, int price, @NotNull String icon, @NotNull String displayName,
                          @NotNull String description, @NotNull List<SpellProperty> properties, @NotNull SpellBehavior behavior) {

    public static Builder builder(@NotNull String spellId, int price){
        return new Builder(Tyzsskills.MODID, spellId, price);
    }

    public static Builder of(@NotNull String modId, @NotNull String spellId, int price){
        return new Builder(modId, spellId, price);
    }


    public static class Builder{
        public Builder(@NotNull String modId, @NotNull String spellId, int price){
            this.spellId = spellId.toLowerCase();
            this.price = price;

            this.icon = modId + ":textures/gui/spells/" + this.spellId + ".png";
            this.displayName = "spell." + modId + "." + this.spellId + ".displayName";
            this.description = "spell." + modId + "." + this.spellId + ".description";
        }

        //Mandatories
        private final String spellId;
        private final int price;


        //Defaults
        private boolean active = true;
        private String icon;
        private String displayName;
        private String description;
        private List<SpellProperty> properties;
        private SpellBehavior behavior;

        //Fluents
        public Builder active(boolean isActive){active = isActive; return this;}
        public Builder withIcon(@NotNull String iconPath){icon = iconPath; return this;}
        public Builder withDisplayName(@NotNull String displayNameKey){displayName = displayNameKey; return this;}
        public Builder withDescription(@NotNull String descriptionKey){description = descriptionKey; return this;}
        public Builder withBehavior(@NotNull SpellBehavior behavior){this.behavior = behavior; return this;}

        public Builder withProperties(@NotNull List<SpellProperty> properties){this.properties = new ArrayList<>(properties); return this;}
        public Builder addProperty(@NotNull SpellProperty property){
            if(properties == null) properties = new ArrayList<>();
            properties.add(property);
            return this;
        }

        public @NotNull SpellPrefab build(){
            return new SpellPrefab(active, spellId, price,
                    icon, displayName, description, properties, behavior);
        }
    }
}
