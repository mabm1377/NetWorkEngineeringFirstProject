import org.pcap4j.core.*;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.BpfProgram.BpfCompileMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import exeptions.*;
import org.pcap4j.packet.namednumber.IpNumber;

public class Sniffer {
    private ArrayList<Packet> currentCapturedPackets = new ArrayList<>();
    private HashMap<String, Statistics> applicationStaticsMap = new HashMap<>();
    private HashMap<String, Statistics> networkStaticsMap = new HashMap<>();
    private PcapNetworkInterface currentInterfaceInSniff = null;
    private PcapHandle pcapHandle = null;
    private static Sniffer instance = null;
    private static final int READ_TIMEOUT = 10; // [ms]

    private static final int SNAPLEN = 65536; // [bytes]

    private static final int BUFFER_SIZE = 1 * 1024 * 1024; // [bytes]
    public void printSniffedPacket() throws NotOpenException {
        getNextPacket();
        System.out.println("applicationLayer");
        for(var asm :applicationStaticsMap.values()){
            System.out.println(asm.getStatistics());
        }
        System.out.println("***************************************************");
        System.out.println("networkLayer");
        for(var nsm: networkStaticsMap.values()){
            System.out.println(nsm.getStatistics());
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

    public Packet getNextPacket() throws NotOpenException {
        synchronized (this) {
            while (true) {
                Packet packet = this.pcapHandle.getNextPacket();
                if (packet != null) {
                    // application layer
                    if (packet.contains(TcpPacket.class)) {
                        StringBuilder key = new StringBuilder();
                        TcpPacket tcpPacket = packet.get(TcpPacket.class);
                        TcpPacket.TcpHeader tcpHeader = tcpPacket.getHeader();
                        key.append("tcp").append("*").append(tcpHeader.getSrcPort().toString()).append("*").append(tcpHeader.getDstPort().toString());
                        if (!applicationStaticsMap.containsKey(key.toString())) {
                            applicationStaticsMap.put(key.toString(), new ApplicationStatics(tcpPacket.length(), "tcp",
                                    tcpHeader.getSrcPort().toString(), tcpHeader.getDstPort().toString()));
                        } else {
                            applicationStaticsMap.get(key.toString()).update(tcpPacket.length());
                        }
                    }

                    if (packet.contains(UdpPacket.class)) {
                        StringBuilder key = new StringBuilder();
                        UdpPacket udpPacket = packet.get(UdpPacket.class);
                        UdpPacket.UdpHeader udpHeader = udpPacket.getHeader();
                        key.append("udp").append("*").append(udpHeader.getSrcPort().toString()).append("*").append(udpHeader.getDstPort().toString());
                        if (!applicationStaticsMap.containsKey(key.toString())) {
                            applicationStaticsMap.put(key.toString(), new ApplicationStatics(udpPacket.length(), "tcp",
                                    udpHeader.getSrcPort().toString(), udpHeader.getDstPort().toString()));
                        } else {
                            applicationStaticsMap.get(key.toString()).update(udpHeader.length());
                        }
                    }
                    // network layer
                    if (packet.contains(IpV4Packet.class)) {
                        IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                        IpV4Packet.IpV4Header ipV4Header = ipV4Packet.getHeader();
                        if (ipV4Header.getProtocol().equals(IpNumber.TCP)) {
                            StringBuilder key = new StringBuilder();
                            key.append("tcp").append("*").append(ipV4Header.getSrcAddr().toString()).append("*").append(ipV4Header.getDstAddr().toString());
                            if (!networkStaticsMap.containsKey(key.toString())) {
                                networkStaticsMap.put(key.toString(), new NetworkStatics(ipV4Packet.length(), "tcp", ipV4Header.getSrcAddr().toString(), ipV4Header.getDstAddr().toString()));
                            }
                            else {
                                networkStaticsMap.get(key.toString()).update(ipV4Packet.length());
                            }
                        } else if (ipV4Header.getProtocol().equals(IpNumber.UDP)) {
                            StringBuilder key = new StringBuilder();
                            key.append("udp").append("*").append(ipV4Header.getSrcAddr().toString()).append("*").append(ipV4Header.getDstAddr().toString());
                            if (!networkStaticsMap.containsKey(key.toString())) {
                                networkStaticsMap.put(key.toString(), new NetworkStatics(ipV4Packet.length(), "udp", ipV4Header.getSrcAddr().toString(), ipV4Header.getDstAddr().toString()));
                            }
                            else {
                                networkStaticsMap.get(key.toString()).update(ipV4Packet.length());
                            }
                        } else if (ipV4Header.getProtocol().equals(IpNumber.ICMPV4)) {
                            StringBuilder key = new StringBuilder();
                            key.append("icmpv4").append("*").append(ipV4Header.getSrcAddr().toString()).append("*").append(ipV4Header.getDstAddr().toString());
                            if (!networkStaticsMap.containsKey(key.toString())) {
                                networkStaticsMap.put(key.toString(), new NetworkStatics(ipV4Packet.length(), "icmpv4", ipV4Header.getSrcAddr().toString(), ipV4Header.getDstAddr().toString()));
                            }
                            else {
                                networkStaticsMap.get(key.toString()).update(ipV4Packet.length());
                            }

                        } else if (ipV4Header.getProtocol().equals(IpNumber.IGMP)) {
                            StringBuilder key = new StringBuilder();
                            key.append("igmp").append("*").append(ipV4Header.getSrcAddr().toString()).append("*").append(ipV4Header.getDstAddr().toString());
                            if (!networkStaticsMap.containsKey(key.toString())) {
                                networkStaticsMap.put(key.toString(), new NetworkStatics(ipV4Packet.length(), "igmp", ipV4Header.getSrcAddr().toString(), ipV4Header.getDstAddr().toString()));
                            }
                            else {
                                networkStaticsMap.get(key.toString()).update(ipV4Packet.length());
                            }
                        }
                    }
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

    public static void main(String[] args) throws PcapNativeException, InterFaceNotSelected, NotOpenException, PcapHandlerNotInitialized {
        Sniffer sniffer = new Sniffer();
        var nifs = sniffer.getAllNetworkInterfacesNames();
        sniffer.setCurrentInterfaceInSniff(nifs.get(5));
        sniffer.createPcapHandle();
        for (int i = 0; i < 15; i++) {
            sniffer.printSniffedPacket();
        }
    }
}
