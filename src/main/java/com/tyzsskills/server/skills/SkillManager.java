package com.tyzsskills.server.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyzsskills.Config;
import com.tyzsskills.server.active.AttributeRegistry;
import com.tyzsskills.server.active.PowerManager;
import com.tyzsskills.server.active.SpManager;
import com.tyzsskills.server.effects.GenericEffects;
import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.Trait;
import com.tyzsskills.server.payloads.SkillBookmarksPayload;
import com.tyzsskills.server.payloads.SkillLevelSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class SkillManager {

    private static final SkillManager instance = new SkillManager();
    public static SkillManager Get() {return instance;}

    public static final String BOOKMARK_SIGNATURE = "_bookmark";
    public static final String SKILL_LEVEL_SIGNATURE = "_lvl";

    private final Map<String, Skill> skillCollection = new HashMap<>();




    public void RegisterSKill(Skill skill)
    {
        var behaviour = SkillBehaviourRegistry.GetBehaviour(skill.GetID());
        if(behaviour != null){skill.SetBehaviour(behaviour);}

        if(!skillCollection.containsKey(skill.GetID())) skillCollection.put(skill.GetID(), skill);
    }

    public void ClearSkills(){
        skillCollection.clear();
    }


    public void BuySkill(ServerPlayer player, String id)
    {
        var skill = GetSkill(id);
        if(player == null || skill == null) return;

        var data = player.getPersistentData();
        var key = skill.GetID() + SKILL_LEVEL_SIGNATURE;

        int currentLvl = data.getInt(key);
        if(currentLvl >= skill.GetMaximumLevel()) return;

        if(skill instanceof Trait trait){
            var attr = player.getAttribute(AttributeRegistry.TRAIT_POWER);
            if(attr == null) return;

            int freeSpace = (int)attr.getValue() - PowerManager.GetPower(player);
            if(trait.getPowerWeight() > freeSpace)return;

            var prices = skill.GetPrices();
            if(currentLvl >= prices.size()) return;
            int price = prices.get(currentLvl);


            if(SpManager.GetSP(player) >= price){
                SpManager.RemoveSP(player, price);
                data.putInt(key, currentLvl+1);
                PacketDistributor.sendToPlayer(player, new SkillLevelSyncPayload(skill.GetID(), currentLvl+1));
                PowerManager.AddPower(player, trait.getPowerWeight());
            }
        }
        else{
            var prices = skill.GetPrices();
            if(currentLvl >= prices.size()) return;
            int price = prices.get(currentLvl);


            if(SpManager.GetSP(player) >= price){
                SpManager.RemoveSP(player, price);
                data.putInt(key, currentLvl+1);
                PacketDistributor.sendToPlayer(player, new SkillLevelSyncPayload(skill.GetID(), currentLvl+1));
                if(skill.GetType() == Skill.SkillType.GENERIC) GenericEffects.ApplyEffect(skill, player);
            }
        }

    }

    public void RefundSkill(ServerPlayer player, String id)
    {
        var skill = GetSkill(id);
        if(player == null || skill == null || !Config.REFUND_SYSTEM.get()) return;

        var data = player.getPersistentData();
        var key = skill.GetID() + SKILL_LEVEL_SIGNATURE;

        int currentLvl = data.getInt(key);
        if(currentLvl <= 0 || currentLvl > skill.GetMaximumLevel()) return;

        var prices = skill.GetPrices();
        if (currentLvl > prices.size()) return;
        int initialPrice = prices.get(currentLvl - 1);
        int finalPrice = Math.max(1, (int)(initialPrice * (Config.REFUND_PERCENTAGE.get() / 100f)));

        SpManager.AddSP(player, finalPrice);

        int newLvl = currentLvl - 1;

        if(newLvl > 0) data.putInt(key, currentLvl - 1);
        else data.remove(key);

        if(skill instanceof Trait trait) PowerManager.RemovePower(player, trait.getPowerWeight());

        PacketDistributor.sendToPlayer(player, new SkillLevelSyncPayload(skill.GetID(), currentLvl-1));

        if(skill.GetType() == Skill.SkillType.GENERIC){
            if(currentLvl - 1 <= 0) GenericEffects.RemoveEffect(skill, player);
            else GenericEffects.ApplyEffect(skill, player);
        }
    }

    public void RestaureSkillData(ServerPlayer oldPlayer, ServerPlayer newPlayer)
    {
        if(oldPlayer == null || newPlayer == null) return;
        for(var skill : skillCollection.values()){
            String key = skill.GetID() + SKILL_LEVEL_SIGNATURE;

            int oldValue = oldPlayer.getPersistentData().getInt(key);
            if(oldValue > 0){
                newPlayer.getPersistentData().putInt(key, oldValue);
                PacketDistributor.sendToPlayer(newPlayer, new SkillLevelSyncPayload(skill.GetID().toLowerCase(), oldValue));
            }

            String key2 = skill.GetID() + BOOKMARK_SIGNATURE;
            boolean oldValue2 = oldPlayer.getPersistentData().getBoolean(key2);
            if(oldValue2){
                newPlayer.getPersistentData().putBoolean(key2, true);
                PacketDistributor.sendToPlayer(newPlayer, new SkillBookmarksPayload(skill.GetID().toLowerCase(), true));
            }
        }
    }

    public void BookmarkSkill(ServerPlayer player, String id){
        if(player == null || GetSkill(id.toLowerCase()) == null) return;

        var data = player.getPersistentData();
        var key = id.toLowerCase() + BOOKMARK_SIGNATURE;

        var isCurrentlyBookmarked = data.getBoolean(key);
        var newValue = !isCurrentlyBookmarked;

        if(newValue) data.putBoolean(key, true);
        else data.remove(key);

        PacketDistributor.sendToPlayer(player, new SkillBookmarksPayload(id.toLowerCase(), newValue));
    }

    public void SetSkillLevel(ServerPlayer player, String id, int lvl){
        if(player == null || lvl < 0 || lvl > 10) return;

        var skill = GetSkill(id.toLowerCase());
        if(skill == null) return;

        var data = player.getPersistentData();
        var key = id + SKILL_LEVEL_SIGNATURE;

        lvl = Math.max(0, Math.min(lvl, skill.GetMaximumLevel()));
        int oldLvl = GetPlayerSkillLevel(player, id);

        if(lvl > 0) data.putInt(key, lvl);
        else data.remove(key);

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

    public void AddSKillLevel(ServerPlayer player, String id, int amount){
        int current = GetPlayerSkillLevel(player, id);
        SetSkillLevel(player, id, current + amount);
    }

    public void RemoveSkillLevel(ServerPlayer player, String id, int amount){
        int current = GetPlayerSkillLevel(player, id);
        SetSkillLevel(player, id, current - amount);
    }




    //getters
    public Skill GetSkill(String id){return skillCollection.getOrDefault(id.toLowerCase(), null);}
    public int GetLoadedSkills(){return  skillCollection.size();}
    public List<Skill> GetAllSkills() {return new ArrayList<>(skillCollection.values());}
    public int GetPlayerSkillLevel(ServerPlayer player, String id){
        var data = player.getPersistentData();

        var skill = GetSkill(id);
        if(skill == null) return 0;

        var key = id.toLowerCase() + SKILL_LEVEL_SIGNATURE;
        return data.getInt(key);
    }
}