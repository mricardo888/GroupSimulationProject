import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Tower class represents the base structure that each side needs to defend.
 * Units will attack the enemy tower, and the side whose tower is destroyed first loses.
 */
public class Tower extends Actor
{
    private int hp;
    private int maxHP;
    private int side; // 1 for left side, 2 for right side
    private HPBar hpBar;
    private int age;
    
    /**
     * Constructor for the Tower class
     * 
     * @param side The side this tower belongs to (1 for left, 2 for right)
     * @param hp The hit points of the tower
     * @param age The current age of the owner side
     */
    public Tower(int side, int hp, int age)
    {
        this.side = side;
        this.hp = hp;
        this.maxHP = hp;
        this.age = age;
        
        // Load the tower image based on the age
        updateImage();
    }
    
    /**
     * Update the tower image based on age
     */
    private void updateImage()
    {
        // Load the image based on age
        String imagePath = "images/bases/" + age + ".png";
        GreenfootImage image = new GreenfootImage(imagePath);
        
        // Resize the image to a reasonable size
        int width = image.getWidth();
        int height = image.getHeight();
        
        // If the image is too big, resize it
        if (width > 200 || height > 300) {
            double scale = Math.min(200.0 / width, 300.0 / height);
            image.scale((int)(width * scale), (int)(height * scale));
        }
        
        // FIX: Only flip the image for side 2 (right side) if the default image is facing left
        // This assumes original images are facing left (towards side 1)
        if (side == 2) {
            image.mirrorHorizontally();
        }
        
        setImage(image);
    }
    
    public void addedToWorld(World world)
    {
        // Create and add HP bar when the tower is added to the world
        hpBar = new HPBar(this, maxHP);
        world.addObject(hpBar, getX(), getY() - getImage().getHeight()/2 - 20);
    }
    
    public void act()
    {
        // Check if tower is destroyed
        if (hp <= 0) {
            // Game over - transition to finish screen
            MyWorld myWorld = (MyWorld) getWorld();
            if (myWorld != null) {
                myWorld.gameOver(side);
            }
        }
    }
    
    /**
     * Method to damage the tower
     * 
     * @param damage The amount of damage to inflict
     */
    public void damage(int damage)
    {
        hp -= damage;
        if (hp < 0) {
            hp = 0;
        }
    }
    
    /**
     * Update the tower age and image
     * 
     * @param newAge The new age for the tower
     */
    public void updateAge(int newAge)
    {
        if (newAge != age) {
            age = newAge;
            updateImage();
        }
    }
    
    /**
     * Get the current HP of the tower
     * 
     * @return The tower's current HP
     */
    public int getHP()
    {
        return hp;
    }
    
    /**
     * Get the side this tower belongs to
     * 
     * @return The side (1 for left, 2 for right)
     */
    public int getSide()
    {
        return side;
    }
}