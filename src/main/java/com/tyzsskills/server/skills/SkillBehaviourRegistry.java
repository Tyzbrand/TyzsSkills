package com.tyzsskills.server.skills;

import com.tyzsskills.server.effects.skillEffects.*;
import com.tyzsskills.server.model.SkillBehaviour;

import java.util.HashMap;
import java.util.Map;

public class SkillBehaviourRegistry {
    private static final Map<String, SkillBehaviour> behaviours = new HashMap<>();

    public static void Init(){
        behaviours.put("venomous_attack", new VenomousAttackEffect());
        behaviours.put("critical_hit", new CriticalHitEffect());
        behaviours.put("damage_deal", new DamageDealEffect());
        behaviours.put("bloodlust", new BloodlustEffect());
        behaviours.put("resistance", new ResistanceEffect());
        behaviours.put("backstab", new BackstabEffect());
        behaviours.put("rage", new RageEffect());
        behaviours.put("adrenaline", new AdrenalineEffect());
        behaviours.put("resilience", new ResilienceEffect());

        behaviours.put("green_thumb", new GreenThumbEffect());
        behaviours.put("nutrition", new NutritionEffect());
        behaviours.put("experience_boost", new ExperienceBoostEffect());
        behaviours.put("magnet", new MagnetEffect());
    }

    public static SkillBehaviour GetBehaviour(String id){
        return behaviours.getOrDefault(id.toLowerCase(), null);
    }
}
