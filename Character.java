import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class Character here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class Character extends Actor
{
    protected int age; // Stone Age = 1, Modern Age = 2, Space Age = 3
    protected int hp;
    protected int direction; // -1 left to right, 1 right to left
    protected boolean moving;
    
    public abstract void attack();
    
    public Character(int age, int hp, int direction) {
        this.age = age;
        this.hp = hp;
        this.direction = direction;
        moving = false;
    }
    
    public void act()
    {
        if (moving) {
            
        }
    }
    
    public int getSide() {
        if (direction == -1) {
            return 1;
        } else {
            return 2;
        }
    }
    
    public Character enemyInRange(int r) {
        ArrayList<Character> enemies = (ArrayList<Character>) getObjectsInRange(r, Character.class);
        return enemies.get(0);
    }
}
