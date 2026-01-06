package com.tyzsskills.server.active;

import com.tyzsskills.server.payloads.SpUpdatePayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class SpManager {

        private static final String dataKey = "SKILL_POINT";

        public static void SetSP(ServerPlayer player, int amount){
            if(amount < 0) return;

            var playerData = player.getPersistentData();
            playerData.putInt(dataKey, amount);

            UpdateClient(player);
        }

        public static void AddSP(ServerPlayer player, int amount){
            if(amount <= 0) return;
            SetSP(player, GetSP(player) + amount);

            UpdateClient(player);
        }

        public static void RemoveSP(ServerPlayer player, int amount){
            if(amount <= 0) return;
            var result = Math.max(0, GetSP(player) - amount);
            SetSP(player, result);

            UpdateClient(player);
        }

        public static void RestorePlayerSPData(ServerPlayer oldPlayer, ServerPlayer newPlayer){

            var oldData = oldPlayer.getPersistentData();
            var newData = newPlayer.getPersistentData();

            if(oldData.contains(dataKey)) {
                newData.putInt(dataKey, oldData.getInt(dataKey));
                UpdateClient(newPlayer);
            }
            else SetSP(newPlayer, 0);
        }

        public static void EnsureDefaultSP(ServerPlayer player){
            if(!player.getPersistentData().contains(dataKey)) SetSP(player,0);
            else UpdateClient(player);
        }

        //Utilitaire
        private static void UpdateClient(ServerPlayer player){
            PacketDistributor.sendToPlayer(player, new SpUpdatePayload(GetSP(player)));
        }

        //Getters
        public static int GetSP(ServerPlayer player){
            return player.getPersistentData().getInt(dataKey);
        }

    }


