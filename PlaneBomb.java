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
    
    /**
     * Constructor for the PlaneBomb skill
     */
    public PlaneBomb() {
        animator = new Animator("images/plane");
        animator.setSpeed(100);
    }
    
    /**
     * Start the plane bombing sequence
     */
    public void start() {
        World world = getWorld();
        if (world != null && !active) {
            // Initialize plane position
            planeX = 0;
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
            planeX += planeSpeed;
            
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
            if (planeX >= world.getWidth()) {
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
                
                // Mark bomb for removal
                bombsToRemove.add(bomb);
                world.removeObject(bomb);
            }
        }
        
        // Remove exploded bombs
        bombs.removeAll(bombsToRemove);
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
            setImage("images/bomb.png");
            Greenfoot.playSound("/sounds/bombsFalling.mp3");
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