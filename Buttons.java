import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Abstract base class for all buttons 
 * 
 * @author Mark Huang 
 */
public abstract class Buttons extends Actor
{
    /**
     * Act - do whatever the Buttons wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    protected GreenfootImage image;
    protected World world;
    
    public Buttons(GreenfootImage e){
        this.image = e;
        this.world = null;
        setImage(image);
    }
    public Buttons(GreenfootImage e, World w){
        this.image = e;
        this.world = w;
        setImage(image);
    }
    
    
    public void act()
    {
        // Add your action code here.
    }
}
