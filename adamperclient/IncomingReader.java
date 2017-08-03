package adamperclient;

public class IncomingReader implements Runnable {

  public IncomingReader(javax.swing.JFrame form) {
    _mainFrame = (AdamperChat) form;
  }

  @Override
  public void run() {
    String[] data;
    String stream, done = "Done", connect = "Connect", disconnect = "Disconnect", chat = "Chat";

    try {
      while ((stream = _mainFrame.getReaderLine()) != null) {
        data = stream.split(":");

        if (data[2].equals(chat)) {
          _mainFrame.chatMsgIncomingReader(data[0], data[1]);
        }
        else if (data[2].equals(connect)) {
          _mainFrame.connectIncomingReader(data[0]);
        }
        else if (data[2].equals(disconnect)) {
          _mainFrame.disconnectIncomingReader(data[0]);
        }
        else if (data[2].equals(done)) {
          _mainFrame.doneIncomingReader();
        }
      }
    }
	catch (Exception ex) {
		
    }
  }

  private AdamperChat _mainFrame;

}
