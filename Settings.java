import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)


public class Settings extends World
{
    //variables
    private int gold1;
    private int gold2;
    private int xp1;
    private int xp2;
    
    
    //Images
    GreenfootImage gold = new GreenfootImage("gold.png");
    GreenfootImage xp = new GreenfootImage("xp.png");
    GreenfootImage back = new GreenfootImage("back.png");
    GreenfootImage settin = new GreenfootImage("setting.png");


    
    
    public Settings()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(1024, 800,1);
        
        
        //Image resize
        back.scale((int)(back.getWidth() * 0.8), (int)(back.getHeight() * 0.8));
        gold.scale((int)(gold.getWidth() * 0.3), (int)(gold.getHeight() * 0.3));
        xp.scale((int)(xp.getWidth() * 0.3), (int)(xp.getHeight() * 0.3));
        settin.scale((int)(settin.getWidth() * 1.4), (int)(settin.getHeight() * 1.4));



        
        
        //Button Back to StartScreen
        BackButton backButton = new BackButton(back);
        addObject(backButton, getWidth()/2, 700);
        
        getBackground().drawImage(gold,110,450);
        getBackground().drawImage(xp,110,250);
        getBackground().drawImage(settin, getWidth()/2 - 150, 40);


    }
    
    public void act(){
        
    }
    
    //Getters && setters
    public int getGold1(){
        return gold1;
    }
    public int getGold2(){
        return gold2;
    }
    public void setGold1(int num){
        gold1 = num;
    }
    public void setGold2(int num){
        gold2 = num;
    }
    public int getXp1(){
        return xp1;
    }
    public int getXp2(){
        return xp2;
    }
    public void setXp1(int num){
        xp1 = num;
    }
    public void setXp2(int num){
        xp2 = num;
    }
    
}
