package com.tyzsskills.api.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class SkillConfiguration {
    public SkillConfiguration(){this(null, null, null, null, null, null, null);}

    public SkillConfiguration(@NotNull CompoundTag parameters){this(null, null, null, null, null, null, parameters);}

    public SkillConfiguration (Integer levelRequirement,
                               List<String> incompatibilities, Map<String, Integer> prerequisites,
                               Boolean refundable, Boolean purchasable, Boolean visible, CompoundTag parameters){


        this.refundable = refundable;
        this.purchasable = purchasable;
        this.visible = visible;

        this.levelRequirement = levelRequirement;

        //Lists
        this.incompatibleSkills = incompatibilities == null ? null : Collections.unmodifiableList(incompatibilities);
        this.skillPrerequisites = prerequisites == null ? null : Collections.unmodifiableMap(prerequisites);

        this.parameters = parameters == null ? new CompoundTag() : parameters;
    }


    //Booleans
    private final Boolean refundable;
    public boolean refundable(){return refundable == null || refundable;}

    private final Boolean purchasable;
    public boolean purchasable(){return purchasable == null || purchasable;}

    private final Boolean visible;
    public boolean visible(){return visible == null || visible;}


    //Ints
    private final Integer levelRequirement;
    public int levelRequirement(){return levelRequirement == null ? 0 : levelRequirement;}

    private final @UnmodifiableView List<String> incompatibleSkills;
    public @NotNull @UnmodifiableView List<String> incompatibleSkills(){return incompatibleSkills == null ? Collections.emptyList() :  incompatibleSkills;}


    private final @UnmodifiableView Map<String, Integer> skillPrerequisites;
    public @NotNull @UnmodifiableView Map<String, Integer> skillPrerequisites(){return skillPrerequisites == null ? Map.of() :  skillPrerequisites;}

    //Tags
    private final CompoundTag parameters;
    public @NotNull CompoundTag parameters(){return parameters;}

    //region Load/Write/Read
    public static final JsonSerializer<SkillConfiguration> GSON_SERIALIZER = ((src, typeOfSrc, ctx) -> {
        var obj = new JsonObject();
        if(!src.purchasable()) obj.addProperty("purchasable", false);
        if(!src.refundable()) obj.addProperty("refundable", false);
        if(!src.visible()) obj.addProperty("visible", false);

        if(src.levelRequirement() > 0) obj.addProperty("levelRequirement", src.levelRequirement());

        if(!src.incompatibleSkills().isEmpty()) obj.add("incompatibleSkills", ctx.serialize(src.incompatibleSkills()));
        if(!src.skillPrerequisites().isEmpty()) obj.add("skillPrerequisites", ctx.serialize(src.skillPrerequisites()));

        if (src.parameters != null && !src.parameters().isEmpty()) {
            var customData = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, src.parameters());
            if (customData.isJsonObject()) {
                customData.getAsJsonObject().entrySet().forEach(entry -> obj.add(entry.getKey(), entry.getValue()));
            }
        }

        return obj;
    });

    //endregion

    //region Network
    public static final StreamCodec<FriendlyByteBuf, SkillConfiguration> STREAM_CODEC = StreamCodec.ofMember(
            SkillConfiguration::writeToBuffer,
            SkillConfiguration::readConfigFromBuffer
    );

    public void writeToBuffer(FriendlyByteBuf buffer){
        buffer.writeBoolean(purchasable());
        buffer.writeBoolean(refundable());
        buffer.writeBoolean(visible());

        buffer.writeInt(levelRequirement());

        buffer.writeCollection(incompatibleSkills(), FriendlyByteBuf::writeUtf);
        buffer.writeMap(skillPrerequisites(), FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);

        buffer.writeNbt(parameters());
    }

    public static @NotNull SkillConfiguration readConfigFromBuffer(FriendlyByteBuf buffer){
        var purchasableToRead = buffer.readBoolean();
        Boolean purchasable = purchasableToRead ? null : false;

        var refundableToRead = buffer.readBoolean();
        Boolean refundable = refundableToRead ? null : false;

        var visibleToRead = buffer.readBoolean();
        Boolean visible = visibleToRead ? null : false;

        var intToRead = buffer.readInt();
        Integer levelRequirement =  intToRead <= 0 ? null : intToRead;

        List<String> incompatibleSkills = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readUtf);

        Map<String, Integer> skillPrerequisites = buffer.readMap(HashMap::new, FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt);

        var parametersToRead = buffer.readNbt();
        CompoundTag parameters = parametersToRead.isEmpty() ? null : parametersToRead;

        return new SkillConfiguration(levelRequirement, incompatibleSkills, skillPrerequisites,refundable, purchasable, visible, parameters);
    }
    //endregion
}
