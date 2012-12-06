package menu.elements;

import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import java.util.ArrayList;
import menu.utils.FontToMesh;

/**
 * A Button is a clickable Label, with an invisible box to catch clicks between
 * characters.
 */
public final class Button extends menu.elements.Label
{
    ArrayList<ActionListener> actionListeners = new ArrayList<>();
    public Button(FontToMesh font, String text)
    {
        super(font, text);
        setName("Button");
    }

    /**
     * When clicked, fire all action listeners.
     */
    @Override
    public void processClick(boolean pressedOrReleased,Vector3f cursorPosition)
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

    /**
     * Adds an action listener to this button, that will be fired at each
     * clicked.
     */
    public void addActionListener(ActionListener listener)
    {
        actionListeners.add(listener);
    }

    @Override
    public void refresh()
    {
        super.refresh();
        Geometry geom;
        if (font.isExtruded())
        {
            // Generate an invisible box to intercept clicks.
            Box b = new Box(getLocalMinBound(), getLocalMaxBound());
            geom = new Geometry("Box", b);
        }
        else
        {
            // For a 2D font, only create quad.
            Vector3f maxBound = getLocalMaxBound();
            Quad q = new Quad(maxBound.x, maxBound.y);
            geom = new Geometry("Quad", q);
        }

        geom.setMaterial(Panel.invisibleMaterial);


        attachChild(geom);
    }
}
