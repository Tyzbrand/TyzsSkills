package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.Config;
import com.tyzsskills.server.attachments.BlockMarker;
import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import com.tyzsskills.server.skills.SkillManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class DeepLodeEffect extends SkillBehaviour {

    private static final ThreadLocal<Boolean> IS_MINING = ThreadLocal.withInitial(() -> false);

    @Override
    public void onPlayerBreakBlock(BlockEvent.BreakEvent event, ServerPlayer player, int lvl, Skill skill) {
        if (IS_MINING.get()) return;
        if(!(event.getLevel() instanceof ServerLevel level)) return;

        if (player.isShiftKeyDown()) return;

        BlockState state = event.getState();
        if (!state.is(Tags.Blocks.ORES)) return;

        ItemStack tool = player.getMainHandItem();
        if (!tool.isCorrectToolForDrops(state)) return;

        BlockPos startPos = event.getPos();

        boolean griefProtection = Config.DEEP_LODE_GRIEF_PROTECTION.getAsBoolean();
        if(griefProtection && BlockMarker.IsPlayerPlaced(level, startPos)) return;
        player.sendSystemMessage(Component.literal("test"));

        Block targetBlock = state.getBlock();

        event.setCanceled(true);
        IS_MINING.set(true);

        int MAX_BLOCKS = Config.MAX_ORES.getAsInt();

        try {
            Queue<BlockPos> queue = new LinkedList<>();
            Set<BlockPos> visited = new HashSet<>();

            queue.add(startPos);
            visited.add(startPos);

            int blocksBroken = 0;

            Vec3 dropPos = Vec3.atCenterOf(startPos).add(0, 0.5, 0);

            Holder<Enchantment> silkTouchHolder = level.registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.SILK_TOUCH);

            ItemEnchantments enchants = tool.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
             boolean hasSilkTouch = enchants.getLevel(silkTouchHolder) > 0;

            while (!queue.isEmpty()) {
                if (blocksBroken >= MAX_BLOCKS) break;

                BlockPos currentPos = queue.poll();
                BlockState currentState = level.getBlockState(currentPos);

                if (currentState.is(targetBlock)) {

                    if (griefProtection && BlockMarker.IsPlayerPlaced(level, currentPos)) continue;

                    if (!currentPos.equals(startPos)) {
                        BlockEvent.BreakEvent checkEvent = new BlockEvent.BreakEvent(level, currentPos, currentState, player);
                        NeoForge.EVENT_BUS.post(checkEvent);
                        if (checkEvent.isCanceled()) continue;
                    }

                    LootParams.Builder lootParams = new LootParams.Builder(level)
                            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(currentPos))
                            .withParameter(LootContextParams.TOOL, tool)
                            .withParameter(LootContextParams.THIS_ENTITY, player)
                            .withParameter(LootContextParams.BLOCK_STATE, currentState);

                    List<ItemStack> drops = currentState.getDrops(lootParams);

                    if (SkillManager.Get().GetPlayerSkillLevel(player, "refiner") > 0 && !hasSilkTouch) {
                        List<ItemStack> smelted = RefinerEffect.smeltDrops(level, drops);
                        if (smelted != null) {
                            drops = smelted;
                            NotifyClient(player, SkillManager.Get().GetSkill("refiner"));
                        }
                    }

                    int vanillaXp = currentState.getExpDrop(level, currentPos, level.getBlockEntity(currentPos), player, tool);

                    if (hasSilkTouch) {
                        vanillaXp = 0;
                    }

                    boolean success;
                    if (currentPos.equals(startPos)) {
                        success = level.destroyBlock(currentPos, false, player);
                    } else {
                        success = level.removeBlock(currentPos, false);
                        if(success) {
                            player.awardStat(Stats.BLOCK_MINED.get(currentState.getBlock()));
                            if (vanillaXp > 0) {
                                ExperienceOrb orb = new ExperienceOrb(level, dropPos.x, dropPos.y, dropPos.z, vanillaXp);
                                level.addFreshEntity(orb);
                            }
                        }
                    }

                    if (success) {
                        for (ItemStack item : drops) {
                            if(!item.isEmpty()) {
                                ItemEntity entity = new ItemEntity(level, dropPos.x, dropPos.y, dropPos.z, item.copy());
                                entity.setDefaultPickUpDelay();
                                entity.setDeltaMovement(Vec3.ZERO);
                                level.addFreshEntity(entity);
                            }
                        }

                        player.causeFoodExhaustion(0.005F);

                        blocksBroken++;
                        tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                        if (tool.isEmpty()) break;

                        addNeighbors(currentPos, queue, visited);
                    }
                }
            }

            if(blocksBroken > 1) NotifyClient(player, skill);

        } finally {
            IS_MINING.set(false);
        }
    }

    private void addNeighbors(BlockPos pos, Queue<BlockPos> queue, Set<BlockPos> visited) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    BlockPos neighbor = pos.offset(dx, dy, dz);
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }
    }
}