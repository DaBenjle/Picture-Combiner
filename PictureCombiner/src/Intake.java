import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Intake
{
	private static Intake instance;
	private File folder = null;
	private byte[] colors;
	
	private Intake()
	{
		
	}
	
	public void run()
	{
		File[] files = folder.listFiles();
		colors = new byte[files.length * 3];
		Arrays.sort(files);
		for(int i = 0; i < files.length; i++)
		{
			BufferedImage bi = null;
			try
			{
				bi = ImageIO.read(files[i]);
				final int width = bi.getWidth(), height = bi.getHeight();
				BufferedImage newBi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
				newBi.createGraphics().drawImage(bi, 0, 0, width, height, null);
				bi = newBi;
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			if(bi == null)
				continue;
			byte[] cols = getMeanColor(bi);
			colors[i * 3] = cols[0];//blue
			colors[i * 3 + 1] = cols[1];//green
			colors[i * 3 + 2] = cols[2];//red
		}
	}
	
	public static Intake getInstance()
	{
		if(instance == null) instance = new Intake();
		return instance;
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
	
	public static byte[] getMeanColor(BufferedImage bi)
	{
		if(bi.getType() != BufferedImage.TYPE_3BYTE_BGR) throw new IllegalArgumentException();
		Double avgB = 0.0, avgG = 0.0, avgR = 0.0;
		byte[] pixels = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
		for(int curPixelSet = 0; curPixelSet < pixels.length; curPixelSet += 3)
		{
			byte red = pixels[curPixelSet + 2];
			byte green = pixels[curPixelSet + 1];
			byte blue = pixels[curPixelSet];
			avgB = ((avgB * curPixelSet) + (blue & 0xFF)) / (curPixelSet + 1);
			avgG = ((avgG * curPixelSet) + (green & 0xFF)) / (curPixelSet + 1);
			avgR = ((avgR * curPixelSet) + (red & 0xFF)) / (curPixelSet + 1);
		}
		
		int roundedB = (int)(avgB + .5), roundedG = (int)(avgG + .5), roundedR = (int)(avgR + .5);
		return new byte[] {(byte) roundedB, (byte) roundedG, (byte) roundedR};
	}
	
	public static String getByteString(byte input)
	{
		return String.format("%8s", Integer.toBinaryString(input & 0xFF)).replace(' ', '0');
	}
}
