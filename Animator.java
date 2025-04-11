import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.io.File;
import java.util.ArrayList;

/**
 * Created and owned by Ricardo Lee
 */
public class Animator
{
    /**
     * Act - do whatever the Animator wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    private ArrayList<GreenfootImage> animation;
    private ArrayList<GreenfootImage> originalImages; // Store original unflipped images
    private int animationSpeed;
    private SimpleTimer animationTimer;
    private int imageIndex;
    private boolean pause;
    private boolean isFlipped;
    
    public Animator(String directoryPath) {
        // Get all files in the directory
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        // Add them to the ArrayList
        animation = new ArrayList<GreenfootImage>();
        originalImages = new ArrayList<GreenfootImage>();
        for (File file : files) {
            GreenfootImage img = new GreenfootImage(directoryPath + "/" + file.getName());
            animation.add(img);
            // Store a copy of the original image
            originalImages.add(new GreenfootImage(img));
        }
        // Init Timer for Animation
        animationTimer = new SimpleTimer();
        animationTimer.mark();
        imageIndex = 0;
        animationSpeed = 100; // Default speed
        pause = false;
        isFlipped = false;
    }
    
    /**
     * Flip images in the library horizontally
     */
    public void flip() {
        isFlipped = !isFlipped;
        for (GreenfootImage a : animation) {
            a.mirrorHorizontally();
        }
    }
    
    /**
     * Scale images
     */
    public void scale(int x, int y) {
        for (GreenfootImage a : animation) {
            a.scale(x, y);
        }
    }
    
    /**
     * Check if the animator is flipped
     */
    public boolean isFlipped() {
        return isFlipped;
    }
    
    /**
     * Reset all images to their original unflipped state
     */
    public void resetToOriginal() {
        for (int i = 0; i < animation.size(); i++) {
            animation.set(i, new GreenfootImage(originalImages.get(i)));
        }
        isFlipped = false;
    }
    
    /**
     * Set animation speed
     */
    public void setSpeed(int s) {
        animationSpeed = s;
    }
    
    /**
     * Get current speed
     */
    public int getSpeed() {
        return animationSpeed;
    }
    /**
     * Change the frame
     */
    private void changeAnimation() {
        // Change frame according to the speed set
        if (animationTimer.millisElapsed() < animationSpeed || pause) {
            return;
        }
        // Set animation image to next frame
        animationTimer.mark();
        imageIndex = (imageIndex + 1) % animation.size();
    }
    
    /**
     * Return current frame image
     */
    public GreenfootImage getCurrentFrame() {
        changeAnimation();
        return animation.get(imageIndex);
    }
    
    /**
     * Return current index
     */
    public int getImageIndex() {
        return imageIndex;
    }
    
    /**
     * Get Images ArrayList
     */
    public ArrayList<GreenfootImage> getImages() {
        return animation;
    }
    
    /**
     * Get size of the animator files
     */
    public int getSize() {
        return animation.size();
    }
    
    /**
     * Status of animation
     */
    public boolean isRunning() {
        return !pause;
    }
    
    public void pause() {
        imageIndex = 0;
        pause = true;
    }
    
    public void resume() {
        pause = false;
    }
}