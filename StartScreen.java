import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class StartScreen here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class StartScreen extends World
{

    /**
     * Constructor for objects of class StartScreen.
     * 
     */
    
    public StartScreen()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(1024, 800, 1); 
        
        
        
        
        // Button to Setting Screen
        Settings s = new Settings();
        GreenfootImage sbI = new GreenfootImage("Start1.png");
        NextBut sb = new NextBut(sbI,s);
        addObject(sb,100,100);
        
    }
    public void act(){
        
    }
}
