package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.model.SkillBehavior;

public class RefinerEffect extends SkillBehavior {

//    private static final ThreadLocal<Boolean> IS_SMELTING = ThreadLocal.withInitial(() -> false);
//
//    @Override
//    public void onPlayerBreakBlock(BlockEvent.BreakEvent event, ServerPlayer player, int lvl, ISkill skill) {
//        if (IS_SMELTING.get()) return;
//        if(!(event.getLevel() instanceof ServerLevel level)) return;
//        if (player.isShiftKeyDown()) return;
//
//        if(LevelManager.getLevel(player) < Config.TRAIT_UNLOCK_LEVEL.get()) return;
//
//        if (SkillManager.get().getPlayerSkillLevel(player, "deep_lode") > 0) return;
//
//        BlockState state = event.getState();
//
//        if (!state.is(Tags.Blocks.ORES)) return;
//
//        ItemStack tool = player.getMainHandItem();
//        if (!tool.isCorrectToolForDrops(state)) return;
//
//        Holder<Enchantment> silkTouchHolder = level.registryAccess()
//                .lookupOrThrow(Registries.ENCHANTMENT)
//                .getOrThrow(Enchantments.SILK_TOUCH);
//
//
//        ItemEnchantments enchants = tool.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
//        if(enchants.getLevel(silkTouchHolder) > 0) return;
//
//        IS_SMELTING.set(true);
//
//        try {
//            BlockEvent.BreakEvent checkEvent = new BlockEvent.BreakEvent(level, event.getPos(), state, player);
//            NeoForge.EVENT_BUS.post(checkEvent);
//            if (checkEvent.isCanceled()) return;
//
//            LootParams.Builder lootParams = new LootParams.Builder(level)
//                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(event.getPos()))
//                    .withParameter(LootContextParams.TOOL, tool)
//                    .withParameter(LootContextParams.THIS_ENTITY, player)
//                    .withParameter(LootContextParams.BLOCK_STATE, state);
//
//            List<ItemStack> originalDrops = state.getDrops(lootParams);
//
//            List<ItemStack> smeltedDrops = smeltDrops(level, originalDrops);
//
//            if (smeltedDrops == null) return;
//
//            event.setCanceled(true);
//
//            boolean success = level.destroyBlock(event.getPos(), false, player);
//
//            if (success) {
//                for (ItemStack item : smeltedDrops) {
//                    BlockPos pos = event.getPos();
//                    ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, item.copy());
//                    entity.setDefaultPickUpDelay();
//                    level.addFreshEntity(entity);
//                }
//
//                notifyClient(player, skill);
//            }
//        }
//        finally {IS_SMELTING.set(false);}
//    }
//
//    public static List<ItemStack> smeltDrops(Level level, List<ItemStack> originalDrops) {
//        List<ItemStack> newDrops = new ArrayList<>();
//        boolean atLeastOneSmelted = false;
//
//        for (ItemStack stack : originalDrops) {
//            Optional<net.minecraft.world.item.crafting.RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager()
//                    .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level);
//
//            if (recipe.isPresent()) {
//                ItemStack result = recipe.get().value().getResultItem(level.registryAccess()).copy();
//                result.setCount(stack.getCount() * result.getCount());
//                newDrops.add(result);
//                atLeastOneSmelted = true;
//            } else {
//                newDrops.add(stack);
//            }
//        }
//
//        return atLeastOneSmelted ? newDrops : null;
//    }
}