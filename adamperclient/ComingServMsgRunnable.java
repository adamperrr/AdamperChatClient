package adamperclient;

import msg.*;

public class ComingServMsgRunnable implements Runnable {

  public ComingServMsgRunnable(AdamperChat form) {
    _mainFrame = form;
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
    } catch (java.lang.NullPointerException e) {
      // This exception is thrown after every connect attempt when server is off.
    } catch (java.net.SocketException e) {
      // This exception is thrown after every disconnect of client.
    } catch (Exception e) {
      _mainFrame.appendError("run: " + e.toString());
    }
  }

  private AdamperChat _mainFrame;

}
