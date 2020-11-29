import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

public class PacketsListView extends JFrame implements PropertyChangeListener {
    private ScrollableList<Statistics> applicationStatics = null;
    private ScrollableList<Statistics> networkStatics = null;

    PacketsListView(String s, int width, int height) {
        super(s);
        setLayout(new FlowLayout());
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() != null) {
            this.setVisible(true);
            if (evt.getPropertyName().equals("applicationStatics")) {
                if (applicationStatics == null) {
                    applicationStatics = new ScrollableList<>(((HashMap<String, Statistics>) evt.getNewValue()).values(),
                            "applicationStatics", 1500, 100);
                    this.add(applicationStatics);
                } else {
                    System.out.println("hoora");;
                    applicationStatics.updateList(((HashMap<String, Statistics>) evt.getNewValue()).values());
                }
            }
            if (evt.getPropertyName().equals("networkStatics")) {
                if (networkStatics == null) {
                    networkStatics = new ScrollableList<>(((HashMap<String, Statistics>) evt.getNewValue()).values(),
                            "networkStatics", 1500, 100);
                    this.add(networkStatics);
                } else {
                    networkStatics.updateList(((HashMap<String, Statistics>) evt.getNewValue()).values());
                }
            }
        }
    }
}
