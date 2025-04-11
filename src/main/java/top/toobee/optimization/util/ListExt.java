package top.toobee.optimization.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ListExt {
    public static <T> List<T> prioritize(List<T> list, Predicate<T> filter, Predicate<T> priority) {
        final var size = list.size() << 1;
        final var m = new ArrayList<T>(size);
        final var n = new ArrayList<T>(size);
        for (var e : list)
            if (filter.test(e)) {
                if (priority.test(e))
                    m.add(e);
                else
                    n.add(e);
            }
        m.addAll(n);
        return m;
    }
}
