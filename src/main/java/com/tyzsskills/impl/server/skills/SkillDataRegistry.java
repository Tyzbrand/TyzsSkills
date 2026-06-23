package com.tyzsskills.impl.server.skills;

import com.tyzsskills.api.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@ApiStatus.Internal
public class SkillDataRegistry {
    private static final Map<String, SkillBehavior> behaviors = new HashMap<>();

    //Behaviors
    public static void registerCustomBehavior(String id, SkillBehavior behavior) {
        if (id == null || id.isBlank()) return;
        if (behavior == null) return;

        behaviors.put(id.toLowerCase(), behavior);
    }

    public static @Nullable SkillBehavior getBehavior(String id) {
        return behaviors.getOrDefault(id.toLowerCase(), null);
    }
}
