package com.tyzsskills.impl.client;

import com.tyzsskills.Config;
import com.tyzsskills.Tyzsskills;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;

@EventBusSubscriber(modid = Tyzsskills.MODID, value = Dist.CLIENT)
public class FovHandler {

    private static int latestCacheVersion = -1;
    private static float cachedSpeedFactorSum = 0f;

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event){
        double reduction = Config.FOV_REDUCTION.get();
        if (reduction <= .0001D) return;

        var cache = ClientCache.get();

        if (cache.getVersion() != latestCacheVersion) {
            latestCacheVersion = cache.getVersion();
            cachedSpeedFactorSum = calculateSpeedFactor(cache);
        }


        if (cachedSpeedFactorSum > 0f) {
            var totalDampening = (float)(cachedSpeedFactorSum * reduction * .5f);
            var currentFov = event.getFovModifier();
            var newFov = currentFov - totalDampening;

            if (currentFov >= 1f) event.setNewFovModifier(Math.max(1f, newFov));
        }
    }

    private static float calculateSpeedFactor(ClientCache cache){
        float total = 0f;

        for (var kvp : cache.getOwnedSkills().entrySet()){
            var level = kvp.getValue();
            if(level <= 0) continue;

            var skill = cache.getSkill(kvp.getKey());
            if(skill == null) continue;

            for(var modifier : skill.getModifiers()){
                if(modifier.attribute().equals("minecraft:generic.movement_speed")) {
                    total += (modifier.getValue(level) / 100f);
                    break;
                }
            }
        }
        return total;
    }
}
