package com.tyzsskills.api.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.mojang.serialization.JsonOps;
import com.tyzsskills.api.records.Category;
import com.tyzsskills.api.records.STag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class SkillConfiguration {
    //Fluent
    public SkillConfiguration withRefundable(boolean refundable){this.refundable = refundable; return this;}
    public SkillConfiguration withPurchasable(boolean purchasable){this.purchasable = purchasable; return this;}
    public SkillConfiguration withVisible(boolean visible){this.visible = visible; return this;}
    public SkillConfiguration withPermanent(boolean permanent){this.permanent = permanent; return this;}
    public SkillConfiguration withLevelRequirement(int levelRequirement){this.levelRequirement = levelRequirement; return this;}
    public SkillConfiguration withParameters(@NotNull CompoundTag parameters){this.parameters = parameters; return this;}

    public SkillConfiguration withIncompatibilities(@NotNull List<String> incompatibilities){
        this.incompatibleSkills = Collections.unmodifiableList(incompatibilities);return this;}

    public SkillConfiguration withPrerequisites(@NotNull Map<String, Integer> prerequisites){
        this.skillPrerequisites = Collections.unmodifiableMap(prerequisites);return this;}

    public SkillConfiguration withTags(@NotNull List<STag> tags){
        this.tags = Collections.unmodifiableList(tags);return this;}


    //Booleans
    private Boolean refundable = null;
    public boolean refundable(){return refundable == null || refundable;}

    private Boolean purchasable = null;
    public boolean purchasable(){return purchasable == null || purchasable;}

    private Boolean visible = null;
    public boolean visible(){return visible == null || visible;}

    private Boolean permanent = null;
    public boolean permanent(){return permanent != null && permanent;}


    //Ints
    private Integer levelRequirement = null;
    public int levelRequirement(){return levelRequirement == null ? 0 : levelRequirement;}

    private @UnmodifiableView List<String> incompatibleSkills = null;
    public @NotNull @UnmodifiableView List<String> incompatibleSkills(){return incompatibleSkills == null ? Collections.emptyList() :  incompatibleSkills;}


    private @UnmodifiableView Map<String, Integer> skillPrerequisites;
    public @NotNull @UnmodifiableView Map<String, Integer> skillPrerequisites(){return skillPrerequisites == null ? Map.of() :  skillPrerequisites;}

    //Specific Parameters
    private CompoundTag parameters = null;
    public @NotNull CompoundTag parameters(){return parameters;}

    //Tags
    private @UnmodifiableView List<STag> tags = null;
    public @NotNull @UnmodifiableView List<STag> tags(){return tags == null ? List.of() : tags;}

    //region Load/Write/Read
    public static final JsonSerializer<SkillConfiguration> CONFIG_SERIALIZER = ((src, typeOfSrc, ctx) -> {
        var obj = new JsonObject();
        if(!src.purchasable()) obj.addProperty("purchasable", false);
        if(!src.refundable()) obj.addProperty("refundable", false);
        if(!src.visible()) obj.addProperty("visible", false);
        if(src.permanent()) obj.addProperty("permanent", true);

        if(src.levelRequirement() > 0) obj.addProperty("levelRequirement", src.levelRequirement());

        if(!src.incompatibleSkills().isEmpty()) obj.add("incompatibleSkills", ctx.serialize(src.incompatibleSkills()));
        if(!src.skillPrerequisites().isEmpty()) obj.add("skillPrerequisites", ctx.serialize(src.skillPrerequisites()));

        if(!src.tags().isEmpty()){
            var tags = new JsonArray();
            for(var tag : src.tags) tags.add(tag.id());
            obj.add("tags", tags);
        };

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
        buffer.writeBoolean(permanent());

        buffer.writeInt(levelRequirement());

        buffer.writeCollection(incompatibleSkills(), FriendlyByteBuf::writeUtf);
        buffer.writeMap(skillPrerequisites(), FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);

        buffer.writeCollection(tags(), STag.STREAM_CODEC);

        buffer.writeNbt(parameters());
    }

    public static @NotNull SkillConfiguration readConfigFromBuffer(FriendlyByteBuf buffer){
        var skillConfig = new SkillConfiguration();

        var purchasableToRead = buffer.readBoolean();
        skillConfig.withPurchasable(purchasableToRead);

        var refundableToRead = buffer.readBoolean();
        skillConfig.withRefundable(refundableToRead);

        var visibleToRead = buffer.readBoolean();
        skillConfig.withVisible(visibleToRead);

        var permanentToRead = buffer.readBoolean();
        skillConfig.withPermanent(permanentToRead);

        var intToRead = buffer.readInt();
        if(intToRead > 0) skillConfig.withLevelRequirement(intToRead);

        List<String> incompatibleSkills = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readUtf);
        if(!incompatibleSkills.isEmpty()) skillConfig.withIncompatibilities(incompatibleSkills);

        Map<String, Integer> skillPrerequisites = buffer.readMap(HashMap::new, FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt);
        if(!skillPrerequisites.isEmpty()) skillConfig.withPrerequisites(skillPrerequisites);

        List<STag> tags = buffer.readCollection(ArrayList::new, STag.STREAM_CODEC);
        if(!tags.isEmpty()) skillConfig.withTags(tags);

        var parametersToRead = buffer.readNbt();
        if(parametersToRead != null && !parametersToRead.isEmpty()) skillConfig.withParameters(parametersToRead);

        return skillConfig;
    }
    //endregion
}
