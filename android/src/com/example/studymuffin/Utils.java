package com.example.studymuffin;

import java.util.ArrayList;

public class Utils {
    /*
     * Average: O(log(n))
     * Best: O(1)
     * Worst: O(log(n))
     */
    static <T extends Comparable<T>> int binarySearch(ArrayList<T> list, T val) {
        int max = list.size() - 1;
        int min = 0;

        // if the size is 0
        if (max < min) {
            return -1;
        } else if (list.get(min).compareTo(val) > 0 || list.get(max).compareTo(val) < 0) {
            return -1;
        } else if (list.size() == 1) {
            return 0;
        }

        int i = (min + max) / 2;

        while (min <= max) {
            if (list.get(i).compareTo(val) == 0) {
                return i;
            } else if (list.get(i).compareTo(val) < 0) {
                min = i + 1;
            } else if (list.get(i).compareTo(val) > 0) {
                max = i - 1;
            }

            i = (min + max) / 2;
        }

        return -1;
    }

    /**
     * linearly searches through the list and returns the indices of the elements in the list that
     * contain the search query
     * @param list the list to search through
     * @param searchQuery the query to search for within the elements
     * @return indices of the elemnts in the list that contain the search query
     */
    static ArrayList<Integer> filterList(ArrayList<String> list, String searchQuery) {
        ArrayList<Integer> indices = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).contains(searchQuery)) {
                indices.add(i);
            }
        }

        return indices;
    }
}
