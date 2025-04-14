import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class MyWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MyWorld extends World
{

    // Initial gold and XP from Settings
    private int gold1;
    private int gold2;
    
    // Unit costs
    private static final int LOW_COST = 30;
    private static final int MID_COST = 50;
    private static final int HIGH_COST = 100;
    
    // Special skill costs
    private static final int METEOR_COST = 200;
    private static final int RAINING_ARROWS_COST = 300;
    private static final int PLANE_BOMB_COST = 400;
    private static final int LASER_BEAM_COST = 500;
    
    // XP rewards for defeating units
    private static final int LOW_XP_REWARD = 10;
    private static final int MID_XP_REWARD = 25;
    private static final int HIGH_XP_REWARD = 50;
    
    // XP thresholds for advancing to next age
    private static final int AGE_2_XP_THRESHOLD = 100;
    private static final int AGE_3_XP_THRESHOLD = 300;
    private static final int AGE_4_XP_THRESHOLD = 600;
    private int xp1;
    private int xp2;
    
    // Game state variables
    private int age1 = 1; // Age for player 1
    private int age2 = 1; // Age for player 2
    private int spawnTimer1 = 0;
    private int spawnTimer2 = 0;
    private int spawnDelay = 100; // Delay between spawns
    
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
    
    // Tower references
    private Tower tower1;
    private Tower tower2;
    
    // Tower HP based on age
    private static final int TOWER_BASE_HP = 1000;
    
    // Game state
    private boolean gameEnded = false;
    

    /**
     * Constructor for objects of class MyWorld.
     * 
     */
    public MyWorld()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(1024, 800, 1); 
    }
}
