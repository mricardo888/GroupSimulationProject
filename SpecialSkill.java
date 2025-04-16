import greenfoot.*;

public abstract class SpecialSkill extends Actor
{   
    protected boolean status;
    protected boolean active = false; // Add this field to track active state
    
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
    
    /**
     * Check if the skill is currently active
     */
    public boolean isActive() {
        return active;
    }
    
    public void act()
    {
        // Add your action code here.
    }
}
