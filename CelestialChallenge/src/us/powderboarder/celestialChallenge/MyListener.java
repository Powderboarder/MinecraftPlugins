package us.powderboarder.celestialChallenge;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by Emily (Powderboarder) on 09/10/17.
 * Made for: Celestial Gaming Minecraft Server
 * Project: Celestial Challenge
 *
 * Project Details:
 * Minecraft plugin that will introduce daily and weekly challenges for player to accomplish. If achieved a reward can
 * claimed
 *
 * Environment: IntelliJ IDEA.
 */
public class MyListener implements Listener {



    private static String daily;
    private static String daily2;
    private static String dailyRules;
    private static boolean dailyReward;
    private static boolean dailyRewardClaimed;

    private static Material randFood;
    private static ArrayList<Material> foodItems = new ArrayList<Material>();

    private int dropCount;
    private int playerDeathCount;

    private int initialStepCount;
    BossBar walkProgress = Bukkit.createBossBar("Steps to Goal", BarColor.BLUE, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY, BarFlag.CREATE_FOG);


    private long initialLoginTime;
    private long initialMoveTime;


    // Fires event when player joins server
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        // plays welecome message for player when joining server
        event.setJoinMessage("Welcome, " + event.getPlayer().getName() + "!");

        // Broadcasts to all a message
        Bukkit.broadcastMessage("The new Daily Challenge is: " + daily);
        Bukkit.broadcastMessage("Daily Challenge Rules: " + dailyRules);

        initialStepCount = event.getPlayer().getStatistic(Statistic.WALK_ONE_CM)/100;
        walkProgress.setProgress((0)/50);
        walkProgress.addPlayer(event.getPlayer());

        // Get initial time of login for polling player movement every 30 secs
        initialLoginTime = event.getPlayer().getPlayerTime();
        initialMoveTime = initialLoginTime;
        Bukkit.broadcastMessage("Time: " + initialLoginTime);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        // Broadcasts to all a message
        /*Bukkit.broadcastMessage(event.getPlayer().getName() + " dropped "
            + event.getItemDrop());*/

        //Check if dropped item matches specific item
        if (daily.equals(daily2 + " - " + randFood) && (event.getItemDrop().getItemStack().getType().equals(randFood))){

            // Test message to verify that item type is being tracked correctly.
            /*Bukkit.broadcastMessage(event.getPlayer().getName() + " dropped tested 1 "
                    + randFood);*/


            // increment item drop counter
            dropCount ++;
        }


        // Check if dropCount matches goals (test amount)
        if (dropCount == 5){
            Bukkit.broadcastMessage(event.getPlayer().getName() + " dropped 5 "
                    + Material.BRICK.name());

            Bukkit.broadcastMessage(event.getPlayer().getName() + " dropped 5 "
                    + randFood);

            // reset item drop counter
            dropCount = 0;
            dailyReward = true;
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        playerDeathCount ++;

        if (playerDeathCount == 2){
            Bukkit.broadcastMessage(event.getEntity().getDisplayName() + " has joined the river styx.");
            playerDeathCount = 0;
            dailyReward = true;
        }
    }

    // Might not want to use due to amount of stress on server resources. Might try polling every 30 seconds.
    @EventHandler
    public void onPlayerStep(PlayerMoveEvent event){

        boolean movePolling=false;
        long moveTime = event.getPlayer().getPlayerTime();

        // attempt to wait 30 seconds then display message. Believe it is measured in milliseconds.
        // Believed that this is based off of server tick speed. typically 20 ticks per second.
        // Would need 20*30 for 30 secs, which was verified to be 30 secs.
        if ((initialMoveTime + (20*30)) <= moveTime){
            Bukkit.broadcastMessage("30 Seconds reached.");
            initialMoveTime=moveTime;
            movePolling = true;
        }

        // Check if daily challenge is step challenge
        if (daily == "Step off!" && movePolling==true ) {
            // On player move, get current measurement from server
            int totalWalkedCM = event.getPlayer().getStatistic(Statistic.WALK_ONE_CM);

            // Convert server measurement to total number of blocks walked.
            int currentBlocksWalked = totalWalkedCM / 100;

            // Gets current step progress to goal - current test is 50 blocks.
            double stepProgress = (currentBlocksWalked - initialStepCount) / 50.0;

            // Updates the step overview meter bar at top.
            walkProgress.setProgress(stepProgress);

            // Once step progress is 100% or more, display achievement message
            if (stepProgress >= 1) {
                Bukkit.broadcastMessage(event.getPlayer().getDisplayName() + " has walked the daily requirement of activity. Time to take a nap!");
            }
        }

        // checks if daily message is boat races
        //if (daily == ""){
            int totalBoatDistance = (event.getPlayer().getStatistic(Statistic.BOAT_ONE_CM))/100;

            /*if(){
                Bukkit.broadcastMessage(event.getPlayer().getDisplayName() + " paddled a boat!");

            }*/
        //}

        // checks if daily message is Mountain Climber
        //if (daily == ""){
            int totalClimbDistance = (event.getPlayer().getStatistic(Statistic.CLIMB_ONE_CM))/100;
        //}

        // checks if daily message is Olypmic Diver
        //if (daily == ""){
            int totalDiveDistance = (event.getPlayer().getStatistic(Statistic.DIVE_ONE_CM))/100;
        //}

        // checks if daily message is Sky Diver
        //if (daily == ""){
            int totalFallDistance = (event.getPlayer().getStatistic(Statistic.FALL_ONE_CM))/100;
        //}

        // checks if daily message is Soaring Eagle
        //if (daily == ""){
            int totalFlyDistance = (event.getPlayer().getStatistic(Statistic.FLY_ONE_CM))/100;
        //}

        // checks if daily message is Jump, Jump
        //if (daily == ""){
            int totalJump = event.getPlayer().getStatistic(Statistic.JUMP);
        //}

        // checks if daily message is Run Forest
        //if (daily == ""){
            int totalRunDistance = (event.getPlayer().getStatistic(Statistic.SPRINT_ONE_CM))/100;
        //}

        // checks if daily message is Michael Phelps
        //if (daily == ""){
            int totalSwimDistance = (event.getPlayer().getStatistic(Statistic.SWIM_ONE_CM))/100;
        //}

        // checks if daily message is ...
        //if (daily == ""){

        //}

    }



    public static String setDaily(){
        int totalChallenges = 1;

        // setup random number generator
        Random rand = new Random();
        int randNum = rand.nextInt(totalChallenges) + 1;

        if (randNum == 1){
            daily2 =  "Feed the Planet";
            dailyRules = "Player must drop a specific number of food to get reward";

            // Setup new random number for use with FoodList array
            Random randMat = new Random();

            // Setup FoodList Array
            foodList();

            // Get size of FooList array, and generates random item from list
            int numFood = foodItems.size();
            System.out.println(numFood);
            int randMaterial = randMat.nextInt(foodItems.size());

            // Pulls out Material item from foodList array based on random value
            randFood = foodItems.get(randMaterial);


            daily = daily2 + " - " + randFood;
        }
        if (randNum == 2){
            daily = "First to Die";
            dailyRules = "Player accumulates death to get reward";
        }
        if (randNum == 3) {
            daily = "Step off!";
            dailyRules = "Player accumulates steps while walking to get reward";
        }

        return daily;
    }

    private String getDateTime(){
        // set time variable to polling every so often.
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);

    }

    public static String getDailyChallenges(){
        return daily;
    }
    public static String getDailyChallenges2(){
        return daily2;
    }
    public static String getDailyChallengeRules(){
        return dailyRules;
    }
    public static boolean getDailyReward(){
        return dailyReward;
    }
    public static boolean getDailyRewardClaimed(){
        return dailyRewardClaimed;
    }
    public static boolean setDailyReward(){
        return dailyReward;
    }
    public static boolean setDailyRewardClaimed(boolean claimed){
        dailyRewardClaimed = claimed;
        return dailyRewardClaimed;
    }
    public static ArrayList<Material> foodList() {

        foodItems.add(Material.APPLE);
        foodItems.add(Material.BAKED_POTATO);
        foodItems.add(Material.BEETROOT);
        foodItems.add(Material.BEETROOT_SEEDS);
        foodItems.add(Material.BREAD);
        foodItems.add(Material.BROWN_MUSHROOM);
        foodItems.add(Material.CAKE);
        foodItems.add(Material.CARROT);
        foodItems.add(Material.COCOA);
        foodItems.add(Material.COOKED_BEEF);
        foodItems.add(Material.COOKED_CHICKEN);
        foodItems.add(Material.COOKED_FISH);
        foodItems.add(Material.COOKED_MUTTON);
        foodItems.add(Material.COOKED_RABBIT);
        foodItems.add(Material.COOKIE);
        foodItems.add(Material.MELON);
        foodItems.add(Material.MELON_SEEDS);
        foodItems.add(Material.MILK_BUCKET);
        foodItems.add(Material.MUSHROOM_SOUP);
        foodItems.add(Material.MUTTON);
        foodItems.add(Material.PORK);
        foodItems.add(Material.POTATO);
        foodItems.add(Material.PUMPKIN);
        foodItems.add(Material.PUMPKIN_PIE);
        foodItems.add(Material.PUMPKIN_SEEDS);
        foodItems.add(Material.RABBIT);
        foodItems.add(Material.RABBIT_STEW);
        foodItems.add(Material.RAW_BEEF);
        foodItems.add(Material.RAW_CHICKEN);
        foodItems.add(Material.RAW_FISH);
        foodItems.add(Material.RED_MUSHROOM);
        foodItems.add(Material.SEEDS);
        foodItems.add(Material.SUGAR);
        foodItems.add(Material.SUGAR_CANE);
        foodItems.add(Material.WHEAT);

        return foodItems;
    }
}
