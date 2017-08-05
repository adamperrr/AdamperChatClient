package msg;

import java.text.*;
import java.util.*;
import java.util.regex.*;

public class Message {

  public Message(String message) throws Exception {
    String[] msgParts = message.trim().split(_separator);

    if (msgParts.length >= 5) {// There must be at least five parts (last one may be split unnecessarily)
      _message = message.trim();

      setUsername(msgParts[1]);
      setDate(msgParts[2]);
      setTime(msgParts[3]);

      // Setting content - to avoid spliting content by separator
      int start = 4;
      String tempContent = "";
      for (int i = start; i < msgParts.length; i++) {
        if (i != start) {
          tempContent += _separator;
        }
        tempContent += msgParts[i];
      }
      setContent(tempContent);
      
      setType(msgParts[0]); // Setting type - must be last because may set everything as error
    } else {
      throw new Exception("Wrong message format");
    }
  }

  public Message(MsgType type, String username, String content) throws Exception {
    setType(type);
    setUsername(username);
    setCurrentDateNTime();
    setContent(content);
    buildMessage();
  }

  public Message(MsgType type, String username, Date dateObj, String content) throws Exception {
    this(type, username, content);
    
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    String date = dateFormat.format(dateObj);
    String time = timeFormat.format(dateObj);

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

  public Date getDateObj() throws ParseException {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY); // Java doesn't like Poland :(
    Date date = format.parse(_date + " " + _time);
    return date;
  }  
  
  public String getDate() {
    return _date;
  }

  public String getTime() {
    return _time;
  }  
  
  public String toString() {
    String result = "_type = " + _type.toString() + "\n"
            + "_username = " + _username + "\n"
            + "_content = " + _content + "\n"
            + "_message = " + _message + "\n"
            + "_separator = " + _separator + "\n"
            + "_time = " + _time + "\n"
            + "_date = " + _date + "\n";

    return result;
  }

  private void setType(String type) throws Exception {
    if (type.equals("Chat")) {
      setChatType();
    } else if (type.equals("Connect")) {
      setConnectType();
    } else if (type.equals("Disconnect")) {
      setDisconnectType();
    } else if (type.equals("Done")) {
      setDoneType();
    } else {
      throw new Exception("Wrong type");
    }
  }
  
  private void setType(MsgType type) throws Exception {
    if (type == MsgType.None || type == null) {
      throw new Exception("Wrong type");
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

  private void setUsername(String username) throws Exception {
    if (isBlank(username)) {
      throw new Exception("Username is blank");
    } else {
      _username = username.trim();
    }
  }

  private void setDate(String date) throws Exception {
    Pattern pattern = Pattern.compile("^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$");
    Matcher matcher = pattern.matcher(date);
    boolean check = matcher.matches();
    
    if(check) {
      _date = date;
    } else {
      throw new Exception("Wrong date format");
    }
  }
  
  private void setTime(String time) throws Exception {
    Pattern pattern = Pattern.compile("^([0-1][0-9]|2[0-4])(:([0-5][0-9])){2}$");
    Matcher matcher = pattern.matcher(time);
    boolean check = matcher.matches();
    
    if(check) {
      _time = time;
    } else {
      throw new Exception("Wrong time format");
    }
  }
  
  private void setContent(String content) {
    _content = content.trim();
  }

  private void setCurrentDateNTime() {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    Date dateObj = new Date();

    _date = dateFormat.format(dateObj);
    _time = timeFormat.format(dateObj);
  }
  
  private void buildMessage() {
    String[] tempMessage = new String[5];
    tempMessage[0] = _type.toString();
    tempMessage[1] = _username;
    tempMessage[2] = _date;
    tempMessage[3] = _time;
    tempMessage[4] = _content;

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
  private String _time = "";
  private String _date = "";
  private String _separator = "/";
}
