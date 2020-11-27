public class SnifferController {
    private SnifferController() {
        Sniffer sniffer = new Sniffer();
        SnifferView snifferView = new SnifferView("aa", null);
    }
    private static SnifferController instance = null;
    public static SnifferController getInstance() {
        if(instance== null){
            instance = new SnifferController();
        }
        return instance;
    }

}
