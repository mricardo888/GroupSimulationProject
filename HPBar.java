import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * A health bar that displays the HP of a unit.
 */
public class HPBar extends Actor
{
    private Unit unit;
    private int maxHP;
    private int width = 50; // Width of the health bar
    private int height = 5; // Height of the health bar
    
    public HPBar(Unit unit, int maxHP)
    {
        this.unit = unit;
        this.maxHP = maxHP;
        updateImage();
    }
    
    public void act() 
    {
        if (unit != null && unit.getWorld() != null) {
            // Update position to follow the unit
            setLocation(unit.getX(), unit.getY() - 30); // Position above the unit
            updateImage();
        } else {
            // Remove this HP bar if the unit is removed
            if (getWorld() != null) {
                getWorld().removeObject(this);
            }
        }
    }
    
    private void updateImage()
    {
        // Calculate the width of the health portion based on current HP
        int healthWidth = (int)((double)unit.getHP() / maxHP * width);
        
        // Create a new image for the health bar
        GreenfootImage image = new GreenfootImage(width + 2, height + 2);
        
        // Draw the border (black rectangle)
        image.setColor(Color.BLACK);
        image.drawRect(0, 0, width + 1, height + 1);
        
        // Draw the background (white rectangle)
        image.setColor(Color.WHITE);
        image.fillRect(1, 1, width, height);
        
        // Determine color based on health percentage
        if (unit.getHP() > maxHP * 0.7) {
            image.setColor(Color.GREEN);
        } else if (unit.getHP() > maxHP * 0.3) {
            image.setColor(Color.YELLOW);
        } else {
            image.setColor(Color.RED);
        }
        
        // Draw the health portion
        image.fillRect(1, 1, healthWidth, height);
        
        setImage(image);
    }
}