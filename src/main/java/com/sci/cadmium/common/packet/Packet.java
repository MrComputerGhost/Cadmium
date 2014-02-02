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
	/**
	 * Map of packet id's to packet classes
	 */
	private static Map<Integer, Class<? extends Packet>> packets = new HashMap<Integer, Class<? extends Packet>>();

	/**
	 * ID of the packet
	 * @return
	 */
	public abstract int getID();

	/**
	 * Write data to the packet
	 * @param out
	 * @throws IOException
	 */
	public abstract void write(DataOutputStream out) throws IOException;

	/**
	 * Read fata from the packet
	 * @param in
	 * @throws IOException
	 */
	public abstract void read(DataInputStream in) throws IOException;

	/**
	 * Create a packet by id
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static Packet createPacket(int id) throws Exception
	{
		if(packets.get(id) == null)
			throw new IllegalArgumentException("Packet ID " + id + " doesn't exist!");
		return packets.get(id).newInstance();
	}

	/**
	 * Register a packet class
	 * @param packet
	 * @throws Exception
	 */
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