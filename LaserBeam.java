import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * LaserBeam is a special skill that shoots laser beams across the screen.
 * Modified to fire vertically downward like RainingArrows but with laser beams.
 * Only damages units (not towers) and targets the middle area of the screen.
 */
public class LaserBeam extends SpecialSkill
{
    private ArrayList<Beam> beams = new ArrayList<Beam>();
    private ArrayList<Impact> impacts = new ArrayList<Impact>();
    private int spawnRate = 5; // Time between laser beam spawns (lower = more frequent)
    private int spawnTimer = 0;
    private int beamsRemaining = 20; // Default number of beams to spawn
    private int beamSpeed = 10; // Falling speed
    private int duration = 120; // Duration of the effect in acts
    private int timer = 0;
    private int ownerSide = 0; // Side that activated the skill
    private int laserDamage = 250; // Damage per laser hit
    
    /**
     * Constructor for the LaserBeam skill
     */
    public LaserBeam() {
        // Initialize with transparent image
        GreenfootImage transparentImage = new GreenfootImage(1, 1);
        transparentImage.setTransparency(0);
        setImage(transparentImage);
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
            timer = 0;
            beams.clear();
            impacts.clear();
        }
    }
    
    /**
     * Set the number of shots to fire
     */
    public void setShots(int shots) {
        this.beamsRemaining = shots * 5; // Multiply to create more beams
        this.duration = shots * 15;  // Set duration based on shots
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
            
            // Spawn new laser beams
            spawnTimer++;
            if (spawnTimer >= spawnRate) {
                spawnBeam(world);
                spawnTimer = 0;
            }
        }
        
        // Always update beams and impacts, even if the laser has stopped firing
        try {
            updateBeams(world);
        } catch (ConcurrentModificationException e) {
            // If we get a concurrent modification exception, try again next frame
            System.out.println("Caught ConcurrentModificationException in updateBeams");
        }
        
        try {
            updateImpacts(world);
        } catch (ConcurrentModificationException e) {
            // If we get a concurrent modification exception, try again next frame
            System.out.println("Caught ConcurrentModificationException in updateImpacts");
        }
    }
    
    /**
     * Fire a laser beam from the top of the screen
     */
    private void spawnBeam(World world) {
        // Calculate spawn position in the middle area
        int worldWidth = world.getWidth();
        int middleX = worldWidth / 2;
        int middleWidth = worldWidth / 3; // 1/3 of the screen width for middle area
        
        // Calculate bounds for the middle area
        int startX = middleX - middleWidth / 2;
        int endX = middleX + middleWidth / 2;
        
        // Random X position within the middle area
        int x = Greenfoot.getRandomNumber(endX - startX) + startX;
        int y = 0; // Start at the top
        
        // Create the beam - direction 90 means straight down
        Beam beam = new Beam(x, y);
        beams.add(beam);
        world.addObject(beam, x, y);
    }
    
    /**
     * Update all active beams
     */
    private void updateBeams(World world) {
        // Create a copy of the beams list to avoid ConcurrentModificationException
        ArrayList<Beam> beamsCopy = new ArrayList<Beam>(beams);
        ArrayList<Beam> beamsToRemove = new ArrayList<Beam>();
        
        // Iterate through the copy of the list
        for (Beam beam : beamsCopy) {
            // Skip if beam is no longer in the world
            if (beam.getWorld() == null) {
                beamsToRemove.add(beam);
                continue;
            }
            
            beam.update();
            
            // Check if beam has hit a unit
            Unit hitUnit = beam.checkUnitHit();
            if (hitUnit != null) {
                try {
                    // Damage the unit
                    hitUnit.hitBySpecialSkill(laserDamage, ownerSide);
                    
                    // Create impact effect at the unit's position
                    Impact impact = new Impact(hitUnit.getX(), hitUnit.getY());
                    impacts.add(impact);
                    world.addObject(impact, impact.getX(), impact.getY());
                    
                    // Mark beam for removal
                    beamsToRemove.add(beam);
                    world.removeObject(beam);
                } catch (Exception e) {
                    // Silent fail if we can't process the hit
                    beamsToRemove.add(beam);
                }
                continue; // Skip checking ground hit
            }
            
            // Check if beam has hit edge or reached bottom
            if (beam.isAtGround(world)) {
                try {
                    // Create impact effect
                    Impact impact = new Impact(beam.getX(), beam.getY());
                    impacts.add(impact);
                    world.addObject(impact, impact.getX(), impact.getY());
                    
                    // Mark beam for removal
                    beamsToRemove.add(beam);
                    world.removeObject(beam);
                } catch (Exception e) {
                    // Silent fail if we can't remove the beam
                    beamsToRemove.add(beam);
                }
            }
        }
        
        // Remove beams outside the loop
        beams.removeAll(beamsToRemove);
    }
    
    /**
     * Update all active impacts
     */
    private void updateImpacts(World world) {
        // Create a copy of the impacts list to avoid ConcurrentModificationException
        ArrayList<Impact> impactsCopy = new ArrayList<Impact>(impacts);
        ArrayList<Impact> impactsToRemove = new ArrayList<Impact>();
        
        // Iterate through the copy of the list
        for (Impact impact : impactsCopy) {
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
     * Inner class to represent a Laser Beam
     */
    private class Beam extends Actor {
        private int x, y;
        private int speed = 10;
        private GreenfootImage beamImage;
        private SimpleTimer existenceTimer = new SimpleTimer();
        
        public Beam(int x, int y) {
            this.x = x;
            this.y = y;
            
            // Create vertically oriented laser beam image
            beamImage = new GreenfootImage(6, 40);
            beamImage.setColor(Color.RED);
            beamImage.fill();
            
            // Add glow effect
            GreenfootImage glowImage = new GreenfootImage(12, 46);
            glowImage.setColor(new Color(255, 0, 0, 80)); // Red with transparency
            glowImage.fillOval(0, 0, 12, 46);
            glowImage.drawImage(beamImage, 3, 3);
            
            setImage(glowImage);
            existenceTimer.mark();
        }
        
        public void update() {
            // Move beam straight down
            y += speed;
            
            try {
                setLocation(x, y);
            } catch (Exception e) {
                // Handle possible exception if actor is removed
            }
            
            // Time out after 3 seconds (safety check)
            if (existenceTimer.millisElapsed() > 3000) {
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
            return y >= 600 || (world != null && y >= world.getHeight() - 20);
        }
        
        /**
         * Check if this beam hit a unit
         */
        public Unit checkUnitHit() {
            try {
                // Look for units this beam might have hit
                Actor actor = getOneIntersectingObject(Unit.class);
                if (actor != null && actor instanceof Unit) {
                    Unit unit = (Unit) actor;
                    // Only hit units from the opposite side
                    if (unit.getSide() != ownerSide) {
                        return unit;
                    }
                }
            } catch (Exception e) {
                // Silent fail if we can't check for unit hits
            }
            return null;
        }
    }
    
    /**
     * Inner class to represent a Laser Impact Effect
     */
    private class Impact extends Actor {
        private int x, y;
        private boolean completed = false;
        private int frame = 0;
        private final int MAX_FRAMES = 10;
        private SimpleTimer impactTimer = new SimpleTimer();
        
        public Impact(int x, int y) {
            this.x = x;
            this.y = y;
            
            // Create a simple impact effect
            GreenfootImage impactImg = new GreenfootImage(20, 20);
            impactImg.setColor(new Color(255, 0, 0, 180)); // Red with transparency
            impactImg.fillOval(0, 0, 20, 20);
            impactImg.setColor(new Color(255, 255, 0, 150)); // Yellow with transparency
            impactImg.fillOval(5, 5, 10, 10);
            setImage(impactImg);
            impactTimer.mark();
        }
        
        public boolean update() {
            // Simple animation that fades out with a timeout
            frame++;
            if (frame >= MAX_FRAMES || impactTimer.millisElapsed() > 1000) {
                completed = true;
            } else {
                try {
                    // Fade out the impact
                    GreenfootImage img = getImage();
                    int transparency = 180 - (frame * 18);
                    if (transparency > 0) {
                        img.setTransparency(transparency);
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