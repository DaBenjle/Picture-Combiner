import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Intake
{
	private static Intake instance;
	private File folder = null;
	private File[] files = null;
	private byte[] colors;
	
	private Intake()
	{
		
	}
	
	//TODO Make colors a file instead of memory, but it IS efficient memory
	public String run(Wrapper<Integer> minimumSideLengthWrapper)
	{
		files = folder.listFiles();
		//TODO Implement proper file number check
		if(files.length < 5)
		{
			return null;
		}
		colors = new byte[files.length * 3];
		Arrays.sort(files);
		int minimumSideLength = Integer.MAX_VALUE;
		for(int i = 0; i < files.length; i++)
		{
			BufferedImage bi = null;
			try
			{
				bi = ImageIO.read(files[i]);
				final int width = bi.getWidth(), height = bi.getHeight();
				if(width < minimumSideLength) minimumSideLength = width;
				if(height < minimumSideLength) minimumSideLength = height;
				BufferedImage newBi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
				newBi.createGraphics().drawImage(bi, 0, 0, width, height, null);
				bi = newBi;
			} 
			catch (IOException e)
			{
				return files[i].getPath();
			}
			byte[] cols = ColorMethods.getMeanColor(bi);
			colors[i * 3] = cols[0];//blue
			colors[i * 3 + 1] = cols[1];//green
			colors[i * 3 + 2] = cols[2];//red
		}
		minimumSideLengthWrapper.val = minimumSideLength;
		return "";
	}
	
	public static Intake getInstance()
	{
		if(instance == null) instance = new Intake();
		return instance;
	}
	
	public static File[] getContents()
	{
		return getInstance().files;
	}
	
	public static File getFolder()
	{
		return getInstance().folder;
	}
	
	public static byte[] getColors()
	{
		return getInstance().colors;
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
