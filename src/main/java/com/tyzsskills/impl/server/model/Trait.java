package com.tyzsskills.impl.server.model;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ITrait;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Trait extends Skill implements ITrait {

    public Trait(boolean active, String id, int powerWeight,int price, boolean purchasable,
                 String icon, String displayName, String description){

        super(active, id, 1, List.of(price) , List.of(), Enums.SkillType.TRAIT,
                Enums.CategoryType.TRAITS, "", AttributeModifier.Operation.ADD_VALUE, purchasable, icon, displayName, description, "");

        this.powerWeight = powerWeight;
    }

    protected int powerWeight;

    public int getPowerWeight(){return powerWeight;}

    @Override
    public void writeToBuffer(FriendlyByteBuf buffer) {
        super.writeToBuffer(buffer);
        buffer.writeInt(powerWeight);
    }

    public static @NotNull Trait readTraitFromBuffer(FriendlyByteBuf buffer) {
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

        int powerWeight = buffer.readInt();

        int price = prices.isEmpty() ? 0 : prices.getFirst();

        return new Trait(active, id, powerWeight, price, purchasable, icon, displayName, description);
    }
}
