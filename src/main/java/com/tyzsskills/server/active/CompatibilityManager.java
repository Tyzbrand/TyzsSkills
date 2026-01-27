package com.tyzsskills.server.active;

import com.tyzsskills.server.attachments.LegacyData;
import com.tyzsskills.server.attachments.StatsTracker;
import com.tyzsskills.server.skills.SkillManager;
import com.tyzsskills.server.xp.XpManager;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Locale;

public class CompatibilityManager {
    private static final String MIGRATION_TAG = "tyzs_migrated_v1";

    public static void ProcessMigration(ServerPlayer player){
        if(player.getPersistentData().getBoolean(MIGRATION_TAG)) return;

        var legacy = player.getData(LegacyData.PLAYER_VARIABLES);

        if(!legacy.hasData()){
            player.getPersistentData().putBoolean(MIGRATION_TAG, true);
            return;
        }


        //Main data
        double oldLevel = legacy.getOldValue("Level");
        if(oldLevel > 1) LevelManager.SetLevel(player, (int)oldLevel);

        double oldXp = legacy.getOldValue("Skill_XP");
        if(oldXp > 0) XpManager.SetXP(player, (float)oldXp);

        double oldSp = legacy.getOldValue("researchpoints");
        if(oldSp > 0) SpManager.SetSP(player, (int)oldSp);


        //Stat data
        var data = player.getData(StatsTracker.DATA);

        double oldLifeTimeXp = legacy.getOldValue("lifetime_xp");
        if(oldLifeTimeXp > 0) data.addXp((float)oldLifeTimeXp);

        double oldEarns = legacy.getOldValue("earned_points");
        if(oldEarns > 0) data.addSpEarned((int)oldEarns);

        double oldGains = legacy.getOldValue("spent_points");
        if(oldGains > 0) data.addSpSpent((int)oldGains);


        RemoveOldModifier(player, Attributes.MOVEMENT_SPEED, "speedmodifier");
        RemoveOldModifier(player, Attributes.OXYGEN_BONUS, "OxygeneModifier");
        RemoveOldModifier(player, Attributes.ATTACK_DAMAGE, "AttackDamageModifer");
        RemoveOldModifier(player, Attributes.BLOCK_INTERACTION_RANGE, "ReachModifer");
        RemoveOldModifier(player, Attributes.MAX_HEALTH, "HealthModifer");
        RemoveOldModifier(player, Attributes.SNEAKING_SPEED, "sneakingModifier");
        RemoveOldModifier(player, Attributes.STEP_HEIGHT, "StepModifier");
        RemoveOldModifier(player, Attributes.MINING_EFFICIENCY, "MiningModifer");
        RemoveOldModifier(player, NeoForgeMod.SWIM_SPEED, "SwimModifer");

        MigrateSkill(player, legacy, "block_reach_lvl", "block_reach");
        MigrateSkill(player, legacy, "attack_damage_lvl", "attack_damage");
        MigrateSkill(player, legacy, "max_health_lvl", "health_boost");
        MigrateSkill(player, legacy, "venomous_attack_lvl", "venomous_attack");
        MigrateSkill(player, legacy, "xp_boost_lvl", "experience_boost");
        MigrateSkill(player, legacy, "damage_deal_lvl", "damage_deal");
        MigrateSkill(player, legacy, "resistance_lvl", "resistance");
        MigrateSkill(player, legacy, "skill_xp_boost_lvl", "swift_learn");
        MigrateSkill(player, legacy, "critical_hit_lvl", "critical_hit");
        MigrateSkill(player, legacy, "restaured_hunger_lvl", "nutrition");
        MigrateSkill(player, legacy, "swim_speed", "swim_speed");
        MigrateSkill(player, legacy, "speed_lvl", "speed_boost");
        MigrateSkill(player, legacy, "step_height_lvl", "step_height");
        MigrateSkill(player, legacy, "bloodlust_lv", "bloodlust");
        MigrateSkill(player, legacy, "oxygene_boost_lvl", "oxygen_boost");
        MigrateSkill(player, legacy, "mining_speed_level", "excavation");
        MigrateSkill(player, legacy, "adrenaline_lvl", "adrenaline");
        MigrateSkill(player, legacy, "sneaking_speed_lvl", "sneak_speed");

        int totalRefund = 0;
        totalRefund += CheckRefund(legacy, "second_life_lvl", 20);
        totalRefund += CheckRefund(legacy, "Villager_follow_lvl", 12);
        totalRefund += CheckRefund(legacy, "night_vision_lvl", 18);
        totalRefund += CheckRefund(legacy, "fire_resistance_lvl", 18);
        totalRefund += CheckRefund(legacy, "friendly_piglins", 12);
        totalRefund += CheckRefund(legacy, "comestible_rotten_flesh_lvl", 12);
        totalRefund += CheckRefund(legacy, "harvester_lvl", 14);

        if(totalRefund > 0){
            SpManager.AddSP(player, totalRefund);
            player.sendSystemMessage(Component.literal("§e[Tyzs Skills] §rObsolete skills converted to points: §6+" + totalRefund + " SP"));
            player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5f, 1.0f);
        }


        player.getPersistentData().putBoolean(MIGRATION_TAG, true);

        XpManager.LevelUpCheck(player);
    }

    private static void MigrateSkill(ServerPlayer player, LegacyData data, String oldKey, String newId){
        double value = data.getOldValue(oldKey);
        if(value > 0) SkillManager.Get().SetSkillLevel(player, newId, (int)value);
    }


    private static int CheckRefund(LegacyData data, String oldKey, int amount){
        double value = data.getOldValue(oldKey);
        if(value > 0) return amount;
        else return 0;
    }

    private static void RemoveOldModifier(ServerPlayer player, Holder<Attribute> attribute, String suffix){
        var instance = player.getAttribute(attribute);
        if(instance == null) return;

        suffix = suffix.toLowerCase(Locale.ROOT);
        ResourceLocation id = ResourceLocation.parse("tyzs_skills:" + suffix);
        if(instance.hasModifier(id)) instance.removeModifier(id);

    }
}
