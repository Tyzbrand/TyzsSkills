package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import com.tyzsskills.server.skills.SkillManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags; // Import pour les Tags ORES
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RefinerEffect extends SkillBehaviour {

    private static final ThreadLocal<Boolean> IS_SMELTING = ThreadLocal.withInitial(() -> false);

    @Override
    public void onPlayerBreakBlock(BlockEvent.BreakEvent event, ServerPlayer player, int lvl, Skill skill) {
        if (IS_SMELTING.get()) return;
        if(!(event.getLevel() instanceof ServerLevel level)) return;
        if (player.isShiftKeyDown()) return;

        if (SkillManager.Get().GetPlayerSkillLevel(player, "deep_lode") > 0) return;

        BlockState state = event.getState();

        if (!state.is(Tags.Blocks.ORES)) return;

        ItemStack tool = player.getMainHandItem();
        if (!tool.isCorrectToolForDrops(state)) return;

        Holder<Enchantment> silkTouchHolder = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.SILK_TOUCH);


        ItemEnchantments enchants = tool.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        if(enchants.getLevel(silkTouchHolder) > 0) return;

        IS_SMELTING.set(true);

        try {
            BlockEvent.BreakEvent checkEvent = new BlockEvent.BreakEvent(level, event.getPos(), state, player);
            NeoForge.EVENT_BUS.post(checkEvent);
            if (checkEvent.isCanceled()) return;

            LootParams.Builder lootParams = new LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(event.getPos()))
                    .withParameter(LootContextParams.TOOL, tool)
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .withParameter(LootContextParams.BLOCK_STATE, state);

            List<ItemStack> originalDrops = state.getDrops(lootParams);

            List<ItemStack> smeltedDrops = smeltDrops(level, originalDrops);

            if (smeltedDrops == null) return;

            event.setCanceled(true);

            boolean success = level.destroyBlock(event.getPos(), false, player);

            if (success) {
                for (ItemStack item : smeltedDrops) {
                    BlockPos pos = event.getPos();
                    ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, item.copy());
                    entity.setDefaultPickUpDelay();
                    level.addFreshEntity(entity);
                }
            }
        }
        finally {IS_SMELTING.set(false);}
    }

    public static List<ItemStack> smeltDrops(Level level, List<ItemStack> originalDrops) {
        List<ItemStack> newDrops = new ArrayList<>();
        boolean atLeastOneSmelted = false;

        for (ItemStack stack : originalDrops) {
            Optional<net.minecraft.world.item.crafting.RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager()
                    .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level);

            if (recipe.isPresent()) {
                ItemStack result = recipe.get().value().getResultItem(level.registryAccess()).copy();
                result.setCount(stack.getCount() * result.getCount());
                newDrops.add(result);
                atLeastOneSmelted = true;
            } else {
                newDrops.add(stack);
            }
        }

        return atLeastOneSmelted ? newDrops : null;
    }
}