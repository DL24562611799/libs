package shark.bukkitlib.module.dc

import org.bukkit.entity.Player

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class CustomFunction(
    val identity: String
)

fun onFunction(indexName: String? = null, length: Int = 0, async: Boolean = false, executor: (player: Player, screen: ViewScreen, args: List<String>) -> Unit): ViewFunction {
    return ViewFunction(indexName, length, async, executor)
}

data class ViewFunction(val indexName: String?, val length: Int, val async: Boolean, val executor: (player: Player, screen: ViewScreen, args: List<String>) -> Unit)

fun Class<*>.injectFunctions() {
    var `is` = false
    for (field in declaredFields) {
        if (field.isAnnotationPresent(CustomFunction::class.java)) {
            `is` = true
            break
        }
    }
    if (`is`) {
        try {
            val any = this.getConstructor().newInstance()
            for (field in declaredFields) {
                if (field.isAnnotationPresent(CustomFunction::class.java)) {
                    field.isAccessible = true
                    val function = field.get(any) as ViewFunction
                    ViewListener.functions[field.getAnnotation(CustomFunction::class.java).identity.uppercase()] = function
                }
            }
        }catch (e: ReflectiveOperationException) {
            e.printStackTrace()
        }
    }
}