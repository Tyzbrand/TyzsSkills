package com.tyzsskills;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.model.settings.*;
import com.tyzsskills.api.records.SettingMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@ApiStatus.Internal
public final class Settings {
    public static final String CONFIG_VERSION = "7.2";

    private static final Map<String, Setting<?>> SERVER_REGISTRY = new LinkedHashMap<>();
    private static final Collection<Setting<?>> SERVER_REGISTRY_VIEW = Collections.unmodifiableCollection(SERVER_REGISTRY.values());

    private static final Map<String, Setting<?>> CLIENT_REGISTRY = new LinkedHashMap<>();
    private static final Collection<Setting<?>> CLIENT_REGISTRY_VIEW = Collections.unmodifiableCollection(CLIENT_REGISTRY.values());

    static {Stock.init();}

    //IMPLEMENTATION
    private static @NotNull Map<String, Setting<?>> registry(Setting<?> setting){
        return setting.meta().isClient() ? CLIENT_REGISTRY : SERVER_REGISTRY;
    }

    public static @NotNull <T extends Setting<?>> T register(T setting){
        var registry = registry(setting);
        var key = setting.key();

        if(registry.containsKey(key)) throw new IllegalStateException("Duplicate setting key: " + key);
        registry.put(key, setting);

        return setting;
    }

    public static void resetServerSettings(){
        SERVER_REGISTRY.values().forEach(Setting::reset);
    }

    public static void resetClientSettings(){
        CLIENT_REGISTRY.values().forEach(Setting::reset);
    }

    //GETTERS
    public static @Nullable Setting<?> getByKey(@NotNull String key, boolean isClient){
        return isClient ? CLIENT_REGISTRY.get(key) : SERVER_REGISTRY.get(key);
    }
    public static @NotNull @UnmodifiableView Collection<Setting<?>> getServerSettings(){
        return SERVER_REGISTRY_VIEW;
    }
    public static @NotNull @UnmodifiableView Collection<Setting<?>> getClientSettings(){
        return CLIENT_REGISTRY_VIEW;
    }

    //BUILDERS
    public static @NotNull BoolSetting bool(String key, Enums.SettingCategory cat, boolean def){
        var setting = new BoolSetting(new SettingMeta(key, cat), def);
        return Settings.register(setting);
    }
    public static @NotNull IntSetting integer(String key, Enums.SettingCategory cat, int def, int min, int max){
        var setting = new IntSetting(new SettingMeta(key, cat), def, min, max);
        return Settings.register(setting);
    }
    public static @NotNull FloatSetting fl(String key, Enums.SettingCategory cat, float def, float min, float max){
        var setting = new FloatSetting(new SettingMeta(key, cat), def, min, max);
        return Settings.register(setting);
    }
    public static @NotNull ColorSetting color(String key, Enums.SettingCategory cat, String def){
        var setting = new ColorSetting(new SettingMeta(key, cat), def);
        return Settings.register(setting);
    }


    public static class Stock {

        protected static void init(){}

        //region SERVER
        //GENERAL
        public static final BoolSetting USE_SPELLS =
                bool("use_spells", Enums.SettingCategory.SERVER_GENERAL, true);
        public static final BoolSetting PURCHASE_SYSTEM =
                bool("purchase_sys", Enums.SettingCategory.SERVER_GENERAL, true);
        public static final BoolSetting REFUND_SYSTEM =
                bool("refund_sys", Enums.SettingCategory.SERVER_GENERAL, true);
        public static final FloatSetting REFUND_PERCENTAGE =
                fl("refund_percentage", Enums.SettingCategory.SERVER_GENERAL, 30f, 0f, 100f);
        public static final IntSetting BASE_SPELL_SLOTS =
                integer("base_spell_slots", Enums.SettingCategory.SERVER_GENERAL, 3, 0, 25);

        //DEATH PENALTIES
        public static final FloatSetting DEATH_XP_LOSS =
                fl("death_xp_loss", Enums.SettingCategory.DEATH_PENALTIES, 0f, 0f, 100f);
        public static final FloatSetting DEATH_SP_LOSS =
                fl("death_sp_loss", Enums.SettingCategory.DEATH_PENALTIES, 0f, 0f, 100f);
        public static final IntSetting DEATH_LEVEL_LOSS =
                integer("death_lvl_loss", Enums.SettingCategory.DEATH_PENALTIES, 0, 0, Integer.MAX_VALUE);
        public static final FloatSetting DEATH_SKILL_LOSS =
                fl("death_skill_loss", Enums.SettingCategory.DEATH_PENALTIES, 0f, 0f, 100f);

        //XP VALUES
        public static final BoolSetting EARN_XP_IN_CREATIVE =
                bool("earn_in_creative", Enums.SettingCategory.XP, false);
        public static final BoolSetting PREVENT_XP_FROM_PLACED_BLOCKS =
                bool("prevent_xp_from_marked", Enums.SettingCategory.XP, true);
        public static final BoolSetting EARN_FROM_EATING =
                bool("earn_from_eating", Enums.SettingCategory.XP, true);
        public static final BoolSetting EARN_FROM_KILLING =
                bool("earn_from_killing", Enums.SettingCategory.XP, true);
        public static final BoolSetting EARN_FROM_MINING =
                bool("earn_from_mining", Enums.SettingCategory.XP, true);
        public static final FloatSetting GAINS_FROM_SLEEPING =
                fl("sleep_gains", Enums.SettingCategory.XP, 25f, 0f, Float.MAX_VALUE);
        public static final FloatSetting GAINS_FROM_FISHING =
                fl("fish_gains", Enums.SettingCategory.XP, 7f, 0f, Float.MAX_VALUE);
        public static final FloatSetting GAINS_FROM_CRAFTING =
                fl("craft_gains", Enums.SettingCategory.XP, .2f, 0f, Float.MAX_VALUE);
        public static final FloatSetting GAINS_FROM_BREEDING =
                fl("breed_gains", Enums.SettingCategory.XP, 15f, 0f, Float.MAX_VALUE);
        public static final FloatSetting GAINS_FROM_TASKS =
                fl("task_gains", Enums.SettingCategory.XP, 25f, 0f, Float.MAX_VALUE);
        public static final FloatSetting GAINS_FROM_GOALS =
                fl("goal_gains", Enums.SettingCategory.XP, 100f, 0f, Float.MAX_VALUE);
        public static final FloatSetting GAINS_FROM_CHALLENGES =
                fl("challenge_gains", Enums.SettingCategory.XP, 250f, 0f, Float.MAX_VALUE);
        public static final FloatSetting GAINS_FROM_BIOMES =
                fl("new_biome_gains", Enums.SettingCategory.XP, 45f, 0f, Float.MAX_VALUE);
        public static final FloatSetting GAINS_FROM_DIMENSIONS =
                fl("new_dimension_gains", Enums.SettingCategory.XP, 500f, 0f, Float.MAX_VALUE);
        //endregion

        //region CLIENT
        //GENERAL
        public static final BoolSetting SHOW_DEBUG =
                bool("show_debug", Enums.SettingCategory.CLIENT_GENERAL, false);

        public static final BoolSetting INVENTORY_BUTTON =
                bool("show_inventory_button", Enums.SettingCategory.CLIENT_GENERAL, true);
        public static final IntSetting INVENTORY_X =
                integer("inventory_button_x", Enums.SettingCategory.CLIENT_GENERAL, 3, -1000, 1000);
        public static final IntSetting INVENTORY_Y =
                integer("inventory_button_y", Enums.SettingCategory.CLIENT_GENERAL, 3, -1000, 1000);

        //OVERLAYS
        public static final BoolSetting OVERLAY_CREATIVE =
                bool("overlays_in_creative", Enums.SettingCategory.OVERLAYS, false);

        public static final IntSetting META_OFFSET_X =
                integer("metadata_overlay_offset_x", Enums.SettingCategory.OVERLAYS, 0, -1000, 1000);
        public static final IntSetting META_OFFSET_Y =
                integer("metadata_overlay_offset_y", Enums.SettingCategory.OVERLAYS, 0, -1000, 1000);
        public static final FloatSetting META_SCALE =
                fl("metadata_overlay_scale", Enums.SettingCategory.OVERLAYS, .8f, .1f, 30f);

        public static final BoolSetting XP_OVERLAY =
                bool("show_xp_overlay", Enums.SettingCategory.OVERLAYS, true);
        public static final FloatSetting XP_DURATION =
                fl("xp_overlay_duration", Enums.SettingCategory.OVERLAYS, 3f, .5f, 60f);
        public static final ColorSetting XP_BG =
                color("xp_overlay_bg_color", Enums.SettingCategory.OVERLAYS, "#FF000000");
        public static final ColorSetting XP_BD =
                color("xp_overlay_bd_color", Enums.SettingCategory.OVERLAYS, "#FFFFFFFF");

        public static final BoolSetting LVL_OVERLAY =
                bool("show_lvl_overlay", Enums.SettingCategory.OVERLAYS, true);
        public static final FloatSetting LVL_DURATION =
                fl("lvl_overlay_duration", Enums.SettingCategory.OVERLAYS, 4f, .5f, 60f);
        public static final ColorSetting LVL_BG =
                color("lvl_overlay_bg_color", Enums.SettingCategory.OVERLAYS, "#FF000000");
        public static final ColorSetting LVL_BD =
                color("lvl_overlay_bd_color", Enums.SettingCategory.OVERLAYS, "#FFD6AD55");

        public static final BoolSetting SKILL_OVERLAY =
                bool("show_skill_overlay", Enums.SettingCategory.OVERLAYS, true);
        public static final IntSetting SKILL_OFFSET_X =
                integer("skill_overlay_offset_x", Enums.SettingCategory.OVERLAYS, 0, -1000, 1000);
        public static final IntSetting SKILL_OFFSET_Y =
                integer("skill_overlay_offset_y", Enums.SettingCategory.OVERLAYS, 0, -1000, 1000);
        public static final FloatSetting SKILL_SCALE =
                fl("skill_overlay_scale", Enums.SettingCategory.OVERLAYS, .8f, .1f, 30f);
        public static final FloatSetting SKILL_DURATION =
                fl("skill_overlay_duration", Enums.SettingCategory.OVERLAYS, 3f, .5f, 60f);
        public static final ColorSetting SKILL_BG =
                color("skill_overlay_bg_color", Enums.SettingCategory.OVERLAYS, "#FF000000");
        public static final ColorSetting SKILL_BD =
                color("skill_overlay_bd_color", Enums.SettingCategory.OVERLAYS, "#FFFFFFFF");

        //ACCESSIBILITY
        public static final FloatSetting FOV_REDUCTION =
                fl("fov_reduction", Enums.SettingCategory.ACCESSIBILITY, .95f, 0f, 1f);

        public static final BoolSetting QUERY_PERSISTENCE =
                bool("keep_search_query", Enums.SettingCategory.ACCESSIBILITY, true);
        public static final BoolSetting QUERY_TYPOS =
                bool("allow_query_typos", Enums.SettingCategory.ACCESSIBILITY, false);

        //PERFORMANCES
        public static final BoolSetting RENDER_SHADOWS =
                bool("render_icon_shadows", Enums.SettingCategory.PERFORMANCES, true);

        //endregion
    }
}
