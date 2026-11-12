package com.tyzsskills.impl.server.debug;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.events.PlayerResetEvent;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.attachments.ExplorationProgression;
import com.tyzsskills.impl.server.attachments.StatsTracker;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.spells.SpellManager;
import com.tyzsskills.impl.server.xp.XpManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class Reset {
    public static void reset(ServerPlayer player, Enums.ResetType type){
            switch (type){
                case ALL -> {
                    resetSkills(player);
                    resetSpells(player);
                    resetMetaData(player);
                    resetStats(player);
                }
                case METADATA -> resetMetaData(player);
                case STATS -> resetStats(player);
                case SKILLS -> resetSkills(player);
                case SPELLS -> resetSpells(player);
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

    private static void resetStats(ServerPlayer player){player.getData(StatsTracker.DATA).resetStats();}
    private static void resetSkills(ServerPlayer player){SkillManager.resetSkillLevels(player);}
    private static void resetSpells(ServerPlayer player){SpellManager.resetSpells(player);}

}
