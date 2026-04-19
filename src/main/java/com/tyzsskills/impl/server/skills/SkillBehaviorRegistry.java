package com.tyzsskills.impl.server.skills;

import com.tyzsskills.impl.server.effects.skillEffects.*;
import com.tyzsskills.impl.server.model.SkillBehavior;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
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
        behaviors.put("jinxed", new JinxedEffect());
        behaviors.put("true_strike", new TrueStrikeEffect());
        behaviors.put("sunder_armor", new SunderArmorEffect());

        behaviors.put("green_thumb", new GreenThumbEffect());
        behaviors.put("nutrition", new NutritionEffect());
        behaviors.put("experience_boost", new ExperienceBoostEffect());
        behaviors.put("magnet", new MagnetEffect());
        behaviors.put("stealth", new StealthEffect());
        behaviors.put("shepherd_s_blessing", new ShepherdsBlessingEffect());
        behaviors.put("spare_parts", new SparePartsEffect());
        behaviors.put("twist_of_fate", new TwistOfFateEffect());

        behaviors.put("haggler", new HagglerEffect());
        behaviors.put("keepsake", new KeepsakeEffect());
        behaviors.put("root_cleaver", new RootCleaverEffect());
        behaviors.put("deep_lode", new DeepLodeEffect());
        behaviors.put("refiner", new RefinerEffect());
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
