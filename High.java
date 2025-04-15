import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * High tier unit.
 */
public class High extends Unit
{
    private static final int COST = 130;
    
    public High(int age, int hp, int direction) {
        super(age, hp, direction, "high");
        
        // Set specific attributes for High unit
        setAttackDamage(15);
        changeSpeed(0.7);
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
        return HIGH_REWARD;
    }
    
    @Override
    public int getXPReward() {
        return HIGH_XP_REWARD;
    }
    
    public static final int HIGH_REWARD = 65;
    public static final int HIGH_XP_REWARD = 50;
}