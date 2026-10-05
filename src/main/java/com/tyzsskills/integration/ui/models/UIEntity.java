package com.tyzsskills.integration.ui.models;

import com.tyzsskills.api.Enums;
import com.tyzsskills.integration.ui.UIElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class UIEntity extends UIElement {
    private final Supplier<LivingEntity> entityExtractor;
    private final int entityScale;


    public UIEntity(int offsetX, int offsetY, int width, int height, Supplier<LivingEntity> entityExtractor, int entityScale){
        super(offsetX, offsetY, width, height);

        this.entityExtractor = entityExtractor;
        this.entityScale = entityScale;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        var entity = entityExtractor.get();
        if(entity == null) return;

        float renderX = this.x + (this.width / 2.0f);
        float renderY = this.y + this.height;

        float eyeY = renderY - (entityScale * 1.6f);
        float mouseXOffset = renderX - mouseX;
        float mouseYOffset = eyeY - mouseY;

        var f = (float)Math.atan((double)mouseXOffset / 40f);
        var f1 = (float)Math.atan((double)mouseYOffset / 40f);

        var quatF = (new Quaternionf()).rotateZ((float)Math.PI);
        var quatF1 = (new Quaternionf()).rotateX(f1 * 20f * (float)Math.PI/180f);
        quatF.mul(quatF1);

        var f2 = entity.yBodyRot;
        var f3 = entity.getYRot();
        var f4 = entity.getXRot();
        var f5 = entity.yHeadRotO;
        var f6 = entity.yHeadRot;

        entity.yBodyRot = 180f + f * 20f;
        entity.setYRot(180f + f * 40f);
        entity.setXRot(-f1 * 20f);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();

        InventoryScreen.renderEntityInInventory(gui, renderX, renderY, entityScale, new Vector3f(), quatF, null, entity);

        entity.yBodyRot = f2;
        entity.setYRot(f3);
        entity.setXRot(f4);
        entity.yHeadRot = f5;
        entity.yHeadRotO = f6;
    }

    @Override
    public UIElement withScale(float scale, Enums.ScalePivot scalePivot) {return this;}
    @Override
    public UIElement withShade(Supplier<Boolean> supplier) {return this;}
}
