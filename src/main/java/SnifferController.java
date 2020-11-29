import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import exeptions.InterFaceNotSelected;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;

public class SnifferController implements ActionListener {
    private Sniffer sniffer;
    private Thread snifferRunner;
    private SelectNetworkInterfaceView selectInterfaceView;
    private PacketsListView packetsListView;
    private static SnifferController instance = null;

    private SnifferController() {
        sniffer = new Sniffer();
        snifferRunner = new Thread(sniffer);
        selectInterfaceView = new SelectNetworkInterfaceView("sniffer", sniffer.getAllNetworkInterfacesNames());
        packetsListView = new PacketsListView("sniffing", 1000, 3000);
        sniffer.addPropertyChangeListener(packetsListView);
        selectInterfaceView.startSniffButton.addActionListener(this);
        packetsListView.pause.addActionListener(this);
        packetsListView.resume.addActionListener(this);
    }

    public static SnifferController getInstance() {
        if (instance == null) {
            instance = new SnifferController();
        }
        return instance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selectInterfaceView.startSniffButton) {
            selectInterfaceView.setVisible(false);
            PcapNetworkInterface nif = selectInterfaceView.getSelectedInterFace();
            sniffer.setCurrentInterfaceInSniff(nif);
            try {
                sniffer.createPcapHandle();
                snifferRunner.start();
                packetsListView.setVisible(true);
            } catch (PcapNativeException | InterFaceNotSelected pcapNativeException) {
                pcapNativeException.printStackTrace();
            }
        }
        if (e.getSource() ==packetsListView.pause ) {
            sniffer.pause();
        }
        if (e.getSource()==packetsListView.resume){
            sniffer.resume();
        }
        if(e.getSource()== packetsListView.back){

        }
    }

    public static void main(String[] args) {
        SnifferController.getInstance();
    }
}
