package com.tyzsskills.server.active;

import net.minecraft.server.level.ServerPlayer;

public class SpManager {

        private static final String dataKey = "SKILL_POINT";

        public static void SetSP(ServerPlayer player, int amount){
            if(amount < 0) return;

            var playerData = player.getPersistentData();
            playerData.putInt(dataKey, amount);
        }

        public static void AddSP(ServerPlayer player, int amount){
            if(amount <= 0) return;
            SetSP(player, GetSP(player) + amount);
        }

        public static void RemoveSP(ServerPlayer player, int amount){
            if(amount <= 0) return;
            var result = Math.max(0, GetSP(player) - amount);
            SetSP(player, result);
        }

        public static void RestorePlayerSPData(ServerPlayer oldPlayer, ServerPlayer newPlayer){

            var oldData = oldPlayer.getPersistentData();
            var newData = newPlayer.getPersistentData();

            if(oldData.contains(dataKey)) newData.putInt(dataKey, oldData.getInt(dataKey));
            else SetSP(newPlayer, 1);
        }

        public static void EnsureDefaultSP(ServerPlayer player){
            if(!player.getPersistentData().contains(dataKey)) SetSP(player,0);
        }

        //Getters
        public static int GetSP(ServerPlayer player){
            return player.getPersistentData().getInt(dataKey);
        }

    }


