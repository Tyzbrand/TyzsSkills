package com.tyzsskills.impl.server.sp;

import com.tyzsskills.Config;
import com.tyzsskills.api.TyzsSkillsAPI;
import com.tyzsskills.api.events.SkillPointChangeEvent;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.payloads.SpUpdatePayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SpManager {

        public static void setSp(ServerPlayer player, int amount){
            int oldAmount = getSP(player);


            var event = new SkillPointChangeEvent(player, oldAmount, amount);
            NeoForge.EVENT_BUS.post(event);

            if(event.isCanceled()) return;
            int finalAmount = event.getNewAmount();

            if(finalAmount == oldAmount) return;

            var playerData = player.getData(PlayerData.DATA);
            playerData.setSP(finalAmount);
            updateClient(player);
        }

        public static void addSP(ServerPlayer player, int amount){
            if(amount <= 0) return;

            int current = getSP(player);
            int limit = Config.MAX_SP.get();
            int finalAmount = amount;

            if(limit != -1){
                var remaining = limit - current;
                finalAmount = Math.min(amount, Math.max(0, remaining));
            }
            setSp(player, getSP(player) + finalAmount);
        }

        public static void removeSP(ServerPlayer player, int amount){
            if(amount <= 0) return;
            var result = Math.max(0, getSP(player) - amount);
            setSp(player, result);
        }

        //Util
        private static void updateClient(ServerPlayer player){
            PacketDistributor.sendToPlayer(player, new SpUpdatePayload(getSP(player)));
        }


        //Getters
        public static int getSP(ServerPlayer player){
            return player.getData(PlayerData.DATA).getSP();
        }

    }


