package com.tyzsskills.impl.server.skills;

import com.tyzsskills.impl.server.effects.skillEffects.*;
import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.impl.server.model.SkillBehavior;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public class SkillBehaviorRegistry {
    private static final Map<String, SkillBehavior> behaviors = new HashMap<>();

    public static void init(){
        behaviors.put("venomous_attack", new VenomousAttackEffect());
        behaviors.put("critical_hit", new CriticalHitEffect());
        behaviors.put("damage_deal", new DamageDealEffect());
        behaviors.put("bloodlust", new BloodlustEffect());
        behaviors.put("resistance", new ResistanceEffect());
        behaviors.put("backstab", new BackstabEffect());
        behaviors.put("rage", new RageEffect());
        behaviors.put("adrenaline", new AdrenalineEffect());
        behaviors.put("resilience", new ResilienceEffect());
        behaviors.put("chaos_strike", new ChaosStrikeEffect());
        behaviors.put("piercing_strike", new PiercingStrikeEffect());

        behaviors.put("green_thumb", new GreenThumbEffect());
        behaviors.put("nutrition", new NutritionEffect());
        behaviors.put("experience_boost", new ExperienceBoostEffect());
        behaviors.put("magnet", new MagnetEffect());
        behaviors.put("stealth", new StealthEffect());

        behaviors.put("cinder_blood", new CinderBloodEffect());
        behaviors.put("iron_gut", new IronGutEffect());
        behaviors.put("gilded_aura", new GildedAuraEffect());
        behaviors.put("silver_tongue", new SilverTongueEffect());
        behaviors.put("keepsake", new KeepsakeEffect());
        behaviors.put("soundless", new SoundlessEffect());
        behaviors.put("root_cleaver", new RootCleaverEffect());
        behaviors.put("deep_lode", new DeepLodeEffect());
        behaviors.put("refiner", new RefinerEffect());
        behaviors.put("deep_rest", new DeepRestEffect());
        behaviors.put("deep_sight", new DeepSightEffect());
    }

    public static void registerCustomBehavior(String id, SkillBehavior behavior){
        if(id == null || id.isBlank()) return;
        if(behavior == null) return;

        behaviors.put(id.toLowerCase(), behavior);
    }

    public static SkillBehavior getBehavior(String id){
        return behaviors.getOrDefault(id.toLowerCase(), null);
    }
}
