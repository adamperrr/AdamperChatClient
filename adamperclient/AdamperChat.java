package adamperclient;

import java.util.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.text.*;

import java.util.logging.*;
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
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(AdamperChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(AdamperChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(AdamperChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(AdamperChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
    this.getRootPane().setDefaultButton(sendBtn); // Send as a main button
  }

  public void appendMsg(String inputText) {
    StyledDocument doc = mainTextArea.getStyledDocument();
    inputText = inputText.trim() + "\n";
        
    try {
      doc.insertString(doc.getLength(), inputText, null);
      scroolDown();
      playMsgSound();
    } catch (Exception e) {
      System.out.println(e);
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
      System.out.println(e);
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
      System.out.println(e);
    }
  }

  public void disconnect() {
    try {
      appendMsg("Rozłączono\n");
      _socket.close();
    } catch (Exception ex) {
      appendMsg("Rozłączenie nie powiodło się... \n");
    }
    _isConnected = false;
  }

  public void sendDisconnect() {
    try {
      Message tempMsg = new Message(MsgType.Disconnect, _username, "");
      _writer.println(tempMsg.getMessage());
      _writer.flush();
    } catch (Exception e) {
      appendMsg("Błąd wysyłania wiadomości o rozłączeniu... \n");
    }
  }

  public String getReaderLine() throws IOException {
    return _reader.readLine();
  }

  public void ListenThread() {
    IncomingReader tempIR = new IncomingReader(this);
    Thread IncomingReader = new Thread(tempIR);
    IncomingReader.start();
  }

  public void chatMsgIncomingReader(Message msg) {
    String username = msg.getUsername();
    String time = msg.getTime();
    String message = msg.getContent();
    
    if (username.equals(_username)) {
      appendThisUserMsg(message, time);
    } else {
      appendUserMsg(username, message, time);
    }
  }

  public void connectIncomingReader(String username) {
    addUserIncomingReader(username);
  }

  public void disconnectIncomingReader(String username) {
    removeUserIncomingReader(username);
  }

  public void doneIncomingReader(String username) {
    ; // Server operation just ended
  }

  public void addUserIncomingReader(String username) {
    boolean duplicate = false;
    for(String temp : _usersList) {
      if(temp.equals(username.trim())){
        duplicate = true;
        break;
      }  
    }
    
    if(!duplicate) {
      _usersList.add(username.trim());
    }
  }

  public void removeUserIncomingReader(String username) {
    _usersList.remove(username);
    
    appendMsg(username + " jest teraz offline.\n");
  }  
  
  private void scroolDown() {
    mainTextArea.setCaretPosition(mainTextArea.getDocument().getLength());
  }
  
  private void playMsgSound() {
    AudioInputStream stream;
    try {
      stream = AudioSystem.getAudioInputStream(getClass().getResource("/glassy-soft-knock.wav"));
      Clip clip = AudioSystem.getClip();
      clip.open(stream);
      clip.start();    
    } catch (UnsupportedAudioFileException ex) {
      Logger.getLogger(AdamperChat.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(AdamperChat.class.getName()).log(Level.SEVERE, null, ex);
    } catch (LineUnavailableException ex) {
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
    setTitle("Chat - Adamper");
    setMinimumSize(new java.awt.Dimension(410, 340));
    setPreferredSize(new java.awt.Dimension(410, 340));

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
    sendDisconnect();
    disconnect();
  }//GEN-LAST:event_logoutBtnActionPerformed

  private void sendBtnActionMethod() {
    String nothing = "";
    if ((messageTextField.getText()).equals(nothing)) {
      messageTextField.setText("");
      messageTextField.requestFocus();
    } else {
      try {
        Message tempMsg = new Message(MsgType.Chat, _username, messageTextField.getText());
        _writer.println(tempMsg.getMessage());
        _writer.flush(); // flushes the buffer
      } catch (Exception ex) {
        appendMsg("Wiadomość nie została wysłana... \n");
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
        _socket = new Socket(_address, _port);
        InputStreamReader streamreader = new InputStreamReader(_socket.getInputStream());
        _reader = new BufferedReader(streamreader);
        _writer = new PrintWriter(_socket.getOutputStream());

        Message tempMsg = new Message(MsgType.Connect, _username, "połączył się.");
        _writer.println(tempMsg.getMessage());

        _writer.flush();
        _isConnected = true;
      } catch (Exception ex) {
        appendMsg("Błąd połączenia. Spróbuj ponownie... \n");
      }

      ListenThread();

    } else if (_isConnected) {
      appendMsg("Jesteś już połączony... \n");
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
  
  private String _address = "localhost";  
  private int _port = 2222;
  
  Random _randGen = new Random();
  private String _username = "NazwaUsera" + _randGen.nextInt(9999999);
  
  private ArrayList<String> _usersList = new ArrayList();
  
  private boolean _isConnected = false;
  private Socket _socket;
  private BufferedReader _reader;
  private PrintWriter _writer;
  
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
