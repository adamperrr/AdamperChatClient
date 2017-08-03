package msg;

public class Message {

  public Message(String message) {
    String[] msgParts = message.trim().split(_separator);

    if (msgParts.length >= 3) { 
	// There must be at least three parts (last one may be split unnecessarily)
      _message = message.trim();
	  
      setUsername(msgParts[1]);

      // Setting content - to avoid spliting content by separator
      int start = 2;
      String tempContent = "";
      for (int i = start; i < msgParts.length; i++) {
        if (i != start) {
          tempContent += _separator;
        }
        tempContent += msgParts[i];
      }
      setContent(tempContent);

      // Setting type
      if (msgParts[0].equals("Chat")) {
        setChatType();
      } else if (msgParts[0].equals("Connect")) {
        setConnectType();
      } else if (msgParts[0].equals("Disconnect")) {
        setDisconnectType();
      } else if (msgParts[0].equals("Done")) {
        setDoneType();
      } else {
        setErrorType();
      }
    } else {
      setErrorType();
    }
  }

  public Message(MsgType type, String username, String content) {
    setType(type);
    setUsername(username);
    setContent(content);
    buildMessage();
  }

  public String getMessage() {
    return _message;
  }

  public MsgType getType() {
    return _type;
  }

  public String getContent() {
    return _content;
  }

  public String getUsername() {
    return _username;
  }

  public String toString() {
    String result = "_type = " + _type.toString() + "\n"
            + "_username = " + _username + "\n"
            + "_content = " + _content + "\n"
            + "_message = " + _message + "\n"
            + "_separator = " + _separator + "\n";

    return result;
  }

  private void setType(MsgType type) {
    if (type == MsgType.Error || type == MsgType.None || type == null) {
      setErrorType();
    } else {
      _type = type;
    }
  }

  private void setChatType() {
    _type = MsgType.Chat;
  }

  private void setConnectType() {
    _type = MsgType.Connect;
  }

  private void setDisconnectType() {
    _type = MsgType.Disconnect;
  }

  private void setDoneType() {
    _type = MsgType.Done;
  }

  private void setErrorType() {
    _type = MsgType.Error;
    String temp = "Error";
    _message = temp;
    _content = temp;
    _username = temp;
  }

  private void setUsername(String username) {
    if (isBlank(username) || _type == MsgType.Error) { 
    // If string is blank or other function already set Error Type
      setErrorType();
    } else {
      _username = username.trim();
    }
  }

  private void setContent(String content) {
    if (isBlank(content) || _type == MsgType.Error) {
    // If string is blank or other function already set Error Type
      setErrorType();
    } else {
      _content = content.trim();
    }
  }

  private void buildMessage() {
    String[] tempMessage = new String[3];
    tempMessage[0] = _type.toString();
    tempMessage[1] = _username;
    tempMessage[2] = _content;

    _message = String.join(_separator, tempMessage);
  }

  private static boolean isBlank(String str) { // Function from: org.apache.commons.lang.StringUtils
    int strLen;
    if (str == null || (strLen = str.length()) == 0) {
      return true;
    }
    for (int i = 0; i < strLen; i++) {
      if ((Character.isWhitespace(str.charAt(i)) == false)) {
        return false;
      }
    }
    return true;
  }  
  
  private MsgType _type = MsgType.None;
  private String _username = "";
  private String _content = "";
  private String _message = "";

  private String _separator = "/";
}
