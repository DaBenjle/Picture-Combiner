import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

public class Main implements ActionListener
{
	public static void main(String[] args)
	{
		new Main();
	}

	private ButtonGroup group;

	public Main()
	{
		initFrame();
	}

	private void initFrame()
	{
		JFrame frame = new JFrame("Image Combiner");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int width = 300;
		int height = 100;
		frame.setPreferredSize(new Dimension(width, height));
		frame.setVisible(true);
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int sWidth = gd.getDisplayMode().getWidth();
		int sHeight = gd.getDisplayMode().getHeight();
		frame.setLocation(sWidth / 2 - width / 2, sHeight / 2 - height / 2);
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
		frame.validate();
		frame.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch (group.getSelection().getActionCommand())
		{
			case "intake":
				Intake.setFolder(new File("Intake")).run();
				break;
				
			case "google":
				//TODO
				break;
		}
	}
}
