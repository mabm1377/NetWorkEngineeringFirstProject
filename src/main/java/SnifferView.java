import javax.swing.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.List;

import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.util.LinkLayerAddress;

public class SnifferView extends JFrame implements ActionListener{
    JLabel jLabel;
    JRadioButton interfacesButtons[]= null;
    JButton startSniffButton;
    ButtonGroup buttonGroup;
    SnifferView(String s,  List<PcapNetworkInterface> nifs) {
        super(s);
        jLabel = new JLabel();
        add(jLabel);
        jLabel.setText("please select interface for sniff");
        jLabel.setBounds(30,40,450,20);
        buttonGroup = new ButtonGroup();
        interfacesButtons = new JRadioButton[nifs.size()];
        int heightCursor = 80;
        for(int i =0 ; i<nifs.size(); i++){
            interfacesButtons[i] = new JRadioButton();
            add(interfacesButtons[i]);
            buttonGroup.add(interfacesButtons[i]);
            String nifName= generateInterfaceName(nifs.get(i));
            interfacesButtons[i].setBounds(50,heightCursor,1000,20);
            interfacesButtons[i].setText(nifName);
            heightCursor+=30;
        }
        interfacesButtons[5].setSelected(true);
        startSniffButton = new JButton("start sniff");
        startSniffButton.addActionListener(this);
        add(startSniffButton);
        heightCursor+=50;
        startSniffButton.setBounds(185,heightCursor,100,30);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocation(250,100);
        setVisible(true);
        setSize(1000,heightCursor+100);
    }
    public String generateInterfaceName(PcapNetworkInterface nif){
        StringBuilder stringBuilder = new StringBuilder();
        if (nif.getDescription() != null) {
            stringBuilder.append("      : description: ").append(nif.getDescription());
        }
        Iterator var6 = nif.getLinkLayerAddresses().iterator();

        while(var6.hasNext()) {
            LinkLayerAddress addr = (LinkLayerAddress)var6.next();
            stringBuilder.append("      : link layer address: ").append(addr);
        }

        var6 = nif.getAddresses().iterator();

        while(var6.hasNext()) {
            PcapAddress addr = (PcapAddress)var6.next();
            stringBuilder.append("      : address: ").append(addr.getAddress());
        }
        return stringBuilder.toString();
    }
    public static void main(String[] args) {
        new SnifferView("sniffer",new Sniffer().getAllNetworkInterfacesNames());
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}