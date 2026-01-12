package com.tyzsskills.server.skills;

import com.tyzsskills.server.effects.skillEffects.VenomousAttackEffect;
import com.tyzsskills.server.model.SkillBehaviour;

import java.util.HashMap;
import java.util.Map;

public class SkillBehaviourRegistry {
    private static final Map<String, SkillBehaviour> behaviours = new HashMap<>();

    public static void Init(){
        behaviours.put("venomous_attack", new VenomousAttackEffect());
    }

    public static SkillBehaviour GetBehaviour(String id){
        return behaviours.getOrDefault(id.toLowerCase(), null);
    }
}
