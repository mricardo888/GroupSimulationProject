import greenfoot.*;

/**
 * The start screen of the game
 * 
 * @author Mark Huang
 */
public class StartScreen extends World
{

    /**
     * Constructor for the StartScreen
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
        addObject(settinScreen,getWidth()/2,600);
        
        // Button to Setting Screen
        GreenfootImage play = new GreenfootImage("play.png");
        play.scale((int)(play.getWidth() * 0.8), (int)(play.getHeight() * 0.8));
        NextBut playWorld = new NextBut(play,m);
        addObject(playWorld,getWidth()/2,500);
        
        //Bg
        GreenfootImage bg = new GreenfootImage("startingbg.png");
        bg.scale(1024,800);
        setBackground(bg);

        //title
        GreenfootImage title = new GreenfootImage("title.png");
        title.scale((int)(title.getWidth() * 0.3), (int)(title.getHeight() * 0.3));
        getBackground().drawImage(title,getWidth()/2-230,150);
    }
}