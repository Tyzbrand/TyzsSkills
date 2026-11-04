package com.tyzsskills.api.records;

import java.util.Map;

public record ExportData(long worldTime, int currentLvl, float currentXP,
                         int currentSP, Map<String, Integer> purchasedSkills) {
}
