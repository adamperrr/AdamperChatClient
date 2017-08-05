package adamperclient;

import msg.*;

public class ComingServMsgRunnable implements Runnable {

  public ComingServMsgRunnable(javax.swing.JFrame form) {
    _mainFrame = (AdamperChat) form;
  }

  @Override
  public void run() {
    String stream;

    try {
      while ((stream = _mainFrame.getReaderLine()) != null) {
        Message tempMsg = new Message(stream);

        switch (tempMsg.getType()) {
          case Chat:
            _mainFrame.chatMsgIncomingReader(tempMsg);
            break;
          case Connect:
            _mainFrame.connectIncomingReader(tempMsg.getUsername());
            break;
          case Disconnect:
            _mainFrame.disconnectIncomingReader(tempMsg.getUsername());
            break;
          case Done:
            _mainFrame.doneIncomingReader(tempMsg.getUsername());
            break;
        }
      }
    } catch (Exception e) {
      _mainFrame.appendError(e.toString());
    }
  }

  private AdamperChat _mainFrame;

}
