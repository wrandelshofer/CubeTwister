package idx3d.debug;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Panel;

@SuppressWarnings("deprecation")
public class InspectorFrameEntry extends Panel {

    private final static long serialVersionUID = 1L;
    @Nullable Object obj = null;
    @Nullable String type, name, value;
    boolean mouseInside = false;
    InspectorFrame parent;
    boolean isPrimitive;

    @Nonnull Font plain = new Font("Helvetica", 0, 11);
    @Nonnull Font bold = new Font("Helvetica", 1, 11);

    public InspectorFrameEntry(InspectorFrame parent, @Nullable Object obj, String name) {
        this.parent = parent;
        this.type = (obj != null) ? obj.getClass().getName() : "Object";
        if (this.type.startsWith("idx3d.")) {
            this.type = this.type.substring(6);
        }
        this.name = name;
        this.value = (obj == null) ? "null" : "[...]";
        this.obj = obj;
        isPrimitive = (obj == null);
    }

    public InspectorFrameEntry(InspectorFrame parent, String type, String name, @Nullable String value) {
        this.parent = parent;
        this.type = type;
        this.name = name;
        this.value = (value != null) ? value : "[...]";
        this.obj = null;
        isPrimitive = true;
    }

    public void paint(@Nonnull Graphics g) {
        int w = this.size().width;
        int h = this.size().height;
        int m1 = w / 3;
        int m2 = w * 2 / 3;

        g.setFont(plain);
        g.setColor(new Color(80, 80, 80));
        g.fillRect(0, 0, m1, h);
        g.setColor(Color.white);
        g.drawString(type, 2, 14);

        g.setFont(bold);
        g.setColor(new Color(120, 120, 120));
        g.fillRect(m1, 0, m1, h);
        g.setColor(Color.white);
        g.drawString(name, m1 + 2, 14);

        g.setFont(plain);
        g.setColor(new Color(160, 160, 160));
        g.fillRect(m2, 0, m1, h);
        g.setColor(Color.black);
        g.drawString(value, m2 + 2, 14);

        if (mouseInside & !isPrimitive) {
            g.setColor(new Color(0, 0, 0));
            g.drawLine(0, 0, w, 0);

            g.setColor(new Color(192, 192, 192));
        } else {
            g.setColor(new Color(164, 164, 164));
            g.drawLine(0, 0, w, 0);

            g.setColor(new Color(32, 32, 32));
            g.drawLine(0, h - 1, w, h - 1);
        }

    }

    public boolean mouseUp(Event evt, int x, int y) {
        String newName = "";
        if (!parent.id.equals("")) {
            newName += parent.id + ".";
        }
        newName += name;
        Inspector.inspect(obj, newName);
        return true;
    }

    public boolean mouseEnter(Event evt, int x, int y) {
        mouseInside = true;
        if (!isPrimitive) {
            repaint();
        }
        return true;
    }

    public boolean mouseExit(Event evt, int x, int y) {
        mouseInside = false;
        if (!isPrimitive) {
            repaint();
        }
        return true;
    }
}
