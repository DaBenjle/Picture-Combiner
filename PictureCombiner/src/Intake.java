import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
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
			//colors[i] = bi.getMeanColor();
			getMeanColor(bi);
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
	
	public static void getMeanColor(BufferedImage bi)
	{
		if(bi.getType() != BufferedImage.TYPE_3BYTE_BGR) throw new IllegalArgumentException();
		int rBucket = 0; int gBucket = 0; int bBucket = 0;
		byte[] pixels = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
		for(int curPixelSet = 0; curPixelSet < pixels.length; curPixelSet += 3)
		{
			byte red = pixels[curPixelSet + 2];
			System.out.println("Red: " + getByteString(red));
			byte green = pixels[curPixelSet + 1];
			System.out.println("Green: " + getByteString(green));
			byte blue = pixels[curPixelSet];
			System.out.println("Blue: " + getByteString(blue));
		}
	}
	
	public static String getByteString(byte input)
	{
		return String.format("%8s", Integer.toBinaryString(input & 0xFF)).replace(' ', '0');
	}
}
