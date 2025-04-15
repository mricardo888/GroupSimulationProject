import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * LaserBeam is a special skill that shoots laser beams across the screen.
 * Now modified to fire vertically downward from the top of the screen.
 * Only damages units (not towers) and targets the middle area of the screen.
 */
public class LaserBeam extends SpecialSkill
{
    private Animator laserGunAnimator;
    private int shootingDelay = 15; // Time between laser shots
    private int shootTimer = 0;
    private boolean active = false;
    private int gunX, gunY;
    private int shotsRemaining = 5; // Number of laser shots to fire
    private ArrayList<Beam> beams = new ArrayList<Beam>();
    private ArrayList<Impact> impacts = new ArrayList<Impact>();
    private int ownerSide = 0; // Side that activated the skill
    private int laserDamage = 250; // Damage per laser hit
    private int targetWidth; // Width of the target area
    
    /**
     * Constructor for the LaserBeam skill
     */
    public LaserBeam() {
    }
    
    /**
     * Set the side that activated this skill
     */
    public void setOwnerSide(int side) {
        this.ownerSide = side;
    }
    
    /**
     * Start shooting laser beams
     */
    public void start() {
        World world = getWorld();
        if (world != null && !active) {
            active = true;
            shotsRemaining = 5;
            beams.clear();
            impacts.clear();
        }
    }
    
    /**
     * Set the number of shots to fire
     */
    public void setShots(int shots) {
        this.shotsRemaining = shots;
    }
    
    /**
     * Set the position and target area for the laser beam
     */
    public void setGunPosition(int x, int y) {
        this.gunX = x;
        this.gunY = y;
    }
    
    /**
     * Act method called by Greenfoot
     */
    public void act()
    {
        World world = getWorld();
        if (world == null) return;
        
        if (active) {
            // Set image to current frame of animation if available
            if (laserGunAnimator != null) {
                setImage(laserGunAnimator.getCurrentFrame());
            }
            
            // Position at the top-middle of the screen
            setLocation(gunX, gunY);
            
            // Fire laser beams vertically downward
            shootTimer++;
            if (shootTimer >= shootingDelay && shotsRemaining > 0) {
                fireBeam(world);
                shootTimer = 0;
                shotsRemaining--;
                
                // Stop when all shots fired
                if (shotsRemaining <= 0) {
                    active = false;
                }
            }
        }
        
        // Update beams
        updateBeams(world);
        
        // Update impacts
        updateImpacts(world);
    }
    
    /**
     * Fire a laser beam from the top of the screen
     */
    private void fireBeam(World world) {
        // Calculate a random position within the middle area
        int worldWidth = world.getWidth();
        int middleX = worldWidth / 2;
        int middleWidth = worldWidth / 3; // 1/3 of the screen for middle area
        
        // Randomize X position within the target area
        int minX = middleX - middleWidth / 2;
        int maxX = middleX + middleWidth / 2;
        int startX = minX + Greenfoot.getRandomNumber(maxX - minX);
        
        // Start at the top of the screen
        int startY = 0;
        
        // Create the beam - direction 90 means straight down
        Beam beam = new Beam(startX, startY, 90);
        beams.add(beam);
        world.addObject(beam, beam.getX(), beam.getY());
    }
    
    /**
     * Update all active beams
     */
    private void updateBeams(World world) {
        ArrayList<Beam> beamsToRemove = new ArrayList<Beam>();
        
        for (Beam beam : beams) {
            beam.update();
            
            // Check if beam has hit edge or reached bottom
            if (beam.isAtEdge(world)) {
                // Create impact effect
                Impact impact = new Impact(beam.getX(), beam.getY());
                impacts.add(impact);
                world.addObject(impact, impact.getX(), impact.getY());
                
                // Mark beam for removal
                beamsToRemove.add(beam);
                world.removeObject(beam);
                continue; // Skip remaining checks
            }
            
            // Check if beam hit a unit
            Unit hitUnit = beam.checkUnitHit();
            if (hitUnit != null) {
                // Damage the unit
                hitUnit.hitBySpecialSkill(laserDamage, ownerSide);
                
                // Create impact effect at the unit's position
                Impact impact = new Impact(hitUnit.getX(), hitUnit.getY());
                impacts.add(impact);
                world.addObject(impact, impact.getX(), impact.getY());
                
                // Mark beam for removal
                beamsToRemove.add(beam);
                world.removeObject(beam);
                continue; // Skip remaining checks
            }
            
            // Note: Tower damage check is removed - lasers no longer affect towers
        }
        
        // Remove beams that hit edge, obstacle, or unit
        beams.removeAll(beamsToRemove);
    }
    
    /**
     * Update all active impacts
     */
    private void updateImpacts(World world) {
        ArrayList<Impact> impactsToRemove = new ArrayList<Impact>();
        
        for (Impact impact : impacts) {
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
     * Inner class to represent a Laser Beam
     */
    private class Beam extends Actor {
        private int x, y;
        private int direction; // 90 = straight down
        private int speed = 10;
        private GreenfootImage beamImage;
        
        public Beam(int x, int y, int direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
            
            // Create laser beam image - taller for vertical orientation
            beamImage = new GreenfootImage(5, 30);
            beamImage.setColor(Color.RED);
            beamImage.fill();
            
            // Add glow effect
            beamImage.setTransparency(200);
            
            setImage(beamImage);
            setRotation(direction); // Point downward (90 degrees)
        }
        
        public void update() {
            // Move beam straight down
            y += speed;
            setLocation(x, y);
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        public boolean isAtEdge(World world) {
            // Only check for bottom edge since we're firing downward
            return y >= 600 || y >= world.getHeight() - 1;
        }
        
        /**
         * Check if this beam hit a unit
         */
        public Unit checkUnitHit() {
            // Look for units this beam might have hit
            Actor actor = getOneIntersectingObject(Unit.class);
            if (actor != null && actor instanceof Unit) {
                Unit unit = (Unit) actor;
                // Only hit units from the opposite side
                if (unit.getSide() != ownerSide) {
                    return unit;
                }
            }
            return null;
        }
    }
    
    /**
     * Inner class to represent a Laser Impact Effect
     */
    private class Impact extends Actor {
        private int x, y;
        private Animator impactAnimator;
        private boolean completed = false;
        private int frame = 0;
        private final int MAX_FRAMES = 10;
        
        public Impact(int x, int y) {
            this.x = x;
            this.y = y;
            
            // Try to use animator if available
            try {
                impactAnimator = new Animator("images/laserimpact");
                impactAnimator.setSpeed(50);
            } catch (Exception e) {
                // If no animation available, create a simple impact
                GreenfootImage impactImg = new GreenfootImage(20, 20);
                impactImg.setColor(Color.YELLOW);
                impactImg.fillOval(0, 0, 20, 20);
                setImage(impactImg);
                impactAnimator = null;
            }
        }
        
        public boolean update() {
            // If using animator
            if (impactAnimator != null) {
                // Update animation
                setImage(impactAnimator.getCurrentFrame());
                
                // Check if animation has completed one cycle
                if (impactAnimator.getImageIndex() == impactAnimator.getSize() - 1 && !completed) {
                    completed = true;
                }
            } else {
                // Simple impact effect that fades out
                frame++;
                if (frame >= MAX_FRAMES) {
                    completed = true;
                } else {
                    // Fade out the impact
                    GreenfootImage img = getImage();
                    int transparency = 200 - (frame * 20);
                    if (transparency > 0) {
                        img.setTransparency(transparency);
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