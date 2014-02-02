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

public class Packet2Message extends Packet
{
	/**
	 * User who sent the message
	 */
	private String username;

	/**
	 * Message the user sent
	 */
	private String message;

	public Packet2Message()
	{
	}

	public Packet2Message(String username, String message)
	{
		this.username = username;
		this.message = message;
	}

	@Override
	public int getID()
	{
		return 3;
	}

	@Override
	public void write(DataOutputStream out) throws IOException
	{
		out.writeUTF(this.username);
		out.writeUTF(this.message);
	}

	@Override
	public void read(DataInputStream in) throws IOException
	{
		this.username = in.readUTF();
		this.message = in.readUTF();
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}
}