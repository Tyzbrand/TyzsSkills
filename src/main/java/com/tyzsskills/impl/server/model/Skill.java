package com.tyzsskills.impl.server.model;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Skill implements ISkill {




    public Skill(boolean active, String id, int maximumLevel,
                 List<Integer> prices, List<Float> values, Enums.SkillType type, Enums.CategoryType category,
                 String modifier, AttributeModifier.Operation operation, boolean purchasable,
                 String icon, String displayName, String description, String unit)
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
        this.unit = unit;

        if(category == Enums.CategoryType.ALL || category == Enums.CategoryType.BOOKMARKS) category = Enums.CategoryType.MISC;
    }

    protected transient SkillBehaviour behaviour;

    protected boolean active;
    protected String id;
    protected int maximumLevel;
    protected List<Integer> prices;
    protected List<Float> values;
    protected Enums.SkillType type;
    protected Enums.CategoryType category;
    protected String modifier;
    protected AttributeModifier.Operation operation;
    protected boolean purchasable;

    protected String icon;
    protected String displayName;
    protected String description;
    protected String unit;


    //Getters
    public boolean isSkillActive(){return active;}
    public String getID() {return id;}
    public int getMaximumLevel() {return maximumLevel;}
    public List<Integer> getPrices() {return Collections.unmodifiableList(prices);}
    public String getModifier() {return modifier;}
    public List<Float> getValues() {return Collections.unmodifiableList(values);}
    public Enums.SkillType getType(){return type;}
    public Enums.CategoryType getCategory(){return category;}
    public AttributeModifier.Operation getModifierOperation(){return operation;}
    public boolean isPurchasable(){return purchasable;}
    public String getIcon(){return icon;}
    public String getDisplayName(){return displayName;}
    public String getDescription(){return description;}
    public String getUnit(){return unit;}
    public SkillBehaviour getBehaviour(){return behaviour;}
    public boolean HasBehaviour(){return behaviour != null;}


    //Setters
    public void SetBehaviour(SkillBehaviour behaviour){
        if(behaviour != null) this.behaviour = behaviour;
    }



    //Network
    public static final StreamCodec<FriendlyByteBuf, Skill> STREAM_CODEC = StreamCodec.ofMember(
            (skill, buffer) -> {
                boolean isTrait = skill instanceof Trait;
                buffer.writeBoolean(isTrait);
                skill.WriteToBuffer(buffer);
            },
            (buffer) -> {
                boolean isTrait = buffer.readBoolean();
                if(isTrait) return Trait.ReadTraitFromBuffer(buffer);
                else return Skill.ReadSkillFromBuffer(buffer);
            });


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
        buffer.writeUtf(unit);
    }

    public static @NotNull Skill ReadSkillFromBuffer(FriendlyByteBuf buffer){
        boolean active = buffer.readBoolean();
        String id = buffer.readUtf();
        int maxLevel = buffer.readInt();

        List<Integer> prices = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readInt);
        List<Float> values = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readFloat);

        Enums.SkillType type = buffer.readEnum(Enums.SkillType.class);
        Enums.CategoryType category = buffer.readEnum(Enums.CategoryType.class);

        String modifier = buffer.readUtf();
        AttributeModifier.Operation operation = buffer.readEnum(AttributeModifier.Operation.class);
        boolean purchasable = buffer.readBoolean();

        String icon = buffer.readUtf();
        String displayName = buffer.readUtf();
        String description = buffer.readUtf();
        String unit = buffer.readUtf();

        return new Skill(active, id, maxLevel, prices, values, type, category, modifier, operation, purchasable,
                            icon, displayName, description, unit);
    }
}
