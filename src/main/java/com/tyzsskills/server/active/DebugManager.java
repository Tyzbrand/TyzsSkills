package com.tyzsskills.server.active;

import com.tyzsskills.server.skills.SkillManager;
import com.tyzsskills.server.xp.XpManager;
import com.tyzsskills.server.xp.xpEvents.XpBlock;
import com.tyzsskills.server.xp.xpEvents.XpEntity;
import com.tyzsskills.server.xp.xpEvents.XpFood;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.io.IOException;

public class DebugManager {
    public static void DebugReload(MinecraftServer server) throws IOException {
        SkillManager.Get().ClearSkills();
        XpBlock.ClearValues();
        XpEntity.ClearValues();
        XpFood.ClearValues();
        XpManager.ClearPool();

        FileManager.Get().ReadJsons(server);
        FileManager.Get().ReadLevelPool(server);
        FileManager.Get().ReadXpValues(server);
        FileManager.Get().ReadCustomSkills(server);

        var manager = SkillManager.Get();

        for(var player : server.getPlayerList().getPlayers()){
            AutoSyncClient.SyncSkillList(player);
            AutoSyncClient.SyncConfig(player);
            AutoSyncClient.SyncMainData(player);
            XpManager.LevelUpCheck(player);
        }

        for(var player : server.getPlayerList().getPlayers()){
            for (var skill : manager.GetAllSkills()){
                if(manager.GetPlayerSkillLevel(player, skill.GetID().toLowerCase()) > skill.GetMaximumLevel()){
                    manager.SetSkillLevel(player, skill.GetID().toLowerCase(), skill.GetMaximumLevel());
                }
            }
        }
    }

    public static void DebugResetData(ServerPlayer player){
        //IL FAUT PAS OUBLIER DENVOYER UN PACKET AU CLIENT POUR QUE LUI AUSSI RESET DES CHOSES
        XpManager.SetXP(player, 0f);
        LevelManager.SetLevel(player, 1);
        SpManager.SetSP(player, 0);
        PowerManager.SetPower(player, 0);

        var manager = SkillManager.Get();

        for (var skill : manager.GetAllSkills()){
            if(manager.GetPlayerSkillLevel(player, skill.GetID().toLowerCase()) > 0){
                manager.SetSkillLevel(player, skill.GetID().toLowerCase(), 0);
            }
        }
    }
}
