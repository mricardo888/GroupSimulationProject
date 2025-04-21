import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * RainingArrows creates arrows falling from the sky
 * The arrows can damage enemies and create impact effects 
 * when hitting the ground but not towers
 * 
 * @author Ricardo Lee
 */
public class RainingArrows extends SpecialSkill
{
    private ArrayList<Arrow> arrows = new ArrayList<Arrow>();
    private ArrayList<ArrowImpact> impacts = new ArrayList<ArrowImpact>();
    private int spawnRate = 5;
    private int spawnTimer = 0;
    private int arrowsRemaining = 50;
    private int arrowSpeed = 6;
    private int coverage = 80;
    private int duration = 150;
    private int timer = 0;
    private int ownerSide = 0;
    private int arrowDamage = 75;
    private int targetX;
    private int targetWidth;
    
    /**
     * For creating the instance
     */
    public RainingArrows() {}
    
    /**
     * Starts the raining arrows effect
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
     * Sets the side that activated this skill (1 or 2)
     */
    public void setOwnerSide(int side) {
        this.ownerSide = side;
    }
    
    /**
     * Sets the target area
     */
    public void setTargetArea(int x, int width) {
        this.targetX = x;
        this.targetWidth = width;
    }
    
    /**
     * Sets the density of arrows
     * Lower values = more arrows.
     */
    public void setDensity(int rate) {
        this.spawnRate = rate;
    }
    
    /**
     * Sets the duration of the raining arrows effect
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    /**
     * Sets the percentage of screen width to be covered by arrows
     */
    public void setCoverage(int percentage) {
        this.coverage = Math.min(100, Math.max(10, percentage)); // Clamp between 10-100%
    }
    
    /**
     * Sets the speed of falling arrows
     */
    public void setArrowSpeed(int speed) {
        this.arrowSpeed = speed;
    }
    
    /**
     * Spawn new arrows if effect still active
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
        
        try {
            updateArrows(world);
        } catch (ConcurrentModificationException e) {
            // try again next frame
            System.out.println("Caught ConcurrentModificationException in updateArrows");
        }
        
        try {
            updateImpacts(world);
        } catch (ConcurrentModificationException e) {
            // try again next frame
            System.out.println("Caught ConcurrentModificationException in updateImpacts");
        }
    }
    
    private void spawnArrow(World world) {
        int worldWidth = world.getWidth();
        int middleX = worldWidth / 2;
        int middleWidth = worldWidth / 3; 
        int startX = middleX - middleWidth / 2;
        int endX = middleX + middleWidth / 2;
        
        int x = Greenfoot.getRandomNumber(endX - startX) + startX;
        int y = 0; 
        int angle = 80 + Greenfoot.getRandomNumber(21);
        
        // Create the arrow
        Arrow arrow = new Arrow(x, y, angle, arrowSpeed);
        arrows.add(arrow);
        world.addObject(arrow, arrow.getX(), arrow.getY());
    }
    
    private void updateArrows(World world) {
        // Create a copy of the arrows list to avoid ConcurrentModificationException
        ArrayList<Arrow> arrowsCopy = new ArrayList<Arrow>(arrows);
        ArrayList<Arrow> arrowsToRemove = new ArrayList<Arrow>();
        
        // Iterate through the copy of the list
        for (Arrow arrow : arrowsCopy) {
            // Skip if arrow is no longer in the world
            if (arrow.getWorld() == null) {
                arrowsToRemove.add(arrow);
                continue;
            }
            
            arrow.update();
            
            // Check if arrow has hit a unit
            Unit hitUnit = arrow.checkUnitHit();
            if (hitUnit != null) {
                try {
                    // Damage the unit
                    hitUnit.hitBySpecialSkill(arrowDamage, ownerSide);
                    
                    // Create impact effect
                    ArrowImpact impact = new ArrowImpact(arrow.getX(), arrow.getY());
                    impacts.add(impact);
                    world.addObject(impact, impact.getX(), impact.getY());
                    
                    // Play ArrowWoosh sound
                    GreenfootSound arrowSound = new GreenfootSound("./sounds/ArrowWhoosh.wav");
                    arrowSound.play();
                    
                    // Mark arrow for removal
                    arrowsToRemove.add(arrow);
                    world.removeObject(arrow);
                } catch (Exception e) {
                    // Silent fail if we can't remove the arrow
                    arrowsToRemove.add(arrow);
                }
                continue; // Skip checking ground hit
            }
            
            // Check if arrow has hit the ground or Y=600
            if (arrow.isAtGround(world)) {
                try {
                    // Create impact effect
                    ArrowImpact impact = new ArrowImpact(arrow.getX(), arrow.getY());
                    impacts.add(impact);
                    world.addObject(impact, impact.getX(), impact.getY());
                    
                    // Mark arrow for removal
                    arrowsToRemove.add(arrow);
                    world.removeObject(arrow);
                } catch (Exception e) {
                    // Silent fail if we can't remove the arrow
                    arrowsToRemove.add(arrow);
                }
            }
        }
        
        // Remove arrows outside the loop
        arrows.removeAll(arrowsToRemove);
    }
    
    private void updateImpacts(World world) {
        // Create a copy of the impacts list to avoid ConcurrentModificationException
        ArrayList<ArrowImpact> impactsCopy = new ArrayList<ArrowImpact>(impacts);
        ArrayList<ArrowImpact> impactsToRemove = new ArrayList<ArrowImpact>();
        
        // Iterate through the copy of the list
        for (ArrowImpact impact : impactsCopy) {
            // Skip if impact is no longer in the world
            if (impact.getWorld() == null) {
                impactsToRemove.add(impact);
                continue;
            }
            
            if (impact.update()) {
                try {
                    // If impact animation is complete, mark for removal
                    impactsToRemove.add(impact);
                    world.removeObject(impact);
                } catch (Exception e) {
                    // Silent fail if we can't remove the impact
                    impactsToRemove.add(impact);
                }
            }
        }
        
        // Remove impacts outside the loop
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
        private SimpleTimer existenceTimer = new SimpleTimer();
        
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
            existenceTimer.mark();
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
            
            try {
                setLocation(x, y);
            } catch (Exception e) {
                // Handle possible exception if actor is removed
            }
            
            // Time out after 5 seconds (safety check)
            if (existenceTimer.millisElapsed() > 5000) {
                try {
                    World world = getWorld();
                    if (world != null) {
                        world.removeObject(this);
                    }
                } catch (Exception e) {
                    // Silent fail if we can't remove the object
                }
            }
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        public boolean isAtGround(World world) {
            // Return true when Y >= 600 or at world bottom
            return y >= 600 || (world != null && y >= world.getHeight() - 10);
        }
        
        /**
         * Check if this arrow hit a unit
         */
        public Unit checkUnitHit() {
            // Check for any intersecting units
            try {
                List<Unit> units = getIntersectingObjects(Unit.class);
                if (units != null && !units.isEmpty()) {
                    for (Unit unit : units) {
                        // Only damage units from the opposite side
                        if (unit.getSide() != ownerSide) {
                            return unit;
                        }
                    }
                }
            } catch (Exception e) {
                // Handle possible exception if intersecting objects can't be retrieved
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
        private SimpleTimer impactTimer = new SimpleTimer();
        
        public ArrowImpact(int x, int y) {
            this.x = x;
            this.y = y;
            
            // Create simple impact image (dust puff)
            GreenfootImage img = new GreenfootImage(20, 20);
            img.setColor(new Color(200, 200, 200, 200));
            img.fillOval(0, 0, 20, 20);
            setImage(img);
            impactTimer.mark();
        }
        
        public boolean update() {
            // Simple animation that fades out
            frame++;
            if (frame >= MAX_FRAMES || impactTimer.millisElapsed() > 1000) {
                completed = true;
            } else {
                try {
                    // Fade out the impact effect
                    GreenfootImage img = getImage();
                    int alpha = 200 - (frame * 20);
                    if (alpha > 0) {
                        img.setTransparency(alpha);
                    }
                } catch (Exception e) {
                    // Handle possible exception if image can't be modified
                    completed = true;
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