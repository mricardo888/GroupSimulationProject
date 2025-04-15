import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Base class for all special skills in the game.
 */
public abstract class SpecialSkill extends Actor
{   
    protected boolean status;
    
    /**
     * Constructor that creates a transparent initial image
     */
    public SpecialSkill() {
        // Create a transparent 1x1 image as default
        GreenfootImage transparentImage = new GreenfootImage(1, 1);
        transparentImage.setTransparency(0); // Make it fully transparent
        setImage(transparentImage);
    }
    
    /**
     * Start the skill effect
     */
    public abstract void start();
    
    public void act()
    {
        // Add your action code here.
    }
}