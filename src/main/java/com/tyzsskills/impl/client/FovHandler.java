package com.tyzsskills.impl.client;

import com.tyzsskills.Config;
import com.tyzsskills.Tyzsskills;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;

@EventBusSubscriber(modid = Tyzsskills.MODID, value = Dist.CLIENT)
public class FovHandler {

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event){
        double reduction = Config.FOV_REDUCTION.get();
        if(reduction <= 0.0001D) return;

        float totalDampening = 0f;

        for (String rawId : ClientCache.getPurchasedSkills()){
            String id = rawId.toLowerCase();

            int level = ClientCache.GetSkillLevel(id);
            if(level <= 0) continue;

            var skill = ClientCache.GetSkill(id);
            if(skill == null) continue;

            for(var modifier : skill.getModifiers()){
                if(modifier.attribute().equals("minecraft:generic.movement_speed")){

                    var rawValue = modifier.getValue(level);

                    var realFactor = rawValue / 100f;
                    totalDampening += (float)(realFactor * reduction * 0.5f);

                    break;
                }
            }
        }

        if(totalDampening > 0) {
            float currentFov = event.getFovModifier();
            float newFov = currentFov - totalDampening;

            if (currentFov >= 1.0f) {
                event.setNewFovModifier(Math.max(1.0f, newFov));
            }
        }
    }
}
