import java.io.File;

public class Intake extends Thread
{
	private static Intake instance = null;
	private File folder = null;
	
	private Intake()
	{
		
	}
	
	@Override
	public void run()
	{
		super.run();
		for(File fl : folder.listFiles()) System.out.println(fl.toString());
	}
	
	public Intake getInstance()
	{
		if(instance == null) instance = new Intake();
		return instance;
	}
	
	public static Intake setFolder(File input) throws IllegalArgumentException
	{
		System.out.println(input);
		if(input.listFiles() == null)
		{
			throw new IllegalArgumentException();
		}
		instance.folder = input;
		return instance;
	}
}
