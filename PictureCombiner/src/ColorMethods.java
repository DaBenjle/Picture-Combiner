import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class ColorMethods
{
	/*
	 * Assumes the input image is type: BufferedImage.TYPE_3BYTE_BGR
	 */
	public static byte[] getMeanColor(byte[] pixels)
	{
		
		Double avgB = 0.0, avgG = 0.0, avgR = 0.0;
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
	
	public static byte[] getMeanColor(BufferedImage bi)
	{
		if(bi.getType() != BufferedImage.TYPE_3BYTE_BGR) throw new IllegalArgumentException();
		return getMeanColor(((DataBufferByte)bi.getRaster().getDataBuffer()).getData());
	}
	
	public static String getByteString(byte input)
	{
		return String.format("%8s", Integer.toBinaryString(input & 0xFF)).replace(' ', '0');
	}
	
	public short difference(byte[] c1, byte[] c2)
	{
		//						blue difference squared				green difference squared			red difference squared
		return (short)Math.sqrt((c1[0] - c2[0]) * (c1[0] - c2[0]) + (c1[1] - c2[1]) * (c1[1] - c2[1]) + (c1[2] - c2[2]) * (c1[2] - c2[2]));
	}
	
	//Inefficient, only use for testing. BGR
	public static java.awt.Color getRGB(byte[] arr)
	{
		return new java.awt.Color(arr[2] & 0xff, arr[1] & 0xff, arr[0] & 0xff);
	}
	
	/*
	 *  1(1, 2)  2(1, 2)  3(1, 2)  4(1, 2)  5(1, 2)  6(1, 2)
	 *  7(1, 2)  8(1, 2)  9(1, 2) 10(1, 2) 11(1, 2) 12(1, 2)
	 * 13(1, 2) 14(1, 2) 15(1, 2) 16(1, 2) 17(1, 2) 18(1, 2)
	 * 19(1, 2) 20(1, 2) 21(1, 2) 22(1, 2) 23(1, 2) 24(1, 2)
	 * 
	 * 16[2]: y = 2, x = 3, c = 2; raw[47]
	 * target[y][x][c] = raw[(y * targetPixels[y].length * 3) + (x * 3) + c];
	 * 2 * 6 * 3 + 3 * 3 + 2
	 * Equation is working!
	 */
	public static byte[][][] rawArrTo3DArr(byte[] raw, int height, int width, int depth)
	{
		//counter to height: y, counter to width: x, counter to depth: c.
		byte[][][] targetPixels = new byte[height][width][depth];
		for(int y = 0; y < targetPixels.length; y++)
		{
			for(int x = 0; x < targetPixels[y].length; x++)
			{
				for(int c = 0; c < 3; c++)
				{
					targetPixels[y][x][c] = raw[(y * targetPixels[y].length * 3) + (x * 3) + c];
				}
			}
		}
		return targetPixels;
	}
	
	public static int getClosestColor(byte[] targetColor, byte[] useableColors)
	{
		//TODO
		return 0;
	}
}
