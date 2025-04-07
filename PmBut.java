import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)


public class PmBut extends Buttons
{
    private int g1;
    
    public PmBut(GreenfootImage e){
        super(e,null);
    }
    
    public void plusGold1(){
        Settings s = (Settings)getWorld();
        g1 = s.getGold1();
        s.setGold1(g1 + 1);
    }
    
    
    
    public void act()
    {
        // Add your action code here.
    }
}
