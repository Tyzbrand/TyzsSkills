package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.neoforged.neoforge.event.level.BlockEvent;

public class GreenThumbEffect extends SkillBehaviour {
    @Override
    public void onPlayerBreakBlock(BlockEvent.BreakEvent event, ServerPlayer player, int lvl, Skill skill) {
        if(!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        var state = event.getState();
        Block block = state.getBlock();
        boolean flag = false;

        if(block instanceof CropBlock crop && crop.isMaxAge(state)) flag = true;
        else if(state.is(BlockTags.FLOWERS)) flag = true;
        else if (block instanceof NetherWartBlock && state.getValue(NetherWartBlock.AGE) >= 3) {flag = true;}

        if(!flag) return;

        var values = skill.GetValues();
        if (values == null || values.isEmpty()) return;

        int index = Math.min(lvl - 1, values.size() - 1);
        float chancePercentage = values.get(index);

        if(player.getRandom().nextFloat() < (chancePercentage / 100f)){
            BlockPos position = event.getPos();
            Block.dropResources(state, serverLevel, position, serverLevel.getBlockEntity(position), player, player.getMainHandItem());
            NotifyClient(player, skill);
        }

    }
}
