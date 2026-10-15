package de.speedplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class SpeedPlugin extends JavaPlugin implements TabExecutor {

    // Minecraft-Standard fuer Gehen (Sprinten ist automatisch x1.3 davon)
    private static final float DEFAULT_SPEED = 0.2f;
    // Stufe 10 = 1.0 (Maximum, das Minecraft fuer setWalkSpeed erlaubt)
    private static final float MAX_SPEED = 1.0f;

    @Override
    public void onEnable() {
        getCommand("speed").setExecutor(this);
        getCommand("speed").setTabCompleter(this);
        getLogger().info("SpeedPlugin aktiviert.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1 || args.length > 2) {
            sender.sendMessage("§cBenutzung: /" + label + " <0-10> [Spieler]");
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cBitte gib eine ganze Zahl zwischen 0 und 10 an.");
            return true;
        }
        if (level < 0 || level > 10) {
            sender.sendMessage("§cDie Stufe muss zwischen 0 und 10 liegen.");
            return true;
        }

        Player target;
        if (args.length == 2) {
            if (!sender.hasPermission("speedplugin.others")) {
                sender.sendMessage("§cDu darfst die Geschwindigkeit anderer Spieler nicht aendern.");
                return true;
            }
            target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage("§cSpieler '" + args[1] + "' ist nicht online.");
                return true;
            }
        } else {
            if (!(sender instanceof Player p)) {
                sender.sendMessage("§cDie Konsole muss einen Spieler angeben: /" + label + " <0-10> <Spieler>");
                return true;
            }
            target = p;
        }

        // Stufe 0 = normale Geschwindigkeit, Stufe 10 = Maximum
        float speed = DEFAULT_SPEED + (MAX_SPEED - DEFAULT_SPEED) * (level / 10f);
        target.setWalkSpeed(speed);

        String text = level == 0 ? "normal (Stufe 0)" : "Stufe " + level;
        target.sendMessage("§aDeine Geschwindigkeit (Gehen & Sprinten) ist jetzt: §e" + text);
        if (!target.equals(sender)) {
            sender.sendMessage("§aGeschwindigkeit von §e" + target.getName() + " §aauf §e" + text + " §agesetzt.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (int i = 0; i <= 10; i++) {
                result.add(String.valueOf(i));
            }
        } else if (args.length == 2 && sender.hasPermission("speedplugin.others")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    result.add(p.getName());
                }
            }
        }
        return result;
    }
}
