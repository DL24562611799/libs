package shark.bukkitlib.module.dc

abstract class ViewProperty(val values: MutableMap<String, Any?>) {

    operator fun set(key: String, value: Any?) {
        this.values[key] = value
    }

    operator fun get(key: String): Any? {
        return this.values[key]
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getOrDefault(key: String, def: T): T {
        return this.values[key]?.let { it as T } ?: def
    }

}