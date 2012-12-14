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
        this.text = values.get(index).toString();
        super.refresh();

        // Create the triangles.
        Geometry wedge1, wedge2;
        boolean extruded = getMenuFont().isExtruded();
        if (vertical)
        {
            Wedge w = new Wedge(Wedge.Orientation.DOWN, new Vector3f(0.3f, 1f, 0.2f), extruded);
            wedge1 = new Geometry("wedge1", w);
            w = new Wedge(Wedge.Orientation.UP, new Vector3f(0.3f, 1f, 0.2f), extruded);
            wedge2 = new Geometry("wedge2", w);
        } else
        {
            Wedge w = new Wedge(Wedge.Orientation.LEFT, new Vector3f(0.3f, 1f, 0.2f), extruded);
            wedge1 = new Geometry("wedge1", w);
            w = new Wedge(Wedge.Orientation.RIGHT, new Vector3f(0.3f, 1f, 0.2f), extruded);
            wedge2 = new Geometry("wedge2", w);
        }
        Material mat = getMenuMaterial();
        wedge1.setMaterial(mat);
        wedge2.setMaterial(mat);

        attachChild(wedge1);
        wedge1.setLocalTranslation(0, 0, 0);
    }

    @Override
    public void processClick(boolean pressedOrReleased, Vector3f cursorPosition)
    {
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

    @Override
    public void setMaterial(Material mat)
    {
        super.setMaterial(mat);

        //  wedge1.setMaterial(mat);
        // wedge2.setMaterial(mat);
    }
}
