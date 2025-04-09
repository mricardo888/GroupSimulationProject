import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)


public class Settings extends World
{
    //variables
    private static int gold1;
    private static int gold2;
    private static int xp1;
    private static int xp2;
    
    
    //Label
    Label g1 = new Label(0,40);
    Label g2 = new Label(0,40);
    Label x1 = new Label(0,40);
    Label x2 = new Label(0,40);
    Label p1 = new Label("Player 1", 50);
    Label p2 = new Label("Player 2", 50);
    
    //Images
    GreenfootImage gold = new GreenfootImage("gold.png");
    GreenfootImage xp = new GreenfootImage("xp.png");
    GreenfootImage back = new GreenfootImage("back.png");
    GreenfootImage settin = new GreenfootImage("setting.png");
    GreenfootImage plus = new GreenfootImage("plus.png");
    GreenfootImage min = new GreenfootImage("min.png");



    
    
    public Settings()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(1024, 800,1);
        
        
        
        
        
        //Image resize
        back.scale((int)(back.getWidth() * 0.8), (int)(back.getHeight() * 0.8));
        gold.scale((int)(gold.getWidth() * 0.3), (int)(gold.getHeight() * 0.3));
        xp.scale((int)(xp.getWidth() * 0.3), (int)(xp.getHeight() * 0.3));
        settin.scale((int)(settin.getWidth() * 1.4), (int)(settin.getHeight() * 1.4));
        plus.scale((int)(plus.getWidth() * 0.2), (int)(plus.getHeight() * 0.2));
        min.scale((int)(min.getWidth() * 0.2), (int)(min.getHeight() * 0.2));


    
        
        
        //Button Back to StartScreen
        BackButton backButton = new BackButton(back);
        addObject(backButton, getWidth()/2, 700);
        
        
        //Label
        addObject(g1,360,550);
        addObject(g2,660,550);
        addObject(x1,360,350);
        addObject(x2,660,350);
        addObject(p1,365,220);
        addObject(p2,665,220);
        
        //Plus & Min
        PlusBut pg1But = new PlusBut(plus, 1);
        addObject(pg1But,330,600);
        MinBut mg1But = new MinBut(min, 1);
        addObject(mg1But,390,600);
        PlusBut pg2But = new PlusBut(plus, 2);
        addObject(pg2But,630,600);
        MinBut mg2But = new MinBut(min, 2);
        addObject(mg2But,690,600);
        PlusBut px1But = new PlusBut(plus, 3);
        addObject(px1But,330,400);
        MinBut mx1But = new MinBut(min, 3);
        addObject(mx1But,390,400);
        PlusBut px2But = new PlusBut(plus, 0);
        addObject(px2But,630,400);
        MinBut mx2But = new MinBut(min, 0);
        addObject(mx2But,690,400);
        
        getBackground().drawImage(gold,110,450);
        getBackground().drawImage(xp,110,250);
        getBackground().drawImage(settin, getWidth()/2 - 150, 40);


    }
    
    public void act(){
        checkValue();
    }
    
    
    //Label value
    public void checkValue(){
        g1.setValue(gold1);
        g2.setValue(gold2);
        x1.setValue(xp1);
        x2.setValue(xp2);
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
