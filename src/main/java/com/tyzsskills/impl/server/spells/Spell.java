package com.tyzsskills.impl.server.spells;

import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.records.SpellProperty;
import com.tyzsskills.api.records.PropertyContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Spell implements ISpell {
    public Spell(boolean active, String id, int price, String icon, String displayName, String description, List<SpellProperty> properties){
        this.active = active;
        this.id = id;
        this.price = price;

        this.icon = icon;
        this.displayName = displayName;
        this.description = description;

        for(var property : properties) this.properties.put(property.key(), property);
    }


    protected boolean active;
    protected String id;
    protected int price;

    protected String icon;
    protected String displayName;
    protected String description;

    protected final Map<String, SpellProperty> properties = new HashMap<>();

    @Override
    public @NotNull String getID() {return id;}
    @Override
    public @NotNull String getIcon() {return icon;}
    @Override
    public @NotNull String getDisplayName() {return displayName;}
    @Override
    public @NotNull String getDescription() {return description;}
    @Override
    public @Nullable SpellProperty getProperty(@NotNull String propertyKey) {return properties.getOrDefault(propertyKey, null);}
    @Override
    public @NotNull @Unmodifiable List<SpellProperty> getProperties(){
        return List.copyOf(properties.values());
    }
    @Override
    public int getPrice() {return price;}

    //Checks
    @Override
    public boolean canBuyProperty(@NotNull PropertyContext ctx, boolean purchaseEnabled){
        if(!purchaseEnabled) return false;

        var property = getProperty(ctx.propertyKey());
        if(property == null) return  false;

        if(ctx.propertyLevel() >= property.getMaximumLevel()) return false;

        var price = property.getPrice(ctx.propertyLevel() + 1);
        return price <= ctx.playerSP();
    }

    @Override
    public boolean canBuySpell(int playerSp, boolean purchaseEnabled) {
        if(!purchaseEnabled) return false;
        return price <= playerSp;
    }

    //Network
    public static final StreamCodec<FriendlyByteBuf, Spell> STREAM_CODEC = StreamCodec.ofMember(
            Spell::writeToBuffer,
            Spell::readSpellFromBuffer
    );


    public void writeToBuffer(FriendlyByteBuf buffer){
        buffer.writeBoolean(active);
        buffer.writeUtf(id);
        buffer.writeInt(price);

        buffer.writeUtf(icon);
        buffer.writeUtf(displayName);
        buffer.writeUtf(description);

        buffer.writeCollection(properties.values(), (buf, spellProperty) -> spellProperty.writeToBuffer(buf));
    }

    public static @NotNull Spell readSpellFromBuffer(FriendlyByteBuf buffer){
        boolean active = buffer.readBoolean();
        String id = buffer.readUtf();
        int price = buffer.readInt();

        String icon = buffer.readUtf();
        String displayName = buffer.readUtf();
        String description = buffer.readUtf();

        List<SpellProperty> readProperties = buffer.readCollection(ArrayList::new, SpellProperty::readFromBuffer);

        return new Spell(active, id, price, icon, displayName, description, readProperties);
    }
}
