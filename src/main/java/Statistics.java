import org.pcap4j.packet.Packet;

abstract class Statistics {
    int maxLength = Integer.MIN_VALUE;
    int minLength = Integer.MAX_VALUE;
    double averageLength = 0;
    String protocol = "";
    int numberOfPacketSniffed = 0;

    Statistics(double length, String protocol) {
        this.maxLength = (int) length;
        this.minLength = (int) length;
        this.averageLength = length;
        this.protocol = protocol;
        this.numberOfPacketSniffed =1;
    }

    public void setAverageLength(double averageLength) {
        this.averageLength = averageLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public void setNumberOfPacketSniffed(int numberOfPacketSniffed) {
        this.numberOfPacketSniffed = numberOfPacketSniffed;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public double getAverageLength() {
        return averageLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public int getMinLength() {
        return minLength;
    }

    public String getProtocol() {
        return protocol;
    }

    public void  update(int length){
        this.averageLength = (this.averageLength*numberOfPacketSniffed+ length)/(++this.numberOfPacketSniffed);
        this.maxLength = Math.max(this.maxLength,length);
        this.minLength = Math.min(this.minLength,length);
    }

}
