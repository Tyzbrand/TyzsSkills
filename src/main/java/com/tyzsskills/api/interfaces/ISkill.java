package com.tyzsskills.api.interfaces;

import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;

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

    /**
     * @return the modifier affected by the skill as a string
     * NOTE: if the skill isn't an ABILITIES skill, it will be null
     */
    String getModifier();

    /**
     * @return a list of values (1st value in the list is the value used at lvl 1)
     * NOTE: If the skill is ABILITIES and operation isn't ADD_VALUE, values will be treated as percentage
     */
    List<Float> getValues();


    Enums.SkillType getType();


    Enums.CategoryType getCategory();


    AttributeModifier.Operation getModifierOperation();

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

    /**
     * 'unit' refer to the small text next to the value in the GUI's buying tooltip
     * @return a localization key
     */
    String getUnit();


}
