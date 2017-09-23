package us.powderboarder.celestialChallenge;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
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



    private static String challengeTitle;
    private static String daily2;
    private static String challengeRules;
    private static String progressMsg;
    private static boolean dailyReward;
    private static boolean dailyRewardClaimed;

    private static Material randFood;
    private static ArrayList<Material> foodItems = new ArrayList<Material>();

    private int dropCount;
    private int playerDeathCount;

    private static int distanceChallenge;

    private static int distanceGoal;
    private int initialStepCount;
    private int initialBoatCount;
    private int initialClimbCount;
    private int initialDiveCount;
    private int initialFallCount;
    private int initialFlyCount;
    private int initialJumpCount;
    private int initialSprintCount;
    private int initialSwimCount;


    BossBar goalProgress = Bukkit.createBossBar("Progress to Goal - " + challengeTitle, BarColor.BLUE, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY, BarFlag.CREATE_FOG);
    double goalProgressBar;

    private long initialLoginTime;
    private long initialMoveTime;


    // Fires event when player joins server
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        // plays welecome message for player when joining server
        event.setJoinMessage("Welcome, " + event.getPlayer().getName() + "!");

        // Broadcasts to all a message
        Bukkit.broadcastMessage("The new Daily Challenge is: " + challengeTitle);
        Bukkit.broadcastMessage("Daily Challenge Rules: " + challengeRules);

        // Get initial player statistics values on login
        resetChallenge(event);

        // Setup goal progress bar, and add player to progress tracking
        goalProgress.setProgress((0)/50);
        goalProgress.addPlayer(event.getPlayer());

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
        if (challengeTitle.equals(daily2 + " - " + randFood) && (event.getItemDrop().getItemStack().getType().equals(randFood))){

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

        // TODO: 09/21/17 : make progress page, and test if player died my mob
        playerDeathCount ++;

        if (playerDeathCount == 2){
            Bukkit.broadcastMessage(event.getEntity().getDisplayName() + " has joined the river styx.");
            Bukkit.broadcastMessage("You have lost!");
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

        // Check if challengeTitle challenge is step challenge
        // Counts steps and adds meter for player to track progress toward goal
        if (distanceChallenge==1 && movePolling) {
            // Convert server measurement to total number of blocks walked.
            int totalWalkDistance = (event.getPlayer().getStatistic(Statistic.WALK_ONE_CM)) / 100;

            getGoalProgress(totalWalkDistance, initialStepCount, distanceGoal);
            Bukkit.broadcastMessage("Distance walked: " + totalWalkDistance);

            // Once step progress is 100% or more, display achievement message
            /*if (goalProgressBar >= 1.0) {
                Bukkit.broadcastMessage(event.getPlayer().getDisplayName() + " has walked the challengeTitle requirement of activity. Time to take a nap!");

                // Sets reward availability and restarts challenge
                resetChallenge(event);
                dailyReward = true;
            }*/
            //challengeProgress(event);
            checkGoalComplete(progressMsg, totalWalkDistance, event);
        }

        // checks if challengeTitle message is boat races
        // Counts distance travelled by boat, and give progress meter for player tracking.
        if (distanceChallenge==2 && movePolling){
            // Convert server measurement to total number of blocks
            int totalBoatDistance = (event.getPlayer().getStatistic(Statistic.BOAT_ONE_CM))/100;

            getGoalProgress(totalBoatDistance, initialBoatCount, distanceGoal);
            Bukkit.broadcastMessage("Distance by Boat: " + totalBoatDistance);

            /*if(goalProgressBar >= 1.0){
                Bukkit.broadcastMessage(event.getPlayer().getDisplayName() + " paddled a boat " + totalBoatDistance + " blocks!");

                // Sets reward availability and restarts challenge
                resetChallenge(event);
                dailyReward = true;
            }*/
            //challengeProgress(event);
            checkGoalComplete(progressMsg, totalBoatDistance, event);
        }

        // checks if challengeTitle message is Tree Climber - Vine/ladder climb
        // Counts the distance player has climbed by either vine or ladder, and provide progress bar for player.
        if (distanceChallenge==3 && movePolling){
            // Convert server measurement to total number of blocks
            int totalClimbDistance = (event.getPlayer().getStatistic(Statistic.CLIMB_ONE_CM))/100;

            getGoalProgress(totalClimbDistance, initialClimbCount, distanceGoal);
            Bukkit.broadcastMessage("Climbed Blocks: " + totalClimbDistance);

            //challengeProgress(event);
            checkGoalComplete(progressMsg, totalClimbDistance, event);
        }

        // checks if challengeTitle message is Olypmic Diver
        //
        if (distanceChallenge==4 && movePolling){
            // Convert server measurement to total number of blocks
            int totalDiveDistance = (event.getPlayer().getStatistic(Statistic.DIVE_ONE_CM))/100;

            getGoalProgress(totalDiveDistance, initialDiveCount, distanceGoal);
            Bukkit.broadcastMessage("Depth dived: " + totalDiveDistance);

            //challengeProgress(event);
            checkGoalComplete(progressMsg, totalDiveDistance, event);
        }

        // checks if challengeTitle message is Sky Diver
        //
        if (distanceChallenge==5 && movePolling){
            // Convert server measurement to total number of blocks
            int totalFallDistance = (event.getPlayer().getStatistic(Statistic.FALL_ONE_CM))/100;

            getGoalProgress(totalFallDistance, initialFallCount, distanceGoal);
            Bukkit.broadcastMessage("Distance fell: " + totalFallDistance);

            //challengeProgress(event);
            checkGoalComplete(progressMsg, totalFallDistance, event);
        }

        // checks if challengeTitle message is Soaring Eagle
        //
        if (distanceChallenge==6 && movePolling){
            // Convert server measurement to total number of blocks
            int totalFlyDistance = (event.getPlayer().getStatistic(Statistic.FLY_ONE_CM))/100;

            getGoalProgress(totalFlyDistance, initialFlyCount, distanceGoal);
            Bukkit.broadcastMessage("Distance flying: " + totalFlyDistance);

            //challengeProgress(event);
            checkGoalComplete(progressMsg, totalFlyDistance, event);
        }

        // checks if challengeTitle message is Jump, Jump - Counts number of jumps
        // Counts number of times player jumps, and updates goal progress bar.
        if (distanceChallenge==7 && movePolling){
            // total number of jumps calculated by server
            int totalJump = event.getPlayer().getStatistic(Statistic.JUMP);

            getGoalProgress(totalJump, initialJumpCount, distanceGoal);
            Bukkit.broadcastMessage("Total jumps: " + totalJump);

            //challengeProgress(event);
            checkGoalComplete(progressMsg, totalJump, event);
        }

        // checks if challengeTitle message is Run Forest - Count number of blocks run
        // Counts number of blocks players sprints, and updates progress bar progress.
        if (distanceChallenge==8 && movePolling){
            // Convert server measurement to total number of blocks
            int totalRunDistance = (event.getPlayer().getStatistic(Statistic.SPRINT_ONE_CM))/100;

            getGoalProgress(totalRunDistance, initialSprintCount, distanceGoal);
            Bukkit.broadcastMessage("Sprint Distance: " + totalRunDistance);

            //challengeProgress(event);
            checkGoalComplete(progressMsg, totalRunDistance, event);
        }

        // checks if challengeTitle message is Michael Phelps
        if (distanceChallenge==9 && movePolling){
            // Convert server measurement to total number of blocks
            int totalSwimDistance = (event.getPlayer().getStatistic(Statistic.SWIM_ONE_CM))/100;

            getGoalProgress(totalSwimDistance, initialSwimCount, distanceGoal);
            Bukkit.broadcastMessage("Distance swam: " + totalSwimDistance);

            //challengeProgress(event);
            checkGoalComplete(progressMsg, totalSwimDistance, event);
        }

        // checks if challengeTitle message is ...
        //if (distanceChallenge== && movePolling){

        //}

    }

    // TODO : Add command to allow server administrator to be able to reset daily challenges or disable challenges
    public static String setDaily(){
        int totalChallenges = 11;

        // setup random number generator
        Random rand = new Random();
        int randNum = rand.nextInt(totalChallenges) + 1;

        // Setup distance challenges goal amount
        int minDistance = 10;
        int maxDistance = 100;
        distanceGoal = rand.nextInt(maxDistance) + minDistance;

        if (randNum == 1){
            daily2 =  "Feed the Planet";
            challengeRules = "Player must drop a specific number of food to get reward";
            progressMsg = "Total food dropped: ";

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


            challengeTitle = daily2 + " - " + randFood;
        }
        if (randNum == 2){
            challengeTitle = "First to Die";
            challengeRules = "Player accumulates death to get reward";
            progressMsg = "Current player deaths: ";
        }
        if (randNum == 3) {
            challengeTitle = "Step off! Challenge - " + distanceGoal + " blocks";
            challengeRules = "Player accumulates steps while walking to get reward";
            progressMsg = "Current block distance (walking): ";

            // Set which distance challenge to look at
            distanceChallenge = 1;
        }
        if (randNum == 4) {
            challengeTitle = "Boat Race! Challenge - " + distanceGoal + " blocks";
            challengeRules = "Player accumulates # of block while using a boar to get reward";
            progressMsg = "Current block distance (by boat): ";

            // Set which distance challenge to look at
            distanceChallenge = 2;
        }
        if (randNum == 5) {
            challengeTitle = "Tree Climber! Challenge - " + distanceGoal + " blocks";
            challengeRules = "Player accumulates # of blocks while climbing vines or ladders to get reward";
            progressMsg = "Current block distance (climbing): ";

            // Set which distance challenge to look at
            distanceChallenge = 3;
        }
        if (randNum == 6) {
            challengeTitle = "Olympic Diver! Challenge - " + distanceGoal + " blocks";
            challengeRules = "Player accumulates # of blocks while underwater to get reward";
            progressMsg = "Current block distance (diving): ";

            // Set which distance challenge to look at
            distanceChallenge = 4;
        }
        if (randNum == 7) {
            challengeTitle = "Sky Diver! Challenge - " + distanceGoal + " blocks";
            challengeRules = "Player accumulates # of blocks while falling to get reward";
            progressMsg = "Current block distance (falling): ";

            // Set which distance challenge to look at
            distanceChallenge = 5;
        }
        if (randNum == 8) {
            challengeTitle = "Soaring Eagle! Challenge - " + distanceGoal + " blocks";
            challengeRules = "Player accumulates # of block while flying to get reward";
            progressMsg = "Current block distance (flying): ";

            // Set which distance challenge to look at
            distanceChallenge = 6;
        }
        if (randNum == 9) {
            challengeTitle = "Jump, Jump! Challenge - " + distanceGoal + " blocks";
            challengeRules = "Player accumulates # of jumps to get reward";
            progressMsg = "Current jump count: ";

            // Set which distance challenge to look at
            distanceChallenge = 7;
        }
        if (randNum == 10) {
            challengeTitle = "Run, Forest! Challenge - " + distanceGoal + " blocks";
            challengeRules = "Player accumulates steps while sprinting to get reward";
            progressMsg = "Current block distance (sprinting): ";

            // Set which distance challenge to look at
            distanceChallenge = 8;
        }
        if (randNum == 11) {
            challengeTitle = "Michael Phelps! Challenge - " + distanceGoal + " blocks";
            challengeRules = "Player accumulates # of blocks while swimming to get reward";
            progressMsg = "Current block distance (swimming): ";

            // Set which distance challenge to look at
            distanceChallenge = 9;
        }

        return challengeTitle;
    }

    private void resetChallenge(PlayerEvent event){
        // reset all initial variables
        initialStepCount = event.getPlayer().getStatistic(Statistic.WALK_ONE_CM)/100;
        initialBoatCount  = event.getPlayer().getStatistic(Statistic.BOAT_ONE_CM)/100;
        initialClimbCount = event.getPlayer().getStatistic(Statistic.CLIMB_ONE_CM)/100;
        initialDiveCount = event.getPlayer().getStatistic(Statistic.DIVE_ONE_CM)/100;
        initialFallCount = event.getPlayer().getStatistic(Statistic.FALL_ONE_CM)/100;
        initialFlyCount = event.getPlayer().getStatistic(Statistic.FLY_ONE_CM)/100;
        initialJumpCount = event.getPlayer().getStatistic(Statistic.JUMP);
        initialSprintCount = event.getPlayer().getStatistic(Statistic.SPRINT_ONE_CM)/100;
        initialSwimCount = event.getPlayer().getStatistic(Statistic.SWIM_ONE_CM)/100;
    }
    private double getGoalProgress(int totalCount, int initialCount, double totalGoal){
        // Gets current step progress to goal - current test is 50 blocks.
        goalProgressBar = (totalCount - initialCount) / totalGoal;

        // Check if goalProgressBar is larger than 1, which will cause an exception
        if (goalProgressBar > 1.0){
            goalProgressBar=1.0;
        }

        // Updates the step overview meter bar at top.
        goalProgress.setProgress(goalProgressBar);

        return goalProgressBar;
    }

    private Boolean checkGoalComplete(String progressMsg, int totalCount, PlayerMoveEvent event){
        Bukkit.broadcastMessage(progressMsg + totalCount);

        if (goalProgressBar >=1.0){
            Bukkit.broadcastMessage("You Win!");

            // Sets reward availability and restarts challenge
            resetChallenge(event);
            dailyReward = true;
        }
        return dailyReward;
    }


    public static String getDailyChallenges(){
        return challengeTitle;
    }
    public static String getDailyChallenges2(){
        return daily2;
    }
    public static String getDailyChallengeRules(){
        return challengeRules;
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
    /*public void challengeProgress(PlayerEvent event){
        if(goalProgressBar >= 1.0){
            Bukkit.broadcastMessage("You Win!");

            // Sets reward availability and restarts challenge
            resetChallenge(event);
            dailyReward = true;
        }
    }*/
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
