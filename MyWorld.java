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
    private int xp1;
    private int xp2;
    
    // Unit costs
    private static final int LOW_COST = 30;
    private static final int MID_COST = 70;
    private static final int HIGH_COST = 130;
    
    // Special skill costs
    private static final int METEOR_COST = 200;
    private static final int RAINING_ARROWS_COST = 300;
    private static final int PLANE_BOMB_COST = 400;
    private static final int LASER_BEAM_COST = 500;
    
    // XP rewards for defeating units
    private static final int LOW_XP_REWARD = 10;
    private static final int MID_XP_REWARD = 25;
    private static final int HIGH_XP_REWARD = 50;
    
    // # of spawned units
    private int unitsSpawnedSide1 = 0;
    private int unitsSpawnedSide2 = 0;
    private static final int UNITS_BEFORE_SKILLS = 25;
    
    // XP thresholds for advancing to next age
    private static final int AGE_2_XP_THRESHOLD = 100;
    private static final int AGE_3_XP_THRESHOLD = 300;
    private static final int AGE_4_XP_THRESHOLD = 700;
    private int unitCountThreshold = 25;
    
    // Add kill counters and trigger threshold
    private int killCount1 = 0; // Kill counter for side 1
    private int killCount2 = 0; // Kill counter for side 2
    private int killThreshold1 = 0; // Current kill threshold for side 1
    private int killThreshold2 = 0; // Current kill threshold for side 2
        
    // Game state variables
    private int age1 = 1; // Age for player 1
    private int age2 = 1; // Age for player 2
    private int spawnTimer1 = 0;
    private int spawnTimer2 = 0;
    private int spawnDelay = 60; // Delay between spawns
    
    // Special skill references
    private Meteor meteor;
    private RainingArrows rainingArrows;
    private PlaneBomb planeBomb;
    private LaserBeam laserBeam;
    
    // Skill timers
    private int skillTimer1 = 0;
    private int skillTimer2 = 0;
    private int skillDelay = 500; // Longer delay than unit spawning
    
    // Display labels
    private Label goldLabel1;
    private Label goldLabel2;
    private Label xpLabel1;
    private Label xpLabel2;
    private Label ageLabel1;
    private Label ageLabel2;
    private Label killLabel1;
    private Label killLabel2;

    
    // Tower references
    private Tower tower1;
    private Tower tower2;
    
    // Tower HP based on age
    private static final int TOWER_BASE_HP = 1000;
    
    // Game state
    private boolean gameEnded = false;
    
    /**
     * Constructor for the game world.
     */
    public MyWorld() {
        // Create a new world with 1024x800 cells with a cell size of 1x1 pixels.
        super(1024, 800, 1);
        
        // Set background image
        setBackground("images/background.png");
        
        // Initialize gold and XP from Settings with higher defaults
        gold1 = Settings.getGold1() > 0 ? Settings.getGold1() : 200;
        gold2 = Settings.getGold2() > 0 ? Settings.getGold2() : 200;
        xp1 = Settings.getXp1();
        xp2 = Settings.getXp2();
        
        // Initialize kill thresholds (random between 5-10)
        resetKillThreshold1();
        resetKillThreshold2();
        
        // Create UI elements
        setupUI();
        
        // Create towers for both sides
        setupTowers();
        
        // Setup special skills
        setupSpecialSkills();
        
        // Initial spawn
        spawnInitialUnits();
    }
    /**
     * Set up UI elements like labels and buttons
     */
    private void setupUI() {
        // Create UI panel backgrounds
        GreenfootImage panel1 = new GreenfootImage(200, 120);
        panel1.setColor(new Color(0, 0, 0, 150));
        panel1.fillRect(0, 0, 200, 120);
        panel1.setColor(Color.WHITE);
        panel1.drawRect(0, 0, 199, 119);
        getBackground().drawImage(panel1, 50, 20);
        
        GreenfootImage panel2 = new GreenfootImage(200, 120);
        panel2.setColor(new Color(0, 0, 0, 150));
        panel2.fillRect(0, 0, 200, 120);
        panel2.setColor(Color.WHITE);
        panel2.drawRect(0, 0, 199, 119);
        getBackground().drawImage(panel2, getWidth() - 250, 20);
        
        // Add player titles
        Label player1Title = new Label("Player 1", 30);
        player1Title.setLineColor(Color.YELLOW);
        addObject(player1Title, 150, 30);
        
        Label player2Title = new Label("Player 2", 30);
        player2Title.setLineColor(Color.YELLOW);
        addObject(player2Title, getWidth() - 150, 30);
        
        // Create gold display labels with icons
        try {
            GreenfootImage goldIcon = new GreenfootImage("gold.png");
            goldIcon.scale(30, 30);
            getBackground().drawImage(goldIcon, 60, 60);
            getBackground().drawImage(goldIcon, getWidth() - 240, 60);
        } catch (Exception e) {
            // If gold icon isn't available, just leave it out
        }
        
        goldLabel1 = new Label("Gold: " + gold1, 24);
        goldLabel2 = new Label("Gold: " + gold2, 24);
        addObject(goldLabel1, 150, 70);
        addObject(goldLabel2, getWidth() - 150, 70);
        
        // Create XP display labels
        try {
            GreenfootImage xpIcon = new GreenfootImage("xp.png");
            xpIcon.scale(30, 30);
            getBackground().drawImage(xpIcon, 60, 100);
            getBackground().drawImage(xpIcon, getWidth() - 240, 100);
        } catch (Exception e) {
            // If xp icon isn't available, just leave it out
        }
        
        xpLabel1 = new Label("XP: " + xp1, 24);
        xpLabel2 = new Label("XP: " + xp2, 24);
        addObject(xpLabel1, 150, 110);
        addObject(xpLabel2, getWidth() - 150, 110);
        
        // Create Age display labels
        ageLabel1 = new Label("Age: " + getAgeDescription(1), 24);
        ageLabel2 = new Label("Age: " + getAgeDescription(2), 24);
        addObject(ageLabel1, 150, 150);
        addObject(ageLabel2, getWidth() - 150, 150);
        
        // Add kill counter displays
        killLabel1 = new Label("Kills: 0/" + killThreshold1, 20);
        killLabel2 = new Label("Kills: 0/" + killThreshold2, 20);
        addObject(killLabel1, 150, 180);
        addObject(killLabel2, getWidth() - 150, 180);
    }
    
    /**
     * Set up towers for both sides
     */
    private void setupTowers() {
        // Create towers with HP based on their age
        tower1 = new Tower(1, TOWER_BASE_HP * age1, age1);
        tower2 = new Tower(2, TOWER_BASE_HP * age2, age2);
        
        // Add towers to the world - positioned near the edges
        addObject(tower1, 100, 600); // Left side tower (close to left edge)
        addObject(tower2, getWidth() - 100, 600); // Right side tower (close to right edge)
    }

    
    /**
     * Initialize and setup the special skills
     */
    private void setupSpecialSkills() {
        // Create special skills
        meteor = new Meteor();
        rainingArrows = new RainingArrows();
        planeBomb = new PlaneBomb();
        laserBeam = new LaserBeam();
        
        // Create an initial transparent image for special skills
        GreenfootImage transparentImage = new GreenfootImage(1, 1);
        transparentImage.setTransparency(0); // Make it fully transparent
        
        // Set the transparent image to each skill
        meteor.setImage(transparentImage);
        rainingArrows.setImage(transparentImage);
        planeBomb.setImage(transparentImage);
        laserBeam.setImage(transparentImage);
        
        // Add special skills to the world (initially invisible)
        // Place them at -100, -100 (offscreen) instead of 0, 0
        addObject(meteor, -100, -100);
        addObject(rainingArrows, -100, -100);
        addObject(planeBomb, -100, -100);
        addObject(laserBeam, -100, -100);
    }
    
    /**
     * The main game loop.
     */
    public void act() {
        // Skip if game has ended
        if (gameEnded) return;
        
        // Update gold display
        goldLabel1.setValue("Gold: " + gold1);
        goldLabel2.setValue("Gold: " + gold2);
        
        // Update XP display
        xpLabel1.setValue("XP: " + xp1);
        xpLabel2.setValue("XP: " + xp2);
        
        // Update kill counter display
        killLabel1.setValue("Kills: " + killCount1 + "/" + killThreshold1);
        killLabel2.setValue("Kills: " + killCount2 + "/" + killThreshold2);
        
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
        if (Greenfoot.getRandomNumber(200) == 0) {
            gold1 += 25;
            gold2 += 25;
        }
    }

    /**
     * Decide which skill to use based on available gold and age.
     * Returns 1 for Meteor, 2 for RainingArrows, 3 for PlaneBomb, 4 for LaserBeam, 0 for none
     */
    private int decideSkillToUse(int age, int gold) {
        // Random chance to use any skill (increased to 70% after threshold)
        if (Greenfoot.getRandomNumber(100) < 70) {
            // Choose the most powerful skill available for the age and gold
            if (age >= 4 && gold >= LASER_BEAM_COST) {
                return 4; // LaserBeam
            } else if (age >= 3 && gold >= PLANE_BOMB_COST) {
                return 3; // PlaneBomb
            } else if (age >= 2 && gold >= RAINING_ARROWS_COST) {
                return 2; // RainingArrows
            } else if (age >= 1 && gold >= METEOR_COST) {
                return 1; // Meteor
            }
        }
        return 0; // Don't use any skill this time
    }
    
    private int countUnits(int side) {
        int count = 0;
        for (Unit unit : getObjects(Unit.class)) {
            if ((side == 1 && unit.getDirection() == 1) || 
                (side == 2 && unit.getDirection() == -1)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Configure and activate the Meteor skill
     */
    private void configureMeteor(int side, int targetX, int targetY) {
        // Configure meteor for the target area - always target the enemy territory
        meteor.setMeteorCount(8);
        meteor.setSpawnRate(20);
        meteor.setSizeRange(20, 50);
        meteor.setSpeedRange(3, 6);
        meteor.setOwnerSide(side); // Set which side is using the skill
        
        int targetWidth = getWidth() / 3;
        
        // Ensure meteor targets the opposite side
        int enemyTargetX;
        if (side == 1) {
            enemyTargetX = getWidth() * 3 / 4; // Target right side (enemy of side 1)
        } else {
            enemyTargetX = getWidth() / 4; // Target left side (enemy of side 2)
        }
        
        meteor.setTargetArea(enemyTargetX, targetWidth);
        meteor.start();
    }
    
    /**
     * Configure and activate the LaserBeam skill
     */
    private void configureLaserBeam(int side, int targetX, int targetY) {
        // Configure laser beam to fire from the player's side across to the enemy side
        int direction, startX;
        
        if (side == 1) {
            // Side 1 fires from left to right
            direction = 0; // 0 = right
            startX = 100; // Start from left side
            targetY = 600; // Target height where most units will be
        } else {
            // Side 2 fires from right to left
            direction = 180; // 180 = left
            startX = getWidth() - 100; // Start from right side
            targetY = 600; // Target height where most units will be
        }
        
        laserBeam.setDirection(direction);
        laserBeam.setGunPosition(startX, targetY);
        laserBeam.setShots(5);
        laserBeam.setOwnerSide(side);
        laserBeam.start();
    }
    
    /**
     * Configure and activate the RainingArrows skill
     */
    private void configureRainingArrows(int side, int targetX, int targetY) {
        // Configure raining arrows for the target area - always enemy territory
        rainingArrows.setCoverage(40); // 40% coverage
        rainingArrows.setDuration(150);
        rainingArrows.setDensity(3);
        rainingArrows.setOwnerSide(side);
        
        // Target the enemy territory
        int enemyTargetX;
        if (side == 1) {
            enemyTargetX = getWidth() * 3 / 4; // Target right side (enemy of side 1)
        } else {
            enemyTargetX = getWidth() / 4; // Target left side (enemy of side 2)
        }
        
        int targetWidth = getWidth() / 3;
        rainingArrows.setTargetArea(enemyTargetX, targetWidth);
        
        rainingArrows.start();
    }
    
    /**
     * Configure and activate the PlaneBomb skill
     */
    private void configurePlaneBomb(int side, int targetX, int targetY) {
        // Configure plane direction
        int direction;
        
        if (side == 1) {
            // Side 1 plane should fly in a direction to drop bombs on enemy (side 2)
            // Since side 2 is on the right, plane should fly left to right for better targeting
            direction = 1; // Left to right
        } else {
            // Side 2 plane should fly in a direction to drop bombs on enemy (side 1)
            // Since side 1 is on the left, plane should fly right to left for better targeting
            direction = -1; // Right to left
        }
        
        planeBomb.setDirection(direction);
        planeBomb.setOwnerSide(side);
        planeBomb.start();
    }
    
    /**
     * Use the selected skill from the specified side.
     * @param side The side using the skill (1 or 2)
     * @param skillType 1 for Meteor, 2 for RainingArrows, 3 for PlaneBomb, 4 for LaserBeam
     */
    private void useSkill(int side, int skillType) {
        int targetX;
        int targetY = getHeight() / 2;
        
        // Target the opposite side
        if (side == 1) {
            targetX = getWidth() * 3 / 4; // Target right side for side 1
        } else {
            targetX = getWidth() / 4; // Target left side for side 2
        }
        
        // Deduct gold based on skill type
        int cost = 0;
        
        switch (skillType) {
            case 4: // LaserBeam
                configureLaserBeam(side, targetX, targetY);
                cost = LASER_BEAM_COST;
                break;
            case 3: // PlaneBomb
                configurePlaneBomb(side, targetX, targetY);
                cost = PLANE_BOMB_COST;
                break;
            case 2: // RainingArrows
                configureRainingArrows(side, targetX, targetY);
                cost = RAINING_ARROWS_COST;
                break;
            case 1: // Meteor
                configureMeteor(side, targetX, targetY);
                cost = METEOR_COST;
                break;
        }
        
        // Deduct gold
        if (side == 1) {
            gold1 -= cost;
        } else {
            gold2 -= cost;
        }
    }    
    /**
     * Spawn initial units for both sides.
     */
    private void spawnInitialUnits() {
        // Spawn one unit on each side to start with correct directions
        spawnUnit(1, 1);  // side 1 (left to right)
        spawnUnit(2, -1); // side 2 (right to left)
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
        if (age1 < 4) { // Max age is 4
            int nextAgeThreshold = getNextAgeThreshold(age1);
            if (xp1 >= nextAgeThreshold) {
                advanceAge(1);
            }
        }
        
        // Side 2 age advancement
        if (age2 < 4) { // Max age is 4
            int nextAgeThreshold = getNextAgeThreshold(age2);
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
            age1 = Math.min(age1 + 1, 4); // Side 1's age (capped at 4)
            ageLabel1.setValue("Age: " + getAgeDescription(1));
            tower1.updateAge(age1); // Update tower's age and appearance
        } else {
            age2 = Math.min(age2 + 1, 4); // Side 2's age (capped at 4)
            ageLabel2.setValue("Age: " + getAgeDescription(2));
            tower2.updateAge(age2); // Update tower's age and appearance
        }
    }
    
    private String getAgeDescription(int side) {
        int playerAge = (side == 1) ? age1 : age2;
        switch (playerAge) {
            case 1: return "Stone Age";
            case 2: return "Cave Age";
            case 3: return "Modern Age";
            case 4: return "Space Age";
            default: return "Unknown Age";
        }
    }
    
    // Method to reset kill threshold for side 1
    private void resetKillThreshold1() {
        killThreshold1 = 5 + Greenfoot.getRandomNumber(6); // 5 to 10
    }
    
    // Method to reset kill threshold for side 2
    private void resetKillThreshold2() {
        killThreshold2 = 5 + Greenfoot.getRandomNumber(6); // 5 to 10
    }
    
    // Method to track kills and potentially activate special skills
    public void recordKill(int killerSide) {
        if (killerSide == 1) {
            killCount1++;
            
            // Check if kill threshold reached
            if (killCount1 >= killThreshold1) {
                // Activate a special skill based on the side's age
                activateAgeBasedSkill(1);
                
                // Reset kill counter and threshold
                killCount1 = 0;
                resetKillThreshold1();
            }
        } else if (killerSide == 2) {
            killCount2++;
            
            // Check if kill threshold reached
            if (killCount2 >= killThreshold2) {
                // Activate a special skill based on the side's age
                activateAgeBasedSkill(2);
                
                // Reset kill counter and threshold
                killCount2 = 0;
                resetKillThreshold2();
            }
        }
    }
    
    // Method to activate an appropriate skill based on the side's age
    private void activateAgeBasedSkill(int side) {
        int age = (side == 1) ? age1 : age2;
        
        // Choose skill based on age
        int skillType;
        if (age >= 4) {
            // In Space Age, use LaserBeam (most powerful)
            skillType = 4;
        } else if (age >= 3) {
            // In Modern Age, use PlaneBomb
            skillType = 3;
        } else if (age >= 2) {
            // In Cave Age, use RainingArrows
            skillType = 2;
        } else {
            // In Stone Age, use Meteor (least powerful)
            skillType = 1;
        }
        
        // Activate the chosen skill
        useSkill(side, skillType);
    }
    
    public int getPlayerAge(int side) {
        return (side == 1) ? age1 : age2;
    }
    
    private int getNextAgeThreshold(int currentAge) {
        switch (currentAge) {
            case 1: return AGE_2_XP_THRESHOLD;
            case 2: return AGE_3_XP_THRESHOLD;
            case 3: return AGE_4_XP_THRESHOLD;
            default: return Integer.MAX_VALUE; // No next age
        }
    }
    
    private void side1() {
        int unitCount = countUnits(1);
        
        // Allow manual skill activation when enough gold is available
        if (gold1 >= METEOR_COST) {
            // Higher chance to use skills manually in higher ages
            int useSkillChance = 5 + (age1 * 5); // 10% in Age 1, 15% in Age 2, etc.
            
            if (Greenfoot.getRandomNumber(100) < useSkillChance) {
                int skillChoice = decideSkillToUse(age1, gold1);
                if (skillChoice > 0) {
                    useSkill(1, skillChoice);
                    return;
                }
            }
        }
        
        // Focus on spawning units
        // If we have enough gold for a High unit, occasionally spawn one
        if (gold1 >= HIGH_COST + (30 * age1) && Greenfoot.getRandomNumber(100) < 20) {
            spawnUnitBySide(1, 3); // High unit
            return;
        }
        
        // If we have enough gold for a Mid unit, spawn one with moderate chance
        if (gold1 >= MID_COST + (30 * age1) && Greenfoot.getRandomNumber(100) < 35) {
            spawnUnitBySide(1, 2); // Mid unit
            return;
        }
        
        // Otherwise, spawn low units whenever possible
        if (gold1 >= LOW_COST + (30 * age1)) {
            spawnUnitBySide(1, 1); // Low unit
            unitsSpawnedSide1++;
        }
    }
    
    private void side2() {
        int unitCount = countUnits(2);
        
        // Allow manual skill activation when enough gold is available
        if (gold2 >= METEOR_COST) {
            // Higher chance to use skills manually in higher ages
            int useSkillChance = 5 + (age2 * 5); // 10% in Age 1, 15% in Age 2, etc.
            
            if (Greenfoot.getRandomNumber(100) < useSkillChance) {
                int skillChoice = decideSkillToUse(age2, gold2);
                if (skillChoice > 0) {
                    useSkill(2, skillChoice);
                    return;
                }
            }
        }
        
        // Focus on spawning units
        // If we have enough gold for a High unit, occasionally spawn one
        if (gold2 >= HIGH_COST + (30 * age2) && Greenfoot.getRandomNumber(100) < 20) {
            spawnUnitBySide(2, 3); // High unit
            return;
        }
        
        // If we have enough gold for a Mid unit, spawn one with moderate chance
        if (gold2 >= MID_COST + (30 * age2) && Greenfoot.getRandomNumber(100) < 35) {
            spawnUnitBySide(2, 2); // Mid unit
            return;
        }
        
        // Otherwise, spawn low units whenever possible
        if (gold2 >= LOW_COST + (30 * age2)) {
            spawnUnitBySide(2, 1); // Low unit
            unitsSpawnedSide2++;
        }
    }

    private int decideUnitToSpawn(int gold, int age, int unitCount) {
        int highCost = HIGH_COST + (30 * age);
        int midCost = MID_COST + (30 * age);
        
        // As unit count increases, so does chance of higher tier units
        int highUnitChance = Math.min(30 + (unitCount / 2), 70); // Up to 70% chance
        int midUnitChance = Math.min(50 + (unitCount / 3), 80);  // Up to 80% chance
        
        if (gold >= highCost && Greenfoot.getRandomNumber(100) < highUnitChance) {
            return 3; // High unit
        } else if (gold >= midCost && Greenfoot.getRandomNumber(100) < midUnitChance) {
            return 2; // Mid unit
        } else {
            return 1; // Low unit
        }
    }
    
    private int decideUnitToSpawn(int gold) {
        if (gold >= HIGH_COST && Greenfoot.getRandomNumber(10) < 3) {
            return 3; // 30% chance to spawn High if enough gold
        } else if (gold >= MID_COST && Greenfoot.getRandomNumber(10) < 5) {
            return 2; // 50% chance to spawn Mid if enough gold
        } else {
            return 1; // Otherwise spawn Low
        }
    }
    
    private void spawnUnitBySide(int side, int unitType) {
        // Fix: Assign the correct direction based on side
        int direction = (side == 1) ? 1 : -1;  // Side 1 moves left to right, Side 2 moves right to left
        Unit unit = null;
        int playerAge = getPlayerAge(side);
        int hp = 100 * playerAge; // Default HP
        
        switch (unitType) {
            case 3: // High
                unit = new High(playerAge, hp * 2, direction);
                break;
            case 2: // Mid
                unit = new Mid(playerAge, hp * 3/2, direction);
                break;
            default: // Low
                unit = new Low(playerAge, hp, direction);
                break;
        }
        
        // Get the dynamic cost based on the unit's implementation
        int cost = unit.getCost();
        
        // Deduct gold based on side
        if (side == 1) {
            gold1 -= cost;
        } else {
            gold2 -= cost;
        }
        
        // Add the unit to the world - 50 pixels away from each tower on the correct side
        if (side == 1) {
            addObject(unit, 100 + 50, 600); // 50 pixels to the right of left tower
        } else {
            addObject(unit, getWidth() - 100 - 50, 600); // 50 pixels to the left of right tower
        }
    }
    
    private void spawnUnit(int side, int direction) {
        int playerAge = getPlayerAge(side);
        Unit unit = new Low(playerAge, 100, direction);
        if (side == 1) {
            addObject(unit, 100 + 50, 600); // 50 pixels to the right of left tower
            gold1 -= LOW_COST;
        } else {
            addObject(unit, getWidth() - 100 - 50, 600); // 50 pixels to the left of right tower
            gold2 -= LOW_COST;
        }
    }
    
    /**
     * Called when a tower is destroyed to end the game
     * 
     * @param losingSide The side whose tower was destroyed
     */
    public void gameOver(int losingSide) {
        if (gameEnded) return; // Prevent multiple calls
        
        gameEnded = true;
        
        // Determine the winning side
        int winningSide = (losingSide == 1) ? 2 : 1;
        
        // Create a fade-out effect
        GreenfootImage fadeOut = new GreenfootImage(getWidth(), getHeight());
        fadeOut.setColor(new Color(0, 0, 0, 150));
        fadeOut.fill();
        getBackground().drawImage(fadeOut, 0, 0);
        
        // Add a game over message
        Label gameOverLabel = new Label("Game Over!", 80);
        gameOverLabel.setLineColor(Color.WHITE);
        addObject(gameOverLabel, getWidth()/2, getHeight()/2 - 50);
        
        // Add winner message
        Label winnerLabel = new Label("Player " + winningSide + " Wins!", 60);
        winnerLabel.setLineColor(Color.YELLOW);
        addObject(winnerLabel, getWidth()/2, getHeight()/2 + 50);
        
        // Wait a moment before transitioning to finish screen
        Greenfoot.delay(100);
        
        // Transition to finish screen
        Greenfoot.setWorld(new FinishScreen(winningSide));
    }
}