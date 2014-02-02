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

public class Packet3Kick extends Packet
{
	private String message;

	public Packet3Kick()
	{
	}

	public Packet3Kick(String message)
	{
		this.message = message;
	}

	@Override
	public int getID()
	{
		return 4;
	}

	@Override
	public void write(DataOutputStream out) throws IOException
	{
		out.writeUTF(this.message);
	}

	@Override
	public void read(DataInputStream in) throws IOException
	{
		this.message = in.readUTF();
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