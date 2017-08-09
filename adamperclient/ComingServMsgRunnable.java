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
        Message receivedMsg = new Message(stream);

        switch (receivedMsg.getType()) {
          case Chat:
            _mainFrame.chat_ComingServMsg(receivedMsg);
            break;
          case Connect:
            _mainFrame.connect_ComingServMsg(receivedMsg.getFrom());
            break;
          case Disconnect:
            _mainFrame.disconnect_ComingServMsg();
            break;
          case Done:
            _mainFrame.done_ComingServMsg(receivedMsg.getFrom());
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
