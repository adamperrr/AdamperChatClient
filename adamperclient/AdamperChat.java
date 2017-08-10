package adamperclient;

import java.util.*;
import java.util.logging.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.sound.sampled.*;

import msg.*;

public class AdamperChat extends javax.swing.JFrame {

  public static void main(String args[]) {
    /* Set the Nimbus look and feel */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException e) {
      java.util.logging.Logger.getLogger(AdamperChat.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
    } catch (InstantiationException e) {
      java.util.logging.Logger.getLogger(AdamperChat.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
    } catch (IllegalAccessException e) {
      java.util.logging.Logger.getLogger(AdamperChat.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
    } catch (javax.swing.UnsupportedLookAndFeelException e) {
      java.util.logging.Logger.getLogger(AdamperChat.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
    }
    //</editor-fold>
    //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new AdamperChat().setVisible(true);
      }
    });
  }

  public AdamperChat() {  
    initComponents();
    
    setTitle(programTitle);
    getRootPane().setDefaultButton(sendBtn); // Send as a main button
    setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/adamperclient/icon.png")));
    loadProperties();

    Frame login = new Login(this);
    login.setVisible(true);
    
    _isConnected = false;
    logoutBtn.setEnabled(_isConnected);
    displayOnlineUsersBtn.setEnabled(_isConnected);
    sendBtn.setEnabled(_isConnected);
    messageTextField.setEnabled(_isConnected);
    connectBtn.setEnabled(!_isConnected);    
  }

  public void appendError(String inputText) {
    StyledDocument doc = mainTextArea.getStyledDocument();
    inputText = inputText.trim() + "\n";

    SimpleAttributeSet keyWord = new SimpleAttributeSet();
    StyleConstants.setForeground(keyWord, Color.RED);
    StyleConstants.setBold(keyWord, true);

    try {
      doc.insertString(doc.getLength(), inputText, keyWord);
      scroolDown();
    } catch (Exception e) {
      appendMsg("appendError: " + e.toString());
    }
  }

  public void appendMsg(String inputText) {
    StyledDocument doc = mainTextArea.getStyledDocument();
    inputText = inputText.trim() + "\n";

    try {
      doc.insertString(doc.getLength(), inputText, null);
      scroolDown();
      playMsgSound();
    } catch (Exception e) {
      appendError("appendMsg: " + e.toString());
    }
  }

  public void appendUserMsg(String username, String inputText, String time) {
    StyledDocument doc = mainTextArea.getStyledDocument();
    username = username.trim() + ": ";
    inputText = inputText.trim() + "\n";

    SimpleAttributeSet timeStyle = new SimpleAttributeSet();
    StyleConstants.setForeground(timeStyle, Color.GRAY);
    StyleConstants.setItalic(timeStyle, true);

    SimpleAttributeSet keyWord = new SimpleAttributeSet();
    StyleConstants.setBold(keyWord, true);

    try {
      doc.insertString(doc.getLength(), time + " ", timeStyle);
      doc.insertString(doc.getLength(), username, keyWord);
      doc.insertString(doc.getLength(), inputText, null);
      scroolDown();
      playMsgSound();
    } catch (Exception e) {
      appendError("appendUserMsg: " + e.toString());
    }
  }

  public void appendThisUserMsg(String inputText, String time) {
    StyledDocument doc = mainTextArea.getStyledDocument();
    String username = _username.trim() + ": ";
    inputText = inputText.trim() + "\n";

    SimpleAttributeSet keyWord = new SimpleAttributeSet();
    StyleConstants.setForeground(keyWord, Color.BLUE);
    StyleConstants.setBold(keyWord, true);

    SimpleAttributeSet timeStyle = new SimpleAttributeSet();
    StyleConstants.setForeground(timeStyle, Color.GRAY);
    StyleConstants.setItalic(timeStyle, true);

    try {
      doc.insertString(doc.getLength(), time + " ", timeStyle);
      doc.insertString(doc.getLength(), username, keyWord);
      doc.insertString(doc.getLength(), inputText, null);
      scroolDown();
    } catch (Exception e) {
      appendError("appendThisUserMsg: " + e.toString());
    }
  }

  public void appendPrivMsg(String inputText, String time, String to, String from) {
    StyledDocument doc = mainTextArea.getStyledDocument();
    String tempFrom = from.trim() + ": ";
    inputText = inputText.trim() + "\n";

    SimpleAttributeSet keyWord = new SimpleAttributeSet();
    StyleConstants.setForeground(keyWord, new Color(147, 35, 114));
    if (to.equals(_username)) {
      StyleConstants.setBold(keyWord, true);
      playMsgSound();
    }

    SimpleAttributeSet timeStyle = new SimpleAttributeSet();
    StyleConstants.setForeground(timeStyle, Color.GRAY);
    StyleConstants.setItalic(timeStyle, true);

    try {
      doc.insertString(doc.getLength(), time + " ", timeStyle);
      doc.insertString(doc.getLength(), tempFrom, keyWord);
      doc.insertString(doc.getLength(), inputText, keyWord);
      scroolDown();
    } catch (Exception e) {
      appendError("appendThisUserMsg: " + e.toString());
    }
  }

  public void disconnect() {
    try {
      _socket.close();
      appendMsg("Rozłączono");
      setTitle(programTitle);
    } catch (Exception e) {
      appendError("Rozłączenie nie powiodło się...");
    }
    _isConnected = false;

    logoutBtn.setEnabled(_isConnected);
    displayOnlineUsersBtn.setEnabled(_isConnected);
    sendBtn.setEnabled(_isConnected);
    messageTextField.setEnabled(_isConnected);
    connectBtn.setEnabled(!_isConnected);
  }

  public void sendDisconnectMsg() {
    try {
      Message tempMsg = new Message(MsgType.Disconnect, _username, "DisconnectMsgFromUser");
      _writer.println(tempMsg.getMessage());
      _writer.flush();
    } catch (Exception e) {
      appendError("Błąd wysyłania wiadomości o rozłączeniu...");
    }
  }

  public String getReaderLine() throws IOException {
    return _reader.readLine();
  }

  public void startListenThread() {
    ComingServMsgRunnable tempIR = new ComingServMsgRunnable(this);
    Thread IncomingReader = new Thread(tempIR);
    IncomingReader.start();
  }

  public synchronized void chat_ComingServMsg(Message msg) {
    String from = msg.getFrom();
    String time = msg.getTime();
    String message = msg.getContent();
    String to = msg.getTo();

    if (!(to.equals("all"))) {
      appendPrivMsg(message, time, to, from);
    } else if (from.equals(_username)) {
      appendThisUserMsg(message, time);
    } else {
      appendUserMsg(from, message, time);
    }
  }

  public synchronized void connect_ComingServMsg(String username) {
    // Connect message informs which users are now online and update local users list
    startUsersUpdate();
    addUser_ComingServMsg(username);
  }

  public synchronized void disconnect_ComingServMsg() {
    appendMsg("Rozłączanie połączenia zlecone przez serwer w toku...");
    disconnect();
  }  
  
  public synchronized void startUsersUpdate() {
    if (!updatingUsersList) {
      _usersList = new ArrayList();
      updatingUsersList = true;
    }
  }

  public synchronized void stopUsersUpdate() {
    if (updatingUsersList) {
      updatingUsersList = false;
    }
  }

  public synchronized void done_ComingServMsg(String username) {
    stopUsersUpdate();
  }

  public synchronized void addUser_ComingServMsg(String username) {
    _usersList.add(username.trim());
  }

  public void setUsername(String username) {
    _username = username;
  }
  
  public String getHost() {
    return _host;
  }
  
  public int getPort() {
    return _port;
  }  
  
  private void scroolDown() {
    mainTextArea.setCaretPosition(mainTextArea.getDocument().getLength());
  }
  
  private void loadProperties() {
    Properties prop = new Properties();
    InputStream input = null;

    try {
      input = this.getClass().getResourceAsStream("/adamperclient/config.properties");

      // load a properties file
      prop.load(input);

      // get the property value and print it out
      _host = prop.getProperty("host");
      _port = Integer.parseInt(prop.getProperty("port"));
      _soundOn = prop.getProperty("sound").equals("on");
      
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  private void playMsgSound() {
    if(!_soundOn) {
      return;
    }
    
    try {
      _audioStream = AudioSystem.getAudioInputStream(AdamperChat.class.getResource("/adamperclient/glassy-soft-knock.wav"));
      _audioClip = AudioSystem.getClip();
      _audioClip.open(_audioStream);
      _audioClip.start();
    } catch (LineUnavailableException ex) {
      Logger.getLogger(AdamperChat.class.getName()).log(Level.SEVERE, null, ex);
    } catch (UnsupportedAudioFileException ex) {
      Logger.getLogger(AdamperChat.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(AdamperChat.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
    
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    messageTextField = new javax.swing.JTextField();
    logoutBtn = new javax.swing.JButton();
    messageFieldLabel = new javax.swing.JLabel();
    sendBtn = new javax.swing.JButton();
    connectBtn = new javax.swing.JButton();
    jScrollPane2 = new javax.swing.JScrollPane();
    mainTextArea = new javax.swing.JTextPane();
    displayOnlineUsersBtn = new javax.swing.JButton();
    clearScreenBtn = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("AdamperChat");
    setAutoRequestFocus(false);
    setEnabled(false);
    setIconImages(null);
    setMinimumSize(new java.awt.Dimension(410, 340));
    setPreferredSize(new java.awt.Dimension(410, 340));
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
    });

    logoutBtn.setText("Rozłącz");
    logoutBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        logoutBtnActionPerformed(evt);
      }
    });

    messageFieldLabel.setText("Treść wiadomości");

    sendBtn.setText("Wyślij");
    sendBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        sendBtnActionPerformed(evt);
      }
    });

    connectBtn.setText("Połącz");
    connectBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        connectBtnActionPerformed(evt);
      }
    });

    mainTextArea.setEditable(false);
    jScrollPane2.setViewportView(mainTextArea);

    displayOnlineUsersBtn.setText("Wyświetl użytkowników online");
    displayOnlineUsersBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        displayOnlineUsersBtnActionPerformed(evt);
      }
    });

    clearScreenBtn.setText("Wyczyść ekran");
    clearScreenBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        clearScreenBtnActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(messageFieldLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jScrollPane2)
          .addGroup(layout.createSequentialGroup()
            .addComponent(messageTextField)
            .addGap(10, 10, 10)
            .addComponent(sendBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(connectBtn)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(logoutBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, Short.MAX_VALUE))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGap(0, 93, Short.MAX_VALUE)
            .addComponent(clearScreenBtn)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(displayOnlineUsersBtn)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGap(10, 10, 10)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(logoutBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(connectBtn))
        .addGap(10, 10, 10)
        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
        .addGap(10, 10, 10)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(displayOnlineUsersBtn)
          .addComponent(clearScreenBtn))
        .addGap(10, 10, 10)
        .addComponent(messageFieldLabel)
        .addGap(5, 5, 5)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(sendBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
          .addComponent(messageTextField))
        .addGap(10, 10, 10))
    );

    pack();
    setLocationRelativeTo(null);
  }// </editor-fold>//GEN-END:initComponents

  private void logoutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutBtnActionPerformed
    sendDisconnectMsg();
    disconnect();
  }//GEN-LAST:event_logoutBtnActionPerformed

  private void sendBtnActionMethod() {
    if ((messageTextField.getText()).equals("")) {
      messageTextField.setText("");
      messageTextField.requestFocus();
    } else {
      try {
        Message tempMsg = new Message(MsgType.Chat, _username, messageTextField.getText());
        _writer.println(tempMsg.getMessage());
        _writer.flush(); // flushes the buffer
      } catch (Exception e) {
        appendError("Wiadomość nie została wysłana...");
      }
      messageTextField.setText("");
      messageTextField.requestFocus();
    }

    messageTextField.setText("");
    messageTextField.requestFocus();
  }

  private void sendBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendBtnActionPerformed
    sendBtnActionMethod();
  }//GEN-LAST:event_sendBtnActionPerformed

  private void connectBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectBtnActionPerformed
    if (!_isConnected) {
      try {
        _socket = new Socket(_host, _port);
        InputStreamReader streamreader = new InputStreamReader(_socket.getInputStream());
        _reader = new BufferedReader(streamreader);
        _writer = new PrintWriter(_socket.getOutputStream());

        Message tempMsg = new Message(MsgType.Connect, _username, "połączył się.");
        _writer.println(tempMsg.getMessage());

        _writer.flush();

        _isConnected = true;
        logoutBtn.setEnabled(_isConnected);
        displayOnlineUsersBtn.setEnabled(_isConnected);
        sendBtn.setEnabled(_isConnected);
        messageTextField.setEnabled(_isConnected);
        connectBtn.setEnabled(!_isConnected);
        setTitle(programTitle + ": " + _username);
      } catch (Exception e) {
        appendError("Błąd połączenia. Spróbuj ponownie...");
      }

      startListenThread();

    } else if (_isConnected) {
      appendMsg("Jesteś już połączony...");
    }
  }//GEN-LAST:event_connectBtnActionPerformed

  private void clearScreenBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearScreenBtnActionPerformed
    mainTextArea.setText("");
  }//GEN-LAST:event_clearScreenBtnActionPerformed

  private void displayOnlineUsersBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayOnlineUsersBtnActionPerformed
    appendMsg("\n Użytkownicy online :");
    for (String currentUser : _usersList) {
      appendMsg("\t" + currentUser);
    }
  }//GEN-LAST:event_displayOnlineUsersBtnActionPerformed

  private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    sendDisconnectMsg();
    disconnect();
  }//GEN-LAST:event_formWindowClosing
  
  // Loaded properties   
  private String _host = "localhost"; // Default value
  private int _port = 1995; // Default value
  private boolean _soundOn = false;
    
  private boolean updatingUsersList = false; // While updating users list
  
  private String _username = "username" + (new Random()).nextInt(999);
  private ArrayList<String> _usersList = new ArrayList();
  
  private boolean _isConnected = false;
  private Socket _socket;
  private BufferedReader _reader;
  private PrintWriter _writer;

  AudioInputStream _audioStream;
  Clip _audioClip;
  private String programTitle = "AdamperChat";
  private Action accept = new AbstractAction("Accept") { // Afrer clicking enter action
    @Override
    public void actionPerformed(ActionEvent e) {
      sendBtnActionMethod();
    }
  };
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton clearScreenBtn;
  private javax.swing.JButton connectBtn;
  private javax.swing.JButton displayOnlineUsersBtn;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JButton logoutBtn;
  private javax.swing.JTextPane mainTextArea;
  private javax.swing.JLabel messageFieldLabel;
  private javax.swing.JTextField messageTextField;
  private javax.swing.JButton sendBtn;
  // End of variables declaration//GEN-END:variables

}
