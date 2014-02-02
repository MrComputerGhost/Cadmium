package com.sci.cadmium.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import com.sci.cadmium.common.packet.Packet;
import com.sci.cadmium.common.packet.Packet0Connect;
import com.sci.cadmium.common.packet.Packet1Disconnect;

/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public final class CadmiumClient
{
	public static void main(String[] args)
	{
		CadmiumClient.INSTANCE.start();
	}

	/**
	 * Cadmium client instance
	 */
	public static final CadmiumClient INSTANCE = new CadmiumClient();

	/**
	 * Main frame
	 */
	private JFrame frame;

	/**
	 * Menu bar
	 */
	private JMenuBar menuBar;

	/**
	 * Connections menu
	 */
	private JMenu menuConnections;

	/**
	 * Connect menu item
	 */
	private JMenuItem connectMenuItem;

	/**
	 * Disconnect menu item
	 */
	private JMenuItem disconnectMenuItem;

	/**
	 * Exit menu item
	 */
	private JMenuItem exitMenuItem;

	/**
	 * Chat text area
	 */
	private JTextArea chatArea;

	/**
	 * Chat scroll pane
	 */
	private JScrollPane chatSrollPane;

	/**
	 * Chat message field
	 */
	private JTextField chatField;

	/**
	 * This client's username
	 */
	private String username;

	/**
	 * Socket
	 */
	private DatagramSocket socket;

	/**
	 * IP
	 */
	private InetAddress ip;

	/**
	 * Port
	 */
	private int port;

	/**
	 * Is the client connected to a servetr or not
	 */
	private boolean connected;

	private CadmiumClient()
	{
		this.frame = new JFrame("Cadmium");
		this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.frame.setSize(600, 400);
		this.frame.setLocationRelativeTo(null);
		this.frame.setLayout(new BorderLayout());

		this.frame.addWindowListener(new WindowListener()
		{
			@Override
			public void windowActivated(WindowEvent arg0)
			{
			}

			@Override
			public void windowClosed(WindowEvent arg0)
			{
			}

			@Override
			public void windowClosing(WindowEvent arg0)
			{
				CadmiumClient.this.stop();
			}

			@Override
			public void windowDeactivated(WindowEvent arg0)
			{
			}

			@Override
			public void windowDeiconified(WindowEvent arg0)
			{
			}

			@Override
			public void windowIconified(WindowEvent arg0)
			{
			}

			@Override
			public void windowOpened(WindowEvent arg0)
			{
			}
		});

		this.menuBar = new JMenuBar();
		this.menuConnections = new JMenu("Connections");

		this.connectMenuItem = new JMenuItem("Connect");
		this.connectMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ConnectDialog connectDialog = new ConnectDialog(CadmiumClient.this.frame);
				connectDialog.setVisible(true);
			}
		});

		this.disconnectMenuItem = new JMenuItem("Disconnect");
		this.disconnectMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				CadmiumClient.this.disconnect();
			}
		});
		this.disconnectMenuItem.setEnabled(false);

		this.exitMenuItem = new JMenuItem("Exit");
		this.exitMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				CadmiumClient.this.stop();
			}
		});

		this.menuConnections.add(this.connectMenuItem);
		this.menuConnections.add(this.disconnectMenuItem);
		this.menuConnections.addSeparator();
		this.menuConnections.add(this.exitMenuItem);

		this.menuBar.add(this.menuConnections);
		this.frame.setJMenuBar(this.menuBar);

		this.chatArea = new JTextArea();
		this.chatArea.setEditable(false);
		this.chatSrollPane = new JScrollPane(this.chatArea);
		this.frame.add(this.chatSrollPane, BorderLayout.CENTER);

		this.chatField = new JTextField();
		this.chatField.setEditable(false);
		this.frame.add(this.chatField, BorderLayout.SOUTH);
	}

	public void start()
	{
		this.frame.setVisible(true);
	}

	public void stop()
	{
		disconnect();
		this.frame.dispose();
		System.exit(0);
	}

	public void disconnect()
	{
		if(!this.connected)
			return;

		sendPacket(new Packet1Disconnect(this.username));
		this.connected = false;

		this.disconnectMenuItem.setEnabled(this.connected);
		this.connectMenuItem.setEnabled(!this.connected);
		this.chatField.setEditable(this.connected);
	}

	public void connect(String username, String ipText, String portText)
	{
		if(this.connected)
			return;

		this.username = username;
		try
		{
			this.ip = InetAddress.getByName(ipText);
			this.port = Integer.valueOf(portText);

			this.socket = new DatagramSocket();
			sendPacket(new Packet0Connect(this.username));

			this.connected = true;
		}
		catch(UnknownHostException e)
		{
			JOptionPane.showMessageDialog(this.frame, "Invalid IP Address!");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		this.disconnectMenuItem.setEnabled(this.connected);
		this.connectMenuItem.setEnabled(!this.connected);
		this.chatField.setEditable(this.connected);
	}

	public void sendPacket(Packet pkt) // TODO encrypted packets
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream(baos);

			dout.writeInt(pkt.getID());
			pkt.write(dout);

			byte[] data = baos.toByteArray();
			this.socket.send(new DatagramPacket(data, data.length, this.ip, this.port));

			dout.close();
			baos.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}