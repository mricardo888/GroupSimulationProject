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
        
        //World Instances
        Settings s = new Settings();
        MyWorld m = new MyWorld();
        
        
        // Button to Setting Screen
        GreenfootImage settin = new GreenfootImage("setting.png");
        settin.scale((int)(settin.getWidth() * 0.8), (int)(settin.getHeight() * 0.8));
        NextBut settinScreen = new NextBut(settin,s);
        addObject(settinScreen,getWidth()/2,400);
        
        // Button to Setting Screen
        GreenfootImage play = new GreenfootImage("play.png");
        play.scale((int)(play.getWidth() * 0.8), (int)(play.getHeight() * 0.8));
        NextBut playWorld = new NextBut(play,m);
        addObject(playWorld,getWidth()/2,300);
        
        
    }
    
    
    
    public void act(){
        
    }
}
