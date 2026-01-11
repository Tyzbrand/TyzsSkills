package com.tyzsskills.server.active;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyzsskills.server.effects.GenericEffects;
import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.payloads.SkillLevelSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class SkillManager {

    private static final SkillManager instance = new SkillManager();
    public static SkillManager Get() {return instance;}


    private final Map<String, Skill> skillCollection = new HashMap<>();




    public void RegisterSKill(Skill skill)
    {
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
        var key = skill.GetID() + "_lvl";

        int currentLvl = data.getInt(key);
        if(currentLvl >= skill.GetMaximumLevel()) return;

        var prices = skill.GetPrices();
        if(currentLvl > prices.size()) return;
        int price = prices.get(currentLvl);


        if(SpManager.GetSP(player) >= price){
            SpManager.RemoveSP(player, price);
            data.putInt(key, currentLvl+1);
            PacketDistributor.sendToPlayer(player, new SkillLevelSyncPayload(skill.GetID(), currentLvl+1));
            if(skill.GetType() == Skill.SkillType.GENERIC) GenericEffects.ApplyEffect(skill, player);
        }
    }

    public void RefundSkill(ServerPlayer player, String id)
    {
        if(player == null) return;
    }

    public void ResetSkill(ServerPlayer player, String id)
    {
        if(player == null) return;
    }


    //getters
    public Skill GetSkill(String id){return skillCollection.getOrDefault(id, null);}
    public int GetLoadedSkills(){return  skillCollection.size();}
    public List<Skill> GetAllSkills() {return new ArrayList<>(skillCollection.values());}
}