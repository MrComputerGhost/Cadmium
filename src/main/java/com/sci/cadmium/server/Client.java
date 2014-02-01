package com.sci.cadmium.server;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class Client
{
	/**
	 * Client's username
	 */
	private String username;

	/**
	 * Client's IP
	 */
	private InetAddress ipAddress;

	/**
	 * Client's socket
	 */
	private DatagramSocket socket;

	public Client(InetAddress ipAddress) throws SocketException
	{
		this.ipAddress = ipAddress;
		this.socket = new DatagramSocket();
	}

	public DatagramSocket getSocket()
	{
		return socket;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public InetAddress getIpAddress()
	{
		return ipAddress;
	}
}