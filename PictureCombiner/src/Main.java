import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

public class Main
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

		JPanel startPanel = new JPanel(new GridLayout(0, 1, 0, 5));

			JPanel intakePanel = new JPanel(new GridLayout(0, 2, 5, 0));
			group = new ButtonGroup();
			JRadioButtonMenuItem intake = new JRadioButtonMenuItem("Use Intake Folder", true);
			intake.setActionCommand("intake");
			JRadioButtonMenuItem google = new JRadioButtonMenuItem("Search Google");
			google.setActionCommand("google");
			group.add(intake);
			group.add(google);
			intakePanel.add(intake);
			intakePanel.add(google);
	
			JButton start = new JButton("Start");
			start.addActionListener((ActionEvent event) -> selectSettingsNowAction(event));
	
			startPanel.add(intakePanel);
			startPanel.add(start);
		

			
		frame.getContentPane().add(startPanel);
		frame.pack();
		frame.repaint();
	}
	
	/*
	 * Resets text if its not an integer and if its not within the range (min inclusive, max exclusive).
	 * If you set max to 0 it will be an infinite value as well as adding functionality for the user to input 0 to mean infinite as well.
	 * If you set min = to max then max will be an infinite value, but it will not accept 0s.
	 */
	public static InputVerifier getValidater(int min, int max, boolean squareroot)
	{
		return new InputVerifier()
		{
			@Override
			public boolean verify(JComponent input)
			{
				return validate((JTextComponent)input, min, max, squareroot);
			}
			
			@Override
			public boolean shouldYieldFocus(JComponent source)
			{
				if(!verify(source))
				{
					((JTextComponent)source).setText("");
				}
				return true;
			}
		};
	}
	
	public static boolean validate(JTextComponent comp, int min, int max, boolean squareRoot)
	{
		int val;
		if(max == 0)
		{
			max = Integer.MAX_VALUE;
		}
		try
		{
			val = Integer.parseInt(comp.getText());
		}
		catch(NumberFormatException e)
		{
			return false;
		}
		if(squareRoot)
			if(Math.sqrt(val) != (int)Math.sqrt(val))
				return false;
		if(min == max)
			return val >= min;
		return (max == Integer.MAX_VALUE && val == 0) || (val >= min && val < max);
	}

	private void selectSettingsNowAction(ActionEvent e)
	{
		if(group.getSelection().getActionCommand().equals("google"))
		{
			updateFrame("Downloading images from Google.");
			//TODO Fill intake folder with images from google
		}
		updateFrame("Please change settings as needed ->");
		
		JPanel optionsPanel = new JPanel(new GridLayout(0, 2, 0, 5));
			optionsPanel.setEnabled(false);
			
			final JTextField repeats = new JTextField();
			repeats.setBackground(new Color(245, 245, 245));
			repeats.setInputVerifier(getValidater(1, 0, false));
			
			final JTextField pixelsPerPicture = new JTextField();
			pixelsPerPicture.setBackground(new Color(245, 245, 245));
			pixelsPerPicture.setInputVerifier(getValidater(1, 1, true));
			
			final JButton start = new JButton();
			start.addActionListener((ActionEvent startEvent) -> 
			{
				Boolean filled = true;
				Arrays.asList(optionsPanel.getComponents()).forEach((Object o) -> 
				{
					if(o instanceof JTextField)
						if(((JTextField)o).getText().equals(""))
							System.out.print(true);
				});
			});
			
			optionsPanel.add(new JLabel("<html>How many times can images be used. Must be a positive integer. 0 Represents infinite uses.</html>"));
			optionsPanel.add(repeats);
			optionsPanel.add(new JLabel("<html>How many pixels per picture, must be squarerootable so that the width is an integer.</html>"));
			optionsPanel.add(pixelsPerPicture);
			optionsPanel.add(new JLabel("<html>Press when all fields are filled and you are ready to begin.</html>"));
			optionsPanel.add(start);
			
		frame.add(optionsPanel);
	}
	
	private void startAction()
	{
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
		//BufferedImage out = new BufferedImage(targetPixels[0].length, targetPixels.length, BufferedImage.TYPE_3BYTE_BGR);
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
			JTextArea area = new JTextArea();
			area.setBackground(new Color(220, 220, 220));
			frame.getContentPane().add(new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
			smartFrameSetSize(1000, 200);
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
