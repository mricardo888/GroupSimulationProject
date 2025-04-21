import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * A health bar that shows the HP of a unit or tower. 
 * 
 * @author Ricardo Lee
 */
public class HPBar extends Actor
{
    private Actor target;
    private int maxHP;
    private int width = 50; // Width of the health bar
    private int height = 5; // Height of the health bar
    private boolean isUnit; // Unit or Tower
    
    /**
     * Creates a new health bar with the given max HP.
     * Sets hp bar appearance based on the target's current HP.
     */
    public HPBar(Actor target, int maxHP)
    {
        this.target = target;
        this.maxHP = maxHP;
        this.isUnit = target instanceof Unit;
        updateImage();
    }
    
    /**
     * Updates the health bar's position and appearance every game cycle.
     * Positions the bar above its target actor and updates the colour
     * to reflect the current health. Removes itself if target is dead.
     */
    public void act() 
    {
        if (target != null && target.getWorld() != null) {
            if (isUnit) {
                setLocation(target.getX(), target.getY() - 30);
            } else {
                Tower tower = (Tower)target;
                setLocation(tower.getX(), tower.getY() - tower.getImage().getHeight()/2 - 20);
            }
            updateImage();
        } else {
            if (getWorld() != null) {
                getWorld().removeObject(this);
            }
        }
    }
    
    private void updateImage()
    {
        int currentHP;
        
        if (isUnit) {
            currentHP = ((Unit)target).getHP();
        } else {
            currentHP = ((Tower)target).getHP();
        }
        
        int healthWidth = (int)((double)currentHP / maxHP * width);
        
        GreenfootImage image = new GreenfootImage(width + 2, height + 2);
        
        image.setColor(Color.BLACK);
        image.drawRect(0, 0, width + 1, height + 1);
        
        image.setColor(Color.WHITE);
        image.fillRect(1, 1, width, height);
        
        if (currentHP > maxHP * 0.7) {
            image.setColor(Color.GREEN);
        } else if (currentHP > maxHP * 0.3) {
            image.setColor(Color.YELLOW);
        } else {
            image.setColor(Color.RED);
        }
        
        image.fillRect(1, 1, healthWidth, height);
        setImage(image);
    }
}