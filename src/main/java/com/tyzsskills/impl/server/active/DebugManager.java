package com.tyzsskills.impl.server.active;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.events.PlayerResetEvent;
import com.tyzsskills.api.events.SkillReloadEvent;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.attachments.ExplorationProgression;
import com.tyzsskills.impl.server.attachments.StatsTracker;
import com.tyzsskills.impl.server.effects.GenericEffects;
import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.xp.XpGainRegistry;
import com.tyzsskills.impl.server.xp.XpManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@ApiStatus.Internal
public class DebugManager {

    //------------RELOAD------------
    public static void reload(MinecraftServer server) throws IOException {
        ErrorManager.clearErrors();

        SkillManager.clearSkills();
        XpGainRegistry.clearAll();
        LevelManager.clearPool();

        try {
            FileManager.readSkills(server);
            FileManager.readData(server);
        } catch (IOException e) {
            Tyzsskills.LOGGER.error("CRITICAL ERROR: Unable to load files during mod reload", e);
            throw new RuntimeException(e);
        }

        for(var player : server.getPlayerList().getPlayers()){checkForInconsistencies(player);}

        for(var player : server.getPlayerList().getPlayers()){

            PacketDistributor.sendToPlayer(player, UpdatePayloads.getInitPayload(player));

            if (player.hasPermissions(2) && ErrorManager.hasErrors()) {
                ErrorManager.printErrors(player);
            }
        }

        NeoForge.EVENT_BUS.post(new SkillReloadEvent());
    }

    //------------CHECKS------------
    public static void checkForInconsistencies(@NotNull ServerPlayer player){
        LevelManager.checkForLevelUp(player, XpManager.getXP(player));
        GenericEffects.restoreEffects(player);

        for (var skillId : SkillManager.getPlayerOwnedSkillIds(player)){

            var skill = SkillManager.getSkill(skillId);
            if(skill == null) continue;

            var lvl = SkillManager.getPlayerSkillLevel(player, skillId);

            var incompatibilities = skill.getIncompatibilities(SkillManager.getPlayerOwnedSkillIds(player));
            var prerequisites = skill.getPrerequisites(SkillManager.getPlayerOwnedSkillIds(player));

            if(!skill.meetsLevelRequirement(LevelManager.getLevel(player))){
                cleanRefund(0, lvl, player, skill);
                continue;
            }
            else if(!incompatibilities.isEmpty()){
                cleanRefund(0, lvl, player, skill);

                for(var id : incompatibilities){
                    var conflict = SkillManager.getSkill(id);
                    if(conflict != null )cleanRefund(0, SkillManager.getPlayerSkillLevel(player, id), player, conflict);
                }
                continue;
            }
            else if (!prerequisites.isEmpty()){
                cleanRefund(0, lvl, player, skill);
                continue;
            }

            if(!SkillManager.isSkillLoaded(skill.getID())) continue;

            var maxLvl = skill.getMaximumLevel();
            if(lvl <= maxLvl) continue;
            cleanRefund(maxLvl, lvl, player, skill);
        }
    }

    private static void cleanRefund(int targetLvl, int currentLvl, ServerPlayer player, Skill skill){
        if (currentLvl == targetLvl) return;

        var spToRefund = 0;
        for(int i = currentLvl - 1; i >= targetLvl; i--){
            if(i >= skill.getPrices().size()) continue;
            spToRefund += skill.getPrices().get(i);
        }

        SkillManager.setSkillLevel(player, skill.getID(), targetLvl);

        if(spToRefund <= 0) return;
        SpManager.tryAddSp(player, spToRefund);
        player.sendSystemMessage(Component.literal("Skill §9[" + skill.getID() + "] §rrules changed. §6" + spToRefund + " §rSP refunded."));
    }

    //------------RESET------------

    public static void reset(ServerPlayer player, Enums.ResetType type){
        switch (type){
            case ALL -> {
                resetSkills(player);
                resetMetaData(player);
                resetStats(player);
            }
            case METADATA -> resetMetaData(player);
            case STATS -> resetStats(player);
            case SKILLS -> resetSkills(player);
        }
        NeoForge.EVENT_BUS.post(new PlayerResetEvent(player));
        PacketDistributor.sendToPlayer(player, UpdatePayloads.getInitPayload(player));
    }

    private static void resetMetaData(ServerPlayer player){
        LevelManager.resetLevel(player);
        SpManager.resetSp(player);
        XpManager.resetXp(player);
        player.getData(ExplorationProgression.DATA).resetPlayerData();
    }

    private static void resetStats(ServerPlayer player){
        player.getData(StatsTracker.DATA).resetStats();
    }

    private static void resetSkills(ServerPlayer player){
        for (var skill : SkillManager.getAllSkills()){
            if(SkillManager.getPlayerSkillLevel(player, skill.getID().toLowerCase()) > 0){
                SkillManager.setSkillLevel(player, skill.getID().toLowerCase(), 0);
            }
        }
    }
}
