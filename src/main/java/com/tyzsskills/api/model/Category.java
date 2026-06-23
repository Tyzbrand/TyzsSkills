package com.tyzsskills.api.model;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class Category {

    private final String displayName;
    private final String icon;
    private final int order;
    private final transient String  id;


    public Category(@NotNull String displayName, @NotNull String icon, int order, String id){
        this.displayName = displayName;
        this.icon = icon;
        this.order = order;
        this.id = id;
    }

    public Category(@NotNull String displayName, @NotNull String icon, int order){
        this(displayName, icon, order, null);
    }

    @NotNull
    public String displayName(){return this.displayName;}
    @NotNull
    public String icon(){return this.icon;}
    @NotNull
    public String id(){return this.id;}
    public int order(){return this.order;}



    //Network
    public static final StreamCodec<ByteBuf, Category> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, Category::displayName,
            ByteBufCodecs.STRING_UTF8, Category::icon,
            ByteBufCodecs.INT, Category::order,
            ByteBufCodecs.STRING_UTF8, Category::id,
            Category::new
    );
}
