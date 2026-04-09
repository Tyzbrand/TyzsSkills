package com.tyzsskills.impl.server.model;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.IModifier;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.interfaces.IValueSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class Skill implements ISkill {


    public Skill(boolean active, String id, int maximumLevel,
                 List<Integer> prices, Enums.SkillType type, Enums.CategoryType category, boolean purchasable,
                 String icon, String displayName, String description, List<Modifier> modifiers, Map<String, ValueSet> customValues)
    {
        this.active = active;
        this.id = id;
        this.maximumLevel = maximumLevel;
        this.prices = prices != null ?  new ArrayList<>(prices) : new ArrayList<>();
        this.type = type;
        this.category = category;
        this.purchasable = purchasable;

        this.icon = icon;
        this.displayName = displayName;
        this.description = description;

        this.modifiers = modifiers != null? new ArrayList<>(modifiers) : new ArrayList<>();

        this.customValues = customValues != null ? new HashMap<>(customValues) : new HashMap<>();

        if(category == Enums.CategoryType.ALL || category == Enums.CategoryType.BOOKMARKS) this.category = Enums.CategoryType.MISC;
    }

    protected transient SkillBehavior behaviour;

    //Common data -----------------------
    protected boolean active;
    protected String id;
    protected int maximumLevel;
    protected List<Integer> prices;
    protected Enums.SkillType type;
    protected Enums.CategoryType category;
    protected boolean purchasable;

    //Visual -----------------------
    protected String icon;
    protected String displayName;
    protected String description;

    //Generic
    protected List<Modifier> modifiers;

    //Immutables and traits
    protected Map<String, ValueSet> customValues;


    //Getters
    public boolean isSkillActive(){return active;}
    @Override
    public String getID() {return id;}
    @Override
    public int getMaximumLevel() {return maximumLevel;}
    @Override
    public List<Integer> getPrices() {return Collections.unmodifiableList(prices);}
    @Override
    public Enums.SkillType getType(){return type;}
    @Override
    public Enums.CategoryType getCategory(){return category;}
    @Override
    public boolean isPurchasable(){return purchasable;}
    @Override
    public String getIcon(){return icon;}
    @Override
    public String getDisplayName(){return displayName;}
    @Override
    public String getDescription(){return description;}
    @Override
    public Map<String, IValueSet> getValues(){return Map.copyOf(customValues);}
    @Override
    public IValueSet getValueSet(String key){return customValues.getOrDefault(key, null);}
    @Override
    public List<IModifier> getModifiers(){return List.copyOf(modifiers);}



    public SkillBehavior getBehavior(){return behaviour;}
    public boolean hasBehaviour(){return behaviour != null;}

    //Setters
    public void setBehaviour(SkillBehavior behaviour){
        if(behaviour != null) this.behaviour = behaviour;
    }



    //Network
    public static final StreamCodec<FriendlyByteBuf, Skill> STREAM_CODEC = StreamCodec.ofMember(
            (skill, buffer) -> {
                boolean isTrait = skill instanceof Trait;
                buffer.writeBoolean(isTrait);
                skill.writeToBuffer(buffer);
            },
            (buffer) -> {
                boolean isTrait = buffer.readBoolean();
                if(isTrait) return Trait.readTraitFromBuffer(buffer);
                else return Skill.readSkillFromBuffer(buffer);
            });


    public void writeToBuffer(FriendlyByteBuf buffer){
        buffer.writeBoolean(active);
        buffer.writeUtf(id);
        buffer.writeInt(maximumLevel);

        buffer.writeCollection(prices, FriendlyByteBuf::writeInt);

        buffer.writeEnum(type);
        buffer.writeEnum(category);

        buffer.writeBoolean(purchasable);

        buffer.writeUtf(icon);
        buffer.writeUtf(displayName);
        buffer.writeUtf(description);

        buffer.writeCollection(modifiers, (buf, modifier) ->  modifier.writeToBuffer(buf));
        buffer.writeMap(customValues, FriendlyByteBuf::writeUtf, (buf, valueSet) ->  valueSet.writeToBuffer(buf));
    }

    public static @NotNull Skill readSkillFromBuffer(FriendlyByteBuf buffer){
        boolean active = buffer.readBoolean();
        String id = buffer.readUtf();
        int maxLevel = buffer.readInt();

        List<Integer> prices = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readInt);

        Enums.SkillType type = buffer.readEnum(Enums.SkillType.class);
        Enums.CategoryType category = buffer.readEnum(Enums.CategoryType.class);

        boolean purchasable = buffer.readBoolean();

        String icon = buffer.readUtf();
        String displayName = buffer.readUtf();
        String description = buffer.readUtf();

        List<Modifier> readModifiers = buffer.readCollection(ArrayList::new, Modifier::readFromBuffer);
        Map<String, ValueSet> readCustomValues = buffer.readMap(FriendlyByteBuf::readUtf, ValueSet::readFromBuffer);

        return new Skill(active, id, maxLevel, prices, type, category, purchasable,
                            icon, displayName, description, readModifiers, readCustomValues);
    }
}
