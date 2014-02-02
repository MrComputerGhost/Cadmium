package com.sci.cadmium.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import com.sci.cadmium.server.util.Close;

/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ListFile
{
	private File file;

	public ListFile(File file) throws IOException
	{
		this.file = file;
		if(!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		if(!file.exists())
			file.createNewFile();
	}

	public void add(String str)
	{
		if(contains(str))
			return;
		try
		{
			Writer out = new BufferedWriter(new FileWriter(this.file, true));
			out.append(str + "\n");
			out.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void remove(String str)
	{
		List<String> toAdd = new ArrayList<String>();
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(this.file));
			String user;
			while((user = reader.readLine()) != null)
			{
				if(!user.equals(str))
					toAdd.add(user);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			Close.quiet(reader);
		}

		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(new FileWriter(this.file));
			for(String s : toAdd)
			{
				writer.write(s + "\n");
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			Close.quiet(writer);
		}
	}

	public boolean contains(String str)
	{
		boolean contains = false;
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(this.file));
			String user;
			while((user = reader.readLine()) != null)
			{
				if(str.equals(user))
				{
					contains = true;
					break;
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			Close.quiet(reader);
		}
		return contains;
	}
}