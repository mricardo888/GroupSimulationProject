import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.List;

/**
 * PlaneBomb is a special skill that creates a plane flying across the screen
 * and dropping bombs that explode on impact with the ground.
 * Modified to only target units (not towers) and spawn in the middle area.
 * 
 * @author Ricardo Lee
 */
public class PlaneBomb extends SpecialSkill
{
    private Animator animator;
    private int planeSpeed = 3;
    private int bombDropRate = 30;
    private int bombTimer = 0;
    private int planeX, planeY;
    private ArrayList<Bomb> bombs = new ArrayList<Bomb>();
    private ArrayList<Explosion> explosions = new ArrayList<Explosion>();
    private int ownerSide = 0;
    private int bombDamage = 200;
    private int targetDirection = 1;
    
    /**
     * Constructor for the PlaneBomb skill. Initializes the plane animator.
     */
    public PlaneBomb() {
        animator = new Animator("images/plane");
        animator.setSpeed(100);
    }
    
    /**
     * Sets the side that activated this skill
     */
    public void setOwnerSide(int side) {
        this.ownerSide = side;
    }
    
    /**
     * Direction 1 for left to right, -1 for right to left
     */
    public void setDirection(int direction) {
        this.targetDirection = direction;
    }
    
    /**
     * Starts the plane bombing
     */
    public void start() {
        World world = getWorld();
        if (world != null && !active) {
            if (targetDirection > 0) {
                planeX = 0;
                setRotation(0);
            } else {
                planeX = world.getWidth();
                setRotation(180);
            }
            planeY = 100;
            active = true;
            bombs.clear();
            explosions.clear();
        }
    }
    
    /**
     * Updates plane position, drops bombs in the middle area
     */
    public void act()
    {
        World world = getWorld();
        if (world == null) return;
        
        if (active) {
            // Update plane position
            planeX += planeSpeed * targetDirection;
            
            // Set image to current frame of animation
            if (animator != null) {
                setImage(animator.getCurrentFrame());
            }
            setLocation(planeX, planeY);
            
            // Only drop bombs in the middle area
            int middleX = world.getWidth() / 2;
            int middleWidth = world.getWidth() / 3;
            int leftBound = middleX - middleWidth / 2;
            int rightBound = middleX + middleWidth / 2;
            
            // Only drop bombs while in the middle area
            if (planeX >= leftBound && planeX <= rightBound) {
                bombTimer++;
                if (bombTimer >= bombDropRate) {
                    dropBomb(world);
                    bombTimer = 0;
                }
            }
            
            // Check if plane has exited the screen
            if ((targetDirection > 0 && planeX >= world.getWidth() + 100) || 
                (targetDirection < 0 && planeX <= -100)) {
                active = false;
                // Remove the plane object from world when it's off-screen
                setLocation(-200, -200); // Move off-screen before potentially removing
            }
        }
        
        if (!active) {
            // Create a transparent image
            GreenfootImage transparentImage = new GreenfootImage(1, 1);
            transparentImage.setTransparency(0);
            setImage(transparentImage);
        }
        
        // Always update bombs and explosions, even if plane is no longer active
        updateBombs(world);
        updateExplosions(world);
    }
    
    private void dropBomb(World world) {
        Bomb bomb = new Bomb(planeX, planeY + 30);
        bombs.add(bomb);
        world.addObject(bomb, bomb.getX(), bomb.getY());
        // Play Bomb dropping sound
        GreenfootSound bombDrop = new GreenfootSound("./sounds/bombsFalling.mp3");
        bombDrop.play();
    }
    
    private void updateBombs(World world) {
        ArrayList<Bomb> bombsToRemove = new ArrayList<Bomb>();
        
        for (Bomb bomb : bombs) {
            bomb.update();
            
            // Check if bomb hit a unit - Use public methods instead of protected ones
            List<Unit> intersectingUnits = bomb.getIntersectingUnits();
            boolean hitUnit = false;
            
            for (Unit unit : intersectingUnits) {
                // Only damage units from opposite side
                if (unit.getSide() != ownerSide) {
                    hitUnit = true;
                    break;
                }
            }
            
            if (hitUnit) {
                // Create explosion
                Explosion explosion = new Explosion(bomb.getX(), bomb.getY());
                explosions.add(explosion);
                world.addObject(explosion, explosion.getX(), explosion.getY());
                
                // Damage units in explosion radius
                damageUnitsInArea(world, bomb.getX(), bomb.getY(), 100);
                
                // Mark bomb for removal
                bombsToRemove.add(bomb);
                world.removeObject(bomb);
                continue;
            }
            
            // Check if bomb hit ground or Y=600
            if (bomb.isAtGround(world)) {
                // Create explosion
                Explosion explosion = new Explosion(bomb.getX(), bomb.getY());
                explosions.add(explosion);
                world.addObject(explosion, explosion.getX(), explosion.getY());
                
                // Damage units in explosion radius
                damageUnitsInArea(world, bomb.getX(), bomb.getY(), 100);
                
                // Mark bomb for removal
                bombsToRemove.add(bomb);
                world.removeObject(bomb);
            }
        }
        
        // Remove exploded bombs
        bombs.removeAll(bombsToRemove);
    }
    
    private void damageUnitsInArea(World world, int x, int y, int radius) {
        // Get all units in the world
        java.util.List<Unit> units = world.getObjects(Unit.class);
        
        // Check each unit to see if it's in the damage radius
        for (Unit unit : units) {
            int unitX = unit.getX();
            int unitY = unit.getY();
            
            // Calculate distance between unit and explosion center
            double distance = Math.sqrt(Math.pow(unitX - x, 2) + Math.pow(unitY - y, 2));
            
            // If unit is within radius and from enemy side, damage it
            if (distance <= radius && unit.getSide() != ownerSide) {
                unit.hitBySpecialSkill(bombDamage, ownerSide);
            }
        }
    }
    
    private void updateExplosions(World world) {
        ArrayList<Explosion> explosionsToRemove = new ArrayList<Explosion>();
        
        for (Explosion explosion : explosions) {
            if (explosion.update()) {
                // If explosion animation is complete, mark for removal
                explosionsToRemove.add(explosion);
                world.removeObject(explosion);
            }
        }
        
        // Remove completed explosions
        explosions.removeAll(explosionsToRemove);
    }
    
    /**
     * Private class to represent a Bomb
     */
    private class Bomb extends Actor {
        private int x, y;
        private int fallSpeed = 4;
        
        public Bomb(int x, int y) {
            this.x = x;
            this.y = y;
            try {
                GreenfootImage g = new GreenfootImage("images/bomb.png");
                g.scale(30, 30);
                setImage(g);
            } catch (Exception e) {
                // Create a simple bomb if image not available
                GreenfootImage bombImg = new GreenfootImage(20, 30);
                bombImg.setColor(Color.BLACK);
                bombImg.fillOval(0, 0, 20, 20);
                bombImg.fillRect(8, 0, 4, 10);
                setImage(bombImg);
            }
        }
        
        public void update() {
            // Fall down
            y += fallSpeed;
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
         * Get intersecting units - public method to use instead of protected getOneIntersectingObject
         */
        public List<Unit> getIntersectingUnits() {
            return getIntersectingObjects(Unit.class);
        }
    }
    
    /**
     * Private class to represent an Explosion
     */
    private class Explosion extends Actor {
        private int x, y;
        private Animator explosionAnimator;
        private boolean completed = false;
        private int frame = 0;
        private final int MAX_FRAMES = 20;
        private SimpleTimer explosionTimer = new SimpleTimer();
        
        public Explosion(int x, int y) {
            this.x = x;
            this.y = y;
            explosionAnimator = new Animator("images/explosion");
            explosionAnimator.setSpeed(100);
            explosionTimer.mark();
            GreenfootSound explosionSound = new GreenfootSound("./sounds/explode.mp3");
            explosionSound.play();
        }
        
        public boolean update() {
            if (explosionAnimator != null) {
                // Update animation
                setImage(explosionAnimator.getCurrentFrame());
                
                // Check if animation has completed one cycle or if enough time has passed
                if ((explosionAnimator.getImageIndex() == explosionAnimator.getSize() - 1) || 
                    explosionTimer.millisElapsed() > 2000) { // Force completion after 2 seconds
                    completed = true;
                }
            } else {
                // Simple fading animation with a timeout
                frame++;
                if (frame >= MAX_FRAMES || explosionTimer.millisElapsed() > 2000) {
                    completed = true;
                } else {
                    // Fade out gradually
                    GreenfootImage img = getImage();
                    int transparency = 200 - (frame * 10);
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