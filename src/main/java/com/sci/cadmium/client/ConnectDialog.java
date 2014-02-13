package com.sci.cadmium.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ConnectDialog extends JDialog
{
	/**
	 * Make Eclipse happy :P
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Username label
	 */
	private JLabel usernameLabel;

	/**
	 * IP Label
	 */
	private JLabel ipLabel;

	/**
	 * Port label
	 */
	private JLabel portLabel;

	/**
	 * Username field
	 */
	private JTextField username;

	/**
	 * IP field
	 */
	private JTextField ip;

	/**
	 * Port field
	 */
	private JTextField port;

	/**
	 * Connect button
	 */
	private JButton connect;

	public ConnectDialog(JFrame parent)
	{
		super(parent);
		this.setTitle("Connectorz");
		this.setResizable(false);
		this.setSize(250, 160);
		this.setLocationRelativeTo(null);
		this.setLayout(null);

		this.usernameLabel = new JLabel("Name of Ze User:");
		this.usernameLabel.setBounds(10, 10, 100, 26);
		this.add(this.usernameLabel);

		this.username = new JTextField();
		this.username.setBounds(90, 10, 150, 26);
		this.add(this.username);

		this.ipLabel = new JLabel("Interesting Pork:");
		this.ipLabel.setBounds(10, 40, 100, 26);
		this.add(this.ipLabel);

		this.ip = new JTextField();
		this.ip.setBounds(90, 40, 150, 26);
		this.add(this.ip);

		this.portLabel = new JLabel("Portal:");
		this.portLabel.setBounds(10, 70, 100, 26);
		this.add(this.portLabel);

		this.port = new JTextField();
		this.port.setBounds(90, 70, 150, 26);
		this.add(this.port);

		this.connect = new JButton("Connectorz");
		this.connect.setBounds(10, 100, 230, 26);
		this.connect.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				CadmiumClient.INSTANCE.connect(ConnectDialog.this.username.getText(), ConnectDialog.this.ip.getText(), ConnectDialog.this.port.getText());
				ConnectDialog.this.dispose();
			}
		});
		this.add(this.connect);
	}
}
