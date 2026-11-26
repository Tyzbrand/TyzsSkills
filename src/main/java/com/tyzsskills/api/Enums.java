package com.tyzsskills.api;

public class Enums {

    public enum SkillType {GENERIC, CUSTOM, IMMUTABLE}
    public enum ResetType {ALL, METADATA, SKILLS, SPELLS, STATS, QUOTAS}

    public enum SortingDirection {ASCENDING, DESCENDING}
    public enum SortingType {DEFAULT, NAME, PRICE, LEVEL}
    public enum MenuFocus {SKILL, SPELL}
    public enum WheelMode {CAST, ASSIGN}
    public enum SettingsMode {MAIN, CLIENT, SERVER}

    public enum SkillAction {PURCHASE, REFUND, BULK_PURCHASE, BULK_REFUND, BOOKMARK, DEACTIVATION}
    public enum SpellAction{PURCHASE, ASSIGNMENT, UNASSIGNMENT, CAST, PROPERTY_PURCHASE}

    public enum LimitType {REAL_TIME, GAME_TIME}

    public enum PermissionLevel{
        NONE(0), MODERATOR(1), OPERATOR(2), ADMIN(3), OWNER(4);

        PermissionLevel(int level) {this.level = level;}
        private final int level;
        public int level(){return level;}
    }

    //SETTINGS
    public enum SettingCategory {
        SERVER_GENERAL(0, false),
        DEATH_PENALTIES(1, false),
        LIMITS(2, false),
        XP(3, false),
        SERVER_PERFORMANCE(4, false),
        PERMISSIONS(5, false),

        CLIENT_GENERAL(0, true),
        OVERLAYS(1, true),
        ACCESSIBILITY(2, true),
        CLIENT_PERFORMANCE(3, true);

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
    public enum SettingTag {NEED_RESTART, EXPERIMENTAL}

    //UI
    public enum ScalePivot {TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER}
    public enum TextAlignment {LEFT, CENTER, RIGHT}
    public enum OverlayAnchor {TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER}

    public enum BorderType {NONE, INNER, OUTER}
    public enum BackgroundType {NONE, CUT, FULL}


}
