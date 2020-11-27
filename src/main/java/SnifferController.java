import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SnifferController implements ActionListener {
    private Sniffer sniffer= null;
    private SelectInterfaceView selectInterfaceView = null;
    private SnifferController() {
        sniffer = new Sniffer();
        selectInterfaceView = new SelectInterfaceView("sniffer", sniffer.getAllNetworkInterfacesNames());
        selectInterfaceView.startSniffButton.addActionListener(this);
    }
    private static SnifferController instance = null;
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
