package adamperclient;

import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.text.*;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;

import msg.*;

public class AdamperChat extends javax.swing.JFrame {

  /**
   * @param args the command line arguments
   */
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

  /**
   * Creates new form AdamperClient
   */
  public AdamperChat() {
    initComponents();
    this.getRootPane().setDefaultButton(sendBtn); // Send as a main button
  }

  public void appendMsg(String inputText) {
    StyledDocument doc = mainputTextArea.getStyledDocument();
    inputText = inputText.trim() + "\n";
    try {
      doc.insertString(doc.getLength(), inputText, null);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public void appendUserMsg(String username, String inputText) {
    StyledDocument doc = mainputTextArea.getStyledDocument();
    username = username.trim() + ": ";
    inputText = inputText.trim() + "\n";

    SimpleAttributeSet keyWord = new SimpleAttributeSet();
    StyleConstants.setBold(keyWord, true);

    try {
      doc.insertString(doc.getLength(), username, keyWord);
      doc.insertString(doc.getLength(), inputText, null);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public void appendThisUserMsg(String inputText) {
    StyledDocument doc = mainputTextArea.getStyledDocument();
    String username = _username.trim() + ": ";
    inputText = inputText.trim() + "\n";

    SimpleAttributeSet keyWord = new SimpleAttributeSet();
    StyleConstants.setForeground(keyWord, Color.BLUE);
    StyleConstants.setBold(keyWord, true);

    try {
      doc.insertString(doc.getLength(), username, keyWord);
      doc.insertString(doc.getLength(), inputText, null);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public void connect() {
    if (_isConnected == false) {
      try {
        _sock = new Socket(_address, _port);
        InputStreamReader streamreader = new InputStreamReader(_sock.getInputStream());
        _reader = new BufferedReader(streamreader);
        _writer = new PrintWriter(_sock.getOutputStream());
        
        Message tempMsg = new Message(MsgType.Connect, _username, "połączył się.");
        _writer.println(tempMsg.getMessage());
        
        _writer.flush();
        _isConnected = true;
      } catch (Exception ex) {
        appendMsg("Błąd połączenia. Spróbuj ponownie... \n");
      }

      ListenThread();

    } else if (_isConnected == true) {
      appendMsg("Jesteś już połączony... \n");
    }
  }

  public void disconnect() {
    try {
      appendMsg("Rozłączono\n");
      _sock.close();
    } catch (Exception ex) {
      appendMsg("Rozłączenie nie powiodło się... \n");
    }
    _isConnected = false;
  }

  public void sendDisconnect() {
    Message tempMsg = new Message(MsgType.Disconnect, _username, "");
    String text = tempMsg.getMessage();
    try {
      _writer.println(text);
      _writer.flush();
    } catch (Exception e) {
      appendMsg("Błąd wysyłania wiadomości o rozłączeniu... \n");
    }
  }

  public void chatMsgIncomingReader(String username, String message) {
    if (username.equals(_username)) {
      appendThisUserMsg(message);
    } else {
      appendUserMsg(username, message);
    }
  }

  public void connectIncomingReader(String username) {
    mainputTextArea.removeAll();
    addUserIncomingReader(username);
  }

  public void disconnectIncomingReader(String username) {
    removeUserIncomingReader(username);
  }

  public void doneIncomingReader() {
    _users.clear();
  }

  public void addUserIncomingReader(String username) {
    _users.add(username);
  }

  public void removeUserIncomingReader(String username) {
    appendMsg(username + " jest teraz offline.\n");
  }

  public String getReaderLine() throws IOException {
    return _reader.readLine();
  }

  public void ListenThread() {
    IncomingReader tempIR = new IncomingReader(this);
    Thread IncomingReader = new Thread(tempIR);
    IncomingReader.start();
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    messageTextField = new javax.swing.JTextField();
    logoutBtn = new javax.swing.JButton();
    messageFieldLabel = new javax.swing.JLabel();
    sendBtn = new javax.swing.JButton();
    connectBtn = new javax.swing.JButton();
    jScrollPane2 = new javax.swing.JScrollPane();
    mainputTextArea = new javax.swing.JTextPane();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("Chat - Adamper");
    setMinimumSize(new java.awt.Dimension(410, 320));
    setPreferredSize(new java.awt.Dimension(410, 320));

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

    jScrollPane2.setViewportView(mainputTextArea);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane2)
          .addGroup(layout.createSequentialGroup()
            .addComponent(messageTextField)
            .addGap(10, 10, 10)
            .addComponent(sendBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(messageFieldLabel)
              .addGroup(layout.createSequentialGroup()
                .addComponent(connectBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logoutBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGap(0, 237, Short.MAX_VALUE)))
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
        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
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
    connect();
  }//GEN-LAST:event_connectBtnActionPerformed

  Random _randGen = new Random();
  private String _username = "NazwaUsera" + _randGen.nextInt();
  private String _address = "localhost";

  private ArrayList<String> _users = new ArrayList();
  private int _port = 2222;
  private boolean _isConnected = false;

  private Socket _sock;
  private BufferedReader _reader;
  private PrintWriter _writer;

  private Action accept = new AbstractAction("Accept") { // Afrer clicking enter action
    @Override
    public void actionPerformed(ActionEvent e) {
      sendBtnActionMethod();
    }
  };
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton connectBtn;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JButton logoutBtn;
  private javax.swing.JTextPane mainputTextArea;
  private javax.swing.JLabel messageFieldLabel;
  private javax.swing.JTextField messageTextField;
  private javax.swing.JButton sendBtn;
  // End of variables declaration//GEN-END:variables
}
