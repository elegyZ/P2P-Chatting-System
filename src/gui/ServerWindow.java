package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import server.Server;

import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;

public class ServerWindow extends JFrame 
{
	private Server server;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblEnterMessage;
	private JLabel lbClientList;
	private JTextArea jtaReceive;
	private JTextArea jtaSend;
	private JTextArea jtaClientList;
	private JTextField tfClientID;
	private JButton btnSend;
	
	//add the received message to the server's screen
	public void setReceive(String message)
	{
		jtaReceive.append(message + "\n");
	}
	//add the latest client into the ClientList show on the screen
	public void setClientList(String clientID)
	{
		jtaClientList.append(clientID + "\n");
	}
	//fresh the ClientList after any client's leaving
	public void freshClientList(List<String> clientList)
	{
		jtaClientList.setText("");
		for(String clientID:clientList)
			jtaClientList.append(clientID + "\n");
	}
	//show warning dialog to the user
	public void warning(String info)
	{
		JOptionPane.showMessageDialog(rootPane, info, "Warning", JOptionPane.WARNING_MESSAGE);
	}
	//add click event to the "Send" button
	public void setLstSend()
	{
		ActionListener lstSend = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String clientID = tfClientID.getText();
				String message = jtaSend.getText();
				if(clientID.isEmpty())
					JOptionPane.showMessageDialog(rootPane, "You Have Not Enter A Client ID!", "Warning", JOptionPane.WARNING_MESSAGE);
				else if(message.isEmpty())
					JOptionPane.showMessageDialog(rootPane, "You Can Not Send An Empty Message!", "Warning", JOptionPane.WARNING_MESSAGE);
				else
				{
					server.send(clientID, message);
					jtaSend.setText("");
				}
			}			
		};
		btnSend.addActionListener(lstSend);
	}
	
	/**
	 * Create the frame.
	 */
	public ServerWindow(Server server) 
	{
		this.server = server;
		setTitle("Server");
		setBackground(SystemColor.inactiveCaptionBorder);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 700);
		contentPane = new JPanel();
		contentPane.setForeground(SystemColor.textHighlight);
		contentPane.setBackground(SystemColor.inactiveCaptionBorder);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		jtaReceive = new JTextArea();
		jtaReceive.setBackground(SystemColor.control);
		jtaReceive.setFont(new Font("Calibri", Font.BOLD, 20));
		jtaReceive.setEditable(false);
		JScrollPane jspReceive = new JScrollPane();
		jspReceive.setBounds(14, 13, 420, 380);
		jspReceive.setViewportView(jtaReceive);
		jspReceive.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		contentPane.add(jspReceive);
		
		lbClientList = new JLabel("Client List");
		lbClientList.setForeground(SystemColor.textHighlight);
		lbClientList.setFont(new Font("Calibri", Font.BOLD | Font.ITALIC, 20));
		lbClientList.setBounds(497, 13, 130, 20);
		contentPane.add(lbClientList);
		
		jtaClientList = new JTextArea();
		jtaClientList.setFont(new Font("Calibri", Font.PLAIN, 20));
		jtaClientList.setEditable(false);
		JScrollPane jspClientList = new JScrollPane(jtaClientList);
		jspClientList.setBounds(448, 40, 220, 353);
		jspClientList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(jspClientList);
		
		lblEnterMessage = new JLabel("Send To Client:");
		lblEnterMessage.setForeground(SystemColor.textHighlight);
		lblEnterMessage.setFont(new Font("Calibri", Font.BOLD | Font.ITALIC, 20));
		lblEnterMessage.setBounds(14, 416, 130, 20);
		contentPane.add(lblEnterMessage);
		
		tfClientID = new JTextField();
		tfClientID.setFont(new Font("Calibri", Font.PLAIN, 25));
		tfClientID.setBounds(148, 406, 520, 40);
		contentPane.add(tfClientID);
		tfClientID.setColumns(10);
		
		jtaSend = new JTextArea();
		jtaSend.setFont(new Font("Calibri", Font.PLAIN, 25));
		JScrollPane jspSend = new JScrollPane(jtaSend);
		jspSend.setBounds(14, 459, 654, 128);
		jspSend.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(jspSend);
		
		btnSend = new JButton("Send");
		btnSend.setFont(new Font("Calibri", Font.BOLD, 25));
		btnSend.setBounds(518, 600, 150, 40);
		contentPane.add(btnSend);
		
		setLstSend();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try {
					ServerWindow frame = new ServerWindow(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
