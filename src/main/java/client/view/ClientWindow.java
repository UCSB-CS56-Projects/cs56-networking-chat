package client.view;

import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.*;
import javax.swing.JComboBox;
import java.awt.GraphicsEnvironment;
import javax.swing.*;
import java.awt.Dimension;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;

//Import random library
import java.util.Random;

import client.controller.ClientController;
import client.model.Client;
import client.model.Contact;

/**
 * Represents a JFrame window which has components that are needed for chatting
 * @author Peng Wang, Andro Stotts, Bryce Filler, Max Hinson
 * @version 0.4
 */
public class ClientWindow extends JFrame{

    private static final long serialVersionUID = 1L;
    private JTextField tfInput;
    private JTextArea taOutput;
    private JTextField tfUsername;
    private JTextField tfNickName;
    private JTextField tfServerIp;
    private JTextField tfChatRoomParticipants;
    private JPasswordField pfPassword;
    private JScrollPane spScrollPane;
    private JList<String> listContacts;
    private DefaultListModel<String> model;
    private String[] contactList;
    private JLabel lblContact;
    private JLabel lblUserName;
    private JLabel lblNickName;
    private JLabel lblPassword;
    private JLabel lblServerIp;
    private JLabel lblLoginError;
    private JLabel onlineCountNum;
    private JLabel onlineCountText;
    private JLabel userInRoom;
    private JCheckBox soundbox;
    private JButton btConnect;
    private JButton btNickname;
    private JButton btChangeFont;
    private JButton onlineCountUpdate;
    private JButton btAccept;
    private static ClientWindow window;
    private ClientController controller;
    private String name;
    private String nickname;
    private String ip;
    private String password;
    private JFrame nicknameWindow;
    private JFrame registrant;
    //    private JFrame editWindow;
    private int roomUsers;
    private int count;
    
 
    //Pre-determined color to randomly use
    java.awt.Color redColor = new java.awt.Color(255,000,000);
    java.awt.Color greenColor = new java.awt.Color(000,255,000);
    java.awt.Color blueColor = new java.awt.Color(000,000,255);
    String names[] = {"Black", "Blue", "Cyan",  "Gray", "Green", 
		       "Orange", "Pink", "Red", "White", "Yellow"};
    
    Color colors[] = {Color.BLACK, Color.BLUE, Color.CYAN, Color.GRAY, Color.GREEN, Color.ORANGE, Color.PINK, Color.RED, Color.WHITE, Color.YELLOW};
    

    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    Font fonts[] = ge.getAllFonts();
    
   
    /**
     * Default constructor
     */
    private ClientWindow(){
	controller = ClientController.getController();
	tfInput = new JTextField();
	taOutput = new JTextArea();
	spScrollPane = new JScrollPane(taOutput);
	taOutput.setLineWrap(true);
	taOutput.setWrapStyleWord(true);
	taOutput.setForeground(Color.ORANGE);
	taOutput.setFont(new Font("default", Font.PLAIN, 12));
	taOutput.setEditable(false);
	model = new DefaultListModel<String>();
	listContacts = new JList<String>();
	listContacts.setModel(model);
	lblContact = new JLabel("Contacts");
	lblUserName = new JLabel("Username: ");
	lblNickName = new JLabel("New Nickname:");
	lblServerIp = new JLabel("Server IP: ");
	lblPassword = new JLabel("Password: ");
	onlineCountText = new JLabel("Online Count: ");
	onlineCountNum = new JLabel("ERROR");
	onlineCountUpdate = new JButton("Refresh");
	onlineCountNum.setText("");
	lblLoginError = new JLabel("");
	tfUsername = new JTextField(20);
	tfNickName = new JTextField(20);
	tfServerIp = new JTextField(20);
	tfChatRoomParticipants = new JTextField(20);
	pfPassword = new JPasswordField(20);
	btAccept = new JButton("Accept");

	//default connection
	tfServerIp.setText("127.0.0.1");
	soundbox = new JCheckBox("Play Sounds");
	btConnect = new JButton("Connect to Server");
	btNickname = new JButton("Accept");
	name = "";
	password = "";
	ip = "";
    }
	
    /**
     * get the instance of the ClientWindow object
     * @return instance of the ClientWindow 
     */
    public static ClientWindow getWindow(){
	if(window == null){
	    window = new ClientWindow();
	}
	return window;
    }
	
    /**
     * Creates the login window
     */
    public void launchLoginWindow(){
       	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.setLocation(100, 100);
	this.setSize(300, 270);
	this.setTitle("Chatting Client");
	this.setLayout(new FlowLayout());

	this.getContentPane().add(lblUserName);
	this.getContentPane().add(tfUsername);
	this.getContentPane().add(lblPassword);
	this.getContentPane().add(pfPassword);
	this.getContentPane().add(lblServerIp);
	this.getContentPane().add(tfServerIp);
	this.getContentPane().add(lblLoginError);
	this.getContentPane().add(btConnect);
	LoginListener loginListener = new LoginListener();
	btConnect.addActionListener(loginListener);
	tfUsername.addActionListener(loginListener);
	pfPassword.addActionListener(loginListener);
	tfServerIp.addActionListener(loginListener);
	btConnect.setSelected(true);
	this.setVisible(true);
    }
	
    /**
     * Creates the chat window
     * It will be invoked from the LoginListener
     */
    public void launchChatWindow(){
	//add window listener for closing window
	//which tells the server to broadcast the message
	//that this client is going offline
	this.addWindowListener(new WindowAdapter(){
		public void windowClosing(WindowEvent we){
		    controller.sendMsg2Server(name + "&Broadcast:1003");
		    System.exit(0);
		}
	    });
	
	this.getContentPane().removeAll();
	this.setLayout(new BorderLayout());
	this.setSize(1000, 500);
	this.setTitle("Chatting Client-" + name);
	JPanel leftPanel = new JPanel();
	JPanel rightPanel = new JPanel();
	JPanel menuPanel = new JPanel();

	menuPanel.add(onlineCountUpdate, BorderLayout.WEST);
	menuPanel.add(onlineCountText, BorderLayout.WEST);
	menuPanel.add(onlineCountNum, BorderLayout.WEST);
	onlineCountUpdate.addActionListener(new OnlineCountUpdateButtonListener());	
	menuPanel.setBorder(BorderFactory.createLineBorder(Color.black));
	menuPanel.setLayout(new FlowLayout());
	menuPanel.add(soundbox, BorderLayout.EAST);
	

	

	
	FontCellRenderer fontCellRender = new FontCellRenderer();
	JComboBox fontList = new JComboBox(fonts);
	fontList.setRenderer(fontCellRender);
	fontList.setPreferredSize(new Dimension(150,25));
	menuPanel.add(fontList,BorderLayout.EAST);

	//*************here i start to add stuff about change color***************
	ColorCellRenderer colorCellRender = new ColorCellRenderer();
	JComboBox colorList = new JComboBox(colors);
	colorList.setRenderer(colorCellRender);
	colorList.setPreferredSize(new Dimension(100,25));
	menuPanel.add(colorList,BorderLayout.EAST);
	

	JButton deleteUser = new JButton("Delete User");
	menuPanel.add(deleteUser,BorderLayout.EAST);
	deleteUser.addActionListener(new DeleteUserButtonListener());

	JButton logout = new JButton("Logout");
	menuPanel.add(logout,BorderLayout.EAST);
	logout.addActionListener(new LogoutListener());
	
	
	rightPanel.setBorder(BorderFactory.createLineBorder(Color.black));
	rightPanel.setLayout(new BorderLayout());
	rightPanel.add(spScrollPane, BorderLayout.CENTER);
	rightPanel.add(tfInput, BorderLayout.SOUTH);
		
	leftPanel.setBorder(BorderFactory.createLineBorder(Color.black));
	leftPanel.setLayout(new BorderLayout());
	leftPanel.add(lblContact, BorderLayout.NORTH);
	leftPanel.add(listContacts, BorderLayout.CENTER);

	JButton nickName = new JButton("Change nickname");
	JButton chatRoom = new JButton("Chat Room");

	//new
	JButton editRoom = new JButton("Edit my room");
        
	menuPanel.add(editRoom, BorderLayout.EAST);
	
	leftPanel.add(nickName, BorderLayout.SOUTH);
	leftPanel.add(chatRoom, BorderLayout.NORTH);
	
		
	listContacts.setSelectedIndex(0);
	onlineCountUpdate.addActionListener(new OnlineCountUpdateButtonListener());
	onlineCountUpdate.doClick();
		
	this.getContentPane().add(leftPanel, BorderLayout.WEST);
	this.getContentPane().add(rightPanel, BorderLayout.CENTER);
	this.getContentPane().add(menuPanel, BorderLayout.NORTH);
	this.repaint();
	tfInput.addActionListener(new InputListener());
	chatRoom.addActionListener(new RegisterChatRoomListener());
	nickName.addActionListener(new LaunchChangeWindowListener());
	//editRoom.addActionListener(new ChatroomListener());
	fontList.addActionListener(new FontListener());
	colorList.addActionListener(new ColorListener());
	soundbox.addItemListener(new CheckListener());
	soundbox.setSelected(true);
	Timer timer = new Timer();
	timer.schedule(new timerThread(),0,2000);
		
    }

    
    /**
     * Creates the name change window
     */
    public void launchChangeWindow(){
	//creates the window that appears
	//upon requesting to change user name
	nicknameWindow = new JFrame();
	nicknameWindow.setLocation(100, 100);
	nicknameWindow.setSize(300, 125);
	nicknameWindow.setDefaultCloseOperation(EXIT_ON_CLOSE);
	nicknameWindow.setTitle("Set Nickname");
	nicknameWindow.setLayout(new FlowLayout());
	nicknameWindow.getContentPane().add(lblNickName);
	nicknameWindow.getContentPane().add(tfNickName);
	nicknameWindow.getContentPane().add(btNickname);
	nicknameWindow.setVisible(true);
	btNickname.addActionListener(new ChangeNickNameListener());
	tfNickName.addKeyListener(new ChangeNickNameListener());

    }

	/**Method to launch the window to register a new ChatRoom. Creates a popup window that prompts for the nicknames of the
	* participants who are part of the ChatRoom
	* @author jleeong
	* @version F16
	*/
	public void launchRegisterChatRoomWindow(){
		registrant = new JFrame();
		registrant.setLocation(100, 100);
		registrant.setSize(300, 125);
		registrant.setDefaultCloseOperation(EXIT_ON_CLOSE);
		registrant.setTitle("Set Nickname");
		registrant.setLayout(new FlowLayout());
		registrant.getContentPane().add(new JLabel("Enter participant nicknames. Separate nicknames with commas, no spaces."));
		registrant.getContentPane().add(tfChatRoomParticipants);
		tfChatRoomParticipants.setText("user1,user2,user3...");
		registrant.getContentPane().add(btAccept);
		registrant.setVisible(true);
		btAccept.addActionListener(new RegistrantListener());
		tfChatRoomParticipants.addKeyListener(new RegistrantListener());
	}
    /**
     * Get the message display component
     * @return the text area which displays the message
     */
    public JTextArea getTaOutput(){
	return taOutput;
    }      


    /**
     * Get the contact list
     * @return the list component of the client window
     */
    public DefaultListModel getContactList(){
    	return model;
    }
    
    /**
     * Handles actions when buttons are clicked
     * @author Bryce Filler and Max Hinson
     * @version 0.4
     */
    class LaunchChangeWindowListener implements ActionListener{
    	private ClientWindow window2 = ClientWindow.getWindow();
	public void actionPerformed(ActionEvent e){
	    
	    window2.launchChangeWindow();

	}
    }

    class RegisterChatRoomListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
		ClientWindow.getWindow().launchRegisterChatRoomWindow();
        }
    }

    /**
     * Inner class that formats the list output by JComboBox
     * Only prints name of font that exists in the list instead of entire Font object	
     */

    //helper for Fontlistener
   class FontCellRenderer extends JLabel implements ListCellRenderer<Object> {
     public FontCellRenderer() {
         setOpaque(true);
     }

     public Component getListCellRendererComponent(JList<?> list,
                                                   Object value,
                                                   int index,
                                                   boolean isSelected,
                                                   boolean cellHasFocus) {
	 Font thisfont = (Font) value;
         setText(thisfont.getName());

         Color background;
         Color foreground;

         // check if this cell represents the current DnD drop location
         JList.DropLocation dropLocation = list.getDropLocation();
         if (dropLocation != null
                 && !dropLocation.isInsert()
                 && dropLocation.getIndex() == index) {

             background = Color.BLUE;
             foreground = Color.WHITE;

         // check if this cell is selected
         } else if (isSelected) {
             background = Color.GRAY;
             foreground = Color.WHITE;

         // unselected, and not the DnD drop location
         } else {
             background = Color.WHITE;
             foreground = Color.BLACK;
         };

         setBackground(background);
         setForeground(foreground);

         return this;
     }
 } 
   /**
    * Listener class that registers when the changeFont JComboBox is pressed
    * @author Xinyuan Zhang and jleeong
   */
   
    
    class FontListener implements ActionListener{
	public void actionPerformed(ActionEvent e){
	    JComboBox ex = (JComboBox)e.getSource();
	    Font getFont = (Font)ex.getSelectedItem();
	    Font newFont = getFont.deriveFont(12F);
	    //newFont.setForeground(Color.Grey);
	    taOutput.setFont(newFont);
	}
    }




    //******************here I start colorlistener*****************************
  class ColorListener implements ActionListener{
	public void actionPerformed(ActionEvent e){
	    JComboBox ex = (JComboBox)e.getSource();
	    Color newColor = (Color)ex.getSelectedItem();
	    taOutput.setForeground(newColor);
	}
  }
   
    
    class ColorCellRenderer extends JLabel implements ListCellRenderer<Object> {
	 
	public ColorCellRenderer() {
	    setOpaque(true);
	}
	boolean b = false;  
    public Component getListCellRendererComponent(JList<?> list,
                                                   Object value,
                                                   int index,
                                                   boolean isSelected,
                                                   boolean cellHasFocus) {
	Color thiscolor = (Color) value;

	String names[] = {"Black", "Blue", "Cyan", "Dark Gray", "Gray", "Green", "Light Gray","Magenta", "Orange", "Pink", "Red", "White", "Yellow"};
	Color colors[] = {Color.black, Color.blue, Color.cyan, Color.darkGray, Color.gray, Color.green, Color.lightGray,Color.magenta, Color.orange, Color.pink, Color.red, Color.white, Color.yellow};
	  for(int i = 0;i<13;i++){
	      if (thiscolor.equals(colors[i]))
		  setText(names[i]);
	      }

         Color background;
         Color foreground;

         // check if this cell represents the current DnD drop location
         JList.DropLocation dropLocation = list.getDropLocation();
         if (dropLocation != null
                 && !dropLocation.isInsert()
                 && dropLocation.getIndex() == index) {

             background = Color.BLUE;
             foreground = Color.WHITE;

         // check if this cell is selected
         } else if (isSelected) {
             background = Color.gray;
             foreground = Color.WHITE;

         // unselected, and not the DnD drop location
         } else {
             background = Color.WHITE;
             foreground = Color.BLACK;
         };

         setBackground(background);
         setForeground(foreground);

         return this;
     }
 }

/**
     * Handles actions when Delete user button is clicked
     * @author Winfred Huang, Arturo Milanes, and jleeong
     * @version F16
     */
    class DeleteUserButtonListener implements ActionListener{
	public void actionPerformed(ActionEvent e){
		int index = listContacts.getSelectedIndex();
	    if(index == 0){
		controller.displayMsg("Can't delete broadcast.\n");
	    }
	    else if(index > 0){
		controller.displayMsg("You tried to delete " + listContacts.getSelectedValue() +".\n");
		controller.sendMsg2Server("&DELETE:"+listContacts.getSelectedValue());
		model.remove(index);
		controller.displayMsg("Successful");
	    }
	    else{
		controller.displayMsg("No contact to delete. Please select one.\n");
	    }
	}
    }
	    /**
	     * Handles actions when Refresh button is pressed
	     * @author Winfred Huang and Arturo Milanes
	     */
	    class  OnlineCountUpdateButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
		    int count = 1;
		    ListModel<String> list = listContacts.getModel();
		    for(int i = 0; i < list.getSize(); i++){
			if(list.getElementAt(i).endsWith("(Online)")){
			    count++;
			}
		    }
		    onlineCountNum.setText(String.valueOf(count));
		    //revised
		    // menuPanel.add(logout,BorderLayout.EAST);
		    // launchChatWindow lCW = new lauchChatWindow();
		    
		}
		
	    }
	    
	    /**
	     * Handles actions when login button is pressed or when enter is pressed on LoginWindow
	     * @author Peng Wang with Andro Stotts
	     * @version 0.4
	     */		
	    class LoginListener implements ActionListener, KeyListener{
		private ClientWindow window = ClientWindow.getWindow();
		private void attemptLogin(){
		    name = tfUsername.getText();
		    password = new String(pfPassword.getPassword());
		    if(name.isEmpty()||password.isEmpty()){
			tfUsername.setText("");
			lblLoginError.setText("Incorrect Login Credentials");
		    }
		    else{
			ip = tfServerIp.getText();
			int connected = controller.connectServer(ip,name,password);
			if(connected==0){
			    window.launchChatWindow();
			}
			else if(connected==1){
			    tfUsername.setText("");
			    lblLoginError.setText("Wrong username or password");
			}
			else if(connected==2){
			    tfUsername.setText("");
			    lblLoginError.setText("This user has logged in already");
			}
			else{
			    tfServerIp.setText("");
			    lblLoginError.setText("Server unavailable on this IP address");
			}
		    }
		}
		public void actionPerformed(ActionEvent e) {			
		    attemptLogin();
		    // window.getContentPane().repaint();
		}
		
		public void keyTyped(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {}
		public void keyPressed(KeyEvent e) {
		    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			attemptLogin();
		    }
		    else{
			lblLoginError.setText("incorrect keypress");
		    }
		}
	    }	
	    
	    /**
	     * Handles checkbox that toggles sound
	     * @author Bryce Filler and Max Hinson
	     * @version 0.4
	     */
	    class CheckListener implements ItemListener {
		
		public CheckListener(){};
		
		public void itemStateChanged(ItemEvent e) {
		    Object source = e.getItemSelectable();
		    
		    if (e.getStateChange() == ItemEvent.DESELECTED)
			controller.setSound(false);
		    else
			controller.setSound(true);
		}
	    }	
	    
	/**ActionListener class used in the launchRegisterChatRoomWindow
	*@author jleeong
	*
	*@version F16
	*/
	class RegistrantListener implements ActionListener, KeyListener{
		public void actionPerformed(ActionEvent e){
			sendRegistration();
		}
		public void keyTyped(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {}
		public void keyPressed(KeyEvent e) {
		    if (e.getKeyCode() == KeyEvent.VK_ENTER)
			sendRegistration();
		}
		private void sendRegistration(){
			String reg = tfChatRoomParticipants.getText().trim();
			//	String [] arr = reg.split(",");
			//	roomUsers = arr.length;
			controller.sendChatRoomRegistration(reg);
			registrant.dispose();
		}
	}
	    /**
     * Handles actions when name is changed
     * @author Bryce Filler and Max Hinson
     * @version 0.4
     */
    class ChangeNickNameListener implements ActionListener, KeyListener{
	//input listener for name change
	public void actionPerformed(ActionEvent e) {
	    String text = tfNickName.getText().trim();
		    if(!text.isEmpty())
			{
			    tfNickName.setText("");
			    controller.sendMsg2Server(name + "(NAME_CHANGE): " + text + "&" + "NAME_CHANGE" + ":1001");
			    nicknameWindow.dispose();
			}
	}
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
	    int key = e.getKeyCode();
	    if (key == KeyEvent.VK_ENTER) {
		String text = tfNickName.getText().trim();
		if(!text.isEmpty())
		    {
			tfNickName.setText("");
			controller.sendMsg2Server(name + "(NAME_CHANGE): " + text + "&" + "NAME_CHANGE" + ":1001");
			nicknameWindow.dispose();
		    }
	    }
	}    
}
	    /**
	     * Handles the action when user clicks enter on keyboard
	     * @author Peng Wang with Andro Stotts
	     * @version 0.4
	     */
	    class InputListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(listContacts.isSelectionEmpty()){
				taOutput.append("***PLEASE SELECT A CONTACT PERSON FIRST, THEN SEND YOUR MESSAGE***\n");
			}
			else{
				String text = tfInput.getText().trim();
				tfInput.setText("");
				String receiver = listContacts.getSelectedValue();
				String recNoStatus = receiver;
				if(receiver.contains("(Online)")){
					recNoStatus = receiver.substring(0,receiver.indexOf('('));
					controller.sendIM(text,recNoStatus);
				}
				else if(receiver.contains("ChatRoom")){
					String[] parts = receiver.split(":");
					recNoStatus = parts[1];
					controller.sendGrpMsg(text,recNoStatus);
				}
			}
		}	
	    }

    class LogoutListener implements ActionListener {
	public void actionPerformed(ActionEvent e){
	    window.repaint();
	    window.dispose();
	    controller.sendMsg2Server(name+"&Broadcast:1008");
	    window = new ClientWindow();
	    window.launchLoginWindow();
	}
    }
    class timerThread extends java.util.TimerTask{
	
	public void run (){
	    onlineCountUpdate.addActionListener(new OnlineCountUpdateButtonListener());
	    onlineCountUpdate.doClick();
    	}
    }
    
}
