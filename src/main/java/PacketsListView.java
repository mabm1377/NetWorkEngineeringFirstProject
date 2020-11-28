import javax.swing.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;

import org.pcap4j.packet.Packet;
public class PacketsListView extends JFrame implements PropertyChangeListener {
    PacketsListView(String s){
        super(s);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("applicationStatics")){
            if(evt.getNewValue() instanceof )
            for(Statistics statistics : ((HashMap<String, Statistics>)evt.getNewValue()).values()){
                System.out.println(statistics.getStatistics());
            }
        }
        if (evt.getPropertyName().equals("b")) {

        }
    }
}
