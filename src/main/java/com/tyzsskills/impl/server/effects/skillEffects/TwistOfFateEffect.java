package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.tools.TagMatchTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TwistOfFateEffect extends SkillBehavior {

    private static final Set<GlobalPos> PROCESSING_CHESTS = ConcurrentHashMap.newKeySet();

    @Override
    public void registerEvent(IEventBus eventBus, ISkill skill) {
        registerAction(
                eventBus,
                skill,
                PlayerInteractEvent.RightClickBlock.class,
                PlayerInteractEvent.RightClickBlock::getEntity,
                this::onRightClickBlock
        );
    }

    private void onRightClickBlock(PlayerInteractEvent.RightClickBlock event, ServerPlayer player, ISkill skill, int lvl) {
        var values = skill.getValueSet("loot_multiplier");
        if (values == null) return;

        float bonusPercentage = values.getValue(lvl);
        var pos = event.getPos();
        var globalPos = GlobalPos.of(player.level().dimension(), pos);
        var blockEntity = player.level().getBlockEntity(pos);

        if(PROCESSING_CHESTS.contains(globalPos)) return;

        if (blockEntity instanceof RandomizableContainerBlockEntity) {
            CompoundTag nbt = blockEntity.saveWithoutMetadata(player.registryAccess());

            if (nbt.contains("LootTable")) {
                var server = player.getServer();
                if (server != null && !TagMatchTool.isBlockInList(skill.getSpecificParameters(), "container_blacklist", event.getLevel().getBlockState(pos))) {

                    PROCESSING_CHESTS.add(globalPos);
                    server.tell(new TickTask(server.getTickCount() + 1, () -> {
                        try{
                            var openedEntity = player.level().getBlockEntity(pos);
                            if (!(openedEntity instanceof Container container)) return;
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
                        finally {
                            PROCESSING_CHESTS.remove(globalPos);
                        }
                    }));
                }
            }
        }
    }
}


