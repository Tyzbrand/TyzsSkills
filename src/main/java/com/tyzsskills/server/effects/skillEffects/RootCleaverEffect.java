package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.Config;
import com.tyzsskills.server.attachments.BlockMarker;
import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RootCleaverEffect extends SkillBehaviour {


    private static final ThreadLocal<Boolean> IS_TIMBERING = ThreadLocal.withInitial(() -> false);
    private static final Map<Block, Boolean> STRIPPED_CACHE = new ConcurrentHashMap<>();

    @Override
    public void onPlayerBreakBlock(BlockEvent.BreakEvent event, ServerPlayer player, int lvl, Skill skill) {
        if (IS_TIMBERING.get()) return;
        if(!(event.getLevel() instanceof ServerLevel level)) return;

        if (player.isShiftKeyDown()) return;

        BlockState state = event.getState();
        if (!state.is(BlockTags.LOGS)) return;

        ItemStack tool = player.getMainHandItem();

        if (tool.isEmpty()) return;

        if (!tool.is(ItemTags.AXES) && tool.getDestroySpeed(state) <= 1.0F) return;

        if(IsStrippedLog(state.getBlock())){
            return;
        }

        BlockPos startPos = event.getPos();

        boolean griefProtection = Config.ROOT_CLEAVER_GRIEF_PROTECTION.getAsBoolean();
        if(griefProtection && BlockMarker.IsPlayerPlaced(level, startPos)) return;



        Block targetLogBlock = state.getBlock();

        event.setCanceled(true);
        IS_TIMBERING.set(true);

        int MAX_LOGS = Config.MAX_LOGS.getAsInt();
        int MAX_LEAVES = Config.MAX_LEAVES.getAsInt();

        try {
            Queue<BlockPos> queue = new LinkedList<>();
            Set<BlockPos> visited = new HashSet<>();

            queue.add(startPos);
            visited.add(startPos);

            int logsBroken = 0;
            int leavesBroken = 0;
            Block targetLeafBlock = null;

            boolean matchType = Config.MATCH_TYPE.getAsBoolean();

            Vec3 dropPos = Vec3.atCenterOf(startPos).add(0, 0.5, 0);

            while (!queue.isEmpty()) {
                if (logsBroken >= MAX_LOGS) break;

                BlockPos currentPos = queue.poll();
                BlockState currentState = level.getBlockState(currentPos);

                boolean isLog = matchType ? currentState.is(targetLogBlock) : currentState.is(BlockTags.LOGS);
                if(isLog){
                    if(IsStrippedLog(currentState.getBlock())) continue;
                }

                boolean isLeaf = currentState.is(BlockTags.LEAVES);

                if (isLeaf && matchType) {
                    if (targetLeafBlock == null) targetLeafBlock = currentState.getBlock();
                    else if (currentState.getBlock() != targetLeafBlock) continue;
                }

                if (isLog || isLeaf) {
                    if (isLeaf && leavesBroken >= MAX_LEAVES) continue;

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

                    boolean success;
                    if (currentPos.equals(startPos)) {
                        success = level.destroyBlock(currentPos, false, player);
                    } else {
                        success = level.removeBlock(currentPos, false);
                        if (success) {
                            player.awardStat(Stats.BLOCK_MINED.get(currentState.getBlock()));
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

                        if (isLog) {
                            logsBroken++;
                            tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                            if (tool.isEmpty()) break;

                            addNeighbors(currentPos, queue, visited);
                        } else {
                            leavesBroken++;
                            addNeighbors(currentPos, queue, visited);
                        }
                    }
                }
            }

            if(logsBroken > 1) NotifyClient(player, skill);

        } finally {
            IS_TIMBERING.set(false);
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

    private static boolean IsStrippedLog(Block block){
        return STRIPPED_CACHE.computeIfAbsent(block, b -> {
            String name = BuiltInRegistries.BLOCK.getKey(b).getPath();
            return name.contains("stripped");
        });
    }

}