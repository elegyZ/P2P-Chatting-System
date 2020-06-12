package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import client.Client;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextArea;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JScrollPane;

public class ClientWindow extends JFrame 
{
	private Client client;
	private static final long serialVersionUID = 1L;	
	private JPanel contentPane;
	private JButton btnSend;
	private JButton btnBroadcast;
	private JButton btnList;
	private JButton btnStop;
	private JButton btnKick;
	private JButton btnStats;	
	private JLabel lbKickId;
	private JLabel lbStats;	
	private JTextField tfKickId;
	private JTextField tfStatsId;
	private JTextArea jtaReceive;
	private JTextArea jtaSend;
	
	//add the received message to the client's screen
	public void setReceive(String message)
	{
		jtaReceive.append(message + "\n");
	}
	//add click event to the "Send" button
	private void setLstSend()
	{
		ActionListener lstSend = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String message = jtaSend.getText();
				if(message.isEmpty())
					JOptionPane.showMessageDialog(rootPane, "You Can Not Send An Empty Message!", "Warning", JOptionPane.WARNING_MESSAGE);
				else
				{
					client.send(message);
					jtaSend.setText("");
				}
			}			
		};
		btnSend.addActionListener(lstSend);
	}
	//add click event to the "Broadcast" button
	private void setLstBroadcast()
	{
		ActionListener lstBroadcast = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String message = jtaSend.getText();
				if(message.isEmpty())
					JOptionPane.showMessageDialog(rootPane, "You Can Not Broadcast An Empty Message!", "Warning", JOptionPane.WARNING_MESSAGE);
				else
				{
					client.broadcast(message);
					jtaSend.setText("");
				}
			}			
		};
		btnBroadcast.addActionListener(lstBroadcast);
	}
	//add click event to the "List" button
	private void setLstList()
	{
		ActionListener lstList = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				client.list();	
			}			
		};
		btnList.addActionListener(lstList);
	}
	//add click event to the "Stop" button
	private void setLstStop()
	{
		ActionListener lstStop = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				client.stop();
			}			
		};
		btnStop.addActionListener(lstStop);
	}
	//add click event to the "Kick" button
	private void setLstKick()
	{
		ActionListener lstKick = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String id = tfKickId.getText();
				if(id.isEmpty())
					JOptionPane.showMessageDialog(rootPane, "You Have Not Enter A Client ID!", "Warning", JOptionPane.WARNING_MESSAGE);
				else
					client.kick(id);
			}			
		};
		btnKick.addActionListener(lstKick);
	}
	//add click event to the "Stats" button
	private void setLstStats()
	{
		ActionListener lstStats = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String id = tfStatsId.getText();
				if(id.isEmpty())
					JOptionPane.showMessageDialog(rootPane, "You Have Not Enter A Client ID!", "Warning", JOptionPane.WARNING_MESSAGE);
				else
					client.stats(id);
			}			
		};
		btnStats.addActionListener(lstStats);
	}
	//show warning dialog to the user
	public void warning(String info)
	{
		JOptionPane.showMessageDialog(rootPane, info, "Warning", JOptionPane.WARNING_MESSAGE);
		client.setFlag(false);
	}
	

	/**
	 * Create the frame.
	 */
	public ClientWindow(String id, Client client) 
	{
		this.client = client;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 700);
		setTitle(id);
		contentPane = new JPanel();
		contentPane.setForeground(SystemColor.textHighlight);
		contentPane.setBackground(SystemColor.inactiveCaptionBorder);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnSend = new JButton("Send");
		btnSend.setFont(new Font("Calibri", Font.BOLD, 25));
		btnSend.setBounds(518, 601, 150, 40);
		contentPane.add(btnSend);
		
		btnBroadcast = new JButton("Broadcast");
		btnBroadcast.setFont(new Font("Calibri", Font.BOLD, 25));
		btnBroadcast.setBounds(354, 601, 150, 40);
		contentPane.add(btnBroadcast);
		
		btnList = new JButton("List");
		btnList.setFont(new Font("Calibri", Font.BOLD, 25));
		btnList.setBounds(190, 601, 150, 40);
		contentPane.add(btnList);
		
		btnStop = new JButton("Stop");
		btnStop.setFont(new Font("Calibri", Font.BOLD, 25));
		btnStop.setBounds(14, 601, 100, 40);
		contentPane.add(btnStop);
		
		lbKickId = new JLabel("Kick-ID:");
		lbKickId.setFont(new Font("Calibri", Font.BOLD, 25));
		lbKickId.setBounds(14, 366, 80, 30);
		contentPane.add(lbKickId);
		
		tfKickId = new JTextField();
		tfKickId.setFont(new Font("Calibri", Font.PLAIN, 20));
		tfKickId.setBounds(108, 363, 396, 40);
		contentPane.add(tfKickId);
		tfKickId.setColumns(10);
		
		btnKick = new JButton("Kick");
		btnKick.setFont(new Font("Calibri", Font.BOLD, 25));
		btnKick.setBounds(518, 361, 150, 40);
		contentPane.add(btnKick);
		
		lbStats = new JLabel("Stats-ID:");
		lbStats.setFont(new Font("Calibri", Font.BOLD, 25));
		lbStats.setBounds(14, 313, 110, 30);
		contentPane.add(lbStats);
		
		tfStatsId = new JTextField();
		tfStatsId.setFont(new Font("Calibri", Font.PLAIN, 20));
		tfStatsId.setColumns(10);
		tfStatsId.setBounds(138, 310, 366, 40);
		contentPane.add(tfStatsId);
		
		btnStats = new JButton("Stats");
		btnStats.setFont(new Font("Calibri", Font.BOLD, 25));
		btnStats.setBounds(518, 308, 150, 40);
		contentPane.add(btnStats);
		
		jtaSend = new JTextArea();
		jtaSend.setFont(new Font("Calibri", Font.PLAIN, 25));
		JScrollPane jspSend = new JScrollPane(jtaSend);
		jspSend.setBounds(14, 449, 654, 139);
		jspSend.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(jspSend);
		
		jtaReceive = new JTextArea();
		jtaReceive.setBackground(SystemColor.control);
		jtaReceive.setFont(new Font("Calibri", Font.BOLD, 20));
		jtaReceive.setEditable(false);
		JScrollPane jspReceive = new JScrollPane();
		jspReceive.setViewportView(jtaReceive);
		jspReceive.setBounds(14, 13, 654, 277);
		jspReceive.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		contentPane.add(jspReceive);
		
		JLabel lblEnterMessage = new JLabel("Enter Message:");
		lblEnterMessage.setForeground(SystemColor.textHighlight);
		lblEnterMessage.setFont(new Font("Calibri", Font.ITALIC, 20));
		lblEnterMessage.setBounds(14, 416, 140, 20);
		contentPane.add(lblEnterMessage);
		
		addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent e) 
			{
				client.stop();
			}
		
		});

		
		setLstSend();
		setLstBroadcast();
		setLstList();
		setLstStop();
		setLstKick();
		setLstStats();
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
				try 
				{
					ClientWindow frame = new ClientWindow("123",null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
