import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * A health bar that displays the HP of a unit or tower.
 */
public class HPBar extends Actor
{
    private Actor target;
    private int maxHP;
    private int width = 50; // Width of the health bar
    private int height = 5; // Height of the health bar
    private boolean isUnit; // Whether the target is a Unit or a Tower
    
    public HPBar(Actor target, int maxHP)
    {
        this.target = target;
        this.maxHP = maxHP;
        this.isUnit = target instanceof Unit;
        updateImage();
    }
    
    public void act() 
    {
        if (target != null && target.getWorld() != null) {
            // Update position to follow the target
            if (isUnit) {
                setLocation(target.getX(), target.getY() - 30); // Position above the unit
            } else {
                // For tower, position at the top of the tower
                Tower tower = (Tower)target;
                setLocation(tower.getX(), tower.getY() - tower.getImage().getHeight()/2 - 20);
            }
            updateImage();
        } else {
            // Remove this HP bar if the target is removed
            if (getWorld() != null) {
                getWorld().removeObject(this);
            }
        }
    }
    
    private void updateImage()
    {
        // Calculate the width of the health portion based on current HP
        int currentHP;
        if (isUnit) {
            currentHP = ((Unit)target).getHP();
        } else {
            currentHP = ((Tower)target).getHP();
        }
        
        int healthWidth = (int)((double)currentHP / maxHP * width);
        
        // Create a new image for the health bar
        GreenfootImage image = new GreenfootImage(width + 2, height + 2);
        
        // Draw the border (black rectangle)
        image.setColor(Color.BLACK);
        image.drawRect(0, 0, width + 1, height + 1);
        
        // Draw the background (white rectangle)
        image.setColor(Color.WHITE);
        image.fillRect(1, 1, width, height);
        
        // Determine color based on health percentage
        if (currentHP > maxHP * 0.7) {
            image.setColor(Color.GREEN);
        } else if (currentHP > maxHP * 0.3) {
            image.setColor(Color.YELLOW);
        } else {
            image.setColor(Color.RED);
        }
        
        // Draw the health portion
        image.fillRect(1, 1, healthWidth, height);
        
        setImage(image);
    }
}