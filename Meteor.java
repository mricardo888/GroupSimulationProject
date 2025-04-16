import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.List;

/**
 * A Meteor class that creates meteors falling from the sky.
 * The meteors can vary in size, speed, and can create explosions on impact with the ground.
 * Modified to spawn in the middle area and not damage towers.
 */
public class Meteor extends SpecialSkill
{
    private ArrayList<MeteorObject> meteors = new ArrayList<MeteorObject>();
    private ArrayList<MeteorImpact> impacts = new ArrayList<MeteorImpact>();
    private int spawnRate = 50; // Time between meteor spawns
    private int spawnTimer = 0;
    private int meteorsRemaining = 10; // Default number of meteors to spawn
    private int minSize = 20; // Minimum meteor size
    private int maxSize = 60; // Maximum meteor size
    private int minSpeed = 2; // Minimum falling speed
    private int maxSpeed = 7; // Maximum falling speed
    private int ownerSide = 0; // Side that activated the skill
    private int meteorDamage = 150; // Damage per meteor hit
    private int targetX;
    private int targetWidth;
    
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
     * Set the side that activated this skill
     */
    public void setOwnerSide(int side) {
        this.ownerSide = side;
    }
    
    /**
     * Set the target area for the meteors
     * @param x The center x-coordinate of the target area
     * @param width The width of the target area
     */
    public void setTargetArea(int x, int width) {
        this.targetX = x;
        this.targetWidth = width;
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
        
        // Always update meteors and impacts, even if no new meteors are spawning
        updateMeteors(world);
        updateImpacts(world);
    }
    
    /**
     * Spawn a new meteor
     */
    private void spawnMeteor(World world) {
        // Randomize meteor properties
        int size = Greenfoot.getRandomNumber(maxSize - minSize + 1) + minSize;
        int speed = Greenfoot.getRandomNumber(maxSpeed - minSpeed + 1) + minSpeed;
        
        // Always spawn in the middle area of the screen
        int middleX = world.getWidth() / 2;
        int spawnWidth = world.getWidth() / 3; // Use 1/3 of the screen width centered in the middle
        
        // Random X position within the middle area
        int minX = middleX - spawnWidth / 2;
        int maxX = middleX + spawnWidth / 2;
        int x = minX + Greenfoot.getRandomNumber(maxX - minX);
        
        int y = 0; // Start at the top
        
        // Create the meteor
        MeteorObject meteor = new MeteorObject(x, y, size, speed);
        meteor.setSide(ownerSide); // Set the side of the meteor to match the owner
        meteors.add(meteor);
        world.addObject(meteor, meteor.getX(), meteor.getY());
    }
    
    /**
     * Update all active meteors
     */
    private void updateMeteors(World world) {
        ArrayList<MeteorObject> meteorsToRemove = new ArrayList<MeteorObject>();
        
        for (MeteorObject meteor : meteors) {
            meteor.update();
            
            // Get current location for accurate collision check
            int currentX = meteor.getX();
            int currentY = meteor.getY();
            
            // Check if meteor has hit a unit
            boolean hitEnemy = false;
            
            // Get all units at current position
            List<Unit> units = meteor.getIntersectingUnits();
            for (Unit unit : units) {
                // Check if the unit is from the opposite side of the meteor owner
                int unitSide = unit.getSide();
                if (unitSide != ownerSide) {
                    // Damage the unit directly - make sure we use the owner's side for the skill damage
                    unit.hitBySpecialSkill(meteorDamage, ownerSide);
                    hitEnemy = true;
                }
            }
            
            // No longer check for tower hits - skip this part to prevent tower damage
            
            // Check if meteor has hit the ground or a unit
            if (meteor.isAtGround(world) || hitEnemy) {
                // Create impact effect
                MeteorImpact impact = new MeteorImpact(meteor.getX(), meteor.getY(), meteor.getSize());
                impacts.add(impact);
                world.addObject(impact, impact.getX(), impact.getY());
                
                // Damage units in the impact area
                damageUnitsInArea(world, meteor.getX(), meteor.getY(), meteor.getSize() * 2);
                
                // Mark meteor for removal
                meteorsToRemove.add(meteor);
                world.removeObject(meteor);
            }
        }
        
        // Remove meteors that hit the ground or units
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
     * Damage units within the impact radius
     */
    private void damageUnitsInArea(World world, int x, int y, int radius) {
        // Get all units in the world
        java.util.List<Unit> units = world.getObjects(Unit.class);
        
        // Check each unit to see if it's in the damage radius
        for (Unit unit : units) {
            int unitX = unit.getX();
            int unitY = unit.getY();
            
            // Calculate distance between unit and impact center
            double distance = Math.sqrt(Math.pow(unitX - x, 2) + Math.pow(unitY - y, 2));
            
            // Only damage enemy units - units from the opposite side of the owner
            if (distance <= radius && unit.getSide() != ownerSide) {
                unit.hitBySpecialSkill(meteorDamage, ownerSide);
            }
        }
        
        // Removed tower damage section
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
        private int side;  // Added this to track which side the meteor belongs to
        
        public MeteorObject(int x, int y, int size, int speed) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speed = speed;
            this.rotationSpeed = Greenfoot.getRandomNumber(5) + 1; // Random rotation speed
            
            // Create meteor image
            createMeteorImage();
        }
        
        public void setSide(int side) {
            this.side = side;
        }
        
        public int getSide() {
            return side;
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
            // Check if meteor has reached the ground level or hitting Y=600
            return y >= 600 || y >= world.getHeight() - (size / 2);
        }
        
        /**
         * Get all intersecting units - safe way for inner class
         */
        public List<Unit> getIntersectingUnits() {
            return getIntersectingObjects(Unit.class);
        }
        
        /**
         * Get all intersecting towers - safe way for inner class
         */
        public List<Tower> getIntersectingTowers() {
            return getIntersectingObjects(Tower.class);
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
        private int frame = 0;
        private final int MAX_FRAMES = 20;
        
        public MeteorImpact(int x, int y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
            
            // Use the explosion spritesheet for the impact
            try {
                impactAnimator = new Animator("images/explosion");
                impactAnimator.setSpeed(80);
                
                // Scale the impact animation based on meteor size
                int impactScale = size / 20 + 1; // Base impact size on meteor size
            } catch (Exception e) {
                // If animator can't be created, use a simple effect
                GreenfootImage img = new GreenfootImage(size * 2, size * 2);
                img.setColor(new Color(255, 165, 0, 180)); // Orange with transparency
                img.fillOval(0, 0, size * 2, size * 2);
                setImage(img);
                impactAnimator = null;
            }
        }
        
        public boolean update() {
            if (impactAnimator != null) {
                // Update animation using the explosion spritesheet
                setImage(impactAnimator.getCurrentFrame());
                
                // Check if animation has completed one cycle
                if (impactAnimator.getImageIndex() == impactAnimator.getSize() - 1 && !completed) {
                    completed = true;
                }
            } else {
                // Simple animation that fades out
                frame++;
                if (frame >= MAX_FRAMES) {
                    completed = true;
                } else {
                    // Fade out gradually
                    GreenfootImage img = getImage();
                    if (img.getTransparency() > 20) {
                        img.setTransparency(img.getTransparency() - 20);
                    } else {
                        completed = true;
                    }
                }
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