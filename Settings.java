import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)


public class Settings extends World
{

    
    public Settings()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(1024, 800,1);
        
        //Button Back to StartScreen
        GreenfootImage sbI = new GreenfootImage("Start1.png");
        BackButton backButton = new BackButton(sbI);
        addObject(backButton, 200, 200);
    }
    
    public void act(){
        
    }
}
