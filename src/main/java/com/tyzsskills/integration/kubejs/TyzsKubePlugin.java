package com.tyzsskills.integration.kubejs;


import com.tyzsskills.api.TyzsSkillsAPI;
import com.tyzsskills.integration.kubejs.events.*;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.plugin.ClassFilter;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class TyzsKubePlugin implements KubeJSPlugin {

    public static final EventGroup GROUP = EventGroup.of("TyzsSkillsEvents");



    public static final EventHandler SKILL_LOAD_PRE = GROUP.server("skill_load_pre", () -> SkillLoadPreEventJS.class);
    public static final EventHandler SKILL_LOAD_POST = GROUP.server("skill_load_post", () -> SkillLoadPostEventJS.class);
    public static final EventHandler SKILL_RELOAD = GROUP.server("skill_reload", () -> SkillReloadEventJS.class);
    public static final EventHandler PLAYER_RESET = GROUP.server("player_reset", () -> PlayerResetEventJS.class);

    public static final EventHandler SP_CHANGE = GROUP.server("sp_change", () -> SkillPointChangeEventJS.class);
    public static final EventHandler POWER_CHANGE = GROUP.server("power_change", () -> SkillPowerChangeEventJS.class);
    public static final EventHandler XP_CHANGE = GROUP.server("xp_change", () -> SkillXPChangeEventJS.class);

    public static final EventHandler SKILL_PURCHASE_PRE = GROUP.server("skill_purchase_pre", () -> SkillPurchasePreEventJS.class);
    public static final EventHandler SKILL_PURCHASE_POST = GROUP.server("skill_purchase_post", () -> SkillPurchasePostEventJS.class);
    public static final EventHandler SKILL_REFUND_PRE = GROUP.server("skill_refund_pre", () -> SkillRefundPreEventJS.class);
    public static final EventHandler SKILL_REFUND_POST = GROUP.server("skill_refund_post", () -> SkillRefundPostEventJS.class);
    public static final EventHandler SKILL_BOOKMARK = GROUP.server("skill_bookmark", () -> SkillBookmarkEventJS.class);
    public static final EventHandler SKILL_LEVEL_CHANGE = GROUP.server("skill_level_change", () -> SkillLevelChangeEventJS.class);



    @Override
    public void registerEvents(EventGroupRegistry registry){
        registry.register(GROUP);
    }

    @Override
    public void registerBindings(BindingRegistry bindings) {bindings.add("TyzsSkills", TyzsSkillsAPI.class);}

    @Override
    public void registerClasses(ClassFilter filter) {
        filter.allow("com.tyzsskills.api");
    }
}
