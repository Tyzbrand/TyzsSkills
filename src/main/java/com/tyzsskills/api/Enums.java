package com.tyzsskills.api;

public class Enums {

    public enum SkillType {GENERIC, CUSTOM, IMMUTABLE}
    public enum ResetType {ALL, METADATA, SKILLS, SPELLS, STATS}

    public enum SortingDirection {ASCENDING, DESCENDING}
    public enum SortingType {DEFAULT, NAME, PRICE, LEVEL}
    public enum MenuFocus {SKILL, SPELL}
    public enum WheelMode {CAST, ASSIGN}

    public enum SkillAction {PURCHASE, REFUND, BULK_PURCHASE, BULK_REFUND, BOOKMARK, DEACTIVATION}
    public enum SpellAction{PURCHASE, ASSIGNMENT, UNASSIGNMENT, CAST, PROPERTY_PURCHASE}

    //UI
    public enum ScalePivot {TOP_LEFT, CENTER}
    public enum TextAlignment {LEFT, CENTER, RIGHT}
}
