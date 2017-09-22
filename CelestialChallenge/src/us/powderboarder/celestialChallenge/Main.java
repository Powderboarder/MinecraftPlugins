package us.powderboarder.celestialChallenge;

//import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

/**
 * Created by Emily (Powderboarder) on 09/08/17.
 * Made for: Celestial Gaming Minecraft Server
 * Project: Celestial Challenge
 *
 * Project Details:
 * Minecraft plugin that will introduce daily and weekly challenges for player to accomplish. If achieved a reward can
 * claimed
 *
 * Environment: IntelliJ IDEA.
 */
public class Main extends JavaPlugin {


    @Override
    public void onEnable() {
        // Fired when the server enables the plugin

        // Display message on back-end that plugin has been enabled.
        System.out.println("Enabled Celestial Challenges");

        // Register event listener - passes events to plugin
        getServer().getPluginManager().registerEvents(new MyListener(), this);

        // set daily challenge
        MyListener.setDaily();
        System.out.println("Daily Challenge: " + MyListener.getDailyChallenges());
        System.out.println("Daily Challenge: " + MyListener.getDailyChallenges2());
        System.out.println("Daily Challenge Rules: " + MyListener.getDailyChallengeRules());
    }

    @Override
    public void onDisable() {
        // Fired when the server stops and disables all plugins
    }

    /*
    * CommandSender represents whatever is sending the command - Player, ConsoleCommandSender, or BlockCommandSender
    * Command represents what the command being called is - known ahead of time
    * Label represents the exact first word of the command that is entered by the sender
    * Arges is the remainder of the command statement split up by spaces and put into an array
    * */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            // Creates object of player which will take CommandSender information if Player
            Player player = (Player) sender;

            // Give items to player here

            boolean dailyReward = MyListener.getDailyReward();
            boolean dailyRewardClaimed = MyListener.getDailyRewardClaimed();

            if (dailyReward == true && dailyRewardClaimed == false) {
                // Create a new ItemStack (type: diamond)
                ItemStack diamond = new ItemStack(Material.DIAMOND);

                // Create a new ItemStack (type: brick)
                ItemStack bricks = new ItemStack(Material.BRICK);

                // Set the amount of the ItemStack
                bricks.setAmount(20);

                // Give the player our items (comma-seperated list of all ItemStack)
                player.getInventory().addItem(bricks, diamond);

                // Update reward claim
                MyListener.setDailyRewardClaimed(true);
            } else if (dailyReward == true && dailyRewardClaimed == true){
                player.sendRawMessage("You have already claimed your daily reward.");
            } else {
                player.sendRawMessage("You have not achieved this challenge yet.");
            }
        }

        // if the player or console uses our command
        return true;
    }

}

