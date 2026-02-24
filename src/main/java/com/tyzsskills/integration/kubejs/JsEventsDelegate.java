package com.tyzsskills.integration.kubejs;

import com.tyzsskills.api.events.*;
import com.tyzsskills.integration.Integrations;
import net.neoforged.bus.api.SubscribeEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class JsEventsDelegate {

    @SubscribeEvent
    public static void onSpChange(SkillPointChangeEvent event){
        if(Integrations.isKubeJSLoaded()) KubeJSBridge.postSpEvent(event);
    }

    @SubscribeEvent
    public static void onPlayerReset(PlayerResetEvent event){
        if(Integrations.isKubeJSLoaded()) KubeJSBridge.postPlayerResetEvent(event);
    }

    @SubscribeEvent
    public static void onSkillPurchasePre(SkillActionEvent.PurchasePre event){
        if(Integrations.isKubeJSLoaded()) KubeJSBridge.postSkillPurchasePre(event);
    }

    @SubscribeEvent
    public static void onSkillPurchasePost(SkillActionEvent.PurchasePost event){
        if(Integrations.isKubeJSLoaded()) KubeJSBridge.postSkillPurchasePost(event);
    }

    @SubscribeEvent
    public static void onSkillRefundPre(SkillActionEvent.RefundPre event){
        if(Integrations.isKubeJSLoaded()) KubeJSBridge.postSkillRefundPre(event);
    }

    @SubscribeEvent
    public static void onSkillRefundPost(SkillActionEvent.RefundPost event){
        if(Integrations.isKubeJSLoaded()) KubeJSBridge.postSkillRefundPost(event);
    }

    @SubscribeEvent
    public static void onSkillBookmark(SkillActionEvent.Bookmark event){
        if(Integrations.isKubeJSLoaded()) KubeJSBridge.postSkillBookmark(event);
    }

    @SubscribeEvent
    public static void onSkillLevelChange(SkillLevelChangeEvent event){
        if(Integrations.isKubeJSLoaded()) KubeJSBridge.postSkillLevelChange(event);
    }

    @SubscribeEvent
    public static void onSkillLoadPre(SkillLoadEvent.Pre event){
        if(Integrations.isKubeJSLoaded()) KubeJSBridge.postSkillLoadPre(event);
    }

    @SubscribeEvent
    public static void onSkillLoadPost(SkillLoadEvent.Post event){
        if(Integrations.isKubeJSLoaded()) KubeJSBridge.postSkillLoadPost(event);
    }

    @SubscribeEvent
    public static void onPowerChange(SkillPowerChangeEvent event){
        if(Integrations.isKubeJSLoaded()) KubeJSBridge.postPowerEvent(event);
    }

    @SubscribeEvent
    public static void onSkillReload(SkillReloadEvent event){
        if(Integrations.isKubeJSLoaded()) KubeJSBridge.postSkillReload(event);
    }

    @SubscribeEvent
    public static void onXpChange(SkillXPChangeEvent event){
        if(Integrations.isKubeJSLoaded()) KubeJSBridge.postXpEvent(event);
    }

}
