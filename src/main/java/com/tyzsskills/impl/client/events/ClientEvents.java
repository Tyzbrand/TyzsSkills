package com.tyzsskills.impl.client.events;


import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.screen.MainGUI;
import com.tyzsskills.impl.client.tooltips.CategoryTooltip;
import com.tyzsskills.impl.client.tooltips.CategoryTooltipData;
import com.tyzsskills.impl.client.tooltips.SkillTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

public class ClientEvents {
    @SubscribeEvent
    public static void onTooltipColor(RenderTooltipEvent.Color event) {

        if (Minecraft.getInstance().screen instanceof MainGUI) {

        for(var component : event.getComponents()){
            if(component instanceof SkillTooltip tooltip){
                event.setBackground(0xFF000000);

                var skill = tooltip.getSkill();
                boolean isMaxed = ClientCache.getSkillLevel(skill.getID()) >= skill.getMaximumLevel();

                int borderStartColor = isMaxed ? 0xFFD6AD55 : 0xFFD6D6D6;
                int borderEndColor = isMaxed ? 0xFF8B6B25 : 0xFF8B8B8B;

                event.setBorderStart(borderStartColor);
                event.setBorderEnd(borderEndColor);

                break;
            }


        }

        }
    }
}
