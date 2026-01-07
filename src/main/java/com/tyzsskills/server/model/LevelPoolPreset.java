package com.tyzsskills.server.model;

public class LevelPoolPreset {

    public static String GetDefaultRewardValues(){
        return """
        {
            "1" : {"goal" : 100,"reward" : 5},
            "2" : {"goal" : 225,"reward" : 5},
            "3" : {"goal" : 350,"reward" : 5},
            "4" : {"goal" : 475,"reward" : 5},
            "5" : {"goal" : 600,"reward" : 10},
            "6" : {"goal" : 725,"reward" : 5},
            "7" : {"goal" : 850,"reward" : 5},
            "8" : {"goal" : 975,"reward" : 5},
            "9" : {"goal" : 1100,"reward" : 5},
            
            
            "10" : {"goal" : 1225,"reward" : 10},
            "11" : {"goal" : 1475,"reward" : 6},
            "12" : {"goal" : 1725,"reward" : 6},
            "13" : {"goal" : 1975,"reward" : 6},
            "14" : {"goal" : 2225,"reward" : 6},
            "15" : {"goal" : 2475,"reward" : 11},
            "16" : {"goal" : 2725,"reward" : 6},
            "17" : {"goal" : 2975,"reward" : 6},
            "18" : {"goal" : 3225,"reward" : 6},
            "19" : {"goal" : 3475,"reward" : 6},
            
            
            "20" : {"goal" : 3750,"reward" : 11},
            "21" : {"goal" : 4275,"reward" : 7},
            "22" : {"goal" : 4800,"reward" : 7},
            "23" : {"goal" : 5325,"reward" : 7},
            "24" : {"goal" : 5850,"reward" : 7},
            "25" : {"goal" : 6375,"reward" : 12},
            "26" : {"goal" : 6900,"reward" : 7},
            "27" : {"goal" : 7425,"reward" : 7},
            "28" : {"goal" : 7950,"reward" : 7},
            "29" : {"goal" : 8475,"reward" : 7},
            
            
            "30" : {"goal" : 9000,"reward" : 12},
            "31" : {"goal" : 9600,"reward" : 8},
            "32" : {"goal" : 10200,"reward" : 8},
            "33" : {"goal" : 10800,"reward" : 8},
            "34" : {"goal" : 11400,"reward" : 8},
            "35" : {"goal" : 12000,"reward" : 13},
            "36" : {"goal" : 12600,"reward" : 8},
            "37" : {"goal" : 13200,"reward" : 8},
            "38" : {"goal" : 13800,"reward" : 8},
            "39" : {"goal" : 14400,"reward" : 8},
            
            
            "40" : {"goal" : 15200,"reward" : 13},
            "41" : {"goal" : 16000,"reward" : 9},
            "42" : {"goal" : 16900,"reward" : 9},
            "43" : {"goal" : 17800,"reward" : 9},
            "44" : {"goal" : 18700,"reward" : 9},
            "45" : {"goal" : 19600,"reward" : 14},
            "46" : {"goal" : 20500,"reward" : 9},
            "47" : {"goal" : 21500,"reward" : 9},
            "48" : {"goal" : 22500,"reward" : 9},
            "49" : {"goal" : 23500,"reward" : 9},
            
            "50" : {"goal" : 25000,"reward" : 15},
            
            "-1" : {"goal" : 25000,"reward" : 15}
        }""";
    }
}
