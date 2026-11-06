package com.tyzsskills.impl.client.screen;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.impl.client.key.MainKeybind;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.integration.ui.models.UIButton;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIImage;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.records.UIStyles;
import com.tyzsskills.integration.ui.records.UITexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

public class SpellWheel extends Screen {
    public static final int WIDTH = 100, HEIGHT = 100;

    private static final int DEADZONE_HALF_SIZE = 25;
    private static final int WHEEL_RADIUS = 65;
    private static final int SLOT_SIZE = 46;
    private static final int PANEL_CENTER_X = WIDTH / 2;
    private static final int PANEL_CENTER_Y = HEIGHT / 2;

    protected final UIContainer mainPanel;
    protected final ClientCache cache;
    private final Screen parentScreen;

    private int leftPos;
    private int topPos;

    private final Enums.WheelMode currentMode;
    private final ISpell currentSpell;
    private final int slotCount;

    private int hoveredSlot = -1;

    //region STYLES
    private static final UIStyles.ButtonStyle CADRE_CAST_DEFAULT =
            new UIStyles.ButtonStyle(SLOT_SIZE, SLOT_SIZE, 48, 332, 1, 285, Styles.MAIN_TEXTURE);

    private static final UIStyles.ButtonStyle CADRE_ASSIGN_DEFAULT =
            new UIStyles.ButtonStyle(SLOT_SIZE, SLOT_SIZE, 48, 332, 1, 332, Styles.MAIN_TEXTURE);


    private static final UIStyles.ImageStyle CROSS =
            new UIStyles.ImageStyle(11, 11, 40, 226, Styles.MAIN_TEXTURE);
    //endregion

    public SpellWheel(Enums.WheelMode mode, ISpell spell) {
        //TODO: translation
        super(Component.literal("Spell Wheel"));

        this.parentScreen = Minecraft.getInstance().screen;

        var player = Minecraft.getInstance().player;
        slotCount = player == null ? 0 : (int)player.getAttributeValue(AttributeRegistry.MAX_SPELL_SLOTS);

        cache = ClientCache.get();
        currentMode = mode;
        currentSpell = spell;

        mainPanel = new UIContainer(0, 0);
    }

    @Override
    protected void init() {
        super.init();

        leftPos = (width - WIDTH) / 2;
        topPos = (height - HEIGHT) / 2;

        mainPanel.clear();
        mainPanel.updatePosition(leftPos, topPos);

        buildWheel();
    }


    //IMPLEMENTATION
    private void buildWheel() {
        for (int i = 1; i <= slotCount; i++) {
            var slotPos = getSlotPos(i);
            final var currentSlot = i;

            var spellInSlot = cache.SPELLS.getSpellInSlot(i);

            if(currentMode == Enums.WheelMode.CAST){
                mainPanel.addChild(new UIButton(slotPos.x, slotPos.y, () ->  CADRE_CAST_DEFAULT, () -> handleCast(spellInSlot, currentSlot))
                        .withCustomHover((mx, my) -> hoveredSlot == currentSlot)
                        .withShade(() -> hasActiveCooldown(spellInSlot)));

                mainPanel.addChild(new UIText(slotPos.x + SLOT_SIZE/2, slotPos.y + SLOT_SIZE - 11, () -> getCooldownTimer(spellInSlot), false)
                        .withAlignment(Enums.TextAlignment.CENTER)
                        .withVisibility(() -> hasActiveCooldown(spellInSlot)));
            }
            else{
                mainPanel.addChild(new UIButton(slotPos.x, slotPos.y, () ->  CADRE_ASSIGN_DEFAULT, () -> handleAssignation(currentSlot)));
            }

            if(spellInSlot == null) continue;

            var icon = ResourceLocation.tryParse(spellInSlot.getIcon());
            var iconTexture = icon == null ? Styles.DEFAULT_ICON : new UITexture(icon, 16);

            mainPanel.addChild(new UIImage(slotPos.x + 7, slotPos.y + 7, iconTexture.size(), iconTexture.size(), () -> iconTexture)
                    .withScale(2f, Enums.ScalePivot.TOP_LEFT)
                    .withShade(() -> currentMode == Enums.WheelMode.CAST && hasActiveCooldown(spellInSlot)));
        }

        if(currentMode == Enums.WheelMode.CAST){
            int crossSize = CROSS.width();
            int crossX = PANEL_CENTER_X - (crossSize / 2);
            int crossY = PANEL_CENTER_Y - (crossSize / 2);

            mainPanel.addChild(new UIImage(crossX, crossY, () -> CROSS)
                    .withVisibility(() -> hoveredSlot <= 0)
                    .withScale(2f, Enums.ScalePivot.CENTER));
        }
    }

    //PARENTS
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(gui, mouseX, mouseY, partialTick);

        mainPanel.draw(gui, mouseX, mouseY, partialTick);
        if(currentMode == Enums.WheelMode.CAST) hoveredSlot = getHoveredSlot(mouseX, mouseY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (currentMode == Enums.WheelMode.CAST && MainKeybind.OPEN_SPELL_WHEEL.matches(keyCode, scanCode)) {
            if (hoveredSlot > 0) {
                var spellInSlot = cache.SPELLS.getSpellInSlot(hoveredSlot);
                if (spellInSlot != null) cache.SPELLS.triggerAction(spellInSlot, Enums.SpellAction.CAST, hoveredSlot);

            }
            this.onClose();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mX = (int) mouseX;
        int mY = (int) mouseY;

        if(button != 0) return false;
        if(mainPanel.handleClick(mX, mY)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        if (currentMode == Enums.WheelMode.ASSIGN && parentScreen != null) Minecraft.getInstance().setScreen(parentScreen);
        else super.onClose();
    }

    //region EXTRACTED LOGIC
    // ==================== CLICKS ====================
    private boolean handleAssignation(int slot){
         if(currentSpell == null) return false;
         if(cache.SPELLS.triggerAction(currentSpell, Enums.SpellAction.ASSIGNMENT, slot)) {
             this.onClose();
             return true;
         }
         return false;
    }
    private boolean handleCast(ISpell spell, int slot){
        if(spell == null) return false;
        if(cache.SPELLS.triggerAction(spell, Enums.SpellAction.CAST, slot)) {
            this.onClose();
            return true;
        }
        return false;
    }

    // ==================== CONDITIONS ====================
    private boolean hasActiveCooldown(@Nullable ISpell spell){
        if(spell == null) return false;
        return cache.SPELLS.getSpellCooldown(spell.getID()) != null;
    }

    // ==================== ASSETS ====================
    private MutableComponent getCooldownTimer(@Nullable ISpell spell){
        if(spell == null) return Component.empty();

        var cooldown = cache.SPELLS.getSpellCooldown(spell.getID());
        var level = Minecraft.getInstance().level;

        if(cooldown == null || level == null) return Component.empty();

        return Component.literal(cooldown.getFormatedTimer(level.getGameTime()));
    }
    //endregion

    //UTILS
    private Vector2i getSlotPos(int slot){
        var sliceAngle = 2 * Math.PI / slotCount;
        var angle = (slot - 1) * sliceAngle;

        var slotCenterX = PANEL_CENTER_X + (int)Math.round(Math.sin(angle) * WHEEL_RADIUS);
        var slotCenterY = PANEL_CENTER_Y - (int)Math.round(Math.cos(angle) * WHEEL_RADIUS);

        return new Vector2i(slotCenterX - SLOT_SIZE / 2, slotCenterY - SLOT_SIZE / 2);
    }
    private int getHoveredSlot(int mouseX, int mouseY){
        if(slotCount <= 0) return -1;

        var dx = mouseX - this.width / 2;
        var dy = mouseY - this.height / 2;

        if(Math.abs(dx) <= DEADZONE_HALF_SIZE && Math.abs(dy) <= DEADZONE_HALF_SIZE) return -1;

        var angle = Math.atan2(dx, -dy);
        var sliceAngle = 2 * Math.PI / slotCount;
        var normalizedAngle = angle + sliceAngle/2;

        if(normalizedAngle < 0) normalizedAngle += 2* Math.PI;
        if(normalizedAngle >= 2 * Math.PI) normalizedAngle -= 2 * Math.PI;

        return (int)(normalizedAngle/sliceAngle) + 1;
    }
}