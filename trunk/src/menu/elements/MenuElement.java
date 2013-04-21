package menu.elements;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import menu.utils.Jme3DFont;

/**
 * A class describing a menu element (Button, slider, etc...) Some methods can -
 * but not must - be overriden to provide additional behaviours when clicked,
 * dragged, etc.
 *
 */
public abstract class MenuElement extends Node
{

    protected Jme3DFont menuFont = null;
    protected MenuElement menuParent = null;
    protected Material menuMaterial = null;
    protected boolean enabled = true;

    /**
     * Refreshes this element. It sets the material again, actualises labels
     * text, etc.
     */
    abstract protected void refresh();

    /**
     * The local (i.e, expressed in it's own model space) width of the
     * component.
     */
    abstract public float getLocalWidth();

    /**
     * The local (i.e, expressed in it's own model space) height of the
     * component.
     */
    abstract public float getLocalHeight();

    /**
     * The local (i.e, expressed in it's own model space) depth of the
     * component.
     */
    abstract public float getLocalDepth();

    /**
     * Sets the element size to the given x or y (fraction of the parent
     * element). For example, a button can be set to occupy 10% of the parent's
     * space.
     *
     * This changes the scale proportions.
     */
    public final void setSize(float width, float height)
    {
        float xAmount = menuParent.getLocalWidth() * width / getWidth();
        float yAmount = menuParent.getLocalHeight() * height / getHeight();
        scale(xAmount,yAmount,1);
    }

    /**
     * Sets the element width to the given fraction of the parent element. For
     * example, a button can be set to occupy 10% of the parent's space.
     *
     * Setting the width makes the height change accordingly.
     */
    public final void setWidth(float width)
    {
        float amount = menuParent.getLocalWidth() * width / getWidth();
        scale(amount);
    }

    /**
     * Sets the element height to the given fraction of the parent element. For
     * example, a button can be set to occupy 10% of the parent's space.
     *
     * Setting the height makes the width change accordingly.
     */
    public final void setHeight(float height)
    {
        float amount = menuParent.getLocalHeight() * height / getHeight();
        scale(amount);
    }

    public void setPosition(Vector2f v)
    {
        setLocalTranslation(v.x * menuParent.getLocalWidth(), v.y * menuParent.getLocalHeight(), 0);
    }

    /**
     * Returns the elements width, as expressed in it's parent coordinate space.
     */
    public final float getWidth()
    {
        // return getLocalTransform().transformVector(new Vector3f(getLocalWidth(), 0, 0), null).x;
        return getLocalWidth() * getLocalScale().x;
    }

    /**
     * Returns the elements width, as expressed in it's parent coordinate space.
     */
    public final float getHeight()
    {
       // return getLocalTransform().transformVector(new Vector3f(0, getLocalHeight(), 0), null).y;
        return getLocalHeight() * getLocalScale().y;
    }

    /**
     * Returns the elements depth, as expressed in it's parent coordinate space.
     */
    public final float getDepth()
    {
        // return getLocalTransform().transformVector(new Vector3f(0, 0, getLocalHeight()), null).z;
        return getLocalDepth() * getLocalScale().z;
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
     * Sets a font to be used by this element - and, in the case of a panel, by
     * each of its children. If no font is set for this element, it will request
     * it's parent's; this allows to change the font for a whole branch.
     */
    public void setMenuFont(Jme3DFont menuFont)
    {
        this.menuFont = menuFont;
    }

    /**
     * @return the material affected to this menu element or to its ancestor.
     */
    public final Material getMenuMaterial()
    {
        if (menuMaterial != null)
        {
            return menuMaterial;
        } else
        {
            // If the element itself has no material, return the parent, null if no parent.
            if (menuParent != null)
            {
                return menuParent.getMenuMaterial();
            } else
            {
                return null;
            }
        }
    }

    /**
     * Processes a click.
     *
     * @param pressedOrReleased If true, it's a click; else it's a button
     * release.
     */
    public void processClick(boolean pressedOrReleased, Vector3f cursorPosition)
    {
    }

    /**
     * Not implemented yet.
     */
    public void processKey()
    {
    }

    /**
     * Fires when the element is dragged.
     *
     * @param cursorPosition The cursor position, given in the local space.
     */
    public void processDrag(Vector3f cursorPosition)
    {
    }

    /**
     * Fires when the wheel has been rolled.
     *
     * @param step The number (positive or negative) of steps rolled.
     */
    public void processWheel(int step)
    {
    }

    /**
     * Fires every logic update. It can be used to move things around, fire
     * events...
     *
     * @param tpf The time passed since the last frame.
     */
    public void update(float tpf)
    {
    }

    /**
     * Sets a material to be used by this element - and, in the case of a panel,
     * by each of its children. If no material is set for this element, it will
     * request it's parent's; this allows to change the material for a whole
     * branch.
     */
    public void setMenuMaterial(Material menuMaterial)
    {
        this.menuMaterial = menuMaterial;
    }

    /**
     * @return the font affected to this menu element or to its ancestor.
     */
    public Jme3DFont getMenuFont()
    {
        if (menuFont != null)
        {
            return menuFont;
        } else
        {
            // If the element itself has no material, return the parent, null if no parent.
            if (menuParent != null)
            {
                return menuParent.getMenuFont();
            } else
            {
                return null;
            }
        }
    }

    /**
     * Returns true if the element is enabled -- e.g., clickable.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Enables or disables a component. To show that it is disabled, it will
     * rotate by 45°.
     */
    public void setEnabled(boolean enabled)
    {
        if (enabled && !this.enabled)
        {
            // On enabling again:
            rotate(0, -FastMath.QUARTER_PI, 0);
        } else
        {
            // On disabling:
            rotate(0, FastMath.QUARTER_PI, 0);
        }

        this.enabled = enabled;
    }
}
