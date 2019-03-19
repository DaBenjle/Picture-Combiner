import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Main implements ActionListener
{
	public static void main(String[] args)
	{
		new Main();
	}

	private ButtonGroup group;
	private JFrame frame;
	private boolean clickedFrame = false;
	private static GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	private BufferedImage target;
	private byte[][][] targetPixels, finishedPixels;

	public Main()
	{
		initFrame();
	}

	private void initFrame()
	{
		frame = new JFrame("Image Combiner");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(1, 0));
		smartFrameSetSize(300, 100);
		frame.setVisible(true);
		frame.pack();
		frame.repaint();

		JPanel panel = new JPanel(new GridLayout(0, 1, 0, 5));

		JPanel bPanel = new JPanel(new GridLayout(0, 2, 5, 0));
		group = new ButtonGroup();
		JRadioButtonMenuItem intake = new JRadioButtonMenuItem("Use Intake Folder", true);
		intake.setActionCommand("intake");
		JRadioButtonMenuItem google = new JRadioButtonMenuItem("Search Google");
		google.setActionCommand("google");
		group.add(intake);
		group.add(google);
		bPanel.add(intake);
		bPanel.add(google);

		JButton start = new JButton("Start");
		start.addActionListener(this);

		panel.add(bPanel);
		panel.add(start);

		frame.add(panel);
		frame.pack();
		frame.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(group.getSelection().getActionCommand().equals("google"))
		{
			updateFrame("Downloading images from Google.");
			//TODO Fill intake folder with images from google
		}
		boolean success = setTarget() && setIntake() && resizeImages() && setOutput();
		int intSuccess = success ? 0 : -1;
		if(success)
		{
			updateFrame("Finished with 0 errors");
		}
		else
		{
			updateFrame("Cancelled operations due to errors.");
		}
		new Timer().schedule(new TimerTask()
				{
					public void run()
					{
						System.exit(intSuccess);
					}
				}, 10000);
	}
	
	private boolean resizeImages()
	{
		updateFrame("Copying images into buffer and resizing them. (This may take a few minutes)");
		File buffer = new File("IntakeBuffer");
		if(buffer.exists())
		{
			if(!buffer.isDirectory())
			{
				updateFrame("Delete file in working directory \"IntakeBuffer\"");
				return false;
			}
		}
		else
		{
			if(!buffer.mkdir())
			{
				updateFrame("Error creating directory \"IntakeBuffer\"");
			}
		}
		
		
		for(File f : Intake.getContents())
		{
			try
			{
				BufferedImage pic = ImageIO.read(f);
				//TODO Change 100s to proper size
				BufferedImage scaled = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR);
				scaled.createGraphics().drawImage(pic, 0, 0, 100, 100, null);
				ImageIO.write(scaled, "png", new File("IntakeBuffer/" + getEndOfPath(f.getPath())));
			}
			catch (IOException e)
			{
				updateFrame("Error copying pictures to buffer, please ensure all files in \"Intake\" are images.");
				e.printStackTrace();
				return false;
			}	
		}
		
		return true;
	}
	
	private String getEndOfPath(String path)
	{
		String returnVal = "";
		for(int i = 0; i < path.length(); i++)
		{
			char cur = path.charAt(i);
			if(cur == '/' || cur == '\\')
				returnVal = "";
			else
				returnVal += cur;
		}
		return returnVal;
	}

	private boolean setOutput()
	{
		BufferedImage out = new BufferedImage(targetPixels[0].length, targetPixels.length, BufferedImage.TYPE_3BYTE_BGR);
		return true;
	}
	
	private boolean setTarget()
	{
		updateFrame("Attempting to set Target File");
		try
		{
			try
			{
				target = ImageIO.read(new File("Target.png"));
			}
			catch(IOException ex)
			{
				target = ImageIO.read(new File("Target.jpg"));
			}
			final int width = target.getWidth(), height = target.getHeight();
			BufferedImage newBi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
			newBi.createGraphics().drawImage(target, 0, 0, width, height, null);
			target = newBi;
		}
		catch (IOException e)
		{
			updateFrame("Target file not found!\nCancelling operations!");
			return false;
		}
		

		
		updateFrame("Finished setting target file.\nAttempting to load target pixels.");
		targetPixels = ColorMethods.rawArrTo3DArr(((DataBufferByte)target.getData().getDataBuffer()).getData(), target.getHeight(), target.getWidth(), 3);
		updateFrame("Finished loading target pixels.");
		return true;
	}
	
	private boolean setIntake()
	{
		updateFrame("Getting average colors of images. (This may take a few minutes)");
		String check = Intake.setFolder(new File("Intake")).run();
		if(check == null)
		{
			updateFrame((String.format("Not enough input files! You need at least %s files!", "5")));
		}
		else if(check.length() > 0)
		{
			updateFrame("Error reading " + check + " colors, please ensure all files in \"Intake\" are images.");
			return false;
		}
		updateFrame("Done");
		return true;
	}
	
	private void updateFrame(String message)
	{
		updateFrame(message, null);
	}
	
	private void updateFrame(String message, JComponent comp)
	{
		if(!clickedFrame)
		{
			Arrays.asList(frame.getContentPane().getComponents()).forEach((component) -> {frame.getContentPane().remove(component);});
			frame.getContentPane().add(new JScrollPane(new JTextArea(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
			smartFrameSetSize(800, 500);
			clickedFrame = true;
		}
		
		if(frame.getContentPane().getComponent(0) instanceof JScrollPane)
		{
			JScrollPane scrollPane = (JScrollPane) frame.getContentPane().getComponent(0);
			((JTextArea)scrollPane.getViewport().getView()).append(message + '\n');
		}
		if(comp != null) frame.getContentPane().add(comp);
		frame.pack();
		frame.paint(frame.getGraphics());
	}
	
	private void smartFrameSetSize(int width, int height)
	{
		frame.setPreferredSize(new Dimension(width, height));
		int sWidth = gd.getDisplayMode().getWidth();
		int sHeight = gd.getDisplayMode().getHeight();
		frame.setLocation(sWidth / 2 - width / 2, sHeight / 2 - height / 2);
		frame.pack();
		frame.repaint();
	}
}
