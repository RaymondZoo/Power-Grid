import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class PowerGridPanel extends JPanel implements MouseListener, KeyListener
{
	
	private int width;
	private int height;
	private String keyInput;
	
	private GameState game;
	
	
	//UIs
	private boolean START,MAPUI;
	
	
	public PowerGridPanel(int width, int height) throws IOException // we should really be doing try catch statements instead
	{
		super();
		addMouseListener(this);
		addKeyListener(this);
		setFocusable(true);
		setLayout(null);
		setSize(width, height);
		
		//game = new GameState();
		this.width = width;
		this.height = height;
		
		//Initializing each UI
		START = true;
		MAPUI = false;
		

	}
	
	public void paint(Graphics g) 
	{
		// Anti-aliases text so that it is smooth
		((Graphics2D) g).setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		if(START)
		{
			drawMainMenu(g);
		}
		else if(MAPUI)
		{
			drawMAPUI(g);
		}
		
		
	}
	public void drawMainMenu(Graphics g)
	{
		try
		{
			BufferedImage mainMenuBackground = ImageIO.read(new File("src/UI/MainMenu.jpg"));
			g.drawImage(mainMenuBackground, 0, 0, width, height, null);
		}
		catch(IOException e)
		{
			System.out.println("Cannot find Start image!");
		}
	}
	public void drawMAPUI(Graphics g)
	{
		try
		{
			BufferedImage mainMenuBackground = ImageIO.read(new File("src/UI/BestSoFar.jpg"));
			g.drawImage(mainMenuBackground, 0, 0, width, height, null);
		}
		catch(IOException e)
		{
			System.out.println("Cannot find Map image!");
		}
	}
	public void mousePressed(MouseEvent e)
	{
	
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}

	public void keyTyped(KeyEvent e) 
	{
		if(e.getExtendedKeyCode() == KeyEvent.VK_BACK_SPACE || e.getExtendedKeyCode() == 8)
	    {  
			//System.out.println("backspace");
			if(keyInput.length()>=1)
				keyInput = keyInput.substring(0, keyInput.length()-1);
	    }
		else if(e.getExtendedKeyCode() == KeyEvent.VK_ENTER || e.getExtendedKeyCode() == 10)
	    {  
			//System.out.println("enter");
			if(keyInput.length() >0)
				state.getTree().add(new BinaryNode(keyInput));
			keyInput = "";
	    }
		else 
			keyInput+=e.getKeyChar();
		
		
		repaint();
		
	}

	public void keyPressed(KeyEvent e) 
	{
		
	}
	public void keyReleased(KeyEvent e) 
	{
		
	}
}