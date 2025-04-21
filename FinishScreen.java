import greenfoot.*;

/**
 * The FinishScreen is displayed when the game ends
 * 
 * @author Mark Huang
 */
public class FinishScreen extends World
{
    /**
     * Constructor for the FinishScreen
     */
    public FinishScreen(int winner)
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(1024, 800, 1); 
        
        // Create a simple victory background
        GreenfootImage simpleBg = new GreenfootImage(1024, 800);
        simpleBg.setColor(new Color(0, 0, 0));
        simpleBg.fill();
        
        // Add a gradient effect
        for (int y = 0; y < 800; y += 2) {
            int alpha = 50 - (y * 50 / 800);
            Color currentColor;
            if (winner == 1) {
                currentColor = new Color(100, 100, 255, alpha); // Blue for player 1
            } else {
                currentColor = new Color(255, 100, 100, alpha); // Red for player 2
            }
            simpleBg.setColor(currentColor);
            simpleBg.fillRect(0, y, 1024, 2);
        }
        
        setBackground(simpleBg);
        
        // Create winner label
        Label winnerLabel = new Label("Player " + winner + " Wins!", 60);
        winnerLabel.setFillColor(Color.YELLOW);
        winnerLabel.setLineColor(Color.BLACK);
        addObject(winnerLabel, getWidth()/2, 200);
        
        // Create play again button
        GreenfootImage playAgain = new GreenfootImage("./images/play.png");
        playAgain.scale((int)(playAgain.getWidth() * 0.8), (int)(playAgain.getHeight() * 0.8));
        NextBut playAgainButton = new NextBut(playAgain, new MyWorld());
        addObject(playAgainButton, getWidth()/2, 400);
        
        // Create return to start screen button
        GreenfootImage returnToStart = new GreenfootImage("./images/back.png");
        returnToStart.scale((int)(returnToStart.getWidth() * 0.8), (int)(returnToStart.getHeight() * 0.8));
        NextBut returnButton = new NextBut(returnToStart, new StartScreen());
        addObject(returnButton, getWidth()/2, 500);
        
        // Add some decorative elements
        decorateScreen(winner);
    }
    
    /**
     * Default constructor for the FinishScreen
     */
    public FinishScreen()
    {
        this(1); // Default to player 1 as winner
    }
    
    /**
     * Add decorative elements to the finish screen
     */
    private void decorateScreen(int winner) {
        // Add trophy or crown at the top
        GreenfootImage trophy = new GreenfootImage(80, 80);
        trophy.setColor(Color.YELLOW);
        int[] xPoints = {40, 60, 70, 60, 40, 20, 10, 20};
        int[] yPoints = {0, 10, 30, 50, 60, 50, 30, 10};
        trophy.fillPolygon(xPoints, yPoints, 8);
        trophy.setColor(new Color(255, 215, 0)); // Gold color
        trophy.fillRect(30, 60, 20, 20);
        
        getBackground().drawImage(trophy, getWidth()/2 - 40, 80);
        
        // Add some stars around the screen
        for (int i = 0; i < 50; i++) {
            int x = Greenfoot.getRandomNumber(getWidth());
            int y = Greenfoot.getRandomNumber(getHeight());
            int size = Greenfoot.getRandomNumber(5) + 2;
            
            Color starColor;
            if (winner == 1) {
                starColor = new Color(100 + Greenfoot.getRandomNumber(155), 
                                    100 + Greenfoot.getRandomNumber(155), 
                                    200 + Greenfoot.getRandomNumber(55));
            } else {
                starColor = new Color(200 + Greenfoot.getRandomNumber(55), 
                                    100 + Greenfoot.getRandomNumber(155), 
                                    100 + Greenfoot.getRandomNumber(155));
            }
            
            GreenfootImage star = new GreenfootImage(size, size);
            star.setColor(starColor);
            star.fillOval(0, 0, size, size);
            
            getBackground().drawImage(star, x, y);
        }
    }
}