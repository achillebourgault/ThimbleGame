package fr.achillebourgault.thimblegame;

import fr.achillebourgault.thimblegame.events.PlayerEvents;
import fr.achillebourgault.thimblegame.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ThimbleGame extends JavaPlugin {

    public static GameManager manager;
    public static ThimbleGame instance;

    @Override
    public void onEnable() {
        this.manager = new GameManager();
        instance = this;
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
    }

    public static ThimbleGame getInstance() {
        return instance;
    }
}
