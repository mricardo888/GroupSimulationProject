import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * PlaneBomb is a special skill that creates a plane flying across the screen
 * and dropping bombs that explode on impact with the ground.
 * All plane, bomb, and explosion functionality is contained within this class.
 * 
 * @author Your Name
 * @version 1.0
 */
public class PlaneBomb extends SpecialSkill
{
    private Animator animator;
    private int planeSpeed = 3;
    private int bombDropRate = 60; // Drop a bomb every 60 acts
    private int bombTimer = 0;
    private int planeX, planeY;
    private boolean active = false;
    private ArrayList<Bomb> bombs = new ArrayList<Bomb>();
    private ArrayList<Explosion> explosions = new ArrayList<Explosion>();
    private int ownerSide = 0; // Side that activated the skill
    private int bombDamage = 80; // Damage per bomb hit
    private int targetDirection = 1; // 1 = left to right, -1 = right to left
    
    /**
     * Constructor for the PlaneBomb skill
     */
    public PlaneBomb() {
        animator = new Animator("images/plane");
        animator.scale(150, 150);
        animator.setSpeed(100);
    }
    
    /**
     * Set the side that activated this skill
     */
    public void setOwnerSide(int side) {
        this.ownerSide = side;
    }
    
    /**
     * Set the direction of the plane
     * @param direction 1 for left to right, -1 for right to left
     */
    public void setDirection(int direction) {
        this.targetDirection = direction;
    }
    
    /**
     * Start the plane bombing sequence
     */
    public void start() {
        World world = getWorld();
        if (world != null && !active) {
            // Initialize plane position based on direction
            if (targetDirection > 0) {
                // Left to right
                planeX = -50;
            } else {
                // Right to left
                planeX = world.getWidth() + 50;
                // Flip the plane image if needed
                setRotation(180);
            }
            planeY = 100;
            active = true;
            bombs.clear();
            explosions.clear();
        }
    }
    
    /**
     * Act method called by Greenfoot
     */
    public void act()
    {
        World world = getWorld();
        if (world == null) return;
        
        if (active) {
            // Update plane position
            planeX += planeSpeed * targetDirection;
            
            // Set image to current frame of animation
            setImage(animator.getCurrentFrame());
            setLocation(planeX, planeY);
            
            // Drop bombs
            bombTimer++;
            if (bombTimer >= bombDropRate) {
                dropBomb(world);
                bombTimer = 0;
            }
            
            // Check if plane has exited the screen
            if ((targetDirection > 0 && planeX >= world.getWidth() + 50) || 
                (targetDirection < 0 && planeX <= -50)) {
                active = false;
            }
        }
        
        // Update bombs
        updateBombs(world);
        
        // Update explosions
        updateExplosions();
    }
    
    /**
     * Drop a bomb from the plane
     */
    private void dropBomb(World world) {
        Bomb bomb = new Bomb(planeX, planeY + 30);
        bombs.add(bomb);
        world.addObject(bomb, bomb.getX(), bomb.getY());
    }
    
    /**
     * Update all active bombs
     */
    private void updateBombs(World world) {
        ArrayList<Bomb> bombsToRemove = new ArrayList<Bomb>();
        
        for (Bomb bomb : bombs) {
            bomb.update();
            
            // Check if bomb hit ground
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
    
    /**
     * Damage units within the explosion radius
     */
    private void damageUnitsInArea(World world, int x, int y, int radius) {
        // Get all units in the world
        java.util.List<Unit> units = world.getObjects(Unit.class);
        
        // Check each unit to see if it's in the damage radius
        for (Unit unit : units) {
            int unitX = unit.getX();
            int unitY = unit.getY();
            
            // Calculate distance between unit and explosion center
            double distance = Math.sqrt(Math.pow(unitX - x, 2) + Math.pow(unitY - y, 2));
            
            // If unit is within radius, damage it
            if (distance <= radius) {
                unit.hitBySpecialSkill(bombDamage, ownerSide);
            }
        }
    }
    
    /**
     * Update all active explosions
     */
    private void updateExplosions() {
        World world = getWorld();
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
            GreenfootImage g = new GreenfootImage("images/bomb.png");
            g.scale(100, 100);
            setImage(g);
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
            return y >= world.getHeight() - 10;
        }
    }
    
    /**
     * Private class to represent an Explosion
     */
    private class Explosion extends Actor {
        private int x, y;
        private Animator explosionAnimator;
        private boolean completed = false;
        
        public Explosion(int x, int y) {
            this.x = x;
            this.y = y;
            explosionAnimator = new Animator("images/explosion");
            explosionAnimator.setSpeed(100);
        }
        
        public boolean update() {
            // Update animation
            setImage(explosionAnimator.getCurrentFrame());
            
            // Check if animation has completed one cycle
            if (explosionAnimator.getImageIndex() == explosionAnimator.getSize() - 1 && !completed) {
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