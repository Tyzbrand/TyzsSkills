package com.tyzsskills.impl.client.tools;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.records.SortType;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.server.model.Skill;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public class SortTools {

    //TYPE
    private static final List<SortType> activeSortTypes = new ArrayList<>();
    private static int currentSortTypeIndex = 0;

    //SENS
    private static Enums.SortingDirection currentSortingDirection = Enums.SortingDirection.ASCENDING;

    //CATEGORY
    private static Enums.CategoryType currentSkillCategory = Enums.CategoryType.ALL;

    //BOOLS
    private static boolean showUnowned = true;


    private static final List<Skill> currentSkillOrder = new ArrayList<>();


    public static void registerSortingType(@NotNull SortType sortType){
        activeSortTypes.add(sortType);}


    public static List<Skill> refreshList(){
        List<Skill> listToSort =  new ArrayList<>(ClientCache.GetAllSkills());

        if(!showUnowned) listToSort.removeIf(s -> ClientCache.GetSkillLevel(s.getID()) <= 0);

        if(currentSkillCategory == Enums.CategoryType.BOOKMARKS) listToSort.removeIf(s -> !ClientCache.isSkillBookmarked(s.getID()));
        else if(currentSkillCategory != Enums.CategoryType.ALL) listToSort.removeIf(s -> s.getCategory() != currentSkillCategory);

        if(!activeSortTypes.isEmpty()){
            var currentSortType = activeSortTypes.get(currentSortTypeIndex);
            var comparator = currentSortingDirection == Enums.SortingDirection.ASCENDING ?
                    currentSortType.comparator() : currentSortType.comparator().reversed();

            listToSort.sort(comparator);
        }

        currentSkillOrder.clear();
        currentSkillOrder.addAll(listToSort);

        return listToSort;
    }

    public static void CycleSortType(){
        currentSortTypeIndex =  (currentSortTypeIndex + 1) % activeSortTypes.size();
    }

    public static void CycleSortDirection(){
        currentSortingDirection = currentSortingDirection == Enums.SortingDirection.ASCENDING ?
                Enums.SortingDirection.DESCENDING : Enums.SortingDirection.ASCENDING;
    }

    public static void SetCategoryType(Enums.CategoryType category){
        currentSkillCategory = category;
    }

    public static void ToggleUnowned(){
        showUnowned = !showUnowned;
    }


    //Getters
    public static List<Skill> getCurrentSkillOrder() {
        return activeSortTypes.isEmpty() ? refreshList() : List.copyOf(currentSkillOrder);
    }

    public static SortType getCurrentSortType(){
        return activeSortTypes.get(currentSortTypeIndex);
    }

    public static Enums.CategoryType getCurrentSkillCategory(){
        return currentSkillCategory;
    }
}
