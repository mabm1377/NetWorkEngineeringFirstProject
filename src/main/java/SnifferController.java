import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SnifferController implements ActionListener {
    private Sniffer sniffer= null;
    private SelectNetworkInterfaceView selectInterfaceView = null;
    private static SnifferController instance = null;
    private SnifferController() {
        sniffer = new Sniffer();
        selectInterfaceView = new SelectNetworkInterfaceView("sniffer", sniffer.getAllNetworkInterfacesNames());
        selectInterfaceView.startSniffButton.addActionListener(this);
    }
    public static SnifferController getInstance() {
        if(instance== null){
            instance = new SnifferController();
        }
        return instance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
