import greenfoot.*;

/**
 * High tier unit with high damage but slow movement speed.
 * 
 * @author Ricardo
 */
public class High extends Unit
{
    private static final int COST = 130;
    public static final int HIGH_REWARD = 65;
    public static final int HIGH_XP_REWARD = 50;
    
    /**
     * Constructor for the High tier unit
     */
    public High(int age, int hp, int direction) {
        super(age, hp, direction, "high");
        
        // Set specific attributes for High unit
        setAttackDamage(15);
        changeSpeed(0.7);
    }
    
    /**
     * The act method using superclass act
     */
    public void act()
    {
        super.act();
    }
    
    /**
     * Gets the cost of this unit, which increases with age.
     */
    @Override
    public int getCost() {
        return COST + (30 * age);
    }
    
    /**
     * Gets the gold reward
     */
    @Override
    public int getGoldReward() {
        return HIGH_REWARD;
    }
    
    /**
     * Gets XP reward
     */
    @Override
    public int getXPReward() {
        return HIGH_XP_REWARD;
    }
}