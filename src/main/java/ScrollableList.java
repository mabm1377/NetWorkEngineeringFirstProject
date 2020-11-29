import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.*;

public class ScrollableList<T> extends JInternalFrame {
    private final JList<String> list;

    private DefaultListModel<String> produceModel(Collection<T> elements) {
        DefaultListModel<String> defaultListModel = new DefaultListModel<>();
        for (Object element : elements) {
            defaultListModel.addElement(element.toString());
        }
        return defaultListModel;
    }

    ScrollableList(Collection<T> elements, String s) {
        super(s);
        list = new JList<>(produceModel(elements));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(list);
        list.setLayoutOrientation(JList.VERTICAL);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane);
        add(panel);
        setVisible(true);
    }

    public void updateList(Collection<T> elements) {
        list.setModel(produceModel(elements));
    }
}