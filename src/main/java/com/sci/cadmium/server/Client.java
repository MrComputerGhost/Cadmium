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
	 * Client's permission level
	 */
	private int level;

	/**
	 * Client's IP
	 */
	private InetAddress ipAddress;

	/**
	 * Client's port
	 */
	private int port;

	/**
	 * Client's socket
	 */
	private DatagramSocket socket;

	public Client() throws SocketException
	{
		this.socket = new DatagramSocket();
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
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

	public void setIpAddress(InetAddress ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	public InetAddress getIpAddress()
	{
		return ipAddress;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public int getPort()
	{
		return port;
	}

	@Override
	public String toString()
	{
		return this.username;
	}
}