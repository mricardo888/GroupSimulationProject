import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ForwardBut here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class NextBut extends Buttons
{
    private GreenfootImage image;
    private World world;
    
    public NextBut(GreenfootImage e, World w){
        super(e,w);
        this.image = e;
        this.world = w;
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
