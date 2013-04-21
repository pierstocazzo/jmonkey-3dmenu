package menu.elements;

import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import java.util.ArrayList;
import java.util.List;
import menu.utils.Jme3DFont;
import meshes.Wedge;

/**
 * A ValueChooser lets the user select, using arrows on either side to select
 * values.
 */
public class ValueChooser<T> extends Label
{
    private int index = -1;
    private List<T> values;
    private boolean vertical;
    private ArrayList<ActionListener> actionListeners = new ArrayList<>();
    private static final float wedgeSize = 0.45f;
    private static final float spacing = 0.05f;

    public ValueChooser(Jme3DFont font, List<T> values, boolean vertical)
    {
        super("");
        setName("ValueChooser");
        if (values == null)
        {
            throw new NullPointerException();
        }

        this.vertical = vertical;
        this.values = values;

        // If at least one value is given, set the first:
        if (!values.isEmpty())
        {
            index = 0;
        }
    }

    /**
     * Adds an action listener to this button, that will be fired at each click.
     */
    public void addActionListener(ActionListener listener)
    {
        actionListeners.add(listener);
    }

    @Override
    public void refresh()
    {
        // Delete everything, not just the label.
        detachAllChildren();

        // Set the selection as text:
        this.text = values.get(index).toString();
        super.refresh();

        // Create the triangles.
        Geometry wedgeGeometry1, wedgeGeometry2;
        boolean extruded = getMenuFont().isExtruded();

        float height = super.getLocalHeight();
        float width = super.getLocalWidth();
        float extrusion = extruded ? getLocalDepth() : 0;

        if (vertical)
        {
            // Todo: make the wedges follow the mouse :)
            // For a vertical chooser (arrows on top and bottom) :
            Wedge wedge = new Wedge(Wedge.Orientation.DOWN, new Vector3f(wedgeSize / 3, wedgeSize, extrusion), extruded);
            wedgeGeometry1 = new Geometry("wedge1", wedge);
            wedge = new Wedge(Wedge.Orientation.UP, new Vector3f(wedgeSize / 3, wedgeSize, extrusion), extruded);
            wedgeGeometry2 = new Geometry("wedge2", wedge);

            // Place the triangles and move the text node up.
            wedgeGeometry1.setLocalTranslation(0, wedgeSize / 3, 0);
            stringNode.setLocalTranslation(0, wedgeSize / 3 + spacing, 0);
            wedgeGeometry2.setLocalTranslation(0, wedgeSize / 3 + spacing + height + spacing, 0);
        }
        else
        {
            // For a Horizontal chooser (arrows on left and right) :
            Wedge w = new Wedge(Wedge.Orientation.LEFT, new Vector3f(wedgeSize / 3, wedgeSize, extrusion), extruded);
            wedgeGeometry1 = new Geometry("wedge1", w);
            w = new Wedge(Wedge.Orientation.RIGHT, new Vector3f(wedgeSize / 3, wedgeSize, extrusion), extruded);
            wedgeGeometry2 = new Geometry("wedge2", w);

            wedgeGeometry1.setLocalTranslation(0, 0, 0);
            stringNode.setLocalTranslation(wedgeSize / 3 + spacing, 0, 0);
            wedgeGeometry2.setLocalTranslation(wedgeSize / 3 + spacing + width + spacing, 0, 0);
        }
        Material mat = getMenuMaterial();
        wedgeGeometry1.setMaterial(mat);
        wedgeGeometry2.setMaterial(mat);

        attachChild(wedgeGeometry1);
        attachChild(wedgeGeometry2);
    }

    @Override
    public void processClick(boolean pressedOrReleased, Vector3f cursorPosition)
    {
        if (pressedOrReleased)
        {
            if (vertical)
            {
                if (cursorPosition.y > getLocalHeight() / 2)
                {
                    setIndex(index + 1);
                }
                else
                {
                    setIndex(index - 1);
                }
            }
            else
            {
                if (cursorPosition.x > getLocalWidth() / 2)
                {
                    setIndex(index + 1);
                }
                else
                {
                    setIndex(index - 1);
                }
            }

            // If the button has been released, and the button is enabled:
            if (enabled && !pressedOrReleased)
            {
                for (ActionListener listener : actionListeners)
                {
                    listener.onAction(name, true, queueDistance);
                }
            }
            // index++; refresh();
        }

        // Also fire action listeners.
        // If the button has been released, and the button is enabled:
        if (enabled && !pressedOrReleased)
        {
            for (ActionListener listener : actionListeners)
            {
                listener.onAction(name, true, queueDistance);
            }
        }
    }

    /**
     * Sets the given index as selected.
     */
    public void setIndex(int index)
    {
        if (index < 0)
        {
            index = index % values.size() + values.size();
        }

        this.index = index % values.size();
        refresh();
    }

    /* @Override
     public void setMaterial(Material mat)
     {
     super.setMaterial(mat);

     //  wedge1.setMaterial(mat);
     // wedge2.setMaterial(mat);
     }*/
    @Override
    public float getLocalWidth()
    {
        if (vertical)
        {
            return super.getLocalHeight();
        }
        else
        {
            return 2 * wedgeSize / 3 + 2 * spacing + super.getLocalWidth();
        }
    }

    @Override
    public float getLocalHeight()
    {
        if (vertical)
        {
            return 2 * wedgeSize / 3 + 2 * spacing + super.getLocalHeight();
        }
        else
        {
            return super.getLocalWidth();
        }
    }
}
