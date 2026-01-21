package com.tyzsskills.server.model;

public class LevelPoolPreset {

    public static String GetDefaultRewardValues(){
        return """
        {
            "1" : {"goal" : 350,"reward" : 4},
            "2" : {"goal" : 400,"reward" : 4},
            "3" : {"goal" : 475,"reward" : 4},
            "4" : {"goal" : 550,"reward" : 4},
            "5" : {"goal" : 650,"reward" : 5},
            "6" : {"goal" : 775,"reward" : 5},
            "7" : {"goal" : 925,"reward" : 5},
            "8" : {"goal" : 1075,"reward" : 5},
            "9" : {"goal" : 1250,"reward" : 5},
            "10" : {"goal" : 1450,"reward" : 10},
            
            "11" : {"goal" : 1675,"reward" : 6},
            "12" : {"goal" : 1925,"reward" : 6},
            "13" : {"goal" : 2175,"reward" : 6},
            "14" : {"goal" : 2450,"reward" : 6},
            "15" : {"goal" : 2750,"reward" : 15},
            "16" : {"goal" : 3050,"reward" : 7},
            "17" : {"goal" : 3400,"reward" : 7},
            "18" : {"goal" : 3750,"reward" : 7},
            "19" : {"goal" : 4100,"reward" : 7},
            "20" : {"goal" : 4500,"reward" : 20},
            
            "21" : {"goal" : 4900,"reward" : 8},
            "22" : {"goal" : 5325,"reward" : 8},
            "23" : {"goal" : 5775,"reward" : 8},
            "24" : {"goal" : 6250,"reward" : 8},
            "25" : {"goal" : 6725,"reward" : 25},
            "26" : {"goal" : 7225,"reward" : 9},
            "27" : {"goal" : 7750,"reward" : 9},
            "28" : {"goal" : 8300,"reward" : 9},
            "29" : {"goal" : 8850,"reward" : 9},
            "30" : {"goal" : 9425,"reward" : 30},
            
            "31" : {"goal" : 10025,"reward" : 10},
            "32" : {"goal" : 10650,"reward" : 10},
            "33" : {"goal" : 11275,"reward" : 10},
            "34" : {"goal" : 11950,"reward" : 10},
            "35" : {"goal" : 12625,"reward" : 35},
            "36" : {"goal" : 13300,"reward" : 11},
            "37" : {"goal" : 14025,"reward" : 11},
            "38" : {"goal" : 14750,"reward" : 11},
            "39" : {"goal" : 15500,"reward" : 11},
            
            
            "40" : {"goal" : 16275,"reward" : 40},
            "41" : {"goal" : 17050,"reward" : 12},
            "42" : {"goal" : 17875,"reward" : 12},
            "43" : {"goal" : 18700,"reward" : 12},
            "44" : {"goal" : 19525,"reward" : 12},
            "45" : {"goal" : 20400,"reward" : 45},
            "46" : {"goal" : 21275,"reward" : 13},
            "47" : {"goal" : 22175,"reward" : 13},
            "48" : {"goal" : 23100,"reward" : 13},
            "49" : {"goal" : 24050,"reward" : 13},
            
            "50" : {"goal" : 25000,"reward" : 50},
            
            "-1" : {"goal" : 15000,"reward" : 20}
        }""";
    }
}
