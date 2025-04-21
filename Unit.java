import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.io.File;

/**
 * The abstract class for all units
 * 
 * @author Ricardo Lee
 */
public abstract class Unit extends SuperSmoothMover
{
    
    protected int age; // Stone Age = 1, Cave Age = 2, 
    // Modern Age = 3, Space Age = 4
    protected int hp;
    protected int maxHP; // Store the maximum HP for the HP bar
    protected int direction; // 1 left to right, -1 right to left
    protected boolean moving;
    protected boolean attacking;
    protected Animator walkAnimation;
    protected Animator attackAnimation;
    protected Animator deathAnimation;
    protected double speed = 1; // Default speed
    protected int attackDamage = 10; // Default attack damage
    protected int attackCooldown = 0; // Cooldown between attacks
    protected int attackRange = 50; // Default attack range
    protected HPBar hpBar; // Reference to the HP bar
    protected GreenfootSound deathSound; // Sound to play when unit dies
    private boolean hasDealtDamageThisCycle = false; // Flag to track if damage was dealt in current animation cycle
    protected boolean killedBySpecialSkill = false; // Flag to track if unit was killed by special skill
    
    // XP rewards constants
    public static final int LOW_XP_REWARD = 10;
    public static final int MID_XP_REWARD = 25;
    public static final int HIGH_XP_REWARD = 50;
    
    // Gold rewards base values
    public static final int LOW_GOLD_REWARD = 15;
    public static final int MID_GOLD_REWARD = 35;
    public static final int HIGH_GOLD_REWARD = 65;
    
    /**
     * Constructor for the Unit class
     */
    public Unit(int age, int hp, int direction, String type) {
        this.age = age;
        this.hp = hp;
        this.maxHP = hp; // Set the maximum HP
        this.direction = direction;
        moving = true;
        attacking = false;
        String basePath = "images/age" + age + "/" + type + "/";
        walkAnimation = new Animator(basePath + "walk");
        attackAnimation = new Animator(basePath + "attack");
        File d = new File(basePath + "death");
        if (d.exists() && d.isDirectory()) {
            deathAnimation = new Animator(basePath + "death");
        } else {
            deathAnimation = null;
        }
        
        // Only flip the animations if direction is right to left (-1)
        if (direction == -1) {
            walkAnimation.flip();
            attackAnimation.flip();
            if (deathAnimation != null) {
                deathAnimation.flip();
            }
        }
        this.attackDamage = 25;
        // Initialize death sound
        deathSound = new GreenfootSound("./sounds/oof.mp3");
    }
    
    /**
     * Creates and adds HP bar
     */
    public void addedToWorld(World world) {
        hpBar = new HPBar(this, maxHP);
        world.addObject(hpBar, getX(), getY() - 30);
    }
    
    /**
     * Changes the speed
     */
    public void changeSpeed(double s) {
        speed = s;
    }

    /**
     * Checks if there is an enemy tower within the specified range
     */
    public Tower enemyTowerInRange(int r) {
        java.util.List<Tower> towers = getObjectsInRange(r, Tower.class);
        for (Tower t : towers) {
            if ((direction == 1 && t.getSide() == 2) || (direction == -1 && t.getSide() == 1)) {
                return t;
            }
        }
        return null;
    }
    
    /**
     * Checks if this unit is attacking
     */
    public boolean isAttacking() {
        return attacking;
    }
    
    /**
     * Checks for enemies to attack and manages attack cooldowns
     */
    public void act()
    {
        if (hp <= 0) {
            moving = false;
            if (deathAnimation != null) {
                death();
            } else {
                // Play death sound
                deathSound.play();
                
                // Award gold and XP - check if killed by special skill
                if (killedBySpecialSkill) {
                    awardRewardsWithoutKill(); // Don't increment kill counter for special skill kills
                } else {
                    awardRewards(); // Normal kill counter incrementing
                }
                
                // Then remove the unit
                getWorld().removeObject(this);
            }
            return;
        }
        
        if (!attacking) {
            Unit enemy = enemyInRange(attackRange);
            if (enemy != null) {
                attacking = true;
                moving = false;
            } else {
                // If no enemy units in range, check for enemy tower
                Tower enemyTower = enemyTowerInRange(attackRange);
                if (enemyTower != null) {
                    attacking = true;
                    moving = false;
                }
            }
        }
        
        if (attacking) {
            performAttack();
        } else if (moving) {
            // Check if there's a friendly unit ahead before moving
            Unit friendlyAhead = friendlyUnitAhead();
            if (friendlyAhead == null) {
                // Only move if no friendly unit is too close ahead
                setImage(walkAnimation.getCurrentFrame());
                setLocation(getX() + speed * direction, getY());
            }
        }
        
        if (attackCooldown > 0) {
            attackCooldown--;
        }
    }

    /**
     * Dealing damage to enemies or towers in range
     */
    protected void performAttack() {
        // Get the current attack frame without flipping it again
        setImage(attackAnimation.getCurrentFrame());
        
        // If attack animation is at the last frame (one complete cycle)
        if (attackAnimation.getImageIndex() == attackAnimation.getSize() - 1) {
            // Reset flag for next cycle
            hasDealtDamageThisCycle = false;
        }
        
        int attackFrame = attackAnimation.getSize() / 2;
        if (attackAnimation.getImageIndex() == attackFrame && !hasDealtDamageThisCycle && attackCooldown == 0) {
            // First check for enemy units
            Unit enemy = enemyInRange(attackRange);
            if (enemy != null) {
                enemy.attack(attackDamage);
                attackCooldown = 10;
                hasDealtDamageThisCycle = true; 
            } else {
                // If no enemy units, check for enemy tower
                Tower enemyTower = enemyTowerInRange(attackRange);
                if (enemyTower != null) {
                    enemyTower.damage(attackDamage);
                    attackCooldown = 10; // Shorter cooldown
                    hasDealtDamageThisCycle = true; // Mark that we've dealt damage in this cycle
                } else {
                    // No enemy found, resume moving
                    attacking = false;
                    moving = true;
                }
            }
        }
    }
    
    /**
     * Handles death animation sequence and awards rewards
     */
    protected void death() {
        // Play death sound at the start of death animation
        if (deathAnimation.getImageIndex() == 0) {
            deathSound.play();
        }
        
        setImage(deathAnimation.getCurrentFrame());
        if (deathAnimation.getImageIndex() + 1 == deathAnimation.getSize()) {
            // Check if killed by special skill
            if (killedBySpecialSkill) {
                awardRewardsWithoutKill(); // Don't include in kill counter
            } else {
                awardRewards(); // Normal kill counter
            }
            getWorld().removeObject(this);
        }
    }
    
    /**
     * Awards gold and XP rewards
     */
    protected void awardRewards() {
        if (getWorld() instanceof MyWorld) {
            MyWorld world = (MyWorld) getWorld();
            
            if (direction == 1) {
                int goldReward = getCost() * 2;
                world.addGold(2, goldReward);
                
                int killerAge = world.getPlayerAge(2);
                int baseXP = getXPReward();
                int totalXP = baseXP * age * killerAge;
                
                world.addXP(2, totalXP);
                world.recordKill(2); // Record a kill for side 2
            } else {
                int goldReward = getCost() * 2;
                world.addGold(1, goldReward);
                
                int killerAge = world.getPlayerAge(1);
                int baseXP = getXPReward();
                int totalXP = baseXP * age * killerAge;
                
                // Award the calculated XP
                world.addXP(1, totalXP);
                world.recordKill(1); // Record a kill for side 1
            }
        }
    }
    
    /**
     * Awards gold and XP rewards to the opponent without kill counter
     */
    protected void awardRewardsWithoutKill() {
        if (getWorld() instanceof MyWorld) {
            MyWorld world = (MyWorld) getWorld();
            if (direction == 1) {
                int goldReward = getCost() * 2;
                world.addGold(2, goldReward);
                
                int killerAge = world.getPlayerAge(2);
                int baseXP = getXPReward();
                int totalXP = baseXP * age * killerAge;
                
                world.addXP(2, totalXP);
            } else {
                int goldReward = getCost() * 2;
                world.addGold(1, goldReward);
                
                int killerAge = world.getPlayerAge(1);
                int baseXP = getXPReward();
                int totalXP = baseXP * age * killerAge;
                
                world.addXP(1, totalXP);
            }
        }
    }
    
    /**
     * Damages the unit
     */
    public void attack(int damage) {
        hp -= damage;
        killedBySpecialSkill = false;
    }
    
    /**
     * Gets unit direction
     */
    public int getDirection() {
        return direction;
    }
    
    /**
     * Applies damage if the special skill was activated by enemy
     */
    public void hitBySpecialSkill(int damage, int side) {
        int unitSide = (direction == 1) ? 1 : 2;
        
        if (unitSide != side) {
            hp -= damage;
            
            if (hp <= 0) {
                killedBySpecialSkill = true;
                
                deathSound.play();
            }
        }
    }

    /**
     * Checks if there is a friendly unit too close ahead
     */
    public Unit friendlyUnitAhead() {
        int requiredSpacing = 50; 
        
        ArrayList<Unit> units = new ArrayList<Unit>();
        for (Unit u : getWorld().getObjects(Unit.class)) {
            if (Math.abs(u.getY() - getY()) < 10) {
                if (u.getDirection() == this.direction && u != this) {
                    if ((direction == 1 && u.getX() > getX()) ||
                        (direction == -1 && u.getX() < getX())) {
                        int distance = Math.abs(u.getX() - getX());
                        
                        // If unit is too close, add to the list
                        if (distance < requiredSpacing) {
                            units.add(u);
                        }
                    }
                }
            }
        }
        
        if (!units.isEmpty()) {
            Unit closest = units.get(0);
            int closestDistance = Math.abs(closest.getX() - getX());
            
            for (Unit u : units) {
                int distance = Math.abs(u.getX() - getX());
                if (distance < closestDistance) {
                    closest = u;
                    closestDistance = distance;
                }
            }
            
            return closest;
        }
        
        return null;
    }
    
    protected boolean isInArea(int x1, int y1, int x2, int y2) {
        int unitX = getX();
        int unitY = getY();
        
        return unitX >= x1 && unitX <= x2 && unitY >= y1 && unitY <= y2;
    }
    
    /**
     * Gets unit side
     */
    public int getSide() {
        if (direction == -1) {
            return 2;
        } else {
            return 1;
        }
    }
    
    /**
     * Checks if there is an enemy unit within a certain area
     */
    public Unit enemyInRange(int r) {
        ArrayList<Unit> enemies = (ArrayList<Unit>) getObjectsInRange(r, Unit.class);
        for (Unit u : enemies) {
            if (u.getSide() != getSide()) {
                return u;
            }
        }
        return null;
    }
    
    /**
     * Gets hp
     */
    public int getHP() {
        return hp;
    }
    
    /**
     * Sets unit attack damage
     */
    public void setAttackDamage(int damage) {
        // Base damage is multiplied by age factor to scale with progress
        this.attackDamage = damage * 3 * age; // Tripled base values and multiplied by age
    }

    /**
     * Gets unit cost
     */
    public abstract int getCost();
    
    /**
     * Gets kill gold reward
     */
    public abstract int getGoldReward();
    
    /**
     * Gets kill XP reward
     */
    public abstract int getXPReward();
}