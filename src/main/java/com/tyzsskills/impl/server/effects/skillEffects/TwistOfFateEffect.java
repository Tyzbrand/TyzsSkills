package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.tools.TagMatchTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Collections;

public class TwistOfFateEffect extends SkillBehavior {

    @Override
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event, ServerPlayer player, int lvl, ISkill skill) {
        var values = skill.getValueSet("loot_multiplier");
        if (values == null) return;

        float bonusPercentage = values.getValue(lvl);
        var pos = event.getPos();
        var blockEntity = player.level().getBlockEntity(pos);

        if (blockEntity instanceof RandomizableContainerBlockEntity) {
            CompoundTag nbt = blockEntity.saveWithoutMetadata(player.registryAccess());

            if (nbt.contains("LootTable")) {
                var server = player.getServer();
                if (server != null && !TagMatchTool.isBlockInList(skill.getSpecificParameters(), "container_blacklist", event.getLevel().getBlockState(pos))) {
                    server.tell(new TickTask(server.getTickCount() + 1, () -> {

                        var openedEntity = player.level().getBlockEntity(pos);
                        if (openedEntity instanceof Container container) {
                            var triggered = false;

                            var freeSlots = new ArrayList<Integer>();
                            for (int i = 0; i < container.getContainerSize(); i++) {
                                if (container.getItem(i).isEmpty()) freeSlots.add(i);
                            }
                            Collections.shuffle(freeSlots);

                            var baseLoot = new ArrayList<ItemStack>();
                            for (int i = 0; i < container.getContainerSize(); i++) {
                                ItemStack stack = container.getItem(i);
                                if (!stack.isEmpty()) baseLoot.add(stack.copy());
                            }

                            for (ItemStack original : baseLoot) {
                                var rawBonus = original.getCount() * (bonusPercentage / 100f);
                                int extraCount = (int) rawBonus;

                                if (player.getRandom().nextFloat() < (rawBonus - extraCount)) extraCount++;
                                if (extraCount <= 0) continue;
                                if(TagMatchTool.isItemInList(skill.getSpecificParameters(), "item_blacklist", original)) continue;

                                while (extraCount > 0 && !freeSlots.isEmpty()) {
                                    triggered = true;
                                    var toAdd = Math.min(extraCount, original.getMaxStackSize());
                                    ItemStack bonusStack = original.copy();

                                    bonusStack.setCount(toAdd);

                                    var targetSlot = freeSlots.removeFirst();
                                    container.setItem(targetSlot, bonusStack);

                                    extraCount -= toAdd;
                                }
                            }
                            if (triggered) notifyClient(player, skill);
                        }
                    }));
                }
            }
        }
    }
}


