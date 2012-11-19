package menu.transitions;

import com.jme3.math.Vector3f;
import java.util.Random;
import menu.elements.Panel;

/**
 * A sliding transition makes the source and destination panel slide into place.
 */
public class SlideTransition extends Transition
{
    public enum Direction
    {
        LEFT, RIGHT, UP, DOWN
    };
    
    private Direction direction;
    private Vector3f origin;

    public SlideTransition(Panel source, Panel destination, Direction direction)
    {
        super(source, destination);
        this.direction = direction;
        // Save the source position.
        origin = new Vector3f(source.getLocalTranslation());

        // Put the destination on the good side of the source.
        Vector3f location;
        if (direction == Direction.LEFT || direction == Direction.DOWN)
        {
            // The start location is the farthest bound of the source, plus the
            // destination width( or height...). Also add a 5% margin.
            location = source.getAbsoluteMaxBound();
            location.subtractLocal(destination.getRelativeMinBound().multLocal(1.05f));
        } else
        {
            location = source.getAbsoluteMinBound();
            location.subtractLocal(destination.getRelativeMaxBound().multLocal(1.05f));
        }
        

        // Set the destination initial location.
        Vector3f newLocation = destination.getLocalTranslation();
        switch (direction)
        {
            case LEFT:
            case RIGHT:
                newLocation.x = location.x;
                break;
            case UP:
            case DOWN:
                newLocation.y = location.y;
                break;
        }
        destination.setLocalTranslation(newLocation);
    }
    
    /** Creates a sliding transition in a random direction. 
     * */
     public SlideTransition(Panel source, Panel destination)
     {
         this(source,destination,Direction.values()[new Random().nextInt(4)]);
     }

    @Override
    public boolean isOver()
    {
        return (direction == Direction.LEFT && destination.getLocalTranslation().x < origin.x)
                || (direction == Direction.RIGHT && destination.getLocalTranslation().x > origin.x)
                || (direction == Direction.DOWN && destination.getLocalTranslation().y < origin.y)
                || (direction == Direction.UP && destination.getLocalTranslation().y > origin.y);
    }
    @Override
    public void finish()
    {
        // On finish, replace the source on its original location.
        source.setLocalTranslation(origin);
    }
    @Override
    public void update(float tpf)
    {
        Vector3f newDestinationLocation = destination.getLocalTranslation();
        Vector3f newSourceLocation = source.getLocalTranslation();

        tpf *= 5;
        switch (direction)
        {
            case LEFT:
                tpf = -tpf;
            case RIGHT:
                newDestinationLocation.x += tpf;
                newSourceLocation.x += tpf;
                break;
            case DOWN:
                tpf = -tpf;
            case UP:
                newDestinationLocation.y += tpf;
                newSourceLocation.y += tpf;
                break;
        }

        source.setLocalTranslation(newSourceLocation);
        destination.setLocalTranslation(newDestinationLocation);
    }
}
