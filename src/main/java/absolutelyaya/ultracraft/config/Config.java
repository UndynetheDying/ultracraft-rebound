package absolutelyaya.ultracraft.config;

import absolutelyaya.ultracraft.Ultracraft;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class Config
{
	public final List<ConfigEntry<?>> entries = new ArrayList<>();
	
	protected abstract String getExportPath();
	
	protected abstract String getFileName();
	
	public void save()
	{
		Path gameDir = FabricLoader.getInstance().getGameDir();
		try
		{
			Path.of(gameDir.toString(), getExportPath()).toFile().mkdirs();
			File file = Path.of(gameDir.toString(), getExportPath(), getFileName()).toFile();
			file.createNewFile();
			try (FileWriter writer = new FileWriter(file))
			{
				for (ConfigEntry<?> entry : entries)
					writer.write(entry.serialize() + "\n");
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void load()
	{
		Path gameDir = FabricLoader.getInstance().getGameDir();
		Path path = Path.of(gameDir.toString(), getExportPath() + getFileName());
		File file = new File(path.toUri());
		if(!file.exists())
		{
			save();
		}
		try (Scanner reader = new Scanner(file))
		{
			while(reader.hasNextLine())
			{
				String line = reader.nextLine();
				if(line.startsWith("#") || line.isEmpty())
					continue;
				String[] segments = line.split(":");
				if(segments.length != 2)
					continue;
				try
				{
					for (ConfigEntry<?> entry : entries)
					{
						if(entry.id.equals(segments[0]))
							entry.deserialize(segments[1]);
					}
				}
				catch (Exception e)
				{
					Ultracraft.LOGGER.error("An Exception occurred trying to read Server Config Entry '" + segments[0] + "'.");
					e.printStackTrace();
				}
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
