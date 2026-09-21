package com.tyzsskills.impl.server.skills;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillConfiguration;
import com.tyzsskills.api.records.Modifier;
import com.tyzsskills.api.records.ValueSet;
import com.tyzsskills.api.model.SkillBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;


public class Skill implements ISkill {


    public Skill(boolean active, String id,
                 List<Integer> prices, Enums.SkillType type, String category,
                 String icon, String displayName, String description, List<Modifier> modifiers, Map<String, ValueSet> customValues,
                 SkillConfiguration config)
    {
        this.active = active;
        this.id = id;
        this.type = type;
        this.category = category;

        this.icon = icon;
        this.displayName = displayName;
        this.description = description;

        this.config = config != null ? config : new SkillConfiguration();

        this.prices = prices != null ?  new ArrayList<>(prices) : new ArrayList<>();
        this.pricesView = Collections.unmodifiableList(this.prices);

        this.modifiers = modifiers != null? new ArrayList<>(modifiers) : new ArrayList<>();
        this.modifiersView = Collections.unmodifiableList(this.modifiers);

        this.customValues = customValues != null ? new HashMap<>(customValues) : new HashMap<>();
        this.customValuesView = Collections.unmodifiableMap(this.customValues);

        maximumLevel = this.prices.size();
    }

    protected transient SkillBehavior behaviour;

    //Common data -----------------------
    protected boolean active;
    protected final String id;
    protected final transient int maximumLevel;
    protected final Enums.SkillType type;
    protected final String category;

    //Visual -----------------------
    protected final String icon;
    protected final String displayName;
    protected final String description;

    //Config
    protected SkillConfiguration config;

    //Collections
    protected List<Integer> prices;
    protected final transient List<Integer> pricesView;

    protected List<Modifier> modifiers;
    protected final transient List<Modifier> modifiersView;

    protected Map<String, ValueSet> customValues;
    protected final transient Map<String, ValueSet> customValuesView;


    //Getters
    public boolean isSkillActive(){return active;}
    @Override
    public @NotNull String getID() {return id;}
    @Override
    public int getMaximumLevel() {return maximumLevel;}
    @Override
    public @NotNull @UnmodifiableView List<Integer> getPrices() {return pricesView;}
    @Override
    public int getPrice(int lvl) {
        if(lvl <= 0 || lvl > prices.size()) return Integer.MAX_VALUE;
        return prices.get(lvl - 1);
    }
    @Override
    public Enums.SkillType getType(){return type;}
    @Override
    public String getCategory(){return category;}
    @Override
    public boolean isPurchasable(){return config.purchasable();}
    @Override
    public boolean isRefundable(){return config.refundable();}
    @Override
    public boolean isVisible(){return config.visible();}
    @Override
    public @NotNull String getIcon(){return icon;}
    @Override
    public @NotNull String getDisplayName(){return displayName;}
    @Override
    public @NotNull String getDescription(){return description;}
    @Override
    public @NotNull @UnmodifiableView Map<String, ValueSet> getValues(){return customValuesView;}
    @Override
    public @Nullable ValueSet getValueSet(@NotNull String key){return customValues.getOrDefault(key, null);}
    @Override
    public @NotNull @UnmodifiableView List<Modifier> getModifiers(){return modifiersView;}
    @Override
    public int getRequiredLevel() {return config.levelRequirement();}
    @Override
    public @NotNull @UnmodifiableView List<String> getRawIncompatibilities() {return config.incompatibleSkills();}
    @Override
    public @NotNull @UnmodifiableView Map<String, Integer> getRawPrerequisites() {return config.skillPrerequisites();}
    @Override
    public @NotNull CompoundTag getSpecificParameters() {
        return config.parameters();
    }


    public @Nullable SkillBehavior getBehavior(){return behaviour;}
    public boolean hasBehaviour(){return behaviour != null;}


    //Setters
    public void setBehaviour(@NotNull SkillBehavior behaviour){
        this.behaviour = behaviour;
    }


    //region GSON
    public static final JsonSerializer<Skill> SKILL_SERIALIZER = (src, typeOfSrc, ctx) -> {
        var json = new JsonObject();

        json.addProperty("active", src.active);
        json.addProperty("id", src.id);
        json.addProperty("type", src.type.name());
        json.addProperty("category", src.category);
        json.addProperty("icon", src.icon);
        json.addProperty("displayName", src.displayName);
        json.addProperty("description", src.description);

        if (src.config != null) {
            var configJson = ctx.serialize(src.config);
            if (configJson.isJsonObject() && !configJson.getAsJsonObject().isEmpty()) {
                json.add("config", configJson);
            }
        }

        json.add("prices", ctx.serialize(src.prices));

        if (!src.modifiers.isEmpty()) json.add("modifiers", ctx.serialize(src.modifiers));
        if (!src.customValues.isEmpty()) json.add("customValues", ctx.serialize(src.customValues));

        return json;
    };
    //endregion

    //region Network
    public static final StreamCodec<FriendlyByteBuf, Skill> STREAM_CODEC = StreamCodec.ofMember(
            Skill::writeToBuffer,
            Skill::readSkillFromBuffer
    );


    public void writeToBuffer(FriendlyByteBuf buffer){
        buffer.writeBoolean(active);
        buffer.writeUtf(id);

        buffer.writeCollection(prices, FriendlyByteBuf::writeInt);

        buffer.writeEnum(type);
        buffer.writeUtf(category);

        buffer.writeUtf(icon);
        buffer.writeUtf(displayName);
        buffer.writeUtf(description);

        buffer.writeCollection(modifiers, (buf, modifier) ->  modifier.writeToBuffer(buf));
        buffer.writeMap(customValues, FriendlyByteBuf::writeUtf, (buf, valueSet) ->  valueSet.writeToBuffer(buf));

        this.config.writeToBuffer(buffer);
    }

    public static @NotNull Skill readSkillFromBuffer(FriendlyByteBuf buffer){
        boolean active = buffer.readBoolean();
        String id = buffer.readUtf();

        List<Integer> prices = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readInt);

        Enums.SkillType type = buffer.readEnum(Enums.SkillType.class);
        String category = buffer.readUtf();

        String icon = buffer.readUtf();
        String displayName = buffer.readUtf();
        String description = buffer.readUtf();

        List<Modifier> readModifiers = buffer.readCollection(ArrayList::new, Modifier::readFromBuffer);
        Map<String, ValueSet> readCustomValues = buffer.readMap(FriendlyByteBuf::readUtf, ValueSet::readFromBuffer);

        SkillConfiguration config = SkillConfiguration.STREAM_CODEC.decode(buffer);

        return new Skill(active, id, prices, type, category,
                            icon, displayName, description, readModifiers, readCustomValues, config);
    }

    //endregion
}
