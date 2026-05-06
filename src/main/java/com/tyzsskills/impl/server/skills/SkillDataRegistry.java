package com.tyzsskills.impl.server.skills;

import com.tyzsskills.api.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@ApiStatus.Internal
public class SkillDataRegistry {
    private static final Map<String, SkillBehavior> behaviors = new HashMap<>();
    private static final Map<String, List<Predicate<Player>>> conditions = new HashMap<>();


    //Behaviors
    public static void registerCustomBehavior(String id, SkillBehavior behavior){
        if(id == null || id.isBlank()) return;
        if(behavior == null) return;

        behaviors.put(id.toLowerCase(), behavior);
    }

    public static SkillBehavior getBehavior(String id){
        return behaviors.getOrDefault(id.toLowerCase(), null);
    }


    //Conditions
    public static void registerConditions(String id, List<Predicate<Player>> predicates){
        if(id == null || predicates == null || predicates.isEmpty()) return;
        conditions.put(id, predicates);
    }

    public static @NotNull List<Predicate<Player>> getConditions(String id){return conditions.getOrDefault(id, Collections.emptyList());}
}
