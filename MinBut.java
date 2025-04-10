import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class MinBut here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MinBut extends Buttons
{
    private int g1;
    private int g2;
    private int xp1;
    private int xp2;
    private int which;
    
    public MinBut(GreenfootImage e, int x){
        super(e);
        which = x;
    }
    
    public void act()
    {
        which();
    }
    
    
    public void minGold1(){
        Settings s = (Settings)getWorld();
        g1 = s.getGold1();
        s.setGold1(g1 - 1);
    }
    public void minGold2(){
        Settings s = (Settings)getWorld();
        g2 = s.getGold2();
        s.setGold2(g2 - 1);
    }
    public void minXp1(){
        Settings s = (Settings)getWorld();
        xp1 = s.getXp1();
        s.setXp1(xp1 - 1);
    }
    public void minXp2(){
        Settings s = (Settings)getWorld();
        xp2 = s.getXp2();
        s.setXp2(xp2 - 1);
    }
    
    public void which(){
        if (Greenfoot.mouseClicked(this)){
            if (which == 1){
                minGold1();
            }
            else if(which == 2){
                minGold2();
            }
            else if(which == 3){
                minXp1();
            }
            else{
                minXp2();
            }
        }
    }
    
    
    
    
    
    
}
