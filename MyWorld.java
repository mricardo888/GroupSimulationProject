import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.Random;

/**
 * The game world where units spawn and battle.
 */
public class MyWorld extends World
{
    // Initial gold and XP from Settings
    private int gold1;
    private int gold2;
    
    // Unit costs
    private static final int LOW_COST = 30;
    private static final int MID_COST = 70;
    private static final int HIGH_COST = 130;
    
    // XP rewards for defeating units
    private static final int LOW_XP_REWARD = 10;
    private static final int MID_XP_REWARD = 25;
    private static final int HIGH_XP_REWARD = 50;
    
    // XP thresholds for advancing to next age
    private static final int AGE_2_XP_THRESHOLD = 100;
    private static final int AGE_3_XP_THRESHOLD = 300;
    private static final int AGE_4_XP_THRESHOLD = 700;
    private int xp1;
    private int xp2;
    
    // Game state variables
    private int age = 1;
    private int spawnTimer1 = 0;
    private int spawnTimer2 = 0;
    private int spawnDelay = 100; // Delay between spawns
    
    // Display labels
    private Label goldLabel1;
    private Label goldLabel2;
    private Label xpLabel1;
    private Label xpLabel2;
    private Label ageLabel1;
    private Label ageLabel2;
    
    /**
     * Constructor for the game world.
     */
    public MyWorld()
    {    
        // Create a new world with 1024x800 cells with a cell size of 1x1 pixels.
        super(1024, 800, 1);
        
        // Set background image
        setBackground("images/background.png");
        
        // Initialize gold and XP from Settings
        gold1 = Settings.getGold1() > 0 ? Settings.getGold1() : 100;
        gold2 = Settings.getGold2() > 0 ? Settings.getGold2() : 100;
        xp1 = Settings.getXp1();
        xp2 = Settings.getXp2();
        
        // Create gold display labels
        goldLabel1 = new Label("Gold: " + gold1, 24);
        goldLabel2 = new Label("Gold: " + gold2, 24);
        addObject(goldLabel1, 100, 40);
        addObject(goldLabel2, getWidth() - 100, 40);
        
        // Create XP display labels
        xpLabel1 = new Label("XP: " + xp1, 24);
        xpLabel2 = new Label("XP: " + xp2, 24);
        addObject(xpLabel1, 100, 70);
        addObject(xpLabel2, getWidth() - 100, 70);
        
        // Create Age display labels
        ageLabel1 = new Label("Age: " + getAgeDescription(1), 24);
        ageLabel2 = new Label("Age: " + getAgeDescription(2), 24);
        addObject(ageLabel1, 100, 100);
        addObject(ageLabel2, getWidth() - 100, 100);
        
        // Initial spawn
        spawnInitialUnits();
    }
    
    /**
     * The main game loop.
     */
    public void act() {
        // Update gold display
        goldLabel1.setValue("Gold: " + gold1);
        goldLabel2.setValue("Gold: " + gold2);
        
        // Update XP display
        xpLabel1.setValue("XP: " + xp1);
        xpLabel2.setValue("XP: " + xp2);
        
        // Check if any side can advance to the next age
        checkAgeAdvancement();
        
        // Check spawn timers and spawn units if ready
        if (spawnTimer1 <= 0) {
            side1();
            spawnTimer1 = spawnDelay;
        } else {
            spawnTimer1--;
        }
        
        if (spawnTimer2 <= 0) {
            side2();
            spawnTimer2 = spawnDelay;
        } else {
            spawnTimer2--;
        }
        
        // Auto-generate gold over time
        if (Greenfoot.getRandomNumber(300) == 0) {
            gold1 += 10;
            gold2 += 10;
        }
    }
    
    /**
     * Spawn initial units for both sides.
     */
    private void spawnInitialUnits() {
        // Spawn one unit on each side to start
        spawnUnit(1, -1); // side 1 (right to left)
        spawnUnit(2, 1);  // side 2 (left to right)
    }
    
    /**
     * Add gold to a side
     */
    public void addGold(int side, int amount) {
        if (side == 1) {
            gold1 += amount;
        } else {
            gold2 += amount;
        }
    }
    
    /**
     * Add XP to a side
     */
    public void addXP(int side, int amount) {
        if (side == 1) {
            xp1 += amount;
        } else {
            xp2 += amount;
        }
    }
    
    /**
     * Check if any side can advance to the next age
     */
    private void checkAgeAdvancement() {
        // Side 1 age advancement
        int currentAge1 = getPlayerAge(1);
        if (currentAge1 < 4) { // Max age is 4
            int nextAgeThreshold = getNextAgeThreshold(currentAge1);
            if (xp1 >= nextAgeThreshold) {
                advanceAge(1);
            }
        }
        
        // Side 2 age advancement
        int currentAge2 = getPlayerAge(2);
        if (currentAge2 < 4) { // Max age is 4
            int nextAgeThreshold = getNextAgeThreshold(currentAge2);
            if (xp2 >= nextAgeThreshold) {
                advanceAge(2);
            }
        }
    }
    
    /**
     * Advance a player's age
     */
    private void advanceAge(int side) {
        if (side == 1) {
            age = Math.min(age + 1, 4); // Side 1's age (capped at 4)
            ageLabel1.setValue("Age: " + getAgeDescription(1));
            Greenfoot.playSound("level-up.wav"); // Optional: play a sound effect
        } else {
            // For side 2, we track it separately (we could add a player2Age variable)
            // For now we'll just increment the label
            ageLabel2.setValue("Age: " + getAgeDescription(2));
            Greenfoot.playSound("level-up.wav"); // Optional: play a sound effect
        }
    }
    
    /**
     * Get the age description based on the age number
     */
    private String getAgeDescription(int side) {
        int playerAge = getPlayerAge(side);
        switch (playerAge) {
            case 1: return "Stone Age";
            case 2: return "Cave Age";
            case 3: return "Modern Age";
            case 4: return "Space Age";
            default: return "Unknown Age";
        }
    }
    
    /**
     * Get the current age of a player
     */
    private int getPlayerAge(int side) {
        if (side == 1) {
            return age; // Side 1's age
        } else {
            // For side 2, we could track it separately
            // For this example, we'll calculate it based on XP
            if (xp2 >= AGE_4_XP_THRESHOLD) return 4;
            if (xp2 >= AGE_3_XP_THRESHOLD) return 3;
            if (xp2 >= AGE_2_XP_THRESHOLD) return 2;
            return 1;
        }
    }
    
    /**
     * Get the XP threshold for the next age
     */
    private int getNextAgeThreshold(int currentAge) {
        switch (currentAge) {
            case 1: return AGE_2_XP_THRESHOLD;
            case 2: return AGE_3_XP_THRESHOLD;
            case 3: return AGE_4_XP_THRESHOLD;
            default: return Integer.MAX_VALUE; // No next age
        }
    }
    
    /**
     * Algorithm for spawning units on side 1 (right side).
     */
    private void side1() {
        if (gold1 >= LOW_COST) {
            // Decide which unit to spawn based on available gold
            int unitChoice = decideUnitToSpawn(gold1);
            spawnUnitBySide(1, unitChoice);
        }
    }
    
    /**
     * Algorithm for spawning units on side 2 (left side).
     */
    private void side2() {
        if (gold2 >= LOW_COST) {
            // Decide which unit to spawn based on available gold
            int unitChoice = decideUnitToSpawn(gold2);
            spawnUnitBySide(2, unitChoice);
        }
    }
    
    /**
     * Decide which unit to spawn based on available gold.
     * Returns 1 for Low, 2 for Mid, 3 for High
     */
    private int decideUnitToSpawn(int gold) {
        if (gold >= HIGH_COST && Greenfoot.getRandomNumber(10) < 3) {
            return 3; // 30% chance to spawn High if enough gold
        } else if (gold >= MID_COST && Greenfoot.getRandomNumber(10) < 5) {
            return 2; // 50% chance to spawn Mid if enough gold
        } else {
            return 1; // Otherwise spawn Low
        }
    }
    
    /**
     * Spawn a unit on the specified side.
     */
    private void spawnUnitBySide(int side, int unitType) {
        int direction = (side == 1) ? -1 : 1;
        Unit unit = null;
        int hp = 100; // Default HP
        int cost = 0;
        int playerAge = getPlayerAge(side);
        
        switch (unitType) {
            case 3: // High
                unit = new High(playerAge, hp * 2, direction);
                cost = HIGH_COST;
                break;
            case 2: // Mid
                unit = new Mid(playerAge, hp * 3/2, direction);
                cost = MID_COST;
                break;
            default: // Low
                unit = new Low(playerAge, hp, direction);
                cost = LOW_COST;
                break;
        }
        
        // Deduct gold based on side
        if (side == 1) {
            gold1 -= cost;
        } else {
            gold2 -= cost;
        }
        
        // Add the unit to the world
        if (side == 1) {
            addObject(unit, getWidth() - 50, 600);
        } else {
            addObject(unit, 50, 600);
        }
    }
    
    /**
     * Helper method to spawn a unit on the specified side.
     */
    private void spawnUnit(int side, int direction) {
        int playerAge = getPlayerAge(side);
        Unit unit = new Low(playerAge, 100, direction);
        if (side == 1) {
            addObject(unit, getWidth() - 50, 600);
            gold1 -= LOW_COST;
        } else {
            addObject(unit, 50, 600);
            gold2 -= LOW_COST;
        }
    }
}