import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Mid tier unit.
 */
public class Mid extends Unit
{
    private static final int COST = 70;
    
    public Mid(int age, int hp, int direction) {
        super(age, hp, direction, "mid");
        
        // Set specific attributes for Mid unit
        setAttackDamage(10);
        changeSpeed(1.0);
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
        return MID_REWARD;
    }
    
    @Override
    public int getXPReward() {
        return MID_XP_REWARD;
    }
    
    public static final int MID_REWARD = 35;
    public static final int MID_XP_REWARD = 25;
}