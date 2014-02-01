package com.sci.cadmium.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class Packet0Connect extends Packet
{
	/**
	 * Connecting client's username
	 */
	private String username;

	public Packet0Connect()
	{
	}
	
	public Packet0Connect(String username)
	{
		this.username = username;
	}

	@Override
	public int getID()
	{
		return 0;
	}

	@Override
	public void write(DataOutputStream out) throws IOException
	{
		out.writeUTF(this.username);
	}

	@Override
	public void read(DataInputStream in) throws IOException
	{
		this.username = in.readUTF();
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getUsername()
	{
		return this.username;
	}
}