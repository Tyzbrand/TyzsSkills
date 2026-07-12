package com.tyzsskills.impl.server.skills;

import com.tyzsskills.api.interfaces.ISkill;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

@ApiStatus.Internal
public class SkillGraph {
    //A is incompatible with B
    private final Map<String, List<String>> INCOMPATIBILITIES = new HashMap<>();
    //A requires B at lvl x
    private final Map<String, Map<String, Integer>> PREREQUISITES = new HashMap<>();
    //B is a dependency of A at lvl x (A depends on B)
    private final Map<String, Map<String, Integer>> DEPENDENTS = new HashMap<>();


    public void build(@NotNull Collection<? extends ISkill> skillRegistry){
        INCOMPATIBILITIES.clear();
        PREREQUISITES.clear();
        DEPENDENTS.clear();

        for(var skill : skillRegistry){
            var currentId = skill.getID();

            INCOMPATIBILITIES.put(currentId, new ArrayList<>());
            PREREQUISITES.put(currentId, new HashMap<>());
            DEPENDENTS.put(currentId, new HashMap<>());
        }

        for(var skill : skillRegistry){
            var currentId = skill.getID();

            for(var incompatibility : skill.getRawIncompatibilities()){
                if(!SkillManager.isSkillLoaded(incompatibility)) continue;

                var currentList = INCOMPATIBILITIES.get(currentId);
                var targetList = INCOMPATIBILITIES.get(incompatibility);

                if(!currentList.contains(incompatibility)) currentList.add(incompatibility);
                if(!targetList.contains(currentId)) targetList.add(currentId);
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

        for(var entry : INCOMPATIBILITIES.entrySet()) entry.setValue(Collections.unmodifiableList(entry.getValue()));
        for(var entry : PREREQUISITES.entrySet()) entry.setValue(Collections.unmodifiableMap(entry.getValue()));
        for(var entry : DEPENDENTS.entrySet()) entry.setValue(Collections.unmodifiableMap(entry.getValue()));

    }

    //RAW GETTERS
    public @NotNull @UnmodifiableView List<String> getIncompatibilitiesFor(@NotNull String skillId){
        return INCOMPATIBILITIES.getOrDefault(skillId, Collections.emptyList());
    }

    public @NotNull @UnmodifiableView Map<String, Integer> getPrerequisitesFor(@NotNull String skillId){
        return PREREQUISITES.getOrDefault(skillId, Collections.emptyMap());
    }

    public @NotNull @UnmodifiableView Map<String, Integer> getSkillThatDependsOn(@NotNull String skillId){
        return DEPENDENTS.getOrDefault(skillId, Collections.emptyMap());
    }

    //GETTERS
    public boolean isIncompatibleWith(@NotNull String skillA, @NotNull String skillB){
        return INCOMPATIBILITIES.getOrDefault(skillA, Collections.emptyList()).contains(skillB);
    }
}
