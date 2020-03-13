package com.gaoxianglong.maque.utils;

import com.gaoxianglong.maque.db.TelephoneBook;

import java.util.Comparator;

public class PinyinComparator implements Comparator<TelephoneBook> {

    public int compare(SortModel o1, SortModel o2) {
        if (o1.getLetters().equals("@")
                || o2.getLetters().equals("#")) {
            return -1;
        } else if (o1.getLetters().equals("#")
                || o2.getLetters().equals("@")) {
            return 1;
        } else {
            return o1.getLetters().compareTo(o2.getLetters());
        }
    }

    @Override
    public int compare(TelephoneBook o1, TelephoneBook o2) {
        return 0;
    }
}
