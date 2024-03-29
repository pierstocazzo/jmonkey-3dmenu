package menu.elements;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import java.util.ArrayList;
import menu.transitions.Transition;
import menu.utils.Materials;

/**
 * The menu panel is the root of a clickable 3D menu. Add menu elements to it,
 * and register the inputListener, you're ready to roll. To allow a composite
 * pattern, a panel is also a menuElement. Thus, a Panel can contain other
 * panels.
 */
public class Panel extends MenuElement
{
    // The menu prefix is appended to events strings
    public static String menuPrefix = "MGC";
      private Vector2f size = new Vector2f();
    
    private InputListener inputListener = null;
    private ArrayList<MenuElement> menuElements = new ArrayList<>();
    private ArrayList<Transition> transitions = new ArrayList<>();
    private MenuElement clickedElement = null;
    private Application application = null;
  

    protected Panel()
    {
    }

    public Panel(Camera camera, Node parent, float distance)
    {
        initView(camera, parent, distance);
    }

    public void debug(SimpleApplication app)
    {
        Quad quad = new Quad(getLocalWidth(), getLocalHeight());
        Geometry geo = new Geometry("Box", quad);

        geo.setMaterial(Materials.transparentMaterial);

        this.attachChild(geo);
        geo.setLocalTranslation(0, 0, 0);

        app.getFlyByCamera().setEnabled(true);
        app.getInputManager().setCursorVisible(false);
    }

    /**
     * This method creates a subpanel of a parent panel.
     *
     * @param parent The parent panel that will contain this one.
     * @param position The position of the leftmost, bottom corner of the
     * subpanel. It is expressed in the parent's referential (x [0,1], y
     * [0,1]).
     * @param size The proportion of the parent's space to occupy. (x [0,1], y
     * [0,1]).
     */
    public Panel createSubPanel(Vector2f position, Vector2f size)
    {
        Panel result = new Panel();
        
        result.setLocalTranslation(position.x * this.size.x, position.y * this.size.y, 0);
        result.size.set(size.x * this.size.x, size.y * this.size.y);
        
        return result;
    }

    /**
     * This method will set a panel from a camera and a distance, so that the
     * panel surface will match the camera's field of view, and will be located
     * at the given distance from the camera.
     */
    public final void initView(Camera camera, Node parent, float distance)
    {
        // Compute the screen lower left position
        // Find the location of the corner of the screen, at near frustum.
        Vector3f lowerLeft = camera.getWorldCoordinates(new Vector2f(0, 0), 0);
        // Get vector from the camera to that point
        Vector3f viewVector = lowerLeft.subtract(camera.getLocation());
        // Stretch it to the right length
        viewVector.multLocal(distance / viewVector.length());
        lowerLeft = camera.getLocation().add(viewVector);

        // Retrieve zPos for later.
        float zPos = camera.getScreenCoordinates(lowerLeft).z;
        // Attach the node to the right place.
        setLocalTranslation(parent.worldToLocal(lowerLeft, null));

        Vector3f xAxis = camera.getWorldCoordinates(new Vector2f(camera.getWidth(), 0), zPos).subtractLocal(lowerLeft);
        Vector3f yAxis = camera.getWorldCoordinates(new Vector2f(0, camera.getHeight()), zPos).subtractLocal(lowerLeft);
        // Compute the "up" and "right" extents (x and y in local space) that
        // will define the panel's size to match the screen.
        // Eventhough the panel isn't attached yet, we already have the
        // direction (x and y in the panel's local space) and the length is the
        // same as expressed in the root node, as long as there is no scale 
        // applied to the panel.
        size.x = xAxis.length();
        size.y = yAxis.length();

        // Finally, orient the screen toward the camera
        lookAt(lowerLeft.add(xAxis.cross(yAxis)), yAxis);
    }

    /**
     * Registers the menu events (clicks, mouse moves...) in the inputListener.
     * Hooks the menu node into the input manager. Do it once and for all.
     */
    public void register(Application application)
    {
        // (Re-)add the bindings.
        InputManager inputManager = application.getInputManager();
        // Mouse axes.
        inputManager.addMapping(menuPrefix + "MouseRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping(menuPrefix + "MouseLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping(menuPrefix + "MouseUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping(menuPrefix + "MouseDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping(menuPrefix + "MouseWheelUp", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping(menuPrefix + "MouseWheelDown", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        // Mouse buttons.
        inputManager.addMapping(menuPrefix + "LButton", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping(menuPrefix + "RButton", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        // Keyboard.
        inputManager.addMapping(menuPrefix + "LCtrl", new KeyTrigger(KeyInput.KEY_LCONTROL));
        inputManager.addMapping(menuPrefix + "LShift", new KeyTrigger(KeyInput.KEY_LSHIFT));

        // Init materials
        Materials.initMaterials(application.getAssetManager());

        // Register the listeners to this panel.
        application.getInputManager().addListener(this.inputListener, menuPrefix + "MouseLeft", menuPrefix + "MouseRight",
                menuPrefix + "MouseDown", menuPrefix + "MouseUp", menuPrefix + "MouseWheelUp", menuPrefix + "MouseWheelDown", menuPrefix + "LButton", menuPrefix + "RButton", menuPrefix + "LCtrl", menuPrefix + "LShift");

        // Save the application to access various resources.
        this.application = application;
    }

    /**
     * Removes alll hooks. The menus won't work after that.
     */
    public void unRegister(InputManager inputManager)
    {
        application.getInputManager().removeListener(inputListener);
        application = null;
    }

    /**
     * Processes a click - if it's either just been pressed or just released.
     */
    public void processClick(boolean pressedOrReleased)
    {
        /* First of all, only process input if there's no transition running. */
        if (transitions.isEmpty())
        {
            // If it's a press:
            if (pressedOrReleased)
            {
                Vector3f contactPoint = new Vector3f();
                Vector3f contactNormal = new Vector3f();

                // First create a list of all leaf menu elements, to avoid calling 
                // this method on subpanels and raytracing several times.
                ArrayList<MenuElement> candidates = new ArrayList<>();
                findLeaves(candidates);

                // Then cast a ray to find which element is clicked - if there is one.
                MenuElement nodeAimed = (MenuElement) getNodeClicked(application.getCamera(), contactPoint, contactNormal, candidates.toArray(new MenuElement[0]));

                // If something was clicked, fire its process method.
                if (nodeAimed != null)
                {
                    nodeAimed.worldToLocal(contactPoint, contactPoint);
                    nodeAimed.processClick(pressedOrReleased, contactPoint);
                }
                // Also store the node as the currently focused one.
                clickedElement = nodeAimed;
            }
            else
            {
                // If the button has been released:
                if (clickedElement != null)
                {
                    clickedElement.processClick(pressedOrReleased, null);
                    clickedElement = null;
                }
            }
        }
    }

    /**
     * Processes a mouse drag. For locating the mouse during those events, a ray
     * cast won't do, because the mouse can be out of any component.
     */
    public void processDrag()
    {
        /* First of all, only process input if there's no transition running. */
        if (transitions.isEmpty() && clickedElement != null)
        {
            clickedElement.processDrag(getMousePosition(clickedElement));
        }
    }

    /**
     * Processes a mouse wheel turn.
     */
    @Override
    public void processWheel(int step)
    {
        /* First of all, only process input if there's no transition running. */
        if (transitions.isEmpty())
        {
            Vector3f contactPoint = new Vector3f();
            Vector3f contactNormal = new Vector3f();

            // First create a list of all leaf menu elements, to avoid calling 
            // this method on subpanels and raytracing several times.
            ArrayList<MenuElement> candidates = new ArrayList<>();
            findLeaves(candidates);

            // Then cast a ray to find which element is clicked - if there is one.
            MenuElement nodeAimed = (MenuElement) getNodeClicked(application.getCamera(), contactPoint, contactNormal, candidates.toArray(new MenuElement[0]));

            // If something was aimed, fire its process method.
            if (nodeAimed != null)
            {
                nodeAimed.processWheel(step);
            }
        }
    }

    /**
     * On updating, update all children and process all transitions.
     */
    @Override
    public void update(float tpf)
    {
        // A list of transitions to remove if they are over.
        ArrayList<Transition> toRemove = null;
        // For each transition:
        for (Transition transition : transitions)
        {
            // Update the transition.
            transition.update(tpf);

            // If the transition is over, mark it to remove.
            if (transition.isOver())
            {
                if (toRemove == null)
                {
                    toRemove = new ArrayList<>();
                }
                toRemove.add(transition);
            }
        }

        // Remove all finished transitions
        if (toRemove != null)
        {
            for (Transition t : toRemove)
            {
                // Finalize the transition.
                t.finish();
                // Also remove the source panel, now fully replaced by the destination.
                remove(t.getSource());
            }

            transitions.removeAll(toRemove);
        }

        // Finally, update all elements.
        for (MenuElement e : menuElements)
        {
            e.update(tpf);
        }
    }

    public void addTransition(Transition transition)
    {
        // First add the destination panel to the menu.
        add(transition.getDestination());
        // Init the transistion.
        transition.init();
        // Then add the transition itself.
        transitions.add(transition);
    }

    /**
     * Returns the closest node, among the list passed as argument, under the
     * mouse cursor. Also stores the contact point in the given vector (if not
     * null).
     */
    public Node getNodeClicked(Camera camera, Vector3f contactPoint, Vector3f contactNormal, Node... candidates)
    {
        CollisionResults results = new CollisionResults();
        Vector2f click2d = application.getInputManager().getCursorPosition();
        Vector3f click3d = camera.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f);
        Vector3f dir = camera.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();

        // 2. Aim the ray from cam loc to cam direction.
        Ray ray = new Ray(click3d, dir);

        // 3. Collect intersections between Ray and Shootables in results list.
        for (Node n : candidates)
        {
            n.collideWith(ray, results);
        }

        // Retrieve results.
        if (results.size() > 0)
        {
            // If there is at least one collision, return contact point and normal.
            if (contactPoint != null)
            {
                contactPoint.set(results.getClosestCollision().getContactPoint());
            }
            if (contactNormal != null)
            {
                contactNormal.set(results.getClosestCollision().getContactNormal());
            }

            // Finally, retrieve the node that generated the collision.
            Node collidedNode = results.getClosestCollision().getGeometry().getParent();
            for (Node n : candidates)
            {
                if (collidedNode == n || collidedNode.hasAncestor(n))
                {
                    return n;
                }
            }

            return null;
        }
        else
        {
            return null;
        }
    }

    private Vector3f getMousePosition(MenuElement clickedElement)
    {
        // If some component is indeed dragged, get the cursor position on the Z = 0 plane.
        Vector2f click2d = application.getInputManager().getCursorPosition();
        // Then make it 3D and absolute.
        Vector3f click3d = application.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f);
        // Finally, get the direction.
        Vector3f dir = application.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);

        // Next, compute intersection of the ray with the element *local* Z=0 plane.
        // To do that simply, subtract from the click3D position "enough of the
        // direction vector" to nullify its z-component, when converted to the
        // slider's local space.
        clickedElement.worldToLocal(click3d, click3d);
        clickedElement.worldToLocal(dir, dir).normalizeLocal();

        // If the direction along Z is zero, tweak it a bit.
        if (Math.abs(dir.z) < 0.0000001f)
        {
            dir.z = Math.signum(dir.z) * 0.0000001f;
        }

        click3d.subtractLocal(dir.multLocal(click3d.z / dir.z));
        return click3d;
    }

    @Override
    protected void refresh()
    {
        for (MenuElement child : menuElements)
        {
            child.refresh();
        }
    }

    /**
     * For panels - and panels only - the findLeaves iterates on every child.
     */
    @Override
    protected void findLeaves(ArrayList<MenuElement> candidates)
    {
        for (MenuElement element : menuElements)
        {
            element.findLeaves(candidates);
        }
    }

    public void add(MenuElement menuElement)
    {
        // Set this as the parent.
        menuElement.menuParent = this;

        attachChild(menuElement);

        // If a material is set, propagate it to the child.
        Material mat = getMenuMaterial();
        if (mat != null)
        {
            menuElement.setMenuMaterial(mat);
        }

        menuElements.add(menuElement);
        // If a font is available, refresh the added element.
        if (menuElement.getMenuFont() != null)
        {
            menuElement.refresh();
        }
    }

    public void remove(MenuElement menuElement)
    {
        menuElement.menuParent = null;
        detachChild(menuElement);
        menuElements.remove(menuElement);
    }

    /**
     * On changing the menuMaterial, refresh all children.
     */
    @Override
    public void setMenuMaterial(Material menuMaterial)
    {
        super.setMenuMaterial(menuMaterial);

        for (MenuElement child : menuElements)
        {
            child.setMenuMaterial(menuMaterial);
        }

        refresh();
    }

    @Override
    public float getLocalWidth()
    {
        return size.x;
    }

    @Override
    public float getLocalHeight()
    {
        return size.y;
    }

    @Override
    public float getLocalDepth()
    {
        float result = 0;
        for (MenuElement element : menuElements)
        {
            //TODO
            result = Math.max(result, element.getDepth());
        }
        return result;
    }

    /**
     * This global listener wraps the analog and action listeners for the scene.
     * It then redirects the events to all menu elements and their listeners.
     */
    private class InputListener implements AnalogListener, ActionListener
    {
        //   private boolean leftButtonDown = false;
        private boolean ctrlDown = false;
        private boolean shiftDown = false;

        /**
         * This listener is fired on analog events (mouse displacement,
         * scrolling...)
         */
        @Override
        public void onAnalog(String name, float value, float tpf)
        {
            // If the name begins with the menu prefix, strip it and continue.
            if (name.startsWith(menuPrefix))
            {
                name = name.replace(menuPrefix, "");
                int step = 1;
                switch (name)
                {
                case "MouseWheelDown":
                    step = -step;
                case "MouseWheelUp":
                    processWheel(step);
                    break;
                case "MouseLeft":
                case "MouseRight":
                case "MouseDown":
                case "MouseUp":
                    // If the left button is pressed, it's a drag. Process it!
                    processDrag();
                    break;
                }
            }
        }

        /**
         * This listener is fired on non-analog events (keystrokes, clicks...)
         */
        @Override
        public void onAction(String name, boolean isPressed, float tpf)
        {
            // If the name begins with the menu prefix, strip it and continue.
            if (name.startsWith(menuPrefix))
            {
                name = name.replace(menuPrefix, "");
                switch (name)
                {
                case "LShift":
                    shiftDown = isPressed;
                    break;
                case "LCtrl":
                    ctrlDown = isPressed;
                    break;
                case "LButton":
                    // leftButtonDown = isPressed;
                    // Process the click (on or off).
                    processClick(isPressed);

                    break;
                case "RButton":
                    // On click:
                    if (isPressed)
                    {
                    }

                    break;
                }
            }
        }
    }

    public int attachChild(MenuElement child)
    {
        return super.attachChild(child);
    }
}
