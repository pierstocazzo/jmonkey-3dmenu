package menu.elements;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 * A class describing a menu element (Button, slider, etc...) All methods can -
 * but not must - be overriden to provide additional behaviours when clicked,
 * dragged, etc.
 *
 * By convention, Menu Elements have their min bound at (0,0,Z).
 *
 */
public abstract class MenuElement extends Node
{
    protected Material material = null;
    
    /**
     * Processes a click.
     *
     * @param pressedOrReleased If true, it's a click; else it's a button
     * release.
     */
    public void processClick(boolean pressedOrReleased,Vector3f cursorPosition)
    {
    }

    ;
    public void processKey()
    {
    }

    ;
    public void processDrag(Vector3f cursorPosition)
    {
    }

    ;
    public void update(float tpf)
    {
    }

    ;

    abstract protected Vector3f getLocalMinBound();

    abstract protected Vector3f getLocalMaxBound();

    /**
     * Returns the elements minimum bound, as expressed in it's parent
     * coordinate space.
     */
    public Vector3f getAbsoluteMinBound()
    {
        return getLocalTransform().transformVector(getLocalMinBound(), null);
    }

    /**
     * Returns the elements maximum bound, as expressed in it's parent
     * coordinate space.
     */
    public Vector3f getAbsoluteMaxBound()
    {
        return getLocalTransform().transformVector(getLocalMaxBound(), null);
    }

    /**
     * Returns the elements minimum bound, as expressed in the element's own
     * coordinate space.
     */
    public Vector3f getRelativeMinBound()
    {
        return getAbsoluteMinBound().subtractLocal(getLocalTranslation());
    }

    /**
     * Returns the elements maximum bound, as expressed in the element's own
     * coordinate space.
     */
    public Vector3f getRelativeMaxBound()
    {
        return getAbsoluteMaxBound().subtractLocal(getLocalTranslation());
    }

    /**
     * This methods fills the array with every leave in the menu - i.e., finds
     * non-composite elements such as buttons, iterating over composite elements
     * like the panels.
     */
    protected void findLeaves(ArrayList<MenuElement> candidates)
    {
        candidates.add(this);
    }

    /**
     * @return the material
     */
    public Material getMaterial()
    {
        return material;
    }
}
