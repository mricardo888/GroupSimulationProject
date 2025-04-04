import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * LaserBeam is a special skill that shoots laser beams across the screen.
 * The beams can be fired in different directions and create impact effects when hitting targets.
 * 
 * @author Your Name 
 * @version 1.0
 */
public class LaserBeam extends SpecialSkill
{
    private Animator laserGunAnimator;
    private int shootingDelay = 15; // Time between laser shots
    private int shootTimer = 0;
    private boolean active = false;
    private int gunX, gunY;
    private int direction = 0; // 0 = right, 90 = down, 180 = left, 270 = up
    private int shotsRemaining = 5; // Number of laser shots to fire
    private ArrayList<Beam> beams = new ArrayList<Beam>();
    private ArrayList<Impact> impacts = new ArrayList<Impact>();
    
    /**
     * Constructor for the LaserBeam skill
     */
    public LaserBeam() {
        // Assuming there's a directory with laser gun animation frames
        laserGunAnimator = new Animator("images/lasergun");
        laserGunAnimator.setSpeed(100); // Animation speed
    }
    
    /**
     * Start shooting laser beams
     */
    public void start() {
        World world = getWorld();
        if (world != null && !active) {
            // Initialize gun position (center of screen by default)
            gunX = world.getWidth() / 2;
            gunY = world.getHeight() / 2;
            active = true;
            shotsRemaining = 5;
            beams.clear();
            impacts.clear();
        }
    }
    
    /**
     * Set the direction of laser firing
     * @param direction 0 = right, 90 = down, 180 = left, 270 = up
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }
    
    /**
     * Set the position of the laser gun
     */
    public void setGunPosition(int x, int y) {
        this.gunX = x;
        this.gunY = y;
    }
    
    /**
     * Set the number of shots to fire
     */
    public void setShots(int shots) {
        this.shotsRemaining = shots;
    }
    
    /**
     * Act method called by Greenfoot
     */
    public void act()
    {
        World world = getWorld();
        if (world == null) return;
        
        if (active) {
            // Set image to current frame of animation
            setImage(laserGunAnimator.getCurrentFrame());
            setLocation(gunX, gunY);
            setRotation(direction);
            
            // Fire laser beams
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
     * Fire a laser beam from the gun
     */
    private void fireBeam(World world) {
        // Calculate starting position based on gun position and direction
        int startX = gunX;
        int startY = gunY;
        
        // Offset the beam starting position to be at the end of the gun
        int offset = 30; // Distance from center of gun to muzzle
        if (direction == 0) { // Right
            startX += offset;
        } else if (direction == 90) { // Down
            startY += offset;
        } else if (direction == 180) { // Left
            startX -= offset;
        } else if (direction == 270) { // Up
            startY -= offset;
        }
        
        // Create the beam
        Beam beam = new Beam(startX, startY, direction);
        beams.add(beam);
        world.addObject(beam, beam.getX(), beam.getY());
        
        Greenfoot.playSound("/sounds/laserBeam.mp3");
    }
    
    /**
     * Update all active beams
     */
    private void updateBeams(World world) {
        ArrayList<Beam> beamsToRemove = new ArrayList<Beam>();
        
        for (Beam beam : beams) {
            beam.update();
            
            // Check if beam has hit edge or obstacle
            if (beam.isAtEdge(world) || beam.hitObstacle()) {
                // Create impact effect
                Impact impact = new Impact(beam.getX(), beam.getY());
                impacts.add(impact);
                world.addObject(impact, impact.getX(), impact.getY());
                
                // Mark beam for removal
                beamsToRemove.add(beam);
                world.removeObject(beam);
            }
        }
        
        // Remove beams that hit edge or obstacles
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
        private int direction;
        private int speed = 10;
        private GreenfootImage beamImage;
        
        public Beam(int x, int y, int direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
            
            // Create laser beam image
            beamImage = new GreenfootImage(20, 5);
            beamImage.setColor(Color.RED);
            beamImage.fill();
            
            // Add glow effect
            beamImage.setTransparency(200);
            
            setImage(beamImage);
            setRotation(direction);
        }
        
        public void update() {
            // Move beam in the specified direction
            if (direction == 0) { // Right
                x += speed;
            } else if (direction == 90) { // Down
                y += speed;
            } else if (direction == 180) { // Left
                x -= speed;
            } else if (direction == 270) { // Up
                y -= speed;
            }
            
            setLocation(x, y);
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        public boolean isAtEdge(World world) {
            return x <= 0 || x >= world.getWidth() - 1 || 
                   y <= 0 || y >= world.getHeight() - 1;
        }
        
        public boolean hitObstacle() {
            // Check for collision with obstacles
            // This is a simplified example - you might need to customize this
            // based on what classes your obstacles are
            return getOneIntersectingObject(null) != null;
        }
    }
    
    /**
     * Inner class to represent a Laser Impact Effect
     */
    private class Impact extends Actor {
        private int x, y;
        private Animator impactAnimator;
        private boolean completed = false;
        
        public Impact(int x, int y) {
            this.x = x;
            this.y = y;
            
            GreenfootImage impactImg = new GreenfootImage(20, 20);
            impactImg.setColor(Color.YELLOW);
            impactImg.fillOval(0, 0, 20, 20);
            setImage(impactImg);
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
                GreenfootImage img = getImage();
                int transparency = img.getTransparency() - 10;
                if (transparency <= 0) {
                    completed = true;
                } else {
                    img.setTransparency(transparency);
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