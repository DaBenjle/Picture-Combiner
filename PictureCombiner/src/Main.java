import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

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
		updateFrame("Getting average colors of images. (This may take a few minutes)");
		Intake.setFolder(new File("Intake")).run();
		
		final int width = 50, height = 100;
		BufferedImage bi = new BufferedImage(Intake.getColors().length / 3 * width, height, BufferedImage.TYPE_3BYTE_BGR);
		for(int i = 0; i < Intake.getColors().length / 3; i++)
		{
			int blue = Intake.getColors()[i * 3] & 0xff, green = Intake.getColors()[i * 3 + 1] & 0xff, red = Intake.getColors()[i * 3 + 2] & 0xff;
			Graphics2D g = bi.createGraphics();
			g.setColor(new java.awt.Color(red, green, blue));
			g.fillRect(i * width, 0, width, height);
		}
		
		JPanel temp = new JPanel()
		{
			public void paint(Graphics g)
			{
				g.drawImage(bi, 0, 0, bi.getWidth(), bi.getHeight(), null);
			}
		};
		updateFrame("Done", temp);
	}
	
	private void updateFrame(String message)
	{
		JComponent comp = null;
		try
		{
			comp = (JComponent)frame.getComponent(1);
		}
		catch(Exception ex)
		{
			
		}
		updateFrame(message, comp);
	}
	
	private void updateFrame(String message, JComponent comp)
	{
		if(!clickedFrame)
		{
			Arrays.asList(frame.getContentPane().getComponents()).forEach((component) -> {frame.getContentPane().remove(component);});
			frame.getContentPane().add(new JTextField());
			smartFrameSetSize(800, 500);
			clickedFrame = true;
		}
		
		if(frame.getContentPane().getComponent(0) instanceof JTextField)
		{
			((JTextField) frame.getContentPane().getComponent(0)).setText(message);
		}
		if(comp != null) frame.getContentPane().add(comp, 1);
		frame.pack();
		frame.repaint();
	}
	
	private void smartFrameSetSize(int width, int height)
	{
		frame.setPreferredSize(new Dimension(width, height));
		int sWidth = gd.getDisplayMode().getWidth();
		int sHeight = gd.getDisplayMode().getHeight();
		frame.setLocation(sWidth / 2 - width / 2, sHeight / 2 - height / 2);
	}
}
