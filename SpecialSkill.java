import greenfoot.*;

/**
 * Abstract base class for all special skills in the game
 * 
 * @author Ricardo Lee
 */
public abstract class SpecialSkill extends Actor
{   
    protected boolean status;
    protected boolean active = false;
    
    /**
     * All skills start with an invisible representation until activated.
     */
    public SpecialSkill() {
        GreenfootImage transparentImage = new GreenfootImage(1, 1);
        transparentImage.setTransparency(0);
        setImage(transparentImage);
    }
    
    /**
     * Starts the skill effect
     */
    public abstract void start();
    
    /**
     * Checks if the skill is currently active
     */
    public boolean isActive() {
        return active;
    }
}