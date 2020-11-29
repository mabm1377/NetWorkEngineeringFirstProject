import org.pcap4j.core.*;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.BpfProgram.BpfCompileMode;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.List;

import exeptions.*;
import org.pcap4j.packet.namednumber.IpNumber;

public class Sniffer implements Runnable {
    private final HashMap<String, Statistics> applicationStatics = new HashMap<>();
    private final HashMap<String, Statistics> networkStatics = new HashMap<>();
    private PcapNetworkInterface currentInterfaceInSniff = null;
    private volatile boolean running = true;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    private PcapHandle pcapHandle = null;
    private final PropertyChangeSupport support;
    private static final int READ_TIMEOUT = 50; // [ms]
    private static final int SNAPLEN = 65536; // [bytes]
    private static final int BUFFER_SIZE = 1024 * 1024; // [bytes]

    public Sniffer() {
        support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    @Override
    public void run() {
        while (running){
            synchronized (pauseLock){
                if(!running){
                    break;
                }
                if(paused){
                    try {
                        pauseLock.wait();
                    }
                    catch (InterruptedException ex){
                        break;
                    }
                    if(!running){
                        break;
                    }
                }
            }
            try {
                getNextPacket();
            } catch (NotOpenException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
        resume();
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notify(); // Unblocks thread
        }
    }
    public void printSniffedPacket() throws NotOpenException {
        getNextPacket();
        System.out.println("applicationLayer");
        for (var asm : applicationStatics.values()) {
            System.out.println(asm.toString());
        }
        System.out.println("***************************************************");
        System.out.println("networkLayer");
        for (var nsm : networkStatics.values()) {
            System.out.println(nsm.toString());
        }
        System.out.println("___________________________________________________");
    }

    public void setCurrentInterfaceInSniff(PcapNetworkInterface currentInterfaceInSniff) {
        this.currentInterfaceInSniff = currentInterfaceInSniff;
    }

    public void createPcapHandle() throws PcapNativeException, InterFaceNotSelected {
        if (this.currentInterfaceInSniff == null) {
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
        synchronized (this) {
            if (this.pcapHandle == null) {
                throw new PcapHandlerNotInitialized("please create handler before use filter");
            }
            this.pcapHandle.setFilter(filter, BpfCompileMode.OPTIMIZE);
        }
    }

    public void updateApplicationStatics(String protocol, String srcPort, String dstPort, int length) {
        synchronized (this) {
            String key = protocol + "*" + srcPort + "*" + dstPort;
            if (!applicationStatics.containsKey(key)) {
                applicationStatics.put(key, new ApplicationStatics(length, protocol, srcPort, dstPort));
            } else {
                applicationStatics.get(key).update(length);
            }
            support.firePropertyChange("applicationStatics",null,applicationStatics);
        }
    }

    public void updateNetworkStatics(String protocol, String srcAddr, String dstAddr, int length) {
        String key = protocol + "*" + srcAddr + "*" + dstAddr;
        if (!networkStatics.containsKey(key)) {
            networkStatics.put(key, new NetworkStatics(length, protocol, srcAddr, dstAddr));
        } else {
            networkStatics.get(key).update(length);
        }
        support.firePropertyChange("networkStatics",null,networkStatics);
    }

    public void getNextPacket() throws NotOpenException {
        synchronized (this) {
            while (true) {
                Packet packet = this.pcapHandle.getNextPacket();
                if (packet != null) {
                    // application layer
                    if (packet.contains(TcpPacket.class)) {
                        TcpPacket tcpPacket = packet.get(TcpPacket.class);
                        TcpPacket.TcpHeader tcpHeader = tcpPacket.getHeader();
                        updateApplicationStatics("tcp", tcpHeader.getSrcPort().toString(), tcpHeader.getDstPort().toString(), tcpPacket.length());
                    }
                    if (packet.contains(UdpPacket.class)) {
                        UdpPacket udpPacket = packet.get(UdpPacket.class);
                        UdpPacket.UdpHeader udpHeader = udpPacket.getHeader();
                        updateApplicationStatics("udp", udpHeader.getSrcPort().toString(), udpHeader.getDstPort().toString(), udpPacket.length());
                    }
                    // network layer
                    if (packet.contains(IpV4Packet.class)) {
                        IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                        IpV4Packet.IpV4Header ipV4Header = ipV4Packet.getHeader();
                        if (ipV4Header.getProtocol().equals(IpNumber.TCP)) {
                            updateNetworkStatics("tcp", ipV4Header.getSrcAddr().toString(), ipV4Header.getDstAddr().toString(), ipV4Packet.length());
                        } else if (ipV4Header.getProtocol().equals(IpNumber.UDP)) {
                            updateNetworkStatics("udp", ipV4Header.getSrcAddr().toString(), ipV4Header.getDstAddr().toString(), ipV4Packet.length());
                        } else if (ipV4Header.getProtocol().equals(IpNumber.ICMPV4)) {
                            updateNetworkStatics("icmpv4", ipV4Header.getSrcAddr().toString(), ipV4Header.getDstAddr().toString(), ipV4Packet.length());
                        } else if (ipV4Header.getProtocol().equals(IpNumber.IGMP)) {
                            updateNetworkStatics("igmp", ipV4Header.getSrcAddr().toString(), ipV4Header.getDstAddr().toString(), ipV4Packet.length());
                        }
                    }
                    return;
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

    public static void main(String[] args) throws PcapNativeException, InterFaceNotSelected, NotOpenException, PcapHandlerNotInitialized, InterruptedException {
//        Sniffer sniffer = new Sniffer();
//        var nifs = sniffer.getAllNetworkInterfacesNames();
//        sniffer.setCurrentInterfaceInSniff(nifs.get(5));
//        sniffer.createPcapHandle();
//        PacketsListView packetsListView = new PacketsListView("test",1500,1500);
//        sniffer.addPropertyChangeListener(packetsListView);
//        Thread snifferRunner = new Thread(sniffer);
//        snifferRunner.start();
    }
}
