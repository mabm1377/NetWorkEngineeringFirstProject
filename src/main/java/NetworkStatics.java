import org.pcap4j.packet.Packet;

public class NetworkStatics extends Statistics {
    String sourceIp = "";
    String destinationIp = "";
    NetworkStatics(double length, String protocol, String sourceIp, String destinationIp) {
        super(length, protocol);
        this.sourceIp = sourceIp;
        this.destinationIp =destinationIp;
    }

    public String getDestinationIp() {
        return destinationIp;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setDestinationIp(String destinationIp) {
        this.destinationIp = destinationIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    @Override
    public String getStatistics() {
        return new StringBuilder().
                append("protocol:").append(protocol).
                append("     sourceIp:").append(sourceIp).
                append("     destinationIp:").append(destinationIp).
                append("     averageLength:").append(this.averageLength).
                append("     averageLength:").append(this.maxLength).
                append("     minLength:").append(this.minLength).
                append("     numberOfPacketSniffed:").append(this.numberOfPacketSniffed).
                toString();
    }
}
