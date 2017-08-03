package adamperclient;

import java.util.*;
import java.io.*;
import java.net.*;

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
  }

  public void chatMsgIncomingReader(String userName, String msg) {
    mainTextArea.append(username + ": " + msg + "\n");
  }  
  
  public void connectIncomingReader(String userName) {
    mainTextArea.removeAll();
    addUser(userName);
  }

  public void disconnectIncomingReader(String userName) {
    removeUser(userName);
  }

  public void doneIncomingReader(String userName) {
    writeUsers();
    users.clear();
  }

  public void addUser(String userName) {
    users.add(userName);
  }

  public void removeUser(String userName) {
    mainTextArea.append(userName + " is now offline.\n");
  }

  public void writeUsers() {
    String[] tempList = new String[(users.size())];
    users.toArray(tempList);
    for (String token : tempList) {
      //users.append(token + "\n");
    }
  }

  public String getReaderLine() throws IOException {
    return reader.readLine();
  }

  public void ListenThread() {
    IncomingReader tempIR = new IncomingReader(this);
    Thread IncomingReader = new Thread(tempIR);
    IncomingReader.start();
  }

  public void disconnect() {
    try {
      mainTextArea.append("Disconnected.\n");
      sock.close();
    } catch (Exception ex) {
      mainTextArea.append("Failed to disconnect. \n");
    }
    isConnected = false;
//        tf_username.setEditable(true);

  }

  public void sendDisconnect() {
    String bye = (username + ": :Disconnect");
    try {
      writer.println(bye);
      writer.flush();
    } catch (Exception e) {
      mainTextArea.append("Could not send Disconnect message.\n");
    }
  }

  public void connect() {
    if (isConnected == false) {
//		username = tf_username.getText();
//		tf_username.setEditable(false);

      try {
        sock = new Socket(address, port);
        InputStreamReader streamreader = new InputStreamReader(sock.getInputStream());
        reader = new BufferedReader(streamreader);
        writer = new PrintWriter(sock.getOutputStream());
        writer.println(username + ":has connected.:Connect");
        writer.flush();
        isConnected = true;
      } catch (Exception ex) {
        mainTextArea.append("Cannot Connect! Try Again. \n");
//			tf_username.setEditable(true);
      }

      ListenThread();

    } else if (isConnected == true) {
      mainTextArea.append("You are already connected. \n");
    }
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();
    mainTextArea = new javax.swing.JTextArea();
    messageTextField = new javax.swing.JTextField();
    logoutBtn = new javax.swing.JButton();
    messageFieldLabel = new javax.swing.JLabel();
    sendBtn = new javax.swing.JButton();
    connectBtn = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("Chat - Adamper");
    setMinimumSize(new java.awt.Dimension(410, 320));
    setPreferredSize(new java.awt.Dimension(410, 320));

    mainTextArea.setEditable(false);
    mainTextArea.setColumns(20);
    mainTextArea.setRows(5);
    jScrollPane1.setViewportView(mainTextArea);

    logoutBtn.setText("Wyloguj");
    logoutBtn.setActionCommand("Wyloguj");
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

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane1)
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
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
        .addGap(10, 10, 10)
        .addComponent(messageFieldLabel)
        .addGap(5, 5, 5)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(sendBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
          .addComponent(messageTextField))
        .addGap(10, 10, 10))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void logoutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutBtnActionPerformed
    sendDisconnect();
    disconnect();
  }//GEN-LAST:event_logoutBtnActionPerformed

  private void sendBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendBtnActionPerformed
    String nothing = "";
    if ((messageTextField.getText()).equals(nothing)) {
      messageTextField.setText("");
      messageTextField.requestFocus();
    } else {
      try {
        writer.println(username + ":" + messageTextField.getText() + ":" + "Chat");
        writer.flush(); // flushes the buffer
      } catch (Exception ex) {
        mainTextArea.append("Message was not sent. \n");
      }
      messageTextField.setText("");
      messageTextField.requestFocus();
    }

    messageTextField.setText("");
    messageTextField.requestFocus();
  }//GEN-LAST:event_sendBtnActionPerformed

  private void connectBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectBtnActionPerformed
    connect();
  }//GEN-LAST:event_connectBtnActionPerformed

  private String username = "NazwaUsera";
  private String address = "localhost";

  private ArrayList<String> users = new ArrayList();
  private int port = 2222;
  private boolean isConnected = false;

  private Socket sock;
  private BufferedReader reader;
  private PrintWriter writer;

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton connectBtn;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JButton logoutBtn;
  private javax.swing.JTextArea mainTextArea;
  private javax.swing.JLabel messageFieldLabel;
  private javax.swing.JTextField messageTextField;
  private javax.swing.JButton sendBtn;
  // End of variables declaration//GEN-END:variables
}
