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

    //SETTINGS
    public enum SettingCategory {
        SERVER_GENERAL(0, false),
        DEATH_PENALTIES(1, false),
        XP(2, false),
        SERVER_PERFORMANCES(3, false),

        CLIENT_GENERAL(0, true),
        OVERLAYS(1, true),
        ACCESSIBILITY(2, true),
        PERFORMANCES(3, true);

        private final int priority;
        private final boolean isClient;

        SettingCategory(int priority, boolean isClient) {this.priority = priority; this.isClient = isClient;}

        public int priority() {return priority;}
        public boolean isClient() {return isClient;}
        public String label(){
            return isClient ? name().replace("CLIENT_", "")
                    : name().replace("SERVER_", "");
        }
    }
    public enum SettingType { BOOL, INT, FLOAT, COLOR, ENUM}

    //UI
    public enum ScalePivot {TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER}
    public enum TextAlignment {LEFT, CENTER, RIGHT}
    public enum OverlayAnchor {TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER}

    public enum BorderType {NONE, INNER, OUTER}
    public enum BackgroundType {NONE, CUT, FULL}


    //STATIC SETTINGS
    public enum Multiplier {x1, x2, x3, x4, x5, x10, x25, x50, x100, x1000}
}
