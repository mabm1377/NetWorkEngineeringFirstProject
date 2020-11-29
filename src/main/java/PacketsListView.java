import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PacketsListView extends JFrame implements PropertyChangeListener {

    ScrollableList<Statistics> applicationStatics = null;
    ScrollableList<Statistics> networkStatics = null;
    JButton pause;
    JButton resume;
    JButton back;
    PacketsListView(String s, int width, int height) {
        super(s);
        setSize(width, height);
        List<Statistics> statisticsList = new ArrayList<>();
        JLayeredPane pane = getLayeredPane();
        applicationStatics = new ScrollableList<>(statisticsList, "applicationStatics");
        applicationStatics.setBounds(0, 0, width, 200);
        networkStatics = new ScrollableList<>(statisticsList, "networkStatics");
        networkStatics.setBounds(0, 200, width, 200);
        pause = new JButton("pause");
        pause.setBounds(0, 400, width, 30);
        resume = new JButton("resume");
        resume.setBounds(0, 430, width, 30);
        back = new JButton("back");
        back.setBounds(0, 460, width, 30);
        pane.add(back,1);
        pane.add(pause, 2);
        pane.add(resume, 3);
        pane.add(networkStatics, 4);
        pane.add(applicationStatics, 5);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() != null) {
            if (evt.getPropertyName().equals("applicationStatics")) {
                applicationStatics.updateList(((HashMap<String, Statistics>) evt.getNewValue()).values());
            }
            if (evt.getPropertyName().equals("networkStatics")) {
                networkStatics.updateList(((HashMap<String, Statistics>) evt.getNewValue()).values());
            }
        }
    }
}
