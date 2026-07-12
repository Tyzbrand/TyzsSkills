package com.tyzsskills.impl.server.active;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.events.PlayerResetEvent;
import com.tyzsskills.api.events.SkillReloadEvent;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.attachments.ExplorationProgression;
import com.tyzsskills.impl.server.attachments.StatsTracker;
import com.tyzsskills.impl.server.effects.GenericEffects;
import com.tyzsskills.impl.server.skills.Skill;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.skills.SkillRules;
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
import java.util.List;

@ApiStatus.Internal
public class DebugManager {

    public static final class RELOAD{
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

            for(var player : server.getPlayerList().getPlayers()) {
                DebugManager.SANITIZER.repairInconsistencies(player);

                PacketDistributor.sendToPlayer(player, UpdatePayloads.getInitPayload(player));

                if (player.hasPermissions(2) && ErrorManager.hasErrors()) {
                    ErrorManager.printErrors(player);
                }
            }

            NeoForge.EVENT_BUS.post(new SkillReloadEvent());
        }
    }

    public static final class SANITIZER{
        private static final int STACK_PROTECTION = 20;
        public static void repairInconsistencies(@NotNull ServerPlayer player){
            LevelManager.checkForLevelUp(player, XpManager.getXP(player));
            GenericEffects.restoreEffects(player);

            var skillIdsSnapshot = List.copyOf(SkillManager.getPlayerSkillLevels(player).keySet());

            var rollCount = 0;
            var hasChanged = true;

            while(hasChanged && rollCount < STACK_PROTECTION){
                hasChanged = false;
                rollCount++;

                var playerContext = SkillManager.getPlayerContext(player);

                for(var id :  skillIdsSnapshot){
                    var skill = SkillManager.getSkill(id);
                    if(skill == null) continue;

                    int lvl = SkillManager.getPlayerSkillLevel(player, id);
                    if(lvl <= 0) continue;

                    if(skill.getRequiredLevel() > playerContext.playerLevel()){
                        cleanRefund(0, lvl, player, skill);
                        hasChanged = true;
                        continue;
                    }

                    var skillContext = SkillManager.getSkillContext(player, id);

                    var incompatibilities = SkillRules.getActiveIncompatibilities(skillContext, playerContext);
                    if(!incompatibilities.isEmpty()){
                        cleanRefund(0, lvl, player, skill);

                        for(var conflict : incompatibilities) {
                            var conflictSkill = SkillManager.getSkill(conflict);
                            if(conflictSkill != null)
                                cleanRefund(0, SkillManager.getPlayerSkillLevel(player, conflict), player, conflictSkill);
                        }
                        hasChanged = true;
                        continue;
                    }

                    var prerequisites = SkillRules.getActivePrerequisites(skillContext, playerContext);
                    if(!prerequisites.isEmpty()){
                        cleanRefund(0, lvl, player, skill);
                        hasChanged = true;
                        continue;
                    }

                    if(skill.getMaximumLevel() < lvl) {
                        cleanRefund(skill.getMaximumLevel(), lvl, player, skill);
                        hasChanged = true;
                        continue;
                    }
                }

                if(rollCount == STACK_PROTECTION && hasChanged)
                    ErrorManager.printDynamicError(player, Component.literal("Infinite loop detected in Skill rules. Check for circular dependencies or contradictory incompatibilities."));

            }
        }

        private static void cleanRefund(int targetLvl, int currentLvl, @NotNull ServerPlayer player, @NotNull Skill skill){
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
    }

    public static final class RESET{
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
            for (var skillId : SkillManager.getPlayerSkillLevels(player).keySet())
                SkillManager.setSkillLevel(player, skillId, 0);
        }
    }
}
