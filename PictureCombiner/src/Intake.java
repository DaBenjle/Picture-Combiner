import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Intake extends Thread
{
	private static Intake instance;
	private File folder = null;
	private Color[] colors;
	
	private Intake()
	{
		
	}
	
	@Override
	public void run()
	{
		super.run();
		colors = new Color[folder.listFiles().length];
		File[] files = folder.listFiles();
		for(int i = 0; i < colors.length; i++)
		{
			MyBufferedImage bi = null;
			try
			{
				bi = ImageIO.read(files[i]);
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			if(bi == null)
				continue;
			colors[i] = bi.getMeanColor();
		}
	}
	
	public static Intake getInstance()
	{
		if(instance == null) instance = new Intake();
		return instance;
	}
	
	public static Intake setFolder(File input) throws IllegalArgumentException
	{
		if(input.listFiles() == null)
		{
			throw new IllegalArgumentException();
		}
		getInstance().folder = input;
		return instance;
	}
}
