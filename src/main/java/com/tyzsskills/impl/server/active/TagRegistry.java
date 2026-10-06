package com.tyzsskills.impl.server.active;

import com.tyzsskills.api.records.STag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TagRegistry {
    public static final STag CONSUMABLE =
            new STag("consumable", "Consumable", 0xFF193986);

    public static final STag ATTRIBUTE =
            new STag("attribute", "Attribute", 0xFF7F2F74);


    private static final Map<String, STag> tags =
            Map.of(CONSUMABLE.id(), CONSUMABLE,
                    ATTRIBUTE.id(), ATTRIBUTE);

    public static @Nullable STag getTag(@NotNull String tagId){return tags.get(tagId);}
}
