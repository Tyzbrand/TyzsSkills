package com.tyzsskills.impl.client.screen;

import com.tyzsskills.impl.client.ui.UIBackground;
import com.tyzsskills.impl.client.ui.UIContainer;
import com.tyzsskills.impl.client.ui.UIScrollView;
import com.tyzsskills.impl.client.ui.UIStyleRegistries;
import oshi.util.tuples.Pair;

public class SkillDetails {

    private final UIContainer mainPanel;
    private final UIScrollView scrollView;


    public SkillDetails(){
        mainPanel = new UIContainer(0, 0, 0, 0);
        scrollView = new UIScrollView(267, 5, 80, 129);
        //TO SEE SCROLL CONTAINER   scrollView.addChild(new UIBackground(0, 0, 80, 129, UIStyleRegistries.COLOR_BORDER_MAXED));

        mainPanel.addChild(scrollView);
    }

    public void update(){
        mainPanel.clear();

        mainPanel.addChild(new UIBackground(1, 1, 38, 38, UIStyleRegistries.COLOR_BG)
                .withBorder(() -> new Pair<>(true, UIStyleRegistries.COLOR_BORDER)));
    }

    public UIContainer getPanel(){update(); return mainPanel;}
}
