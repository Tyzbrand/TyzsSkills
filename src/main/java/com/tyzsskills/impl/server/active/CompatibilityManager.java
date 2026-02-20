package com.tyzsskills.impl.server.active;

import com.tyzsskills.impl.server.attachments.LegacyData;
import com.tyzsskills.impl.server.attachments.StatsTracker;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.xp.XpManager;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;

@ApiStatus.Internal
public class CompatibilityManager {

    //Migration from old Mcreator attachment -> persistent data
    private static final String MIGRATION_TAG = "tyzs_migrated_v1";
    public static void processMigration(ServerPlayer player){
        if(player.getPersistentData().getBoolean(MIGRATION_TAG)) return;

        var legacy = player.getData(LegacyData.PLAYER_VARIABLES);

        if(!legacy.hasData()){
            player.getPersistentData().putBoolean(MIGRATION_TAG, true);
            return;
        }


        //Main data
        double oldLevel = legacy.getOldValue("Level");
        if(oldLevel > 1) LevelManager.setLevel(player, (int)oldLevel);

        double oldXp = legacy.getOldValue("Skill_XP");
        if(oldXp > 0) XpManager.setXP(player, (float)oldXp);

        double oldSp = legacy.getOldValue("researchpoints");
        if(oldSp > 0) SpManager.setSp(player, (int)oldSp);


        //Stat data
        var data = player.getData(StatsTracker.DATA);

        double oldLifeTimeXp = legacy.getOldValue("lifetime_xp");
        if(oldLifeTimeXp > 0) data.addXp((float)oldLifeTimeXp);

        double oldEarns = legacy.getOldValue("earned_points");
        if(oldEarns > 0) data.addSpEarned((int)oldEarns);

        double oldGains = legacy.getOldValue("spent_points");
        if(oldGains > 0) data.addSpSpent((int)oldGains);


        removeOldModifier(player, Attributes.MOVEMENT_SPEED, "speedmodifier");
        removeOldModifier(player, Attributes.OXYGEN_BONUS, "OxygeneModifier");
        removeOldModifier(player, Attributes.ATTACK_DAMAGE, "AttackDamageModifer");
        removeOldModifier(player, Attributes.BLOCK_INTERACTION_RANGE, "ReachModifer");
        removeOldModifier(player, Attributes.MAX_HEALTH, "HealthModifer");
        removeOldModifier(player, Attributes.SNEAKING_SPEED, "sneakingModifier");
        removeOldModifier(player, Attributes.STEP_HEIGHT, "StepModifier");
        removeOldModifier(player, Attributes.MINING_EFFICIENCY, "MiningModifer");
        removeOldModifier(player, NeoForgeMod.SWIM_SPEED, "SwimModifer");

        migrateSkill(player, legacy, "block_reach_lvl", "block_reach");
        migrateSkill(player, legacy, "attack_damage_lvl", "attack_damage");
        migrateSkill(player, legacy, "max_health_lvl", "health_boost");
        migrateSkill(player, legacy, "venomous_attack_lvl", "venomous_attack");
        migrateSkill(player, legacy, "xp_boost_lvl", "experience_boost");
        migrateSkill(player, legacy, "damage_deal_lvl", "damage_deal");
        migrateSkill(player, legacy, "resistance_lvl", "resistance");
        migrateSkill(player, legacy, "skill_xp_boost_lvl", "swift_learn");
        migrateSkill(player, legacy, "critical_hit_lvl", "critical_hit");
        migrateSkill(player, legacy, "restaured_hunger_lvl", "nutrition");
        migrateSkill(player, legacy, "swim_speed", "swim_speed");
        migrateSkill(player, legacy, "speed_lvl", "speed_boost");
        migrateSkill(player, legacy, "step_height_lvl", "step_height");
        migrateSkill(player, legacy, "bloodlust_lv", "bloodlust");
        migrateSkill(player, legacy, "oxygene_boost_lvl", "oxygen_boost");
        migrateSkill(player, legacy, "mining_speed_level", "excavation");
        migrateSkill(player, legacy, "adrenaline_lvl", "adrenaline");
        migrateSkill(player, legacy, "sneaking_speed_lvl", "sneak_speed");

        int totalRefund = 0;
        totalRefund += checkRefund(legacy, "second_life_lvl", 20);
        totalRefund += checkRefund(legacy, "Villager_follow_lvl", 12);
        totalRefund += checkRefund(legacy, "night_vision_lvl", 18);
        totalRefund += checkRefund(legacy, "fire_resistance_lvl", 18);
        totalRefund += checkRefund(legacy, "friendly_piglins", 12);
        totalRefund += checkRefund(legacy, "comestible_rotten_flesh_lvl", 12);
        totalRefund += checkRefund(legacy, "harvester_lvl", 14);

        if(totalRefund > 0){
            SpManager.addSP(player, totalRefund);
            player.sendSystemMessage(Component.literal("§e[Tyzs Skills] §rObsolete skills converted to points: §6+" + totalRefund + " SP"));
            player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5f, 1.0f);
        }


        player.getPersistentData().putBoolean(MIGRATION_TAG, true);

        XpManager.levelUpCheck(player);
    }

    private static void migrateSkill(ServerPlayer player, LegacyData data, String oldKey, String newId){
        double value = data.getOldValue(oldKey);
        if(value > 0) SkillManager.Get().setSkillLevel(player, newId, (int)value);
    }


    private static int checkRefund(LegacyData data, String oldKey, int amount){
        double value = data.getOldValue(oldKey);
        if(value > 0) return amount;
        else return 0;
    }

    private static void removeOldModifier(ServerPlayer player, Holder<Attribute> attribute, String suffix){
        var instance = player.getAttribute(attribute);
        if(instance == null) return;

        suffix = suffix.toLowerCase(Locale.ROOT);
        ResourceLocation id = ResourceLocation.parse("tyzs_skills:" + suffix);
        if(instance.hasModifier(id)) instance.removeModifier(id);

    }



    //Migration from persistent data -> attachment
    private static final String MIGRATION_TAG_V2 = "tyzs_migrated_v2";
    public static void processMigrationV2(ServerPlayer player){
        var oldData = player.getPersistentData();

        boolean didMigrate = oldData.getBoolean(MIGRATION_TAG_V2);
        if(!didMigrate){
            if(oldData.contains("SKILL_LEVEL")){
                LevelManager.setLevel(player, oldData.getInt("SKILL_LEVEL"));
                oldData.remove("SKILL_LEVEL");
            }

            if(oldData.contains("SKILL_POINT")){
                SpManager.setSp(player, oldData.getInt("SKILL_POINT"));
                oldData.remove("SKILL_POINT");
            }

            if(oldData.contains("SKILL_XP")){
                XpManager.setXP(player, oldData.getFloat("SKILL_XP"));
                oldData.remove("SKILL_XP");
            }

            for (var skill : SkillManager.Get().getAllSkills()){
                var id = skill.GetID().toLowerCase();
                var lvlKey = id + "_lvl";
                var bkKey = id + "_bookmark";

                if(oldData.contains(lvlKey)){
                    SkillManager.Get().setSkillLevel(player, id, oldData.getInt(lvlKey));
                    oldData.remove(lvlKey);
                }

                if(oldData.contains(bkKey)){
                    SkillManager.Get().bookmarkSkill(player, id);
                    oldData.remove(bkKey);
                }
            }
            didMigrate = true;
        }
        oldData.putBoolean(MIGRATION_TAG_V2, true);
    }
}
