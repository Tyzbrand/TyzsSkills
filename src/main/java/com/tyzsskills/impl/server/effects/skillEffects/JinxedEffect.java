package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.ArrayList;
import java.util.Collections;

public class JinxedEffect extends SkillBehavior {

    @Override
    public void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, ISkill skill) {
        if(!(event.getEntity() instanceof ServerPlayer target)) return;

        var values = skill.getValueSet("success_probability");
        if(values == null) return;

        float chancePercentage = values.getValue(lvl);

        if(player.getRandom().nextFloat() < (chancePercentage/100f)){
            disruptHotbar(target);
            notifyClient(player, skill);
        }
    }

    private void disruptHotbar(ServerPlayer target){
        var inventory = target.getInventory();
        var hotbarSnapshot = new ArrayList<ItemStack>();

        for(int i = 0; i < 9; i++) hotbarSnapshot.add(inventory.getItem(i).copy());
        Collections.shuffle(hotbarSnapshot);
        for(int i = 0; i < 9; i++) inventory.setItem(i, hotbarSnapshot.get(i));

        target.containerMenu.broadcastChanges();
    }
}
