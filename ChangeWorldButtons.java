import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Buttons here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class ChangeWorldButtons extends Actor
{
    /**
     * Act - do whatever the Buttons wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    protected GreenfootImage image;
    protected World world;
    
    public ChangeWorldButtons(GreenfootImage e, World w){
        this.image = e;
        this.world = w;
        setImage(image);
    }
    
    
    public void act()
    {
        // Add your action code here.
    }
}
