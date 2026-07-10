package com.tyzsskills.impl.server.skills;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillConfiguration;
import com.tyzsskills.api.records.PlayerContext;
import com.tyzsskills.api.records.BulkPurchaseResult;
import com.tyzsskills.api.records.Modifier;
import com.tyzsskills.api.records.ValueSet;
import com.tyzsskills.api.model.SkillBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class Skill implements ISkill {


    public Skill(boolean active, String id, int maximumLevel,
                 List<Integer> prices, Enums.SkillType type, String category,
                 String icon, String displayName, String description, List<Modifier> modifiers, Map<String, ValueSet> customValues,
                 SkillConfiguration config)
    {
        this.active = active;
        this.id = id;
        this.maximumLevel = maximumLevel;
        this.prices = prices != null ?  new ArrayList<>(prices) : new ArrayList<>();
        this.type = type;
        this.category = category;

        this.icon = icon;
        this.displayName = displayName;
        this.description = description;

        this.modifiers = modifiers != null? new ArrayList<>(modifiers) : new ArrayList<>();

        this.customValues = customValues != null ? new HashMap<>(customValues) : new HashMap<>();

        this.config = config != null ? config : new SkillConfiguration();
    }

    protected transient SkillBehavior behaviour;

    //Common data -----------------------
    protected boolean active;
    protected String id;
    protected int maximumLevel;
    protected List<Integer> prices;
    protected Enums.SkillType type;
    protected String category;

    //Visual -----------------------
    protected String icon;
    protected String displayName;
    protected String description;

    //Generic
    protected List<Modifier> modifiers;

    //Immutable
    protected Map<String, ValueSet> customValues;

    //Config
    protected SkillConfiguration config;


    //Getters
    public boolean isSkillActive(){return active;}
    @Override
    public @NotNull String getID() {return id;}
    @Override
    public int getMaximumLevel() {return maximumLevel;}
    @Override
    public @NotNull List<Integer> getPrices() {return Collections.unmodifiableList(prices);}
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
    public @NotNull Map<String, ValueSet> getValues(){return Map.copyOf(customValues);}
    @Override
    public @NotNull ValueSet getValueSet(@NotNull String key){return customValues.getOrDefault(key, null);}
    @Override
    public @NotNull List<Modifier> getModifiers(){return List.copyOf(modifiers);}
    @Override
    public int getRequiredLevel() {return config.levelRequirement();}
    @Override
    public @NotNull List<String> getRawIncompatibilities() {return config.incompatibleSkills();}
    @Override
    public @NotNull Map<String, Integer> getRawPrerequisites() {return config.skillPrerequisites();}
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


    //Network
    public static final StreamCodec<FriendlyByteBuf, Skill> STREAM_CODEC = StreamCodec.ofMember(
            Skill::writeToBuffer,
            Skill::readSkillFromBuffer
    );


    public void writeToBuffer(FriendlyByteBuf buffer){
        buffer.writeBoolean(active);
        buffer.writeUtf(id);
        buffer.writeInt(maximumLevel);

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
        int maxLevel = buffer.readInt();

        List<Integer> prices = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readInt);

        Enums.SkillType type = buffer.readEnum(Enums.SkillType.class);
        String category = buffer.readUtf();

        String icon = buffer.readUtf();
        String displayName = buffer.readUtf();
        String description = buffer.readUtf();

        List<Modifier> readModifiers = buffer.readCollection(ArrayList::new, Modifier::readFromBuffer);
        Map<String, ValueSet> readCustomValues = buffer.readMap(FriendlyByteBuf::readUtf, ValueSet::readFromBuffer);

        SkillConfiguration config = SkillConfiguration.STREAM_CODEC.decode(buffer);

        return new Skill(active, id, maxLevel, prices, type, category,
                            icon, displayName, description, readModifiers, readCustomValues, config);
    }
}
