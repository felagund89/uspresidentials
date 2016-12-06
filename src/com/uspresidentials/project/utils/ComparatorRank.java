package com.uspresidentials.project.utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;

import com.uspresidentials.project.entity.UserCustom;

public class ComparatorRank implements Comparator<UserCustom> {

	@Override
	public int compare(UserCustom o1, UserCustom o2) {
		if (o1.getPageRank() > o2.getPageRank()) {
			return -1;
		} else {
			return 1;
		}
	}
}