package com.tyzsskills.api.interfaces;

import com.tyzsskills.api.records.SortType;
import org.jetbrains.annotations.NotNull;

/**
 * Interface used to register your custom mod elements on the client side.
 * This is provided via the {@link com.tyzsskills.api.events.TyzsSkillsClientSetupEvent} on the mod {@code MOD BUS}.
 */
public interface ITyzsSkillsClientRegistration {

    /**
     * Registers a custom sorting rule for the skills GUI menu.
     * * @param sortingType A {@link SortType} containing the button's visual information (icon UV) and the comparator used for sorting.
     */
    void registerSortingType(@NotNull SortType sortingType);
}
