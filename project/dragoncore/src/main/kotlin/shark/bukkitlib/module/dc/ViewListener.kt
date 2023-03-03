package shark.bukkitlib.module.dc

import eos.moe.dragoncore.api.gui.event.CustomPacketEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import java.util.concurrent.CompletableFuture

class ViewListener(private val plugin: Plugin): Listener {

    @EventHandler
    private fun onClose(e: CustomPacketEvent) {
        if (e.identifier == "ViewScreenCloseEvent") {
            if (e.data.size == 1) {
                ViewScreen.closeScreen(e.player, e.data[0])
            }
        }
    }

    @EventHandler
    private fun onQuit(e: PlayerQuitEvent) {
        ViewScreen.closeScreen(e.player, false)
        ViewScreen.showings.remove(e.player.name)
    }

    @EventHandler
    private fun onQuit(e: PlayerKickEvent) {
        ViewScreen.closeScreen(e.player, false)
        ViewScreen.showings.remove(e.player.name)
    }

    @EventHandler
    private fun onFunction(e: CustomPacketEvent) {
        if (e.identifier == plugin.name && e.data.size > 0) {
            val identity = e.data.removeAt(0)
            val function = functions[identity.uppercase()] ?: return
            if (e.data.size < function.length) {
                return
            }
            val screen = if (function.indexName == null) {
                ViewScreen.getOpenedGui(e.player)
            }else {
                ViewScreen.getOpenedViewScreen(e.player, function.indexName)
            }
            if (screen == null) {
                return
            }
            if (function.async) {
                CompletableFuture.runAsync{
                    function.executor(e.player, screen, e.data)
                }
            }else {
                function.executor(e.player, screen, e.data)
            }
        }
    }

    companion object {

        internal val functions = mutableMapOf<String, ViewFunction>()

    }
}