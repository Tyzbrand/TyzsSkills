package com.tyzsskills.impl.server.skills;

import com.tyzsskills.api.interfaces.ISkill;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

@ApiStatus.Internal
public class SkillGraph {
    //A is incompatible with B
    private final Map<String, Set<String>> INCOMPATIBILITIES = new HashMap<>();
    //A requires B at lvl x
    private final Map<String, Map<String, Integer>> PREREQUISITES = new HashMap<>();
    //B is a dependency of A at lvl x (A depends on B)
    private final Map<String, Map<String, Integer>> DEPENDENTS = new HashMap<>();

    public void build(@NotNull List<ISkill> skillRegistry){
        INCOMPATIBILITIES.clear();
        PREREQUISITES.clear();
        DEPENDENTS.clear();

        for(var skill : skillRegistry){
            var currentId = skill.getID();

            INCOMPATIBILITIES.put(currentId, new HashSet<>());
            PREREQUISITES.put(currentId, new HashMap<>());
            DEPENDENTS.put(currentId, new HashMap<>());
        }

        for(var skill : skillRegistry){
            var currentId = skill.getID();

            for(var incompatibility : skill.getRawIncompatibilities()){
                if(!SkillManager.isSkillLoaded(incompatibility)) continue;
                INCOMPATIBILITIES.get(currentId).add(incompatibility);
                INCOMPATIBILITIES.get(incompatibility).add(currentId);
            }

            for(var prerequisite : skill.getRawPrerequisites().entrySet()){
                var prerequisiteId = prerequisite.getKey();
                var requiredLevel = prerequisite.getValue();

                if(SkillManager.isSkillLoaded(prerequisiteId)){
                    PREREQUISITES.get(currentId).put(prerequisiteId, requiredLevel);
                    DEPENDENTS.get(prerequisiteId).put(currentId, requiredLevel);
                }
            }
        }
    }

    //RAW GETTERS
    public @NotNull @Unmodifiable List<String> getIncompatibilitiesFor(@NotNull String skillId){
        var set = INCOMPATIBILITIES.get(skillId);
        return set == null ? List.of() : List.copyOf(set);
    }

    public @NotNull @Unmodifiable Map<String, Integer> getPrerequisitesFor(@NotNull String skillId){
        return PREREQUISITES.getOrDefault(skillId, Map.of());
    }

    public @NotNull @Unmodifiable Map<String, Integer> getSkillThatDependsOn(@NotNull String skillId){
        return DEPENDENTS.getOrDefault(skillId, Map.of());
    }

    //GETTERS
    public boolean isIncompatibleWith(@NotNull String skillA, @NotNull String skillB){
        return INCOMPATIBILITIES.getOrDefault(skillA, Set.of()).contains(skillB);
    }
}
