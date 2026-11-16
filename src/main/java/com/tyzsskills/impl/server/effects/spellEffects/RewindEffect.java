package com.tyzsskills.impl.server.effects.spellEffects;

import com.tyzsskills.api.TyzsSkillsAPI;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.model.SpellBehavior;
import com.tyzsskills.api.tools.FormatTools;
import com.tyzsskills.impl.server.spells.SpellManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RewindEffect extends SpellBehavior {
    private final Map<UUID, PendingRewind> pendingRewinds = new HashMap<>();
    public RewindEffect(){NeoForge.EVENT_BUS.register(this);}
    private record PendingRewind(long targetTick, Vec3 startPos, ISpell spell, float startHealth, ResourceKey<Level> currentDimension){}

    @Override
    public boolean onSpellCast(@NotNull ServerPlayer player, @NotNull ISpell spell) {
        if(pendingRewinds.containsKey(player.getUUID())) return false;

        var delayProp = spell.getProperty("teleportation_delay");
        if(delayProp == null) return false;

        var delay = delayProp.getValue(TyzsSkillsAPI.spells().getPropertyLevel(player, delayProp));
        var targetTick = player.server.overworld().getGameTime() + (long)(delay * 20);

        var rewindData = new PendingRewind(targetTick, player.position(), spell, player.getHealth(), player.level().dimension());
        if(!isPositionValid(player, rewindData)) {
            player.displayClientMessage(Component.literal("Invalid Location").withStyle(ChatFormatting.RED), true);
            return false;
        }

        pendingRewinds.put(player.getUUID(), rewindData);
        return true;

    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Pre event){
        if(event.getEntity() instanceof ServerPlayer player){
            var rewindData = pendingRewinds.get(player.getUUID());
            if(rewindData == null) return;

            if(!player.isAlive() || player.isDeadOrDying()){
                pendingRewinds.remove(player.getUUID());
                return;
            }

            var currentTick = player.server.overworld().getGameTime();
            if (currentTick >= rewindData.targetTick) {
                pendingRewinds.remove(player.getUUID());

                if(!isPositionValid(player, rewindData)){
                    SpellManager.removeCooldown(player, rewindData.spell());
                    player.displayClientMessage(Component.literal("Invalid Location").withStyle(ChatFormatting.RED), true);
                    return;
                }

                player.resetFallDistance();
                player.teleportTo(rewindData.startPos.x, rewindData.startPos.y, rewindData.startPos.z);
                player.setHealth(Math.min(rewindData.startHealth(), player.getMaxHealth()));
                player.displayClientMessage(Component.literal("Successfully teleported").withStyle(ChatFormatting.DARK_GREEN), true);
            }
            else{
                var remainingTime =(float)Math.ceil((rewindData.targetTick - currentTick) / 20D);
                player.displayClientMessage(Component.literal(FormatTools.defaultFloat(remainingTime)), true);
            }
        }
    }

    //Utils
    private boolean isPositionValid(ServerPlayer player, PendingRewind rewindData){
        var level = player.level();
        if(level.dimension() != rewindData.currentDimension) return false;

        var blockPos = BlockPos.containing(rewindData.startPos());
        if (!level.isInWorldBounds(blockPos) || !level.isLoaded(blockPos)) return false;

        var box = player.getDimensions(Pose.STANDING).makeBoundingBox(rewindData.startPos());
        return level.noCollision(player, box);
    }
}
