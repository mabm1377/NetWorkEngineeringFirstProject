import org.pcap4j.packet.Packet;

public class ApplicationStatics extends Statistics {
    String sourcePort = "";
    String destinationPort = "";

    ApplicationStatics(double length, String protocol, String sourcePort, String destinationPort) {
        super(length, protocol);
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
    }

    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }

    public void setSourcePort(String sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getDestinationPort() {
        return destinationPort;
    }

    public String getSourcePort() {
        return sourcePort;
    }

    @Override
    public String getStatistics() {
        return new StringBuilder().
                append("protocol:").append(protocol).
                append("     sourcePort:").append(sourcePort).
                append("     destinationPort:").append(destinationPort).
                append("     averageLength:").append(this.averageLength).
                append("     averageLength:").append(this.maxLength).
                append("     minLength:").append(this.minLength).
                append("     numberOfPacketSniffed:").append(this.numberOfPacketSniffed).
                toString();
    }
}
