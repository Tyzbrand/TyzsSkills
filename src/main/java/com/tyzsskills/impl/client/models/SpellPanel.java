package com.tyzsskills.impl.client.models;

import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIImage;
import com.tyzsskills.integration.ui.models.UIScrollView;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.network.chat.Component;

public class SpellPanel extends UIContainer {
    protected final ClientCache cache;

    protected final int SCROLL_ITEM_SPACING = 4;
    protected final int SCROLL_ITEM_PER_LINE = 3;

    protected final UIScrollView scrollView;

    private static final UIStyles.ImageStyle BACKGROUND =
            new UIStyles.ImageStyle(226, 139, 0, 139, Styles.MAIN_TEXTURE_BACKGROUNDS);

   public SpellPanel(int offsetX, int offsetY){
        super(offsetX, offsetY);

        cache = ClientCache.get();

        addChild(new UIImage(0, 0, () -> BACKGROUND));

       scrollView = new UIScrollView(6, 6, 214, 127);
       addChild(scrollView);

       //TO SEE SCROLL VIEW SHAPE => scrollView.addChild(new UIBackground(0, 0, 214, 200, () -> Styles.DEFAULT_BACKGROUND));

       refreshScrollView();
   }

   //IMPLEMENTATION
   public void refreshScrollView(){
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

       var x = 0;
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

           scrollView.addChild(new SpellCard(spell, x, y));

           rowCount++;
       }
   }
}
