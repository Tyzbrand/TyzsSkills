package com.tyzsskills.impl.server.active;

import com.tyzsskills.Config;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.xp.XpManager;
import net.minecraft.server.level.ServerPlayer;

public class DeathPenalties {

    public static void applyLvlPenalty(ServerPlayer player){
        if(!isPenaltyEnabled()) return;

        int lvlToDeduce = Config.LVL_LOSS.get();
        if(lvlToDeduce <= 0) return;

        LevelManager.tryRemoveLevel(player, lvlToDeduce);
    }

    public static void applyXpPenalty(ServerPlayer player){
        if(!isPenaltyEnabled()) return;

        double xpPercentage = Config.XP_LOSS.get();
        if(xpPercentage <= 0) return;

        float xpToRemove = (float)(XpManager.getXP(player) * xpPercentage) / 100f;
        XpManager.tryRemoveXp(player, xpToRemove);
    }

    public static void applySpPenalty(ServerPlayer player){
        if(!isPenaltyEnabled()) return;

        double spPercentage = Config.SP_LOSS.get();
        if(spPercentage <= 0) return;

        int spToRemove = (int)Math.floor((SpManager.getSP(player) * spPercentage) / 100f);
        SpManager.tryRemoveSp(player, spToRemove);
    }

    public static void applySkillPenalty(ServerPlayer player){
        if(!isPenaltyEnabled()) return;

        float lossProbability = (float)Config.SKILL_LOSS.getAsDouble() / 100f;

        var manager = SkillManager.get();
        for(var skill : manager.getAllSkills()){
            if(manager.getPlayerSkillLevel(player, skill.getID()) < 1) continue;

            if (player.getRandom().nextFloat() < lossProbability){
                manager.removeSkillLevel(player, skill.getID(), 1);
            }
        }
    }

    private static boolean isPenaltyEnabled(){return Config.DEATH_PENALTIES.get();}
}
