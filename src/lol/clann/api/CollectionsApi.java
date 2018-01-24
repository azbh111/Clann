package lol.clann.api;

import java.util.*;

public class CollectionsApi {

    /**
     * 将数组转化成List
     *
     * @param pArrays 要转换的数组,如果数组为null,将返回空List
     * @return 转换后的List,非null
     */
    public static <T> ArrayList<T> asList(T[] pArrays) {
        ArrayList<T> newList = new ArrayList<>();
        if (pArrays == null || pArrays.length == 0) {
            return newList;
        }
        for (T t : pArrays) {
            newList.add(t);
        }
        return newList;
    }

    //Map按值排序，up为递增，其他为递减
    public Map<Object, Integer> sortMapByValue(Map<Object, Integer> oriMap, String sort) {
        Map<Object, Integer> sortedMap = new LinkedHashMap<Object, Integer>();
        if (oriMap != null && !oriMap.isEmpty()) {
            List<Map.Entry<Object, Integer>> entryList = new ArrayList<Map.Entry<Object, Integer>>(oriMap.entrySet());
            Collections.sort(entryList, new Comparator<Map.Entry<Object, Integer>>() {
                @Override
                public int compare(Map.Entry<Object, Integer> entry1, Map.Entry<Object, Integer> entry2) {
                    int value1 = 0, value2 = 0;
                    try {
                        value1 = entry1.getValue();
                        value2 = entry2.getValue();
                    } catch (NumberFormatException e) {
                        value1 = 0;
                        value2 = 0;
                    }
                    if (sort.equalsIgnoreCase("up")) {
                        return value1 - value2;
                    } else {
                        return value2 - value1;
                    }
                }
            });
            Iterator<Map.Entry<Object, Integer>> iter = entryList.iterator();
            Map.Entry<Object, Integer> tmpEntry = null;
            while (iter.hasNext()) {
                tmpEntry = iter.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }
        }
        return sortedMap;
    }

    /**
     * 删除list中重复元素
     *
     * @param list
     */
    public static void removeDuplicate(Collection list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
    }

    /**
     * 删除List中重复元素,并保持顺序
     */
    public static void removeDuplicateWithOrder(Collection list) {
        Set set = new HashSet();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (!set.add(element)) {
                iter.remove();
            }
        }
    }

    public static List remove(List l, int index) {
        if (l.isEmpty()) {
            return l;
        }
        if (index < 0) {
            l.remove(0);
        } else if (index >= l.size()) {
            l.remove(l.size() - 1);
        } else {
            l.remove(index);
        }
        return l;
    }

    public static List set(List l, int index, Object o) {
        if (l.isEmpty()) {
            l.add(o);
            return l;
        }
        if (index < 0) {
            l.set(0, o);
        } else if (index >= l.size()) {
            l.set(l.size() - 1, o);
        } else {
            l.set(index, o);
        }
        return l;
    }
}
