import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.List;

/**
 * RainingArrows is a special skill that creates a barrage of arrows falling from the sky.
 * The arrows can damage enemies and create impact effects when hitting the ground.
 * 
 * @author Your Name 
 * @version 1.0
 */
public class RainingArrows extends SpecialSkill
{
    private ArrayList<Arrow> arrows = new ArrayList<Arrow>();
    private ArrayList<ArrowImpact> impacts = new ArrayList<ArrowImpact>();
    private int spawnRate = 5; // Time between arrow spawns (lower = more frequent)
    private int spawnTimer = 0;
    private int arrowsRemaining = 50; // Default number of arrows to spawn
    private boolean active = false;
    private int arrowSpeed = 6; // Falling speed
    private int coverage = 80; // Percentage of screen width covered by arrows
    private int duration = 150; // Duration of the effect in acts
    private int timer = 0;
    private int ownerSide = 0; // Side that activated the skill
    private int arrowDamage = 75; // Damage per arrow hit - increased from 20
    private int targetX;
    private int targetWidth;
    
    /**
     * Constructor for the RainingArrows skill
     */
    public RainingArrows() {
        // Initialize any needed resources
    }
    
    /**
     * Start the raining arrows effect
     */
    public void start() {
        World world = getWorld();
        if (world != null && !active) {
            active = true;
            timer = 0;
            arrows.clear();
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
     * Set the target area for the arrows
     * @param x The center x-coordinate of the target area
     * @param width The width of the target area
     */
    public void setTargetArea(int x, int width) {
        this.targetX = x;
        this.targetWidth = width;
    }
    
    /**
     * Set the density of arrows (how many arrows spawn per act)
     * Lower values = more arrows
     */
    public void setDensity(int rate) {
        this.spawnRate = rate;
    }
    
    /**
     * Set the duration of the raining arrows effect in acts
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    /**
     * Set the percentage of screen width to be covered by arrows
     */
    public void setCoverage(int percentage) {
        this.coverage = Math.min(100, Math.max(10, percentage)); // Clamp between 10-100%
    }
    
    /**
     * Set the speed of falling arrows
     */
    public void setArrowSpeed(int speed) {
        this.arrowSpeed = speed;
    }
    
    /**
     * Act method called by Greenfoot
     */
    public void act()
    {
        World world = getWorld();
        if (world == null) return;
        
        if (active) {
            // Count how long the effect has been active
            timer++;
            
            // Check if effect duration is complete
            if (timer >= duration) {
                active = false;
            }
            
            // Spawn new arrows
            spawnTimer++;
            if (spawnTimer >= spawnRate) {
                spawnArrow(world);
                spawnTimer = 0;
            }
        }
        
        // Update arrows
        updateArrows(world);
        
        // Update impacts
        updateImpacts(world);
    }
    
    /**
     * Spawn a new arrow
     */
    private void spawnArrow(World world) {
        // Calculate spawn position
        int worldWidth = world.getWidth();
        int coverageWidth;
        int startX, endX;
        
        if (targetX > 0) {
            // Use target area if set
            coverageWidth = targetWidth;
            startX = targetX - coverageWidth / 2;
            endX = targetX + coverageWidth / 2;
            
            // Ensure within world bounds
            startX = Math.max(0, startX);
            endX = Math.min(worldWidth, endX);
        } else {
            // Default: use coverage percentage across whole screen
            coverageWidth = (worldWidth * coverage) / 100;
            startX = (worldWidth - coverageWidth) / 2;
            endX = startX + coverageWidth;
        }
        
        // Random X position within the covered area
        int x = Greenfoot.getRandomNumber(endX - startX) + startX;
        int y = 0; // Start at the top
        
        // Randomize slight angle variations
        int angle = 80 + Greenfoot.getRandomNumber(21); // 80-100 degrees
        
        // Create the arrow
        Arrow arrow = new Arrow(x, y, angle, arrowSpeed);
        arrows.add(arrow);
        world.addObject(arrow, arrow.getX(), arrow.getY());
    }
    
    /**
     * Update all active arrows
     */
    private void updateArrows(World world) {
        ArrayList<Arrow> arrowsToRemove = new ArrayList<Arrow>();
        
        for (Arrow arrow : arrows) {
            arrow.update();
            
            // Check if arrow has hit a unit
            Unit hitUnit = arrow.checkUnitHit();
            if (hitUnit != null) {
                // Damage the unit
                hitUnit.hitBySpecialSkill(arrowDamage, ownerSide);
                
                // Create impact effect
                ArrowImpact impact = new ArrowImpact(arrow.getX(), arrow.getY());
                impacts.add(impact);
                world.addObject(impact, impact.getX(), impact.getY());
                
                // Mark arrow for removal
                arrowsToRemove.add(arrow);
                world.removeObject(arrow);
                continue; // Skip checking ground hit
            }
            
            // Check if arrow has hit an enemy tower
            Tower hitTower = arrow.checkTowerHit();
            if (hitTower != null) {
                // Damage the tower
                hitTower.damage(arrowDamage);
                
                // Create impact effect
                ArrowImpact impact = new ArrowImpact(arrow.getX(), arrow.getY());
                impacts.add(impact);
                world.addObject(impact, impact.getX(), impact.getY());
                
                // Mark arrow for removal
                arrowsToRemove.add(arrow);
                world.removeObject(arrow);
                continue; // Skip checking ground hit
            }
            
            // Check if arrow has hit the ground or Y=600
            if (arrow.isAtGround(world)) {
                // Create impact effect
                ArrowImpact impact = new ArrowImpact(arrow.getX(), arrow.getY());
                impacts.add(impact);
                world.addObject(impact, impact.getX(), impact.getY());
                
                // Mark arrow for removal
                arrowsToRemove.add(arrow);
                world.removeObject(arrow);
            }
        }
        
        // Remove arrows that hit the ground, units or towers
        arrows.removeAll(arrowsToRemove);
    }
    
    /**
     * Update all active impacts
     */
    private void updateImpacts(World world) {
        ArrayList<ArrowImpact> impactsToRemove = new ArrayList<ArrowImpact>();
        
        for (ArrowImpact impact : impacts) {
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
     * Inner class to represent an Arrow
     */
    private class Arrow extends Actor {
        private int x, y;
        private int speed;
        private int angle; // Angle in degrees
        private double vx, vy; // Velocity components
        
        public Arrow(int x, int y, int angle, int speed) {
            this.x = x;
            this.y = y;
            this.angle = angle;
            this.speed = speed;
            
            // Calculate velocity components
            double radians = Math.toRadians(angle);
            vx = Math.cos(radians) * speed;
            vy = Math.sin(radians) * speed;
            
            // Create arrow image
            createArrowImage();
        }
        
        private void createArrowImage() {
            // If arrow image can't be loaded, create one programmatically
            GreenfootImage arrowImage = new GreenfootImage(30, 8);
            
            // Draw arrowhead
            arrowImage.setColor(Color.DARK_GRAY);
            int[] xPoints = {25, 30, 25};
            int[] yPoints = {0, 4, 8};
            arrowImage.fillPolygon(xPoints, yPoints, 3);
            
            // Draw arrow shaft
            arrowImage.setColor(Color.CYAN);
            arrowImage.fillRect(0, 3, 25, 2);
            
            // Draw fletching
            arrowImage.setColor(Color.RED);
            int[] fxPoints = {0, 5, 0};
            int[] fyPoints1 = {1, 4, 3};
            int[] fyPoints2 = {5, 4, 7};
            arrowImage.fillPolygon(fxPoints, fyPoints1, 3);
            arrowImage.fillPolygon(fxPoints, fyPoints2, 3);
            
            setImage(arrowImage);
            
            // Set rotation of the arrow based on its trajectory
            setRotation(angle);
        }
        
        public void update() {
            // Move the arrow according to velocity
            x += vx;
            y += vy;
            
            // Apply slight gravity effect to make arc more pronounced
            vy += 0.1;
            
            // Update arrow's rotation to follow its path
            angle = (int) Math.toDegrees(Math.atan2(vy, vx));
            setRotation(angle);
            
            setLocation(x, y);
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        public boolean isAtGround(World world) {
            // Return true when Y >= 600 or at world bottom
            return y >= 600 || y >= world.getHeight() - 10;
        }
        
        /**
         * Check if this arrow hit a unit
         */
        public Unit checkUnitHit() {
            // Check for any intersecting units
            List<Unit> units = getIntersectingObjects(Unit.class);
            if (units != null && !units.isEmpty()) {
                for (Unit unit : units) {
                    // Only damage units from the opposite side
                    if (unit.getSide() != ownerSide) {
                        return unit;
                    }
                }
            }
            return null;
        }
        
        /**
         * Check if this arrow hit a tower
         */
        public Tower checkTowerHit() {
            // Look for towers this arrow might have hit
            List<Tower> towers = getIntersectingObjects(Tower.class);
            if (towers != null && !towers.isEmpty()) {
                for (Tower tower : towers) {
                    // Only damage towers from the opposite side
                    if (tower.getSide() != ownerSide) {
                        return tower;
                    }
                }
            }
            return null;
        }
    }
    
    /**
     * Inner class to represent an Arrow Impact
     */
    private class ArrowImpact extends Actor {
        private int x, y;
        private int frame = 0;
        private final int MAX_FRAMES = 10;
        private boolean completed = false;
        
        public ArrowImpact(int x, int y) {
            this.x = x;
            this.y = y;
            
            // Create simple impact image (dust puff)
            GreenfootImage img = new GreenfootImage(20, 20);
            img.setColor(new Color(200, 200, 200, 200));
            img.fillOval(0, 0, 20, 20);
            setImage(img);
        }
        
        public boolean update() {
            // Simple animation that fades out
            frame++;
            if (frame >= MAX_FRAMES) {
                completed = true;
            } else {
                // Fade out the impact effect
                GreenfootImage img = getImage();
                int alpha = 200 - (frame * 20);
                if (alpha > 0) {
                    img.setTransparency(alpha);
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