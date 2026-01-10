package com.tyzsskills.server.active;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyzsskills.server.model.Skill;
import net.minecraft.server.level.ServerPlayer;

public class SkillManager {

    private static final SkillManager instance = new SkillManager();
    public static SkillManager Get() {return instance;}


    private final Map<String, Skill> skillCollection = new HashMap<>();

    public Boolean IsSkillValid(String id) {return skillCollection.containsKey(id);}



    public void RegisterSKill(Skill skill)
    {
        if(!skillCollection.containsKey(skill.GetID())) skillCollection.put(skill.GetID(), skill);
    }

    public void ClearSkills(){
        skillCollection.clear();
    }


    public void BuySkill(ServerPlayer player, String id)
    {
        if(player == null || !IsSkillValid(id)) return;
    }

    public void RefundSkill(ServerPlayer player, String id)
    {
        if(player == null || !IsSkillValid(id)) return;
    }

    public void ResetSkill(ServerPlayer player, String id)
    {
        if(player == null || !IsSkillValid(id)) return;
    }


    //getters
    public boolean AreSkillsLoaded(){return !skillCollection.isEmpty();}
    public int GetLoadedSkills(){return  skillCollection.size();}
    public List<Skill> GetAllSkills() {return new ArrayList<>(skillCollection.values());}
}