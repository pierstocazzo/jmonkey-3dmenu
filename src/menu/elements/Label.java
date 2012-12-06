package menu.elements;

import com.jme3.material.Material;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import menu.utils.FontToMesh;

/**
 * A label is a simple 3D text, intialized from the given text.
 */
public class Label extends MenuElement
{

    protected FontToMesh font;
    private String text;
    private Node stringNode;
    protected boolean enabled = true;

    public Label(FontToMesh font, String text)
    {
        setName("Label");
        this.font = font;
        this.text = text;
        Label.this.refresh();
    }

    /**
     * Turns the given text into a mesh, that will be the label's physical
     * representation.
     */
    public void setText(String text)
    {
        this.text = text;
        refresh();
    }

    @Override
    public void setMaterial(Material mat)
    {
        this.material = mat;
        // Only change the string material.
        stringNode.setMaterial(mat);
    }

    /**
     * @return the font
     */
    public FontToMesh getFont()
    {
        return font;
    }

    /**
     * @param font the font to set
     */
    public void setFont(FontToMesh font)
    {
        this.font = font;
        refresh();
    }

    /* Regenerates the mesh. */
    public void refresh()
    {
        detachAllChildren();

        // Generate and attach the text mesh.
        stringNode = font.getStringNode(text);
        attachChild(stringNode);
        stringNode.setMaterial(material);
    }

    @Override
    protected Vector3f getLocalMinBound()
    {
        return Vector3f.ZERO;
    }

    @Override
    protected Vector3f getLocalMaxBound()
    {
        Vector3f result = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
        for (char c : text.toCharArray())
        {
            Vector3f glyphSize = font.getGlyphSize(c);
            result.x += glyphSize.x * FontToMesh.spacingRatio;
            result.y = Math.max(result.y, glyphSize.y);
            result.z = Math.max(result.z, glyphSize.z);
        }

        return result;
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
            
        } else
        {
            //setLocalScale(0.3f);
             setLocalRotation(new Quaternion().fromAngles(0f,0.45f, 0));
        }
        //  stringNode.setMaterial(material);
        // setMaterial(material);
    }
}
