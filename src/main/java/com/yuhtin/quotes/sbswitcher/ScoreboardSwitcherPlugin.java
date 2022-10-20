package com.yuhtin.quotes.sbswitcher;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.yuhtin.quotes.sbswitcher.command.PlaceholderTestCommand;
import com.yuhtin.quotes.sbswitcher.model.Board;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
public class ScoreboardSwitcherPlugin extends JavaPlugin {

    private final ConcurrentHashMap<UUID, List<ProtectedRegion>> regionCache = new ConcurrentHashMap<>();
    private final LinkedList<Board> boards = new LinkedList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) return;
            Bukkit.getOnlinePlayers().forEach(player -> regionCache.put(player.getUniqueId(), getRegions(player.getLocation())));
        }, 20L, 20L);

        ConfigurationSection section = getConfig().getConfigurationSection("boards");
        for (String key : section.getKeys(false)) {
            ConfigurationSection boardSection = section.getConfigurationSection(key);

            int priority = Integer.parseInt(key);
            String boardName = boardSection.getString("board-name");
            String permission = boardSection.getString("permission", "");
            String region = boardSection.getString("region", "");

            Board board = new Board(priority, boardName, permission, region);
            boards.add(board);
        }

        Collections.sort(boards);

        PlaceholderAPI.registerPlaceholder(this, "sbswitcher", event -> {
            Player player = event.getPlayer();

            Board board = findBoardByPermission(player);
            if (board != null) return board.getBoardName();

            board = findBoardByRegion(player);
            String s = board == null ? getConfig().getString("board-default") : board.getBoardName();
            player.sendMessage("board: " + s);
            return s;
        });

        getCommand("pltest").setExecutor(new PlaceholderTestCommand());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fb reload");
    }


    @Nullable
    public Board findBoardByPermission(Player player) {
        return boards.stream()
                .filter(board -> !board.getPermission().isEmpty())
                .filter(board -> player.hasPermission(board.getPermission()))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public Board findBoardByRegion(Player player) {
        return boards.stream()
                .filter(board -> !board.getRegionName().isEmpty())
                .filter(board -> regionCache.get(player.getUniqueId()).stream()
                        .anyMatch(region -> region.getId().equalsIgnoreCase(board.getRegionName())))
                .findFirst()
                .orElse(null);
    }

    public static List<ProtectedRegion> getRegions(Location location) {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) return new ArrayList<>();
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(location);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        ApplicableRegionSet set = query.getApplicableRegions(loc);
        return new ArrayList<>(set.getRegions());
    }
}
