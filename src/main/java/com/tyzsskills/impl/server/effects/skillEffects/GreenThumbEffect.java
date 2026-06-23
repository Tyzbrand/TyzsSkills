package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.tools.TagMatchTool;
import com.tyzsskills.impl.server.attachments.BlockMarker;
import com.tyzsskills.api.model.SkillBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class GreenThumbEffect extends SkillBehavior {

    @Override
    public void registerEvent(IEventBus eventBus, ISkill skill) {
        registerAction(
                eventBus,
                skill,
                BlockEvent.BreakEvent.class,
                BlockEvent.BreakEvent::getPlayer,
                this::onPlayerBreakBlock
        );
    }


    private void onPlayerBreakBlock(BlockEvent.BreakEvent event, ServerPlayer player, ISkill skill, int lvl) {
        if(!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        var state = event.getState();
        Block block = state.getBlock();
        boolean isValidPlant = false;

        if(block instanceof CropBlock crop && crop.isMaxAge(state)) isValidPlant = true;
        else if (block instanceof NetherWartBlock && state.getValue(NetherWartBlock.AGE) >= 3) isValidPlant = true;
        else if(block instanceof CocoaBlock && state.getValue(CocoaBlock.AGE) >= 2) isValidPlant = true;
        else if (block instanceof TallGrassBlock || block instanceof DeadBushBlock) isValidPlant = true;
        else if (block instanceof TallSeagrassBlock || block instanceof SeagrassBlock || block instanceof KelpBlock) isValidPlant = true;
        else if(block instanceof PumpkinBlock) isValidPlant = true;
        else if(block instanceof CactusBlock || block instanceof SugarCaneBlock || block instanceof BambooStalkBlock) isValidPlant = true;
        else if(state.is(BlockTags.LEAVES)) isValidPlant = true;
        else if(state.is(Blocks.MELON)) isValidPlant = true;
        else if(state.is(Blocks.FERN) || state.is(Blocks.LARGE_FERN)) isValidPlant = true;
        else if(state.is(Blocks.SHORT_GRASS)) isValidPlant = true;

        if(!isValidPlant) return;

        var values = skill.getValueSet("success_probability");
        if(values == null) return;

        float chancePercentage = values.getValue(lvl);

        if(player.getRandom().nextFloat() < (chancePercentage / 100f)){

            if (BlockMarker.IsPlayerPlaced(serverLevel, event.getPos())) return;
            if(TagMatchTool.isBlockInList(skill.getSpecificParameters(), "block_blacklist", state)) return;

            BlockPos position = event.getPos();
            Block.dropResources(state, serverLevel, position, serverLevel.getBlockEntity(position), player, player.getMainHandItem());
            notifyClient(player, skill);
        }

    }
}
