package adamperclient;

import msg.*;

public class IncomingReader implements Runnable {

  public IncomingReader(javax.swing.JFrame form) {
    _mainFrame = (AdamperChat) form;
  }

  @Override
  public void run() {
    String[] data;
    String stream;

    try {
      while ((stream = _mainFrame.getReaderLine()) != null) {
        Message tempMsg = new Message(stream);

        switch(tempMsg.getType()) {
          case Chat:
            _mainFrame.chatMsgIncomingReader(tempMsg.getUsername(), tempMsg.getContent());
          break;
          case Connect:
            _mainFrame.connectIncomingReader(tempMsg.getUsername());
          break;
          case Disconnect:
            _mainFrame.disconnectIncomingReader(tempMsg.getUsername());
          break;
          case Done:
            _mainFrame.doneIncomingReader();
          break;
        }
      }
    }
	catch (Exception ex) {
		
    }
  }

  private AdamperChat _mainFrame;

}
