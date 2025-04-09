import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.io.File;

/**
 * Write a description of class Character here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class Unit extends SuperSmoothMover
{
    
    protected int age; // Stone Age = 1, Cave Age = 2, 
    // Modern Age = 3, Space Age = 4
    protected int hp;
    protected int direction; // 1 left to right, -1 right to left
    protected boolean moving;
    protected boolean attacking;
    protected Animator walkAnimation;
    protected Animator attackAnimation;
    protected Animator deathAnimation;
    protected double speed = 1; // Default speed is one
    
    public abstract void attack();
    
    public Unit(int age, int hp, int direction, String type) {
        this.age = age;
        this.hp = hp;
        this.direction = direction;
        moving = false;
        String basePath = "images/age" + age + "/" + type + "/";
        walkAnimation = new Animator(basePath + "walk");
        attackAnimation = new Animator(basePath + "attack");
        File d = new File(basePath + "death");
        if (d.exists() && d.isDirectory()) {
            deathAnimation = new Animator(basePath + "death");
        } else {
            deathAnimation = null;
        }
        if (direction == -1) {
            walkAnimation.flip();
            attackAnimation.flip();
            if (deathAnimation != null) {
                deathAnimation.flip();
            }
        }
    }
    
    public void changeSpeed(double s) {
        speed = s;
    }
    
    public void act()
    {
        if (hp <= 0) {
            moving = false;
            if (deathAnimation != null) {
                death();
            }
        }
        if (moving) {
            setImage(walkAnimation.getCurrentFrame());
            setLocation(getX() + speed * direction, getY());
        }
    }
    
    private void death() {
        setImage(deathAnimation.getCurrentFrame());
        if (deathAnimation.getImageIndex() + 1 == deathAnimation.getSize()) {
            getWorld().removeObject(this);
        }
    }
    
    public int getSide() {
        if (direction == -1) {
            return 1;
        } else {
            return 2;
        }
    }
    
    public Unit enemyInRange(int r) {
        ArrayList<Unit> enemies = (ArrayList<Unit>) getObjectsInRange(r, Unit.class);
        return enemies.get(0);
    }
}
