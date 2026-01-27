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
        behaviours.put("stealth", new StealthEffect());

        behaviours.put("cinder_blood", new CinderBloodEffect());
        behaviours.put("iron_gut", new IronGutEffect());
        behaviours.put("gilded_aura", new GildedAuraEffect());
        behaviours.put("silver_tongue", new SilverTongueEffect());
        behaviours.put("keepsake", new KeepsakeEffect());
        behaviours.put("soundless", new SoundlessEffect());
        behaviours.put("root_cleaver", new RootCleaverEffect());
        behaviours.put("deep_lode", new DeepLodeEffect());
        behaviours.put("refiner", new RefinerEffect());
        behaviours.put("deep_rest", new DeepRestEffect());
        behaviours.put("deep_sight", new DeepSightEffect());
    }

    public static SkillBehaviour GetBehaviour(String id){
        return behaviours.getOrDefault(id.toLowerCase(), null);
    }
}
