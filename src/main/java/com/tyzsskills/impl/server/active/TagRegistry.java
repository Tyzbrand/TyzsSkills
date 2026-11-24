package com.tyzsskills.impl.server.active;

import com.tyzsskills.api.records.SkillTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TagRegistry {
    public static final SkillTag CONSUMABLE =
            new SkillTag("consumable", "consumable", 0xFF193986);

    public static final SkillTag ATTRIBUTE =
            new SkillTag("attribute", "attribute", 0xFF7F2F74);


    private static final Map<String, SkillTag> tags =
            Map.of(CONSUMABLE.id(), CONSUMABLE,
                    ATTRIBUTE.id(), ATTRIBUTE);

    public static @Nullable SkillTag getTag(@NotNull String tagId){return tags.get(tagId);}
}
