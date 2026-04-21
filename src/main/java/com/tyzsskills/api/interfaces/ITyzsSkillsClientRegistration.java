package com.tyzsskills.api.interfaces;

import com.tyzsskills.api.records.SortType;
import org.jetbrains.annotations.NotNull;

/**
 * Interface uses to register your mod elements on the client side.
 */
public interface ITyzsSkillsClientRegistration {

    /**
     * Register a sorting rule for the menu.
     * @param sortingType A {@link SortType} containing button infos and sorting rule.
     */
    void registerSortingType(@NotNull SortType sortingType);
}
