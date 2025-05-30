import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Mid tier unit with balanced damage and speed
 * 
 * @author Ricardo Lee
 */
public class Mid extends Unit
{
    private static final int COST = 70;
    private boolean isAttackingWithAlly = false;
    private Unit targetEnemy = null;
    private boolean hasDealtDamageThisCycle = false;
    
    /**
     * Constructor for the Mid tier unit
     */
    public Mid(int age, int hp, int direction) {
        super(age, hp, direction, "mid");
        
        setAttackDamage(10);
        changeSpeed(1.0);
    }
    
    /**
     * The act method, overriding superclass act as it 
     * can attack one unit behind the attacking unit
     * of the side
     */
    @Override
    public void act()
    {
        if (hp <= 0) {
            moving = false;
            if (deathAnimation != null) {
                death();
            } else {
                awardRewards();
                getWorld().removeObject(this);
            }
            return;
        }
        
        // Check if there's a friendly unit ahead that is attacking
        Unit friendlyAhead = friendlyUnitAhead();
        if (friendlyAhead != null && friendlyAhead.isAttacking()) {
            // Find what enemy the unit ahead is attacking
            targetEnemy = findEnemyTarget(friendlyAhead);
            
            if (targetEnemy != null) {
                // If unit ahead is attacking an enemy, this Mid unit should also attack that enemy
                isAttackingWithAlly = true;
                attacking = true;
                moving = false;
            } else {
                // Standard behavior - look for enemies to attack
                standardAttackBehavior();
            }
        } else {
            // No friendly unit ahead that's attacking
            isAttackingWithAlly = false;
            targetEnemy = null;
            standardAttackBehavior();
        }
        
        if (attacking) {
            if (isAttackingWithAlly && targetEnemy != null) {
                performAttackWithAlly();
            } else {
                performAttack();
            }
        } else if (moving) {
            // Check if there's a friendly unit ahead before moving
            if (friendlyAhead == null) {
                // Only move if no friendly unit is too close ahead
                setImage(walkAnimation.getCurrentFrame());
                setLocation(getX() + speed * direction, getY());
            }
        }
        
        // Reduce attack cooldown if it's active
        if (attackCooldown > 0) {
            attackCooldown--;
        }
    }
    
    private void standardAttackBehavior() {
        // Look for enemies to attack
        Unit enemy = enemyInRange(attackRange);
        if (enemy != null) {
            attacking = true;
            moving = false;
            targetEnemy = enemy;
        } else {
            // No enemy units in range, check for enemy tower
            Tower enemyTower = enemyTowerInRange(attackRange);
            if (enemyTower != null) {
                attacking = true;
                moving = false;
            } else {
                attacking = false;
                moving = true;
                targetEnemy = null;
            }
        }
    }
    
    private Unit findEnemyTarget(Unit friendlyUnit) {
        // Get all enemy units in the area
        java.util.List<Unit> enemies = getObjectsInRange(attackRange * 2, Unit.class);
        
        for (Unit enemy : enemies) {
            // Only consider enemy units (from opposite side)
            if (enemy.getSide() != this.getSide()) {
                // Check if the friendly unit is in range to attack this enemy
                double distance = Math.sqrt(
                    Math.pow(friendlyUnit.getX() - enemy.getX(), 2) + 
                    Math.pow(friendlyUnit.getY() - enemy.getY(), 2)
                );
                
                // If enemy is in attack range of the friendly unit, it's likely the target
                if (distance <= attackRange) {
                    return enemy;
                }
            }
        }
        
        return null; // No target found
    }
    
    private void performAttackWithAlly() {
        // Get the current attack frame
        setImage(attackAnimation.getCurrentFrame());
        
        // If attack animation is at the last frame (one complete cycle)
        if (attackAnimation.getImageIndex() == attackAnimation.getSize() - 1) {
            // Reset flag for next cycle
            hasDealtDamageThisCycle = false;
        }
        
        // If we are at the attack frame (middle of animation) and haven't dealt damage yet this cycle
        int attackFrame = attackAnimation.getSize() / 2;
        if (attackAnimation.getImageIndex() == attackFrame && !hasDealtDamageThisCycle && attackCooldown == 0) {
            // Check if target enemy is still valid
            if (targetEnemy != null && targetEnemy.getWorld() != null) {
                // Apply damage to the targeted enemy
                targetEnemy.attack(attackDamage);
                attackCooldown = 10; // Shorter cooldown when supporting
                hasDealtDamageThisCycle = true; // Mark that we've dealt damage in this cycle
            } else {
                // If target is gone, look for a new one
                Unit newTarget = enemyInRange(attackRange);
                if (newTarget != null) {
                    targetEnemy = newTarget;
                    newTarget.attack(attackDamage);
                    attackCooldown = 10;
                    hasDealtDamageThisCycle = true;
                } else {
                    // No target found, reset attack state
                    isAttackingWithAlly = false;
                    attacking = false;
                    moving = true;
                    targetEnemy = null;
                }
            }
        }
        
        // If the unit in front is no longer attacking, stop our support attack
        Unit friendlyAhead = friendlyUnitAhead();
        if (friendlyAhead == null || !friendlyAhead.isAttacking()) {
            isAttackingWithAlly = false;
            attacking = false;
            moving = true;
            targetEnemy = null;
        }
    }
    
    /**
     * Gets unit cost
     */
    @Override
    public int getCost() {
        return COST + (30 * age);
    }
    
    /**
     * Gets unit gold reward
     */
    @Override
    public int getGoldReward() {
        return MID_GOLD_REWARD;
    }
    
    /**
     * Gets unit XP reward
     */
    @Override
    public int getXPReward() {
        return MID_XP_REWARD;
    }
}