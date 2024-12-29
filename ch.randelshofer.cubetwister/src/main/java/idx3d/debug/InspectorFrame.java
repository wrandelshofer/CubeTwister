package idx3d.debug;

import org.jhotdraw.annotation.Nonnull;

import java.awt.Event;
import java.awt.Frame;
import java.awt.Toolkit;

@SuppressWarnings("deprecation")
public abstract class InspectorFrame extends Frame {
    private static int pos = 0;
    String id;
    @Nonnull ScrollList list = new ScrollList(20);

    public InspectorFrame(@Nonnull Object obj, String id) {
        super(id + "  [" + obj.getClass().getName() + "]");
        this.id = id;
        add(list);
        pos = (pos + 20) % (Toolkit.getDefaultToolkit().getScreenSize().height / 2);
        move(pos, pos);
        show();
    }

    public void addEntry(InspectorFrameEntry comp) {
        list.add(comp);
        int screenheight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int fullheight = 20 * (list.countComponents() - 1) + insets().top + insets().bottom;
        int height = (fullheight < screenheight - pos) ? fullheight : screenheight - pos;
        if (height <= size().height) {
            return;
        }
        resize(320, height);
    }

    public boolean handleEvent(@Nonnull Event evt) {
        if (evt.id == Event.WINDOW_DESTROY) {
            hide();
            dispose();
            return true;
        }
        return super.handleEvent(evt);
    }
}
