package menu.elements;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import menu.utils.Jme3DFont;

/**
 * A label is a simple 3D text, intialized from the given text.
 */
public class Label extends MenuElement
{
    protected String text;
    protected Node stringNode;

    public Label(String text)
    {
        setName("Label");
        this.text = text;
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
        // Only change the string material.
        if (stringNode != null)
        {
            stringNode.setMaterial(mat);
        }
    }

    @Override
    public void refresh()
    {
        // Discard the former string node.
        if (stringNode != null && hasChild(stringNode))
        {
            detachChild(stringNode);
        }

        // Generate and attach the text mesh.
        stringNode = getMenuFont().getStringNode(text);

        // If there is a material set, apply it.
        Material mat = getMenuMaterial();

        if (mat != null)
        {
            setMaterial(mat);
        }

        attachChild(stringNode);
    }

    @Override
    public float getLocalWidth()
    {
        Jme3DFont currentFont = getMenuFont();
        float result = 0;
        for (char c : text.toCharArray())
        {
            Vector3f glyphSize = currentFont.getGlyphSize(c);
            result += glyphSize.x * Jme3DFont.spacingRatio;
        }

        return result;
    }

    @Override
    public float getLocalHeight()
    {
        Jme3DFont currentFont = getMenuFont();
        float result = 0;
        for (char c : text.toCharArray())
        {
            Vector3f glyphSize = currentFont.getGlyphSize(c);
            result = Math.max(result,glyphSize.y);
        }

        return result;
    }
    
        @Override
    public float getLocalDepth()
    {
        Jme3DFont currentFont = getMenuFont();
        float result = 0;
        for (char c : text.toCharArray())
        {
            Vector3f glyphSize = currentFont.getGlyphSize(c);
            result = Math.max(result,glyphSize.z);
        }

        return result;
    }
}
