package com.tyzsskills;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.model.settings.*;
import com.tyzsskills.api.records.SettingMeta;
import com.tyzsskills.impl.server.active.ErrorManager;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@ApiStatus.Internal
public final class Settings {
    public static final String CONFIG_VERSION = "7.3";

    private static final Map<String, Setting<?>> SERVER_REGISTRY = new HashMap<>();
    private static final Collection<Setting<?>> SERVER_REGISTRY_VIEW = Collections.unmodifiableCollection(SERVER_REGISTRY.values());

    private static final Map<String, Setting<?>> CLIENT_REGISTRY = new HashMap<>();
    private static final Collection<Setting<?>> CLIENT_REGISTRY_VIEW = Collections.unmodifiableCollection(CLIENT_REGISTRY.values());

    //IMPLEMENTATION
    public static @NotNull <T extends Setting<?>> T register(T setting){
        var registry = registry(setting);
        var key = setting.key();

        if(registry.containsKey(key)) throw new IllegalStateException("Duplicate setting key: " + key);
        registry.put(key, setting);

        return setting;
    }

    //LIFE CYCLE
    private static void resetSettings(boolean isClient){registry(isClient).values().forEach(Setting::reset);}
    private static void saveSettings(boolean isClient){Parser.writeSettings(isClient);}
    private static void readSettings(boolean isClient){
        resetSettings(isClient);
        Parser.parseSettings(isClient);
    }


    //GETTERS
    private static @Nullable Setting<?> getByKey(@NotNull String key, boolean isClient){
        return isClient ? CLIENT_REGISTRY.get(key) : SERVER_REGISTRY.get(key);
    }
    private static @NotNull Map<String, Setting<?>> registry(Setting<?> setting){
        return registry(setting.meta().isClient());
    }
    private static @NotNull Map<String, Setting<?>> registry(boolean isClient){
        return isClient ? CLIENT_REGISTRY : SERVER_REGISTRY;
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
    public static <E extends Enum<E>> @NotNull EnumSetting<E> enumerator(String key, Enums.SettingCategory cat, E def){
        var setting = new EnumSetting<>(new SettingMeta(key, cat), def);
        return Settings.register(setting);
    }

    //DATA
    public static final class Server {
        public static void init(){}

        public static void resetSettings(){Settings.resetSettings(false);}
        public static void saveSettings(){Settings.saveSettings(false);}
        public static void readSettings(){Settings.readSettings(false);}
        public static @NotNull @UnmodifiableView Collection<Setting<?>> getSettings(){
            return SERVER_REGISTRY_VIEW;
        }

        public static @Nullable Setting<?> getByKey(@NotNull String key){
            return SERVER_REGISTRY.get(key);
        }

        public static @NotNull Map<String, String> serializeSettings() {
            return SERVER_REGISTRY.values().stream().collect(Collectors.toMap(Setting::key, Setting::serialize));
        }

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
        //PERFORMANCES
        public static final BoolSetting SMOOTH_ITEM_MOVEMENT =
                bool("smooth_item_movements", Enums.SettingCategory.SERVER_PERFORMANCES, true);
    }
    public static final class Client {
        public static void init(){}

        public static void resetSettings(){Settings.resetSettings(true);}
        public static void saveSettings(){Settings.saveSettings(true);}
        public static void readSettings(){Settings.readSettings(true);}
        public static @NotNull @UnmodifiableView Collection<Setting<?>> getSettings(){
            return CLIENT_REGISTRY_VIEW;
        }

        public static @Nullable Setting<?> getByKey(@NotNull String key){
            return CLIENT_REGISTRY.get(key);
        }


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

    }

    private static class Parser {
        private static String header(boolean isClient){
            var prefix = isClient ? "Client Settings, " : "Server Settings, ";
            return prefix + Settings.CONFIG_VERSION;
        }

        private static @NotNull Path mainPath(boolean isClient){
            var fileName = isClient ? "client_settings.cfg" : "server_settings.cfg";
            return FMLPaths.CONFIGDIR.get().resolve("tyzs_skills").resolve(fileName);
        }

        public static void writeSettings(boolean isClient){
            var path = mainPath(isClient);
            var sideMark = isClient ? "client" : "server";

            try{
                Files.createDirectories(path.getParent());}
            catch (IOException ex){
                throw new IllegalStateException("Unable to init Tyz's Skills config folders (" + sideMark + ")");
            }

            var settings = isClient ? new ArrayList<>(Client.getSettings()) : new ArrayList<>(Server.getSettings());
            settings.sort(Comparator.comparingInt(setting -> setting.meta().category().priority()));

            var prevCat = settings.getFirst().meta().category();

            var sb = new StringBuilder();
            comment(sb, header(isClient));
            brk(sb);
            brk(sb);
            cat(sb, prevCat.label());

            for(var setting : settings){
                var currentCat = setting.meta().category();
                if(currentCat != prevCat){
                    brk(sb);
                    prevCat = currentCat;
                    cat(sb, currentCat.label());
                }

                var comment = setting.comment();
                if(!comment.isEmpty()) comment(sb, comment);
                range(sb, setting);
                value(sb, setting);

                brk(sb);
            }

            var finalString = sb.toString();
            try {Files.writeString(path, finalString, StandardCharsets.UTF_8);}
            catch (IOException ex){throw new IllegalStateException("Unable to write Tyz's Skills default settings (" + sideMark + ")", ex);}
        }
        public static void parseSettings(boolean isClient){
            var path = mainPath(isClient);
            if(!Files.exists(path)) {
                writeSettings(isClient);
                return;
            }

            var sideMark = isClient ? "client" : "server";

            List<String> lines;
            try{lines = Files.readAllLines(path, StandardCharsets.UTF_8);}
            catch(IOException ex){throw new IllegalStateException("Unable to read Tyz's Skills settings ("+ sideMark + ")", ex);}

            var fileVersion = "";
            var foundKeys = new HashSet<>();
            var hasInconsistencies = false;

            for(var rawLine : lines){
                var line = rawLine.trim();

                if(line.startsWith("#" + header(isClient))){
                    fileVersion = line.split(",", 2)[1].trim();
                    continue;
                }

                if(line.startsWith("#") || line.startsWith("~") || line.startsWith("[") || line.isEmpty()) continue;

                var parts = line.split("=", 2);
                if(parts.length < 2) continue;

                var key = parts[0].trim();
                var value = parts[1].trim();

                var setting = Settings.getByKey(key, isClient);
                if(setting == null) {
                    hasInconsistencies = true;
                    continue;
                }
                else if (!foundKeys.add(key)) hasInconsistencies = true;


                try{setting.safeParse(value);}
                catch (IllegalStateException ex){
                    ErrorManager.registerSettingWarn(key, "invalid value format", isClient);
                }
            }

            var sizes = isClient ? CLIENT_REGISTRY.size() : SERVER_REGISTRY.size();
            var missingKeys = foundKeys.size() != sizes;
            var changedVersion = !Settings.CONFIG_VERSION.equalsIgnoreCase(fileVersion);

            if(hasInconsistencies || missingKeys ||changedVersion) writeSettings(isClient);
        }

        //UTILS
        private static void brk(StringBuilder sb){sb.append("\n");}
        private static void cat(StringBuilder sb, String cat){sb.append(String.format("[%s]", cat)); brk(sb);}
        private static void comment(StringBuilder sb, String comment){sb.append(String.format("#%s", comment)); brk(sb);}
        private static void range(StringBuilder sb, Setting<?> set){sb.append(String.format("# ~ %s", set.range())); brk(sb);}
        private static void value(StringBuilder sb, Setting<?> set){sb.append(String.format("%s = %s", set.meta().key(), set.get())); brk(sb);}
    }
}
