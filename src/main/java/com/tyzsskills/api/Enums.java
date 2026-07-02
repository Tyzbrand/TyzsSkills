package com.tyzsskills.api;

public class Enums {

    public enum SkillType {GENERIC, CUSTOM, IMMUTABLE}
    public enum ResetType {ALL, METADATA, SKILLS, STATS}
    public enum TooltipType {REFUND, PURCHASE}

    public enum SortingDirection {ASCENDING, DESCENDING}
    public enum SortingCategory {ALL, BOOKMARKS}

    public enum ClientAction {PURCHASE, REFUND, BULK_PURCHASE, BULK_REFUND, BOOKMARK}

    public enum ScalePivot {TOP_LEFT, CENTER}
}
