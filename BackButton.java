import greenfoot.*;  

public class BackButton extends Buttons 
{
    
    public BackButton(GreenfootImage e){
        super(e,null);
    }
    
    public BackButton(GreenfootImage e, World w){
        super(e,w);
        
    }

    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            Greenfoot.setWorld(new StartScreen());  
        }
    }
}

