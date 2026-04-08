package com.tyzsskills.api.interfaces;

import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.server.model.Modifier;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;
import java.util.Map;

/**
 * ReadOnly interface used to access skill information
 */
public interface ISkill {

    String getID();

    int getMaximumLevel();

    /**
     * @return a list of prices (1st price in the list is the price from lvl 0 to lvl 1)
     */
    List<Integer> getPrices();



    Enums.SkillType getType();


    Enums.CategoryType getCategory();


    boolean isPurchasable();

    /**
     * @return a resourceLocation path as a string
     */
    String getIcon();

    /**
     * @return a localization key
     */
    String getDisplayName();

    /**
     * @return a localization key
     */
    String getDescription();

    Map<String, IValueSet> getValues();

    List<IModifier> getModifiers();



}
