import greenfoot.*;

/**
 * Low tier unit with low damage but faster movement speed
 * 
 * @author Ricardo Lee
 */
public class Low extends Unit
{
    private static final int COST = 30;
    public static final int LOW_REWARD = 15;
    public static final int LOW_XP_REWARD = 10;
    
    /**
     * Constructor for the Low tier unit
     */
    public Low(int age, int hp, int direction) {
        super(age, hp, direction, "low");
        
        setAttackDamage(5);
        changeSpeed(1.5);
    }
    
    /**
     * The act method using the superclass act
     */
    public void act()
    {
        super.act();
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
        return LOW_REWARD;
    }
    
    /**
     * Gets unit XP reward
     */
    @Override
    public int getXPReward() {
        return LOW_XP_REWARD;
    }
}