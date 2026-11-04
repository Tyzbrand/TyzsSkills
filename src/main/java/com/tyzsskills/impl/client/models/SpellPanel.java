package com.tyzsskills.impl.client.models;

import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIScrollView;
import com.tyzsskills.integration.ui.models.UIText;
import net.minecraft.network.chat.Component;

public class SpellPanel extends UIContainer {
    protected final ClientCache cache;

    protected final int SCROLL_ITEM_SPACING = 3;
    protected final int SCROLL_ITEM_PER_LINE = 5;

    protected final UIScrollView scrollView;

   public SpellPanel(int offsetX, int offsetY){
    super(offsetX, offsetY);

    cache = ClientCache.get();

       scrollView = new UIScrollView(98, 25, 214, 108);
       addChild(scrollView);

       //TO SEE SCROLL VIEW SHAPE => scrollView.addChild(new UIBackground(0, 0, 214, 150, () -> Styles.DEFAULT_BACKGROUND));

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
