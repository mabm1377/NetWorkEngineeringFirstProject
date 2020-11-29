import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

public class PacketsListView extends JFrame implements PropertyChangeListener, ItemListener {
    ScrollableList<Statistics> applicationStatics = null;
    ScrollableList<Statistics> networkStatics = null;
    HashMap<String, Statistics> currentApplicationPacketSniffed;
    HashMap<String, Statistics> currentNetworkPacketSniffed;
    String appFilter ="all";
    String netFilter = "all";
    JButton pause;
    JButton resume;
    JButton back;
    JComboBox<String> appProtocols;
    JComboBox<String> netProtocols;

    PacketsListView(String s, int width, int height) {
        super(s);
        setSize(width, height);
        List<Statistics> statisticsList = new ArrayList<>();
        JLayeredPane pane = getLayeredPane();

        String[] appProtocolNames = {"ALL ", "UDP", "TCP"};
        appProtocols = new JComboBox<>(appProtocolNames);
        appProtocols.setBounds(0, 0, width, 30);

        applicationStatics = new ScrollableList<>(statisticsList, "applicationStatics");
        applicationStatics.setBounds(0, 30, width, 200);

        String[] netProtocolNames = {"ALL ", "UDP", "TCP", "ICMPV4", "IGMP"};
        netProtocols = new JComboBox<>(netProtocolNames);
        netProtocols.setBounds(0, 230, width, 30);

        networkStatics = new ScrollableList<>(statisticsList, "networkStatics");
        networkStatics.setBounds(0, 260, width, 200);
        pause = new JButton("pause");
        pause.setBounds(0, 460, width, 30);
        resume = new JButton("resume");
        resume.setBounds(0, 490, width, 30);
        back = new JButton("back");
        back.setBounds(0, 520, width, 30);
        pane.add(back, 1);
        pane.add(pause, 2);
        pane.add(resume, 3);
        pane.add(networkStatics, 4);
        pane.add(netProtocols, 5);
        pane.add(applicationStatics, 6);
        pane.add(appProtocols, 7);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private Collection<Statistics> filterAppStatics() {
        if(this.appFilter.equals("all")){
            return  currentApplicationPacketSniffed.values();
        }
        Collection<Statistics> statistics= new ArrayList<>();
        for(Statistics statistics1 : this.currentApplicationPacketSniffed.values()){
            if (statistics1.protocol.equals(this.appFilter)){
                statistics.add(statistics1);
            }
        }
        return statistics;
    }
    private Collection<Statistics> filterNetStatics() {
        if(this.netFilter.equals("all")){
            return  currentNetworkPacketSniffed.values();
        }
        Collection<Statistics> statistics= new ArrayList<>();
        for(Statistics statistics1 : this.currentNetworkPacketSniffed.values()){
            if (statistics1.protocol.equals(this.netFilter)){
                statistics.add(statistics1);
            }
        }
        return statistics;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() != null) {
            if (evt.getPropertyName().equals("applicationStatics")) {
                this.currentApplicationPacketSniffed = ((HashMap<String, Statistics>) evt.getNewValue());
                applicationStatics.updateList(filterAppStatics());
            }
            if (evt.getPropertyName().equals("networkStatics")) {
                this.currentNetworkPacketSniffed = ((HashMap<String, Statistics>) evt.getNewValue());
                networkStatics.updateList(filterNetStatics());
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == appProtocols) {
            appFilter = ((String) Objects.requireNonNull(appProtocols.getSelectedItem())).toLowerCase();
            applicationStatics.updateList(filterAppStatics());
        } else if (e.getSource() == netProtocols) {
            netFilter = ((String) Objects.requireNonNull(netProtocols.getSelectedItem())).toLowerCase();
            applicationStatics.updateList(filterAppStatics());
        }
    }
}
