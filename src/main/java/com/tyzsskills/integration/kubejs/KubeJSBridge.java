package com.tyzsskills.integration.kubejs;

import com.tyzsskills.api.events.*;
import com.tyzsskills.integration.kubejs.events.*;
import dev.latvian.mods.kubejs.script.ScriptType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class KubeJSBridge {


    public static void postSpEvent(SkillPointChangeEvent event){

        var jsEvent = new SkillPointChangeEventJS(event.getPlayer(), event.getOldAmount(), event.getNewAmount());
        TyzsKubePlugin.SP_CHANGE.post(ScriptType.SERVER, jsEvent);

        if(jsEvent.isCanceled()) event.setCanceled(true);
        else if (jsEvent.getNewAmount() != event.getNewAmount()) event.setNewAmount(jsEvent.getNewAmount());
    }

    public static void postPlayerResetEvent(PlayerResetEvent event){
        TyzsKubePlugin.PLAYER_RESET.post(ScriptType.SERVER, new PlayerResetEventJS(event.getPlayer()));
    }

    public static void postSkillPurchasePre(SkillActionEvent.PurchasePre event){
        var jsEvent = new SkillPurchasePreEventJS(event.getSkill(), event.getPlayer());
        TyzsKubePlugin.SKILL_PURCHASE_PRE.post(ScriptType.SERVER, jsEvent);

        if(jsEvent.isCanceled()) event.setCanceled(true);
    }

    public static void postSkillPurchasePost(SkillActionEvent.PurchasePost event){
        TyzsKubePlugin.SKILL_PURCHASE_POST.post(ScriptType.SERVER, new SkillPurchasePostEventJS(event.getSkill(), event.getPlayer()));
    }

    public static void postSkillRefundPre(SkillActionEvent.RefundPre event){
        var jsEvent = new SkillRefundPreEventJS(event.getSkill(), event.getPlayer());
        TyzsKubePlugin.SKILL_REFUND_PRE.post(ScriptType.SERVER, jsEvent);

        if(jsEvent.isCanceled()) event.setCanceled(true);
    }

    public static void postSkillRefundPost(SkillActionEvent.RefundPost event){
        TyzsKubePlugin.SKILL_REFUND_POST.post(ScriptType.SERVER, new SkillRefundPostEventJS(event.getSkill(), event.getPlayer()));
    }

    public static void postSkillBookmark(SkillActionEvent.Bookmark event){
        TyzsKubePlugin.SKILL_BOOKMARK.post(ScriptType.SERVER, new SkillBookmarkEventJS(event.getSkill(), event.getPlayer()));
    }

    public static void postSkillLevelChange(SkillLevelChangeEvent event){
        var jsEvent = new SkillLevelChangeEventJS(event.getPlayer(), event.getOldLevel(), event.getNewLevel());
        TyzsKubePlugin.SKILL_LEVEL_CHANGE.post(ScriptType.SERVER, jsEvent);

        if(jsEvent.isCanceled()) event.setCanceled(true);
        else if (jsEvent.getNewLevel() != event.getNewLevel()) event.setNewLevel(jsEvent.getNewLevel());
    }

    public static void postSkillLoadPre(SkillLoadEvent.Pre event){
        var jsEvent = new SkillLoadPreEventJS(event.getSkill());
        TyzsKubePlugin.SKILL_LOAD_PRE.post(ScriptType.SERVER, jsEvent);

        if(jsEvent.isCanceled()) event.setCanceled(true);
    }

    public static void postSkillLoadPost(SkillLoadEvent.Post event){
        TyzsKubePlugin.SKILL_LOAD_POST.post(ScriptType.SERVER, new SkillLoadPostEventJS(event.getSkill()));
    }

    public static void postPowerEvent(SkillPowerChangeEvent event){

        var jsEvent = new SkillPowerChangeEventJS(event.getPlayer(), event.getOldPower(), event.getNewPower());
        TyzsKubePlugin.POWER_CHANGE.post(ScriptType.SERVER, jsEvent);

        if(jsEvent.isCanceled()) event.setCanceled(true);
        else if (jsEvent.getNewPower() != event.getNewPower()) event.setNewPower(jsEvent.getNewPower());
    }

    public static void postSkillReload(SkillReloadEvent event){
        TyzsKubePlugin.SKILL_RELOAD.post(ScriptType.SERVER, new SkillReloadEventJS());
    }

    public static void postXpEvent(SkillXPChangeEvent event){

        var jsEvent = new SkillXPChangeEventJS(event.getPlayer(), event.getOldAmount(), event.getNewAmount());
        TyzsKubePlugin.XP_CHANGE.post(ScriptType.SERVER, jsEvent);

        if(jsEvent.isCanceled()) event.setCanceled(true);
        else if (jsEvent.getNewAmount() != event.getNewAmount()) event.setNewAmount(jsEvent.getNewAmount());
    }

}
