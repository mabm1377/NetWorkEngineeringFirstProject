import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import java.util.ArrayList;
import java.util.List;
import exeptions.*;
public class Sniffer {
    private ArrayList<Packet> currentCapturedPackets = new ArrayList<>();
    private PcapNetworkInterface currentInterfaceInSniff = null;
    private PcapHandle pcapHandle= null;

    private static Sniffer instance = null;
    private static final int READ_TIMEOUT = 10; // [ms]

    private static final int SNAPLEN = 65536; // [bytes]

    private static final int BUFFER_SIZE = 1 * 1024 * 1024; // [bytes]

    public void setCurrentInterfaceInSniff(PcapNetworkInterface currentInterfaceInSniff) {
        this.currentInterfaceInSniff = currentInterfaceInSniff;
    }

    public void createPcapHandle() throws PcapNativeException, InterFaceNotSelected {
        if(this.currentInterfaceInSniff == null){
            throw new InterFaceNotSelected("please Select Interface");
        }
        PcapHandle.Builder phb =
                new PcapHandle.Builder(this.currentInterfaceInSniff.getName())
                        .snaplen(SNAPLEN)
                        .promiscuousMode(PcapNetworkInterface.PromiscuousMode.PROMISCUOUS)
                        .timeoutMillis(READ_TIMEOUT)
                        .bufferSize(BUFFER_SIZE);
        phb.timestampPrecision(PcapHandle.TimestampPrecision.NANO);
        this.pcapHandle = phb.build();
    }
    public void setFilterForHandler(String filter) throws PcapHandlerNotInitialized, PcapNativeException, NotOpenException {
        synchronized (this){
            if(this.pcapHandle == null){
                throw new PcapHandlerNotInitialized("please create handler before use filter");
            }
            this.pcapHandle.setFilter(filter, BpfCompileMode.OPTIMIZE);
        }
    }
    public ArrayList<Packet> getCurrentCapturedPackets() {
        synchronized (this) {
            return this.currentCapturedPackets;
        }
    }

    public Packet getNextPacket() throws NotOpenException {
        synchronized (this){
            while (true){
                Packet packet = this.pcapHandle.getNextPacket();
                if(packet != null){
                    this.currentCapturedPackets.add(packet);
                    return packet;
                }
            }
        }
    }

    public List<PcapNetworkInterface> getAllNetworkInterfacesNames() {
        try {
            return Pcaps.findAllDevs();
        } catch (PcapNativeException e) {
            e.printStackTrace();
            return null;
        }
    }
}
