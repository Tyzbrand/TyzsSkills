package com.tyzsskills.impl.server.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyzsskills.Config;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.power.PowerManager;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.attachments.StatsTracker;
import com.tyzsskills.impl.server.effects.GenericEffects;
import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.impl.server.model.Trait;
import com.tyzsskills.impl.server.payloads.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SkillManager {

    private static final SkillManager INSTANCE = new SkillManager();
    public static SkillManager Get() {return INSTANCE;}

    private final Map<String, Skill> skillCollection = new HashMap<>();




    public void registerSKill(Skill skill)
    {
        var behaviour = SkillBehaviourRegistry.GetBehaviour(skill.GetID());
        if(behaviour != null){skill.SetBehaviour(behaviour);}

        if(!skillCollection.containsKey(skill.GetID())) skillCollection.put(skill.GetID(), skill);
    }

    public void clearSkills(){
        skillCollection.clear();
    }


    public boolean buySkill(ServerPlayer player, String id)
    {
        var skill = getSkill(id);
        if(player == null || skill == null) return false;

        if(skill instanceof Trait){
            if(!Config.TRAIT_SYSTEM.get()) return false;
            if(LevelManager.getLevel(player) < Config.TRAIT_UNLOCK_LEVEL.get()) return false;
        }

        var data = player.getData(PlayerData.DATA);

        int currentLvl = data.getSkillLevel(id);
        if(currentLvl >= skill.GetMaximumLevel()) return false;

        if(skill instanceof Trait trait){
            var attr = player.getAttribute(AttributeRegistry.TRAIT_POWER);
            if(attr == null) return false;

            int freeSpace = (int)attr.getValue() - PowerManager.GetPower(player);
            if(trait.getPowerWeight() > freeSpace)return false;

            var prices = skill.GetPrices();
            if(currentLvl >= prices.size()) return false;
            int price = prices.get(currentLvl);


            if(SpManager.getSP(player) >= price){
                SpManager.removeSP(player, price);
                data.setSkillLevel(id, currentLvl+1);

                PacketDistributor.sendToPlayer(player, new SkillLevelSyncPayload(skill.GetID(), currentLvl+1));
                PacketDistributor.sendToPlayer(player, new StatsSpSpentPayload(price));

                player.getData(StatsTracker.DATA).addSpSpent(price);

                PowerManager.AddPower(player, trait.getPowerWeight());
                return true;
            }
        }
        else{
            var prices = skill.GetPrices();
            if(currentLvl >= prices.size()) return false;
            int price = prices.get(currentLvl);


            if(SpManager.getSP(player) >= price){
                SpManager.removeSP(player, price);
                data.setSkillLevel(id, currentLvl+1);

                PacketDistributor.sendToPlayer(player, new SkillLevelSyncPayload(skill.GetID(), currentLvl+1));
                PacketDistributor.sendToPlayer(player, new StatsSpSpentPayload(price));

                player.getData(StatsTracker.DATA).addSpSpent(price);

                if(skill.GetType() == Skill.SkillType.GENERIC) GenericEffects.ApplyEffect(skill, player);
                return true;
            }
        }
        return false;

    }

    public boolean refundSkill(ServerPlayer player, String id)
    {
        var skill = getSkill(id);
        if(player == null || skill == null || !Config.REFUND_SYSTEM.get()) return false;

        if(skill instanceof Trait){
            if(!Config.TRAIT_SYSTEM.get()) return false;
            if(LevelManager.getLevel(player) < Config.TRAIT_UNLOCK_LEVEL.get()) return false;
        }


        var data = player.getData(PlayerData.DATA);

        int currentLvl = data.getSkillLevel(id);
        if(currentLvl <= 0 || currentLvl > skill.GetMaximumLevel()) return false;

        if(skill.GetType() == Skill.SkillType.GENERIC){

            ResourceLocation attributeID = ResourceLocation.tryParse(skill.GetModifier());
            if(attributeID == null) return false;
            Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(attributeID);

            if(attribute == AttributeRegistry.TRAIT_POWER.get()){
                var att = player.getAttribute(AttributeRegistry.TRAIT_POWER);
                if(att == null) return false;

                int max = (int)att.getValue();
                int current = PowerManager.GetPower(player);

                int index = Math.min(currentLvl - 1, skill.GetValues().size() - 1);
                float currentValue = skill.GetValues().get(index);

                float powerLoss = currentValue;

                if (currentLvl > 1) {
                    int prevIndex = Math.min(currentLvl - 2, skill.GetValues().size() - 1);
                    float prevValue = skill.GetValues().get(prevIndex);
                    powerLoss = currentValue - prevValue;
                }
                if(current > (max - (int)powerLoss)) return false;
            }
        }

        var prices = skill.GetPrices();
        if (currentLvl > prices.size()) return false;
        int initialPrice = prices.get(currentLvl - 1);
        int finalPrice = Math.max(1, (int)(initialPrice * (Config.REFUND_PERCENTAGE.get() / 100f)));

        SpManager.addSP(player, finalPrice);

        int newLvl = currentLvl - 1;

        if(newLvl > 0) data.setSkillLevel(id, currentLvl - 1);

        if(skill instanceof Trait trait) PowerManager.RemovePower(player, trait.getPowerWeight());

        PacketDistributor.sendToPlayer(player, new SkillLevelSyncPayload(skill.GetID(), currentLvl-1));
        PacketDistributor.sendToPlayer(player, new StatsSpEarnedPayload(finalPrice));

        player.getData(StatsTracker.DATA).addSpEarned(finalPrice);

        if(skill.GetType() == Skill.SkillType.GENERIC){
            if(currentLvl - 1 <= 0) GenericEffects.RemoveEffect(skill, player);
            else GenericEffects.ApplyEffect(skill, player);
        }

        return true;
    }


    public void bookmarkSkill(ServerPlayer player, String id){
        if(player == null || getSkill(id.toLowerCase()) == null) return;

        var data = player.getData(PlayerData.DATA);

        var isCurrentlyBookmarked = data.isBookmarked(id);
        var newValue = !isCurrentlyBookmarked;

        data.triggerBookmark(id);
        PacketDistributor.sendToPlayer(player, new SkillBookmarksPayload(id.toLowerCase(), newValue));
    }

    public void setSkillLevel(ServerPlayer player, String id, int lvl){
        if(player == null || lvl < 0 || lvl > 10) return;

        var skill = getSkill(id.toLowerCase());
        if(skill == null) return;

        if(skill instanceof Trait){
            if(!Config.TRAIT_SYSTEM.get()) return;
            if(LevelManager.getLevel(player) < Config.TRAIT_UNLOCK_LEVEL.get()) return;
        }


        var data = player.getData(PlayerData.DATA);

        lvl = Math.max(0, Math.min(lvl, skill.GetMaximumLevel()));
        int oldLvl = getPlayerSkillLevel(player, id);

        data.setSkillLevel(id, lvl);

        if(skill instanceof Trait trait){
            if(oldLvl == 0 && lvl > 0) {
                PowerManager.AddPower(player, trait.getPowerWeight());
            }
            else if(oldLvl > 0 && lvl == 0) {
                PowerManager.RemovePower(player, trait.getPowerWeight());
            }
        }


        PacketDistributor.sendToPlayer(player, new SkillLevelSyncPayload(id, lvl));


        if(skill.GetType() == Skill.SkillType.GENERIC){
            if(lvl > 0) GenericEffects.ApplyEffect(skill, player);
            else GenericEffects.RemoveEffect(skill, player);
        }
    }

    public void addSKillLevel(ServerPlayer player, String id, int amount){
        int current = getPlayerSkillLevel(player, id);
        setSkillLevel(player, id, current + amount);
    }

    public void removeSkillLevel(ServerPlayer player, String id, int amount){
        int current = getPlayerSkillLevel(player, id);
        setSkillLevel(player, id, current - amount);
    }




    //getters
    public Skill getSkill(String id){return skillCollection.getOrDefault(id.toLowerCase(), null);}
    public List<Skill> getAllSkills() {return new ArrayList<>(skillCollection.values());}
    public List<String> getAllSkillIDs(){return new ArrayList<>(skillCollection.keySet());}
    public int getPlayerSkillLevel(ServerPlayer player, String id) {return player.getData(PlayerData.DATA).getSkillLevel(id);}
    public boolean isSkillLoaded(String id){return skillCollection.containsKey(id);}

    public boolean isSkillBookmarked(ServerPlayer player,String id) {return player.getData(PlayerData.DATA).isBookmarked(id);}
    public List<String> getAllBookmarkIDs(ServerPlayer player){return player.getData(PlayerData.DATA).getBookmarks();}
}