package com.tyzsskills.api.records;

import com.tyzsskills.api.interfaces.ISkill;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public record SortType(String name, @Nullable Comparator<ISkill> comparator, int u, int v, int uHover, int vHover) {}
