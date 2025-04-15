import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)


public class PlusBut extends Buttons
{
    private int g1;
    private int g2;
    private int xp1;
    private int xp2;
    private int which;
    private boolean isPressed = false;

    
    public PlusBut(GreenfootImage e, int x){
        super(e);
        which = x;
    }
    
    
    public void plusGold1(){
        Settings s = (Settings)getWorld();
        if(isPressed){
            g1 = s.getGold1();
            s.setGold1(g1 + 10);
        }
        
    }
    public void plusGold2(){
        Settings s = (Settings)getWorld();
        if(isPressed){
            g2 = s.getGold2();
            s.setGold2(g2 + 10);
        }
        

    }
    public void plusXp1(){
        Settings s = (Settings)getWorld();
        if(isPressed){
            xp1 = s.getXp1();
            s.setXp1(xp1 + 10);
        }
        

    }
    public void plusXp2(){
        Settings s = (Settings)getWorld();
        if(isPressed){
            xp2 = s.getXp2();
            s.setXp2(xp2 + 10);
        }
        

    }
    
    
    public void which(){
        if (Greenfoot.mouseClicked(this)){
            isPressed = true;
            if (which == 1){
                plusGold1();
            }
            else if(which == 2){
                plusGold2();
            }
            else if(which == 3){
                plusXp1();
            }
            else{
                plusXp2();
            }
        }
        
        if (Greenfoot.mouseDragEnded(this) || Greenfoot.mouseClicked(null)) {
            isPressed = false;
        }
    }
    
    public void act()
    {
        which();
    }
}
