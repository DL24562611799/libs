package shark.bukkitlib.module.dc.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import shark.bukkitlib.module.dc.ViewScreen;

public class ViewScreenOpenEvent extends BaseEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final ViewScreen screen;

    public ViewScreenOpenEvent(Player player, ViewScreen screen) {
        this.player = player;
        this.screen = screen;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public ViewScreen getScreen() {
        return screen;
    }
}
