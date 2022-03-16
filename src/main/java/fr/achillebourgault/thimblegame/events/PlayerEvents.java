package fr.achillebourgault.thimblegame.events;

import fr.achillebourgault.thimblegame.ThimbleGame;
import fr.achillebourgault.thimblegame.manager.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerEvents implements Listener {

    public void addDefaults(Player p) {
        ItemStack is = new ItemStack(Material.CHEST, 1);
        ItemMeta im = is.getItemMeta();

        im.setDisplayName("§e§lChoisir un bloc");
        is.setItemMeta(im);

        p.getInventory().clear();
        p.setFoodLevel(20);
        p.setHealth(20);

        p.getInventory().setItem(0, is);
        if (ThimbleGame.getInstance().manager.getPlayers().containsKey(p)) {
            ItemStack isA = new ItemStack(ThimbleGame.getInstance().manager.getPlayers().get(p), 1);
            ItemMeta imA = is.getItemMeta();

            imA.setDisplayName("§eChoisir un bloc");
            isA.setItemMeta(imA);
            p.getInventory().setItem(0, is);
            p.getInventory().setItem(5, isA);
        }
    }

    public Inventory selectionInventory() {
        Inventory inv = Bukkit.createInventory(null, 9, "§eChoisir un bloc");
        int i = 0;
        ItemStack is = new ItemStack(Material.GRASS, 1);

        for (Material material : ThimbleGame.getInstance().manager.getAllowedBlocks().keySet()) {
            ItemMeta meta = is.getItemMeta();
            is.setType(material);

            if (ThimbleGame.getInstance().manager.getAllowedBlocks().get(material) == true)
                meta.setDisplayName("§e§lALREADY SELECTED");
            else
                meta.setDisplayName("§e§lClick to choose");
            inv.setItem(i, is);
            i++;
        }
        return inv;
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (e.getItem() != null && e.getItem().getType() == Material.CHEST) {
            e.setCancelled(true);
            p.openInventory(selectionInventory());
        }
    }

    @EventHandler
    public void onPlayerClickInInventory(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        e.setCancelled(true);
        if (e.getCurrentItem() == null)
            return;
        else {
            if (ThimbleGame.getInstance().manager.getPlayers().containsKey(p) ||
                    ThimbleGame.getInstance().manager.getPlayers().containsValue(e.getCurrentItem().getType()))
                p.playSound(p.getLocation(), Sound.BLOCK_BASALT_BREAK, 1f, 0f);
            else {
                ThimbleGame.getInstance().manager.getPlayers().put(p, e.getCurrentItem().getType());
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1f, 0f);
                p.closeInventory();
            }

        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (ThimbleGame.getInstance().manager.getCurrentGameState() == GameState.LOBBY) {
            e.setJoinMessage("§f§lGame > §r§e" + p.getName() + " §7a rejoint la partie (§e" +
                    Bukkit.getOnlinePlayers().size() + " §8/§e" + ThimbleGame.getInstance().manager.getMaxPlayers()
                    + "§7).");
            addDefaults(p);

            if (Bukkit.getOnlinePlayers().size() >= 2 &&
                    ThimbleGame.getInstance().manager.getCurrentGameState() != GameState.PRE_GAME) {
                ThimbleGame.getInstance().manager.startPreGame();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (ThimbleGame.getInstance().manager.getCurrentGameState() == GameState.LOBBY) {
            e.setQuitMessage("§f§lGame > §r§e" + p.getName() + " §7a rejoint la partie (§e" +
                    Bukkit.getOnlinePlayers().size() + " §8/§e" + ThimbleGame.getInstance().manager.getMaxPlayers()
                    + "§7).");
        }
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent e) {
        Player p = e.getEntity();

        if (ThimbleGame.getInstance().manager.getCurrentGameState() == GameState.GAME && ThimbleGame.getInstance().manager.getPlayers().containsKey(p)) {
            ThimbleGame.getInstance().manager.getPlayers().remove(p);
            if (ThimbleGame.getInstance().manager.getPlayers().size() > 2)
                e.setDeathMessage("§e" + p.getName() + " §7est mort ! §9§l" + ThimbleGame.getInstance().manager.getPlayers().size() + " §r§7joueurs restants.");
            else {
                e.setDeathMessage("§e" + p.getName() + " §7est mort !");
                for (Player winner : ThimbleGame.getInstance().manager.getPlayers().keySet()) {
                    Bukkit.broadcastMessage("§e§l" + winner.getName() + " §r§7a gagné la partie !");
                    ThimbleGame.getInstance().manager.finishGame();
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Block fallBlock = p.getWorld().getBlockAt(p.getLocation());

        if (fallBlock.getType() == Material.WATER) {
            fallBlock.setType(ThimbleGame.getInstance().manager.getPlayers().get(p));
            p.teleport(ThimbleGame.getInstance().manager.getBasePoint());
        }

    }

}
