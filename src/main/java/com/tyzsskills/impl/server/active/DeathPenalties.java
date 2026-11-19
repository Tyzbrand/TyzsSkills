package com.tyzsskills.impl.server.active;

import com.tyzsskills.Settings;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.xp.XpManager;
import net.minecraft.server.level.ServerPlayer;

public class DeathPenalties {

    public static void applyLvlPenalty(ServerPlayer player){
        int lvlToDeduce = Settings.Stock.DEATH_LEVEL_LOSS.get();
        if(lvlToDeduce <= 0) return;

        LevelManager.tryRemoveLevel(player, lvlToDeduce);
    }

    public static void applyXpPenalty(ServerPlayer player){
        float xpPercentage = Settings.Stock.DEATH_XP_LOSS.get();
        if(xpPercentage <= 0f) return;

        float xpToRemove = (float)(XpManager.getXP(player) * xpPercentage) / 100f;
        XpManager.tryRemoveXp(player, xpToRemove);
    }

    public static void applySpPenalty(ServerPlayer player){
        float spPercentage = Settings.Stock.DEATH_SP_LOSS.get();
        if(spPercentage <= 0f) return;

        int spToRemove = (int)Math.floor((SpManager.getSP(player) * spPercentage) / 100f);
        SpManager.tryRemoveSp(player, spToRemove);
    }

    public static void applySkillPenalty(ServerPlayer player){
        float lossProbability = Settings.Stock.DEATH_SKILL_LOSS.get() / 100f;
        if(lossProbability <= 0f) return;

        for(var skill : SkillManager.getServerSkills()){
            if(SkillManager.getSkillLevel(player, skill) < 1) continue;

            if (player.getRandom().nextFloat() < lossProbability){
                SkillManager.tryRemoveSkillLevel(player, skill, 1);
            }
        }
    }

}
