import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Low tier unit.
 */
public class Low extends Unit
{
    private static final int COST = 30;
    
    public Low(int age, int hp, int direction) {
        super(age, hp, direction, "low");
        
        // Set specific attributes for Low unit
        setAttackDamage(5);
        changeSpeed(1.5);
    }
    
    public void act()
    {
        super.act();
    }
    
    @Override
    public int getCost() {
        return COST + (30 * age);
    }
    
    @Override
    public int getGoldReward() {
        return LOW_REWARD;
    }
    
    @Override
    public int getXPReward() {
        return LOW_XP_REWARD;
    }
    
    public static final int LOW_REWARD = 15;
    public static final int LOW_XP_REWARD = 10;
}