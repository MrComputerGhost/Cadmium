package com.sci.cadmium.common.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class Packet1Disconnect extends Packet
{
	/**
	 * Disconnecting client's username
	 */
	private String username;

	public Packet1Disconnect()
	{
	}

	public Packet1Disconnect(String username)
	{
		this.username = username;
	}

	@Override
	public int getID()
	{
		return 1;
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