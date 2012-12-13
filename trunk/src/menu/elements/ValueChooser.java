package menu.elements;

import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import java.util.ArrayList;
import menu.utils.FontToMesh;
import meshes.Wedge;

public class ValueChooser extends Label
{

    private Geometry wedge1 = null, wedge2 = null;
    private boolean vertical;
    ArrayList<ActionListener> actionListeners = new ArrayList<>();

    public ValueChooser(FontToMesh font, String text, boolean vertical)
    {
        super(font, text);
        this.vertical = vertical;

        // Create the triangles.
        if (vertical)
        {
            Wedge w = new Wedge(Wedge.Orientation.DOWN, new Vector3f(0.3f, 1f, 0.2f), font.isExtruded());
            wedge1 = new Geometry("wedge1", w);
            w = new Wedge(Wedge.Orientation.UP, new Vector3f(0.3f, 1f, 0.2f), font.isExtruded());
            wedge2 = new Geometry("wedge2", w);
        } else
        {
            Wedge w = new Wedge(Wedge.Orientation.LEFT, new Vector3f(0.3f, 1f, 0.2f), font.isExtruded());
            wedge1 = new Geometry("wedge1", w);
            w = new Wedge(Wedge.Orientation.RIGHT, new Vector3f(0.3f, 1f, 0.2f), font.isExtruded());
            wedge2 = new Geometry("wedge2", w);
        }

    }

    /**
     * Adds an action listener to this button, that will be fired at each click.
     */
    public void addActionListener(ActionListener listener)
    {
        actionListeners.add(listener);
    }

    public void refresh()
    {
        refreshLabel();
        attachChild(wedge1);
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
    }

    @Override
    public void setMaterial(Material mat)
    {
        super.setMaterial(mat);

        wedge1.setMaterial(mat);
        wedge2.setMaterial(mat);
    }
}
