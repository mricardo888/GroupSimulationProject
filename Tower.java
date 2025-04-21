import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Tower class is the base that each side defends
 * 
 * @author Ricardo lee
 */
public class Tower extends Actor
{
    private int hp;
    private int maxHP;
    private int side;
    private HPBar hpBar;
    private int age;
    
    /**
     * Constructor for the Tower class
     */
    public Tower(int side, int hp, int age)
    {
        this.side = side;
        this.hp = hp;
        this.maxHP = hp;
        this.age = age;
        
        updateImage();
    }
    
    /**
     * Update the tower image based on age
     */
    private void updateImage()
    {
        String imagePath = "images/bases/" + age + ".png";
        GreenfootImage image = new GreenfootImage(imagePath);
        
        int width = image.getWidth();
        int height = image.getHeight();
        
        if (width > 200 || height > 300) {
            double scale = Math.min(200.0 / width, 300.0 / height);
            image.scale((int)(width * scale), (int)(height * scale));
        }
        
        if (side == 2) {
            image.mirrorHorizontally();
        }
        
        setImage(image);
    }
    
    /**
     * Creates and adds an HP bar for the tower
     */
    public void addedToWorld(World world)
    {
        // Create and add HP bar when the tower is added to the world
        hpBar = new HPBar(this, maxHP);
        world.addObject(hpBar, getX(), getY() - getImage().getHeight()/2 - 20);
    }
    
    /**
     * Checks if the tower is destroyed
     */
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
     * Damages the tower by the specified amount
     */
    public void damage(int damage)
    {
        hp -= damage;
        if (hp < 0) {
            hp = 0;
        }
    }
    
    /**
     * Updates tower image if age changed.
     */
    public void updateAge(int newAge)
    {
        if (newAge != age) {
            age = newAge;
            updateImage();
        }
    }
    
    /**
     * Gets tower HP
     */
    public int getHP()
    {
        return hp;
    }
    
    /**
     * Gets tower side
     */
    public int getSide()
    {
        return side;
    }
}