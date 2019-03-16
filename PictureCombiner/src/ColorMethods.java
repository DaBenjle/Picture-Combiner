import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class ColorMethods
{
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
	
	public short difference(byte[] c1, byte[] c2)
	{
		//						blue difference squared				green difference squared			red difference squared
		return (short)Math.sqrt((c1[0] - c2[0]) * (c1[0] - c2[0]) + (c1[1] - c2[1]) * (c1[1] - c2[1]) + (c1[2] - c2[2]) * (c1[2] - c2[2]));
	}
}
