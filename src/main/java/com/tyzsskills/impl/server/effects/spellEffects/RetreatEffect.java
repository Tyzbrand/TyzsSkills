package com.tyzsskills.impl.server.effects.spellEffects;

import com.tyzsskills.api.TyzsSkillsAPI;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.model.SpellBehavior;
import com.tyzsskills.api.tools.FormatTools;
import com.tyzsskills.impl.server.spells.SpellManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//TODO: translations
public class RetreatEffect extends SpellBehavior {
    private final Map<UUID, PendingRetreat> pendingRetreats = new HashMap<>();
    public RetreatEffect(){NeoForge.EVENT_BUS.register(this);}
    private record PendingRetreat(long targetTick, Vec3 startPos, ISpell spell){}

    @Override
    public boolean onSpellCast(@NotNull ServerPlayer player, @NotNull ISpell spell) {
        if(pendingRetreats.containsKey(player.getUUID())) return false;

        var delayProp = spell.getProperty("teleportation_delay");
        if(delayProp == null) return false;


        var spawnPoint = geValidSpawnPoint(player);
        if(spawnPoint == null) {
            player.displayClientMessage(Component.literal("Invalid Spawn Point").withStyle(ChatFormatting.RED), true);
            return false;
        }

        var delay = delayProp.getValue(TyzsSkillsAPI.spells().getPropertyLevel(player, delayProp));
        var targetTick = player.server.overworld().getGameTime() + (long) (delay * 20f);

        pendingRetreats.put(player.getUUID(), new PendingRetreat(targetTick, player.position(), spell));
        return true;

    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Pre event){
        if(event.getEntity() instanceof ServerPlayer player){
            var retreatData = pendingRetreats.get(player.getUUID());
            if(retreatData == null) return;

            if(!player.isAlive() || player.isDeadOrDying()){
                pendingRetreats.remove(player.getUUID());
                return;
            }

            if (player.position().distanceToSqr(retreatData.startPos()) > .01D) {
                pendingRetreats.remove(player.getUUID());
                SpellManager.removeCooldown(player, retreatData.spell());
                player.displayClientMessage(Component.literal("Teleportation canceled").withStyle(ChatFormatting.RED), true);
                return;
            }

            var currentTick = player.server.overworld().getGameTime();
            if (currentTick >= retreatData.targetTick) {
                pendingRetreats.remove(player.getUUID());

                var spawnPoint = geValidSpawnPoint(player);
                if(spawnPoint == null){
                    SpellManager.removeCooldown(player, retreatData.spell());
                    player.displayClientMessage(Component.literal("Invalid Spawn Point").withStyle(ChatFormatting.RED), true);
                    return;
                }
                player.resetFallDistance();
                player.teleportTo(spawnPoint.x, spawnPoint.y, spawnPoint.z);
                player.displayClientMessage(Component.literal("Successfully teleported").withStyle(ChatFormatting.DARK_GREEN), true);
            }
            else{
                var remainingTime =(float)Math.ceil((retreatData.targetTick - currentTick) / 20D);
                player.displayClientMessage(Component.literal(FormatTools.defaultFloat(remainingTime)), true);
            }
        }
    }

    //Utils
    private @Nullable Vec3 geValidSpawnPoint(ServerPlayer player){
        var transition = player.findRespawnPositionAndUseSpawnBlock(true, DimensionTransition.DO_NOTHING);

        if(transition.newLevel() != player.serverLevel()) return null;
        if(transition.missingRespawnBlock()) return null;

        return transition.pos();
    }

}
