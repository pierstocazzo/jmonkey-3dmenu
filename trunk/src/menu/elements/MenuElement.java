package menu.elements;

import com.jme3.material.Material;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
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
    protected boolean enabled = true;

    /**
     * Processes a click.
     *
     * @param pressedOrReleased If true, it's a click; else it's a button
     * release.
     */
    public void processClick(boolean pressedOrReleased, Vector3f cursorPosition)
    {
    }

    public void processKey()
    {
    }

    public void processDrag(Vector3f cursorPosition)
    {
    }

    public void processWheel(int step)
    {
    }

    public void update(float tpf)
    {
    }

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

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;

        if (enabled)
        {
            //setLocalScale(1f);
            setLocalRotation(Matrix3f.IDENTITY);

        }
        else
        {
            //setLocalScale(0.3f);
            setLocalRotation(new Quaternion().fromAngles(0f, 0.45f, 0));
        }
        //  stringNode.setMaterial(material);
        // setMaterial(material);
    }
}
