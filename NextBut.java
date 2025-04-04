import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ForwardBut here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class NextBut extends Buttons
{
    public NextBut(GreenfootImage e, World w){
        super(e,w);
        
    }
    
    public  void changeWorld(){
        Greenfoot.setWorld(world);
        
    }
    
    public void checkClick(){
        if (Greenfoot.mouseClicked(this)){
            System.out.println(1);
            changeWorld();
        }
    }
    public void act()
    {
        checkClick();
    }
}
