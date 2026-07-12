package com.tyzsskills.api.model;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.skills.SkillGraph;
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

}
