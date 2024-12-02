package top.toobee.optimization.util

fun <T> List<T>.prioritize(predicate: (T) -> Boolean): List<T> {
    val m = ArrayList<T>(this.size)
    val n = ArrayList<T>(this.size)
    for (e in this) {
        if (predicate(e)) {
            m.add(e)
        } else {
            n.add(e)
        }
    }
    m.addAll(n)
    return m
}