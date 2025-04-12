import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.io.File;

/**
 * The base class for all units in the game.
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
    protected double speed = 1; // Default speed is one
    protected int attackDamage = 10; // Default attack damage
    protected int attackCooldown = 0; // Cooldown between attacks
    protected int attackRange = 50; // Default attack range
    protected HPBar hpBar; // Reference to the HP bar
    
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
    }
    
    public void addedToWorld(World world) {
        // Create and add HP bar when the unit is added to the world
        hpBar = new HPBar(this, maxHP);
        world.addObject(hpBar, getX(), getY() - 30);
    }
    
    public void changeSpeed(double s) {
        speed = s;
    }
    // Add this new method to find enemy towers:
    public Tower enemyTowerInRange(int r) {
        java.util.List<Tower> towers = getObjectsInRange(r, Tower.class);
        for (Tower t : towers) {
            if ((direction == 1 && t.getSide() == 2) || (direction == -1 && t.getSide() == 1)) {
                return t;
            }
        }
        return null;
    }
    
    // Replace the existing act() method with this one:
    public void act()
    {
        if (hp <= 0) {
            moving = false;
            if (deathAnimation != null) {
                death();
            } else {
                // If no death animation, still award gold and XP
                awardRewards();
                // Then remove the unit
                getWorld().removeObject(this);
            }
            return;
        }
        
        // Look for enemies to attack (priority to units, then towers)
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
            setImage(walkAnimation.getCurrentFrame());
            setLocation(getX() + speed * direction, getY());
        }
        
        // Reduce attack cooldown if it's active
        if (attackCooldown > 0) {
            attackCooldown--;
        }
    }
    
    // Replace the existing performAttack() method with this one:
    private void performAttack() {
        // Get the current attack frame without flipping it again
        setImage(attackAnimation.getCurrentFrame());
        
        // If attack animation finished one cycle and cooldown is 0
        if (attackAnimation.getImageIndex() == attackAnimation.getSize() - 1 && attackCooldown == 0) {
            // First check for enemy units
            Unit enemy = enemyInRange(attackRange);
            if (enemy != null) {
                enemy.attack(attackDamage);
                attackCooldown = 50; // Set cooldown before next attack
            } else {
                // If no enemy units, check for enemy tower
                Tower enemyTower = enemyTowerInRange(attackRange);
                if (enemyTower != null) {
                    enemyTower.damage(attackDamage);
                    attackCooldown = 50; // Set cooldown before next attack
                } else {
                    // No enemy found, resume moving
                    attacking = false;
                    moving = true;
                }
            }
        }
    }
    
    private void death() {
        setImage(deathAnimation.getCurrentFrame());
        if (deathAnimation.getImageIndex() + 1 == deathAnimation.getSize()) {
            // Award gold and XP to the opponent when a unit dies
            awardRewards();
            getWorld().removeObject(this);
        }
    }
    
    /**
     * Award gold and XP rewards to the opponent
     */
    private void awardRewards() {
        if (getWorld() instanceof MyWorld) {
            MyWorld world = (MyWorld) getWorld();
            
            // Award gold and XP to the opposite side based on direction
            // Side 1 units have direction 1 (left to right)
            // Side 2 units have direction -1 (right to left)
            if (direction == 1) { // If side 1 unit died, award to side 2
                world.addGold(2, getGoldReward());
                world.addXP(2, getXPReward());
            } else { // If side 2 unit died, award to side 1
                world.addGold(1, getGoldReward());
                world.addXP(1, getXPReward());
            }
        }
    }
    
    public void attack(int damage) {
        hp -= damage;
    }
    
    /**
     * Called when a unit is hit by a special skill
     * 
     * @param damage The amount of damage to inflict
     * @param side The side that activated the skill (1 or 2)
     */
    public void hitBySpecialSkill(int damage, int side) {
        // Only take damage if the skill was activated by the opposing side
        // Side 1 units have direction 1 (left to right)
        // Side 2 units have direction -1 (right to left)
        int unitSide = (direction == 1) ? 1 : 2;
        
        if (unitSide != side) {
            // Apply damage
            hp -= damage;
            
            // If killed by special skill, still award rewards
            if (hp <= 0) {
                // The unit will be removed in the next act cycle
                // But we need to award rewards immediately
                awardRewards();
            }
        }
    }
    
    /**
     * Check if this unit is in the specified rectangular area
     */
    public boolean isInArea(int x1, int y1, int x2, int y2) {
        int unitX = getX();
        int unitY = getY();
        
        return unitX >= x1 && unitX <= x2 && unitY >= y1 && unitY <= y2;
    }
    
    public int getSide() {
        if (direction == -1) {
            return 1;
        } else {
            return 2;
        }
    }
    
    public Unit enemyInRange(int r) {
        ArrayList<Unit> enemies = (ArrayList<Unit>) getObjectsInRange(r, Unit.class);
        for (Unit u : enemies) {
            if (u.getSide() != getSide()) {
                return u;
            }
        }
        return null;
    }
    
    // Getter for HP for the HP bar
    public int getHP() {
        return hp;
    }
    
    // Setter for attack damage
    public void setAttackDamage(int damage) {
        this.attackDamage = damage;
    }
    
    // Getter for the unit cost
    public abstract int getCost();
    
    // Get the gold reward for defeating this unit
    public int getGoldReward() {
        // By default, return half the unit's cost as gold reward
        return getCost() / 2;
    }
    
    // Get the XP reward for defeating this unit
    public int getXPReward() {
        // Default XP reward, will be overridden by subclasses
        return 10;
    }
}