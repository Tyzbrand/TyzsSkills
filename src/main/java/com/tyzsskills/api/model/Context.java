package com.tyzsskills.api.model;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.records.SpellProperty;
import com.tyzsskills.impl.server.skills.SkillGraph;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;

public class Context {
    public static class Player{

        private net.minecraft.world.entity.player.Player player;
        public @NotNull net.minecraft.world.entity.player.Player player(){return this.player;}

        private int playerLevel;
        public int playerLevel(){return this.playerLevel;}

        private int playerSp;
        public int playerSp(){return this.playerSp;}

        private Map<String, Integer> ownedSkillLevels;
        public @NotNull @UnmodifiableView Map<String, Integer> ownedSkillLevels(){return this.ownedSkillLevels;}

        public Player updateContext(@NotNull net.minecraft.world.entity.player.Player player, int playerLevel, int playerSp, @NotNull Map<String, Integer> ownedSkillLevels){
            this.player = player;
            this.playerLevel = playerLevel;
            this.playerSp = playerSp;

            this.ownedSkillLevels = ownedSkillLevels;

            return this;
        }
    }

    public static class Skill{
        private ISkill skill;
        public @NotNull ISkill skill(){return this.skill;}

        private int skillLevel;
        public int skillLevel(){return this.skillLevel;}

        private SkillGraph graph;
        public @NotNull SkillGraph graph(){return this.graph;}

        public Skill updateContext(@NotNull ISkill skill, int skillLevel, @NotNull SkillGraph graph){
            this.skill = skill;
            this.skillLevel = skillLevel;
            this.graph = graph;

            return this;
        }
    }

    public static class Spell{
        private ISpell spell;
        public @NotNull ISpell spell(){return this.spell;}

        private boolean isSpellPurchased;
        public boolean isSpellPurchased(){return this.isSpellPurchased;}

        public Spell updateContext(@NotNull ISpell spell, boolean purchased){
            this.spell= spell;
            this.isSpellPurchased = purchased;

            return this;
        }
    }

    public static class Property{
        private SpellProperty property;
        public @NotNull SpellProperty property(){return this.property;}

        private int propertyLevel;
        public int propertyLevel(){return this.propertyLevel;}

        public Property updateContext(@NotNull SpellProperty property, int propertyLevel){
            this.property = property;
            this.propertyLevel = propertyLevel;

            return this;
        }

    }

}
