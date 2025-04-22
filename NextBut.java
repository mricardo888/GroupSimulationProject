import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * button that moves from world to world
 * 
 * @author Mark Huang 
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
            changeWorld();
        }
    }
    public void act()
    {
        checkClick();
    }
}
