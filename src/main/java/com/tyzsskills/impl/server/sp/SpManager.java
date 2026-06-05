package com.tyzsskills.impl.server.sp;

import com.tyzsskills.Config;
import com.tyzsskills.api.events.SkillPointChangeEvent;
import com.tyzsskills.impl.server.attachments.LimitsTracker;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SpManager {

        //CORE
        private static void setSpInternal(ServerPlayer player, int amount, boolean applyLimits){
            int oldAmount = getSP(player);


            var event = new SkillPointChangeEvent(player, oldAmount, amount);
            NeoForge.EVENT_BUS.post(event);

            if(event.isCanceled()) return;
            int targetedAmount = event.getNewAmount();
            if(targetedAmount == oldAmount) return;

            int gain = targetedAmount - oldAmount;

            if(gain > 0 && applyLimits){
                var allowedGain = checkLimit(player, gain);
                if(allowedGain <= 0) return;
                targetedAmount = oldAmount + allowedGain;
                player.getData(LimitsTracker.DATA).incrSp(allowedGain);
            }

            var playerData = player.getData(PlayerData.DATA);
            playerData.setSP(targetedAmount);
            updateClient(player);
        }

        //PUBLIC
        public static void setSP(ServerPlayer player, int amount, boolean applyLimits){
                setSpInternal(player, amount, applyLimits);
        }
        public static void setSP(ServerPlayer player, int amount){
            setSP(player, amount, false);
        }

        public static void addSP(ServerPlayer player, int amount, boolean applyLimits){
            if(amount <= 0) return;
            setSpInternal(player, amount + getSP(player), applyLimits);
        }
        public static void addSP(ServerPlayer player, int amount){
            addSP(player, amount, true);
        }

        public static void removeSP(ServerPlayer player, int amount){
            if(amount <= 0) return;
            var result = Math.max(0, getSP(player) - amount);
            setSpInternal(player, result, false);
        }

        //Util
        private static void updateClient(ServerPlayer player){
            PacketDistributor.sendToPlayer(player, new UpdatePayloads.SpPayload(getSP(player)));
        }

        private static int checkLimit(ServerPlayer player, int amount){
            int currentSP = getSP(player);
            int finalAmount = amount;

            int possessionLimit = Config.MAX_SP.get();
            if(possessionLimit != -1){
                var remaining = possessionLimit - currentSP;
                finalAmount = Math.min(finalAmount, Math.max(0, remaining));
            }

            int gainLimit = Config.MAX_SP_GAIN.get();
            if(gainLimit != -1){
                var data = player.getData(LimitsTracker.DATA);
                var remaining = gainLimit - data.getSpLifetime();
                finalAmount = Math.min(finalAmount, Math.max(0, remaining));
            }

            return finalAmount;
        }


        //Getters
        public static int getSP(ServerPlayer player){
            return player.getData(PlayerData.DATA).getSP();
        }

    }


