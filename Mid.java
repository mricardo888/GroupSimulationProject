import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Mid tier unit.
 * Can now attack the same enemy as the unit in front of it is attacking.
 */
public class Mid extends Unit
{
    private static final int COST = 70;
    private boolean isAttackingWithAlly = false;
    private Unit targetEnemy = null;
    
    public Mid(int age, int hp, int direction) {
        super(age, hp, direction, "mid");
        
        // Set specific attributes for Mid unit
        setAttackDamage(10);
        changeSpeed(1.0);
    }
    
    @Override
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
            
            // Standard behavior - look for enemies to attack
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
    
    /**
     * Standard attack behavior - looking for enemies or towers in range
     */
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
    
    /**
     * Try to find what enemy the unit ahead is attacking
     */
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
    
    /**
     * Perform attack when supporting an ally in front
     * This attack has reduced cooldown but same damage
     */
    private void performAttackWithAlly() {
        // Get the current attack frame
        setImage(attackAnimation.getCurrentFrame());
        
        // If attack animation finished one cycle and cooldown is 0
        if (attackAnimation.getImageIndex() == attackAnimation.getSize() - 1 && attackCooldown == 0) {
            // Check if target enemy is still valid
            if (targetEnemy != null && targetEnemy.getWorld() != null) {
                // Apply damage to the targeted enemy
                targetEnemy.attack(attackDamage);
                attackCooldown = 30; // Reduced cooldown when supporting
            } else {
                // If target is gone, look for a new one
                Unit newTarget = enemyInRange(attackRange);
                if (newTarget != null) {
                    targetEnemy = newTarget;
                    newTarget.attack(attackDamage);
                    attackCooldown = 30;
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
    
    @Override
    public int getCost() {
        return COST + (30 * age);
    }
    
    @Override
    public int getGoldReward() {
        return MID_REWARD;
    }
    
    @Override
    public int getXPReward() {
        return MID_XP_REWARD;
    }
    
    public static final int MID_REWARD = 35;
    public static final int MID_XP_REWARD = 25;
}