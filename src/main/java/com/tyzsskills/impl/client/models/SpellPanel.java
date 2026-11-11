package com.tyzsskills.impl.client.models;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.integration.ui.models.*;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.network.chat.Component;
import org.joml.Vector2i;

public class SpellPanel extends UIContainer {
    protected final ClientCache cache;

    private final UIContainer scrollPanel = new UIContainer(0, 0);
    private final UIContainer focusPanel = new UIContainer(0, 0);

    private ISpell focusedSpell;

    protected final int SCROLL_ITEM_SPACING = 4;
    protected final int SCROLL_ITEM_PER_LINE = 3;

    protected UIScrollView scrollView;

    private static final UIStyles.ImageStyle SCROLL_BACKGROUND =
            new UIStyles.ImageStyle(226, 139, 0, 139, Styles.MAIN_TEXTURE_BACKGROUNDS);
    private static final UIStyles.ImageStyle FOCUS_POPUP =
            new UIStyles.ImageStyle(120, 85, 226, 0, Styles.MAIN_TEXTURE_BACKGROUNDS);

    private static final UIStyles.BackgroundStyle FOCUS_BLUR =
            new UIStyles.BackgroundStyle(0xA6000000, 0xA6000000, 0, 0);

    private static final UIStyles.ButtonStyle QUIT_BTN =
            new UIStyles.ButtonStyle(13, 13, 202, 248, 215, 248, Styles.MAIN_TEXTURE);

   public SpellPanel(int offsetX, int offsetY){
       super(offsetX, offsetY);

       cache = ClientCache.get();
       focusPanel.withVisibility(() -> focusedSpell != null).withZOffset(10f);
       scrollPanel.withInteraction(() -> focusedSpell == null);
       scrollPanel.addChild(new UIImage(0, 0, () -> SCROLL_BACKGROUND));

       addChild(scrollPanel);
       addChild(focusPanel);

       buildScrollPanel();
   }

    //INIT
    private void buildScrollPanel(){
       scrollView = new UIScrollView(6, 6, 214, 127);
       //TO SEE SCROLL VIEW SHAPE => scrollView.addChild(new UIBackground(0, 0, 214, 200, () -> Styles.DEFAULT_BACKGROUND));

       scrollPanel.addChild(scrollView);
       refreshScrollView();
   }
    private void buildFocusPanel(ISpell spell){
       focusPanel.clear();
       var origin = new Vector2i((SCROLL_BACKGROUND.width() - FOCUS_POPUP.width()) / 2, (SCROLL_BACKGROUND.height() - FOCUS_POPUP.height()) / 2);

        focusPanel.addChild(new UIBackground(0, 0, 226, 139, () -> FOCUS_BLUR)
               .withBorder(() -> false, () -> false));
        focusPanel.addChild(new UIImage(origin.x, origin.y, () -> FOCUS_POPUP));

        var originPanel = new UIContainer(origin.x, origin.y);
        focusPanel.addChild(originPanel);

        originPanel.addChild(new UIButton(100, 5, () -> QUIT_BTN, this::unfocusSpell));

        //TODO: translation
        var message = Component.literal("Properties");
        originPanel.addChild(new UIText(10, 9, () -> message)
                .withBackground(() -> Styles.DEFAULT_BACKGROUND, () -> false));

        var scroll = new UIScrollView(5, 23, 110, 57, false);
        originPanel.addChild(scroll);
        //TO SEE SCROLL AREA => scroll.addChild(new UIBackground(0, 0, 110,200, () -> Styles.DEFAULT_BACKGROUND)
        //        .withBorder(() -> false, () -> false));

        var x = 0; var startY = 2;
        for(var property : spell.getProperties()){
            scroll.addChild(new PropertyCard(2, startY, property, spell));
            startY += PropertyCard.HEIGHT + 2;
        }
    }

    //IMPLEMENTATION
    private void refreshScrollView(){
       scrollView.clear();
       var spells = cache.SPELLS.getAllSpells().values();

       if(spells.isEmpty()){
           //TODO: translation
           var message = Component.literal("-No Spells There-");
           scrollView.addChild(new UIText(scrollView.getRawWidth() / 2, scrollView.getRawHeight() / 2, () -> message)
                   .withBackground(() -> Styles.DEFAULT_BACKGROUND, () -> false)
                   .withAlignment(Enums.TextAlignment.CENTER));
           return;
       }

       var x = 3;
       var y = SCROLL_ITEM_SPACING;
       var rowCount = 0;

       for(var spell : spells){
           if(rowCount >= SCROLL_ITEM_PER_LINE){
               y += SCROLL_ITEM_SPACING + SpellCard.HEIGHT;
               x = 0;
               rowCount = 0;
           }

           x += SCROLL_ITEM_SPACING;
           if(rowCount > 0) x += SpellCard.WIDTH;

           scrollView.addChild(new SpellCard(spell, x, y, () -> focusSpell(spell)));

           rowCount++;
       }
   }
    private boolean focusSpell(ISpell spell){
        buildFocusPanel(spell);
        focusedSpell = spell;

        return true;
   }
   private boolean unfocusSpell(){
        focusedSpell = null;
        return true;
   }

    @Override
    protected boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE && focusedSpell != null) {
            focusedSpell = null;
            return true;
        }
        return super.onKeyPressed(keyCode, scanCode, modifiers);
    }
}
