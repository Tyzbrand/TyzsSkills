package com.tyzsskills.server.model;

public class LevelPoolPreset {

    public static String GetDefaultRewardValues(){
        return """
        {
            "1" : {"goal" : 350,"reward" : 5},
            "2" : {"goal" : 400,"reward" : 5},
            "3" : {"goal" : 475,"reward" : 5},
            "4" : {"goal" : 550,"reward" : 5},
            "5" : {"goal" : 650,"reward" : 6},
            "6" : {"goal" : 775,"reward" : 6},
            "7" : {"goal" : 925,"reward" : 6},
            "8" : {"goal" : 1075,"reward" : 6},
            "9" : {"goal" : 1250,"reward" : 10},
            "10" : {"goal" : 1450,"reward" : 7},

            "11" : {"goal" : 1675,"reward" : 7},
            "12" : {"goal" : 1925,"reward" : 7},
            "13" : {"goal" : 2175,"reward" : 7},
            "14" : {"goal" : 2450,"reward" : 15},
            "15" : {"goal" : 2750,"reward" : 8},
            "16" : {"goal" : 3050,"reward" : 8},
            "17" : {"goal" : 3400,"reward" : 8},
            "18" : {"goal" : 3750,"reward" : 8},
            "19" : {"goal" : 4100,"reward" : 20},
            "20" : {"goal" : 4500,"reward" : 9},

            "21" : {"goal" : 4900,"reward" : 9},
            "22" : {"goal" : 5325,"reward" : 9},
            "23" : {"goal" : 5775,"reward" : 9},
            "24" : {"goal" : 6250,"reward" : 25},
            "25" : {"goal" : 6725,"reward" : 10},
            "26" : {"goal" : 7225,"reward" : 10},
            "27" : {"goal" : 7750,"reward" : 10},
            "28" : {"goal" : 8300,"reward" : 10},
            "29" : {"goal" : 8850,"reward" : 30},
            "30" : {"goal" : 9425,"reward" : 11},

            "31" : {"goal" : 10025,"reward" : 11},
            "32" : {"goal" : 10650,"reward" : 11},
            "33" : {"goal" : 11275,"reward" : 11},
            "34" : {"goal" : 11950,"reward" : 35},
            "35" : {"goal" : 12625,"reward" : 12},
            "36" : {"goal" : 13300,"reward" : 12},
            "37" : {"goal" : 14025,"reward" : 12},
            "38" : {"goal" : 14750,"reward" : 12},
            "39" : {"goal" : 15500,"reward" : 40},

            "40" : {"goal" : 16275,"reward" : 13},
            "41" : {"goal" : 17050,"reward" : 13},
            "42" : {"goal" : 17875,"reward" : 13},
            "43" : {"goal" : 18700,"reward" : 13},
            "44" : {"goal" : 19525,"reward" : 45},
            "45" : {"goal" : 20400,"reward" : 14},
            "46" : {"goal" : 21275,"reward" : 14},
            "47" : {"goal" : 22175,"reward" : 14},
            "48" : {"goal" : 23100,"reward" : 14},
            "49" : {"goal" : 24050,"reward" : 50},

            "-1" : {"goal" : 15000,"reward" : 20}
        }""";
    }
}
