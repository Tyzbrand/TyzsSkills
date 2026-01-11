package com.tyzsskills.server.model;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Skill{

    public Skill(boolean active, String id, int maximumLevel,
                 List<Integer> prices, List<Float> values, SkillType type, SkillCategory category,
                 String modifier, AttributeModifier.Operation operation, boolean purchasable,
                 String icon, String displayName, String description)
    {
        this.active = active;
        this.id = id;
        this.maximumLevel = maximumLevel;
        this.prices = new ArrayList<>(prices);
        this.values = new ArrayList<>(values);
        this.type = type;
        this.category = category;
        this.modifier = modifier;
        this.operation = operation;
        this.purchasable = purchasable;

        this.icon = icon;
        this.displayName = displayName;
        this.description = description;
    }

    public enum SkillType {GENERIC, CUSTOM, IMMUTABLE}
    public enum SkillCategory {ABILITIES, FIGHT, MISC, SPECIAL}


    protected boolean active;
    protected String id;
    protected int maximumLevel;
    protected List<Integer> prices;
    protected List<Float> values;
    protected SkillType type;
    protected SkillCategory category;
    protected String modifier;
    protected AttributeModifier.Operation operation;
    protected boolean purchasable;

    protected String icon;
    protected String displayName;
    protected String description;


    public boolean IsSkillActive(){return active;}
    public String GetID() {return id;}
    public int GetMaximumLevel() {return maximumLevel;}
    public List<Integer> GetPrices() {return Collections.unmodifiableList(prices);}
    public String GetModifier() {return modifier;}
    public List<Float> GetValues() {return Collections.unmodifiableList(values);}
    public SkillType GetType(){return type;}
    public SkillCategory GetCategory(){return category;}
    public AttributeModifier.Operation GetModifierOperation(){return operation;}
    public boolean IsPurchasable(){return purchasable;}
    public String GetIcon(){return icon;}
    public String GetDisplayName(){return displayName;}
    public String GetDescription(){return description;}



    //Network
    public static final StreamCodec<FriendlyByteBuf, Skill> STREAM_CODEC = StreamCodec.ofMember(
            Skill::WriteToBuffer,
            Skill::ReadToBuffer);

    public void WriteToBuffer(FriendlyByteBuf buffer){
        buffer.writeBoolean(active);
        buffer.writeUtf(id);
        buffer.writeInt(maximumLevel);

        buffer.writeCollection(prices, FriendlyByteBuf::writeInt);
        buffer.writeCollection(values, FriendlyByteBuf::writeFloat);

        buffer.writeEnum(type);
        buffer.writeEnum(category);

        buffer.writeUtf(modifier);
        buffer.writeEnum(operation);
        buffer.writeBoolean(purchasable);

        buffer.writeUtf(icon);
        buffer.writeUtf(displayName);
        buffer.writeUtf(description);
    }

    public static Skill ReadToBuffer(FriendlyByteBuf buffer){
        boolean active = buffer.readBoolean();
        String id = buffer.readUtf();
        int maxLevel = buffer.readInt();

        List<Integer> prices = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readInt);
        List<Float> values = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readFloat);

        SkillType type = buffer.readEnum(SkillType.class);
        SkillCategory category = buffer.readEnum(SkillCategory.class);

        String modifier = buffer.readUtf();
        AttributeModifier.Operation operation = buffer.readEnum(AttributeModifier.Operation.class);
        boolean purchasable = buffer.readBoolean();

        String icon = buffer.readUtf();
        String displayName = buffer.readUtf();
        String description = buffer.readUtf();

        return new Skill(active, id, maxLevel, prices, values, type, category, modifier, operation, purchasable,
                            icon, displayName, description);
    }
}
