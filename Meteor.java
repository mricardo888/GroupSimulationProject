import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * A Meteor class that creates meteors falling from the sky.
 * The meteors can vary in size, speed, and can create explosions on impact.
 * 
 * @author Your Name 
 * @version 1.0
 */
public class Meteor extends SpecialSkill
{
    private ArrayList<MeteorObject> meteors = new ArrayList<MeteorObject>();
    private ArrayList<MeteorImpact> impacts = new ArrayList<MeteorImpact>();
    private int spawnRate = 50; // Time between meteor spawns
    private int spawnTimer = 0;
    private int meteorsRemaining = 10; // Default number of meteors to spawn
    private boolean active = false;
    private int minSize = 20; // Minimum meteor size
    private int maxSize = 60; // Maximum meteor size
    private int minSpeed = 2; // Minimum falling speed
    private int maxSpeed = 7; // Maximum falling speed
    
    /**
     * Constructor for the Meteor skill
     */
    public Meteor() {
        // Initialize any needed resources
    }
    
    /**
     * Start spawning meteors
     */
    public void start() {
        World world = getWorld();
        if (world != null && !active) {
            active = true;
            meteorsRemaining = 10;
            meteors.clear();
            impacts.clear();
        }
    }
    
    /**
     * Set the number of meteors to spawn
     */
    public void setMeteorCount(int count) {
        this.meteorsRemaining = count;
    }
    
    /**
     * Set the spawn rate (higher = slower spawn)
     */
    public void setSpawnRate(int rate) {
        this.spawnRate = rate;
    }
    
    /**
     * Set the size range of meteors
     */
    public void setSizeRange(int min, int max) {
        this.minSize = min;
        this.maxSize = max;
    }
    
    /**
     * Set the speed range of meteors
     */
    public void setSpeedRange(int min, int max) {
        this.minSpeed = min;
        this.maxSpeed = max;
    }
    
    /**
     * Act method called by Greenfoot
     */
    public void act()
    {
        World world = getWorld();
        if (world == null) return;
        
        if (active) {
            // Spawn new meteors
            spawnTimer++;
            if (spawnTimer >= spawnRate && meteorsRemaining > 0) {
                spawnMeteor(world);
                spawnTimer = 0;
                meteorsRemaining--;
                
                // Stop when all meteors spawned
                if (meteorsRemaining <= 0) {
                    active = false;
                }
            }
        }
        
        // Update meteors
        updateMeteors(world);
        
        // Update impacts
        updateImpacts(world);
    }
    
    /**
     * Spawn a new meteor
     */
    private void spawnMeteor(World world) {
        // Randomize meteor properties
        int size = Greenfoot.getRandomNumber(maxSize - minSize + 1) + minSize;
        int speed = Greenfoot.getRandomNumber(maxSpeed - minSpeed + 1) + minSpeed;
        
        // Random X position within the world
        int x = Greenfoot.getRandomNumber(world.getWidth());
        int y = 0; // Start at the top
        
        // Create the meteor
        MeteorObject meteor = new MeteorObject(x, y, size, speed);
        meteors.add(meteor);
        world.addObject(meteor, meteor.getX(), meteor.getY());
        
        Greenfoot.playSound("./sounds/CometFalling.mp3");
    }
    
    /**
     * Update all active meteors
     */
    private void updateMeteors(World world) {
        ArrayList<MeteorObject> meteorsToRemove = new ArrayList<MeteorObject>();
        
        for (MeteorObject meteor : meteors) {
            meteor.update();
            
            // Check if meteor has hit the ground
            if (meteor.isAtGround(world)) {
                // Create impact effect
                MeteorImpact impact = new MeteorImpact(meteor.getX(), meteor.getY(), meteor.getSize());
                impacts.add(impact);
                world.addObject(impact, impact.getX(), impact.getY());
                
                // Mark meteor for removal
                meteorsToRemove.add(meteor);
                world.removeObject(meteor);
            }
        }
        
        // Remove meteors that hit the ground
        meteors.removeAll(meteorsToRemove);
    }
    
    /**
     * Update all active impacts
     */
    private void updateImpacts(World world) {
        ArrayList<MeteorImpact> impactsToRemove = new ArrayList<MeteorImpact>();
        
        for (MeteorImpact impact : impacts) {
            if (impact.update()) {
                // If impact animation is complete, mark for removal
                impactsToRemove.add(impact);
                world.removeObject(impact);
            }
        }
        
        // Remove completed impacts
        impacts.removeAll(impactsToRemove);
    }
    
    /**
     * Inner class to represent a Meteor object
     */
    private class MeteorObject extends Actor {
        private int x, y;
        private int size;
        private int speed;
        private int rotation = 0;
        private int rotationSpeed;
        
        public MeteorObject(int x, int y, int size, int speed) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speed = speed;
            this.rotationSpeed = Greenfoot.getRandomNumber(5) + 1; // Random rotation speed
            
            // Create meteor image
            createMeteorImage();
        }
        
        private void createMeteorImage() {
            try {
                // Try to load a meteor image from file
                GreenfootImage baseImage = new GreenfootImage("images/meteor.png");
                
                // Scale the image to our desired size
                baseImage.scale(size, size);
                
                // Add glow effect for falling through atmosphere
                GreenfootImage glowImage = new GreenfootImage(size + 10, size + 10);
                glowImage.setColor(new Color(255, 100, 0, 100)); // Orange glow with transparency
                glowImage.fillOval(0, 0, size + 10, size + 10);
                glowImage.drawImage(baseImage, 5, 5);
                
                setImage(glowImage);
            } catch (Exception e) {
                // If meteor image can't be loaded, create one programmatically
                GreenfootImage meteorImage = new GreenfootImage(size, size);
                
                // Draw the meteor (a filled circle with some texture)
                meteorImage.setColor(new Color(139, 69, 19)); // Brown color
                meteorImage.fillOval(0, 0, size, size);
                
                // Add some craters
                meteorImage.setColor(new Color(101, 67, 33)); // Darker brown
                int numCraters = size / 10;
                for (int i = 0; i < numCraters; i++) {
                    int craterSize = Greenfoot.getRandomNumber(size / 4) + size / 10;
                    int craterX = Greenfoot.getRandomNumber(size - craterSize);
                    int craterY = Greenfoot.getRandomNumber(size - craterSize);
                    meteorImage.fillOval(craterX, craterY, craterSize, craterSize);
                }
                
                // Add glow effect for falling through atmosphere
                GreenfootImage glowImage = new GreenfootImage(size + 10, size + 10);
                glowImage.setColor(new Color(255, 100, 0, 100)); // Orange glow with transparency
                glowImage.fillOval(0, 0, size + 10, size + 10);
                glowImage.drawImage(meteorImage, 5, 5);
                
                setImage(glowImage);
            }
        }
        
        public void update() {
            // Fall down
            y += speed;
            
            // Rotate the meteor
            rotation = (rotation + rotationSpeed) % 360;
            setRotation(rotation);
            
            setLocation(x, y);
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        public int getSize() {
            return size;
        }
        
        public boolean isAtGround(World world) {
            return y >= world.getHeight() - (size / 2);
        }
    }
    
    /**
     * Inner class to represent a Meteor Impact
     */
    private class MeteorImpact extends Actor {
        private int x, y;
        private int size;
        private Animator impactAnimator;
        private boolean completed = false;
        
        public MeteorImpact(int x, int y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
            
            // Use the explosion spritesheet for the impact
            impactAnimator = new Animator("images/explosion");
            impactAnimator.setSpeed(80);
            
            // Scale the impact animation based on meteor size
            int impactScale = size / 20 + 1; // Base impact size on meteor size
            
            Greenfoot.playSound("sounds/explosion.mp3");
        }
        
        public boolean update() {
            // Update animation using the explosion spritesheet
            setImage(impactAnimator.getCurrentFrame());
            
            // Check if animation has completed one cycle
            if (impactAnimator.getImageIndex() == impactAnimator.getSize() - 1 && !completed) {
                completed = true;
            }
            
            return completed;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
    }
}