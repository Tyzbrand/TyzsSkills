package com.tyzsskills.server.model;

public class LevelPoolPreset {

    public static String GetDefaultRewardValues(){
        return """
        {
            "1" : {"goal" : 100,"reward" : 3},
            "2" : {"goal" : 225,"reward" : 3},
            "3" : {"goal" : 350,"reward" : 3},
            "4" : {"goal" : 475,"reward" : 4},
            "5" : {"goal" : 600,"reward" : 8},
            "6" : {"goal" : 725,"reward" : 4},
            "7" : {"goal" : 850,"reward" : 4},
            "8" : {"goal" : 975,"reward" : 5},
            "9" : {"goal" : 1100,"reward" : 5},
            "10" : {"goal" : 1225,"reward" : 10},
            
            "11" : {"goal" : 1475,"reward" : 6},
            "12" : {"goal" : 1725,"reward" : 6},
            "13" : {"goal" : 1975,"reward" : 6},
            "14" : {"goal" : 2225,"reward" : 7},
            "15" : {"goal" : 2475,"reward" : 15},
            "16" : {"goal" : 2725,"reward" : 8},
            "17" : {"goal" : 2975,"reward" : 8},
            "18" : {"goal" : 3225,"reward" : 8},
            "19" : {"goal" : 3475,"reward" : 8},
            "20" : {"goal" : 3750,"reward" : 20},
            
            "21" : {"goal" : 4275,"reward" : 9},
            "22" : {"goal" : 4800,"reward" : 9},
            "23" : {"goal" : 5325,"reward" : 9},
            "24" : {"goal" : 5850,"reward" : 10},
            "25" : {"goal" : 6375,"reward" : 25},
            "26" : {"goal" : 6900,"reward" : 10},
            "27" : {"goal" : 7425,"reward" : 10},
            "28" : {"goal" : 7950,"reward" : 10},
            "29" : {"goal" : 8475,"reward" : 10},
            "30" : {"goal" : 9000,"reward" : 30},
            
            "31" : {"goal" : 9600,"reward" : 12},
            "32" : {"goal" : 10200,"reward" : 12},
            "33" : {"goal" : 10800,"reward" : 12},
            "34" : {"goal" : 11400,"reward" : 15},
            "35" : {"goal" : 12000,"reward" : 35},
            "36" : {"goal" : 12600,"reward" : 15},
            "37" : {"goal" : 13200,"reward" : 15},
            "38" : {"goal" : 13800,"reward" : 15},
            "39" : {"goal" : 14400,"reward" : 15},
            
            
            "40" : {"goal" : 15200,"reward" : 40},
            "41" : {"goal" : 16000,"reward" : 17},
            "42" : {"goal" : 16900,"reward" : 17},
            "43" : {"goal" : 17800,"reward" : 17},
            "44" : {"goal" : 18700,"reward" : 17},
            "45" : {"goal" : 19600,"reward" : 45},
            "46" : {"goal" : 20500,"reward" : 20},
            "47" : {"goal" : 21500,"reward" : 20},
            "48" : {"goal" : 22500,"reward" : 20},
            "49" : {"goal" : 23500,"reward" : 20},
            
            "50" : {"goal" : 25000,"reward" : 50},
            
            "-1" : {"goal" : 25000,"reward" : 20}
        }""";
    }
}
