package com.tyzsskills.impl.client.wrappers;

import com.tyzsskills.api.interfaces.ITyzsSkillsClientRegistration;
import com.tyzsskills.api.records.SortType;
import com.tyzsskills.impl.client.tools.SortingTools;
import org.jetbrains.annotations.NotNull;

public class TyzsSkillsClientRegistrationWrapper implements ITyzsSkillsClientRegistration {

    @Override
    public void registerSortingType(@NotNull SortType sortingType) {
        SortingTools.registerSortingType(sortingType);
    }
}
