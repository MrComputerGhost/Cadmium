package com.sci.cadmium.common.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public abstract class Packet
{
	private static Map<Integer, Class<? extends Packet>> packets = new HashMap<Integer, Class<? extends Packet>>();

	public abstract int getID();

	public abstract void write(DataOutputStream out) throws IOException;

	public abstract void read(DataInputStream in) throws IOException;

	public static Packet createPacket(int id) throws Exception
	{
		if(packets.get(id) == null)
			throw new IllegalArgumentException("Packet ID " + id + " doesn't exist!");
		return packets.get(id).newInstance();
	}

	public static void registerPacket(Class<? extends Packet> packet) throws Exception
	{
		Packet pkt = packet.newInstance();
		packets.put(pkt.getID(), packet);
	}

	static
	{
		try
		{
			registerPacket(Packet0Connect.class);
			registerPacket(Packet1Disconnect.class);
			registerPacket(Packet2Message.class);
			registerPacket(Packet3Kick.class);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}