import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.io.File;
import java.util.ArrayList;

/**
 * @author Ricardo Lee
 */
public class Animator
{
    // Instance Variables
    private ArrayList<GreenfootImage> animation;
    private ArrayList<GreenfootImage> originalImages; // Store original unflipped images
    private int animationSpeed;
    private SimpleTimer animationTimer;
    private int imageIndex;
    private boolean pause;
    private boolean isFlipped;
    
    /**
     * Constructs a new Animator with images loaded from the specified directory.
     */
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
     * Flips all animation images horizontally.
     * If the animation is already flipped, it will be unflipped.
     */
    public void flip() {
        isFlipped = !isFlipped;
        for (GreenfootImage a : animation) {
            a.mirrorHorizontally();
        }
    }
    
    /**
     * Scales all animation images to the specified dimensions.
     */
    public void scale(int x, int y) {
        for (GreenfootImage a : animation) {
            a.scale(x, y);
        }
    }
    
    /**
     * Checks if the animation images are currently flipped horizontally.
     * Returns true if images are flipped, false otherwise.
     */
    public boolean isFlipped() {
        return isFlipped;
    }
    
    /**
     * Resets all animation images to their original unflipped state.
     */
    public void resetToOriginal() {
        for (int i = 0; i < animation.size(); i++) {
            animation.set(i, new GreenfootImage(originalImages.get(i)));
        }
        isFlipped = false;
    }
    
    /**
     * Sets the animation speed in milliseconds between frames.
     */
    public void setSpeed(int s) {
        animationSpeed = s;
    }
    
    /**
     * Gets the current animation speed in milliseconds between frames.
     * Returns the current speed value.
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
     * Gets the current frame image after advancing the animation if needed.
     * Returns the current frame as a GreenfootImage.
     */
    public GreenfootImage getCurrentFrame() {
        changeAnimation();
        return animation.get(imageIndex);
    }
    
    /**
     * Gets the current frame index in the animation sequence.
     * Returns the index of the current frame.
     */
    public int getImageIndex() {
        return imageIndex;
    }
    
    /**
     * Gets the list of animation images.
     * Returns ArrayList containing all animation frame images.
     */
    public ArrayList<GreenfootImage> getImages() {
        return animation;
    }
    
    /**
     * Gets the number of frames in the animation.
     * Returns the total number of animation frames.
     */
    public int getSize() {
        return animation.size();
    }
    
    /**
     * Checks if the animation is currently running.
     * Returns true if the animation is running, false if paused.
     */
    public boolean isRunning() {
        return !pause;
    }
    
    /**
     * Pauses the animation and resets to the first frame.
     */
    public void pause() {
        imageIndex = 0;
        pause = true;
    }
    
    /**
     * Resumes a paused animation.
     */
    public void resume() {
        pause = false;
    }
}