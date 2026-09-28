package com.tyzsskills.api.records;

import java.util.Map;

public record ExportData(long worldTime, int currentLvl, float currentXP,
                         int currentSP, float avgXP, Map<String, Integer> purchasedSkills) {
}
