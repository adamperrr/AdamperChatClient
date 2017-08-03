package adamperclient;

public class IncomingReader implements Runnable {

  public IncomingReader(javax.swing.JFrame form) {
    mainFrame = (AdamperChat) form;
  }

  @Override
  public void run() {
    String[] data;
    String stream, done = "Done", connect = "Connect", disconnect = "Disconnect", chat = "Chat";

    try {
      while ((stream = mainFrame.getReaderLine()) != null) {
        data = stream.split(":");

        if (data[2].equals(chat)) {
          mainFrame.chatMsgIncomingReader(data[0], data[1]);
        }
        else if (data[2].equals(connect)) {
          mainFrame.connectIncomingReader(data[0]);
        }
        else if (data[2].equals(disconnect)) {
          mainFrame.disconnectIncomingReader(data[0]);
        }
        else if (data[2].equals(done)) {
          mainFrame.doneIncomingReader(data[0]);
        }
      }
    } catch (Exception ex) {
    }
  }

  private AdamperChat mainFrame;

}
