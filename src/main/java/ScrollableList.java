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

    ScrollableList(Collection<T> elements, String s, int width, int height) {
        super(s);
        list = new JList<>(produceModel(elements));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(list);
        list.setLayoutOrientation(JList.VERTICAL);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane);
        add(panel);
        setSize(width, height);
        setVisible(true);
    }

    public void updateList(Collection<T> elements) {
        list.setModel(produceModel(elements));
    }

    public static void main(String[] args) throws InterruptedException {
        List<String> list = new ArrayList<>();
        for(int i =0; i<20;i++){
            list.add("item" +i);
        }
        ScrollableList<String> scrollableList = new ScrollableList<>(list,"test",100,100);
        ScrollableList<String> scrollableList2 = new ScrollableList<>(list,"test",100,100);
        JFrame frame = new JFrame("test");
        frame.setLayout(new FlowLayout());
        frame.setSize(200,200);
        frame.add(scrollableList);
        frame.add(scrollableList2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}