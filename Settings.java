import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)


public class Settings extends World
{

    
    public Settings()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(1024, 800,1);
        
        //Button Back to StartScreen
        GreenfootImage back = new GreenfootImage("back.png");
        back.scale((int)(back.getWidth() * 0.8), (int)(back.getHeight() * 0.8));
        BackButton backButton = new BackButton(back);
        addObject(backButton, getWidth()/2, 400);
    }
    
    public void act(){
        
    }
}
