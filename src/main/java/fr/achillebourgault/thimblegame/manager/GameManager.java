package fr.achillebourgault.thimblegame.manager;

import fr.achillebourgault.thimblegame.ThimbleGame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class GameManager {

    private HashMap<Player, Material> players;
    private GameState currentGameState;
    private int maxPlayers = 8;
    private Location basePoint;
    private Player currentPlayer;
    private BukkitTask countdownTask;

    private HashMap<Material, Boolean> allowedBlocks;

    public static int countdown = 60;

    public GameManager() {
        this.players = new HashMap<>();
        this.currentGameState = GameState.LOBBY;
        this.basePoint = new Location(Bukkit.getWorld("world"), -10, 97, 75);

        this.allowedBlocks = new HashMap<>();

        this.allowedBlocks.put(Material.RED_WOOL, false);
        this.allowedBlocks.put(Material.GREEN_WOOL, false);
        this.allowedBlocks.put(Material.WHITE_WOOL, false);
        this.allowedBlocks.put(Material.BLUE_WOOL, false);
        this.allowedBlocks.put(Material.MAGENTA_WOOL, false);
        this.allowedBlocks.put(Material.PINK_WOOL, false);
        this.allowedBlocks.put(Material.CYAN_WOOL, false);
        this.allowedBlocks.put(Material.BROWN_WOOL, false);
    }

    public void startPreGame() {
        this.currentGameState = GameState.PRE_GAME;

        countdownTask = new BukkitRunnable() {

            @Override
            public void run() {
                String message = "";
                switch (countdown) {
                    case 50:
                    case 40:
                    case 30:
                    case 20:
                    case 10:
                    case 5:
                    case 3:
                    case 2:
                    case 1:
                        message = "§e§lGame > §r§7La partie demarrera dans §e" + countdown + " §7secondes.";
                        for (Player p : Bukkit.getOnlinePlayers())
                            p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1f, 0f);
                        break;
                    case 0:
                        message = "§e§lGame > §r§7La partie demarre!";
                        startGame();
                        break;
                }
                if (!message.isEmpty())
                    Bukkit.broadcastMessage(message);
                countdown--;
            }

        }.runTaskTimer(ThimbleGame.getInstance(), 0, 20L);
    }

    public void finishGame() {
        int x = -21;
        int z = 71;

        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "fill -12 65 71 -21 66 80 minecraft:water");
    }

    public void startGame() {
        this.currentGameState = GameState.GAME;
        for (Player p : this.players.keySet()) {
            p.getInventory().clear();
            p.teleport(this.basePoint);
            p.playSound(p.getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_PLACE, 1f, 0f);
        }

        Bukkit.broadcastMessage("START GAME");
    }

    public HashMap<Player, Material> getPlayers() {
        return players;
    }

    public GameState getCurrentGameState() {
        return currentGameState;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Location getBasePoint() {
        return basePoint;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public HashMap<Material, Boolean> getAllowedBlocks() {
        return allowedBlocks;
    }
}
