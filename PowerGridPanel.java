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

public class PowerGridPanel extends JPanel implements MouseListener, KeyListener {

	private int width;
	private int height;
	private String keyInput;

	private GameState game;
	
	//Constants (tentative)~
	public static final int PPWIDTH = 260, PPHEIGHT = 200; // PP = powerplant
	public static final int MAPX = 1612, MAPY = 90; // Starting points for powerplants on mapUI
	public String hover = "";
	

	// Colors
	public static final Color GREEN = new Color(17, 59, 8);
	public static final Color TRANSPARENTBLACK = new Color(0, 0, 0, 150);

	// UIs
	private boolean MainMenu, MAPUI, AUCTION;

	public PowerGridPanel(int width, int height) throws IOException // we should really be doing try catch statements
																	// instead
	{
		super();
		addMouseListener(this);
		addKeyListener(this);
		setFocusable(true);
		setLayout(null);
		setSize(width, height);

		// game = new GameState();
		this.width = width;
		this.height = height;

		// Initializing each UI
		MainMenu = true;
		MAPUI = false;
		AUCTION = false;

	}

	public void paint(Graphics g) {
		// Anti-aliases text so that it is smooth
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		if (MainMenu) 
		{
			drawMainMenu(g);
		} 
		else if(AUCTION)
		{
			drawAUCTIONBACKGROUND(g);
		}
		else if (MAPUI) {
			drawMAPUI(g);
		}

	}

	public void drawMainMenu(Graphics g) {
		try {
			BufferedImage mainMenuBackground = ImageIO.read(PowerGridPanel.class.getResource("UI/MainMenu.jpg"));
			//BufferedImage mainMenuBackground = ImageIO.read(new File("src/UI/MainMenu.jpg"));
			g.drawImage(mainMenuBackground, 0, 0, width, height, null);

			g.setFont(new Font("Berlin Sans FB", Font.BOLD, 38));
			g.setColor(TRANSPARENTBLACK);

			int boxW = 300;
			int boxL = 100;

			g.fillRect((1920 - boxW) / 2, 497, boxW, boxL);

			g.fillRect((1920 - boxW) / 2, 700, boxW, boxL);

			g.setColor(Color.WHITE);

			g.drawString("START", 890, 555);
			g.drawString("QUIT", 910, 760);

		} catch (IOException e) {
			System.out.println("Cannot find main menu image!");
		}
	}
	
	public void drawMAPONLY(Graphics g) 
	{
		try {
			BufferedImage mainMenuBackground = ImageIO.read(PowerGridPanel.class.getResource("UI/ALT.jpg"));
			//ImageIO.read(new File("src/UI/BestSoFar.jpg"));
			g.setColor(GREEN); // We could do the player's color here ~ 
			g.fillRect(0, 0, width, height);
			g.drawImage(mainMenuBackground, 0, 0, 1468, 1033, null);
			//1535, 1080
			} catch (IOException e) {
				System.out.println("Cannot find Map image!");
			}
		/*
		BufferedImage mainMenuBackground = ImageIO.read(PowerGridPanel.class.getResource("UI/BestSoFar.jpg"));
		//ImageIO.read(new File("src/UI/BestSoFar.jpg"));
		g.setColor(GREEN); // We could do the player's color here ~
		g.fillRect(0, 0, width, height);
		g.drawImage(mainMenuBackground, 0, 0, 1468, 1033, null);
		//1535, 1080*/
		
		//plus city and turn order ~
		
	
	}

	public void drawMAPUI(Graphics g) {
		drawMAPONLY(g);// We could do the player's color here ~ like for the side bar
		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 38));
		g.setColor(TRANSPARENTBLACK);
		g.drawString("MONEY:", 1573, 48);
		g.setColor(Color.WHITE);
		g.drawString("MONEY:", 1570, 45); //add currentPlayer money here ~
		
		g.setFont(new Font("Berlin Sans FB", Font.PLAIN, 20));
		g.setColor(TRANSPARENTBLACK);
		g.drawString("Power Plants:", 1673, 73);
		g.setColor(Color.WHITE);
		g.drawString("Power Plants:", 1670, 70); 
		
		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 30));
		g.setColor(TRANSPARENTBLACK);
		g.drawString("MESSAGE BOARD:", 1573, 783);
		g.setColor(Color.WHITE);
		g.drawString("MESSAGE BOARD:", 1570, 780); 
		
		
		//Powerplants
		for(int i = 0; i<3; i++)
		{
			g.setColor(TRANSPARENTBLACK);//shadow
			g.fillRect(MAPX+10, MAPY+(i*(PPHEIGHT+20))+10, PPWIDTH, PPHEIGHT);
			
			//ACTUAL PP
		}
		g.setFont(new Font("Berlin Sans FB", Font.PLAIN, 20));
		//End Turn
		g.setColor(TRANSPARENTBLACK);
		g.fillRect(537, 20, 260, 80);
		g.setColor(GREEN); 
		g.fillRect(527, 10, 260, 80);
		g.setColor(TRANSPARENTBLACK);
		g.drawString("END TURN", 608, 48);
		g.setColor(Color.WHITE);
		g.drawString("END TURN", 605, 45);
		
		//Buy
		g.setColor(TRANSPARENTBLACK);
		g.fillRect(25, 675, 100, 80);
		g.setColor(GREEN); 
		g.fillRect(15, 665, 100, 80);
		g.setColor(TRANSPARENTBLACK);
		g.drawString("BUY", 51, 718);
		g.setColor(Color.WHITE);
		g.drawString("BUY", 48, 715);
		
		
		//Other Players
		g.setColor(TRANSPARENTBLACK);
		g.fillRect(1360, 650, 150, 400);
		g.setColor(GREEN); 
		g.fillRect(1350, 640, 150, 400);
		g.setColor(TRANSPARENTBLACK);
		g.drawString("Other Players", 1363, 663);
		g.setColor(Color.WHITE);
		g.drawString("Other Players", 1360, 660);
		
		int colorX = 1380, colorY = 690;
		for(int i = 0; i<3; i++)
		{
			g.setColor(TRANSPARENTBLACK);//shadow
			g.fillRect(colorX+10, colorY+(i*(100+10))+10, 100, 100);
			
			//ACTUAL Color
		}
	}
	public void drawAUCTIONBACKGROUND(Graphics g) 
	{
		g.setColor(GREEN);
		g.fillRect(0, 0, width, height);
		
		//TEMPORARY BUTTON ~
		g.setColor(Color.WHITE);
		g.fillRect(1715, 990, 205, 90);
		g.setFont(new Font("Berlin Sans FB", Font.PLAIN, 15));
		g.setColor(Color.BLACK);
		g.drawString("Switch UI",1760,1010);
		
		//Other Players
		g.setColor(TRANSPARENTBLACK);
		g.fillRect(10, 10, 150, 520);
		g.setColor(GREEN); 
		g.fillRect(0, 0, 150, 520);
		g.setColor(TRANSPARENTBLACK);
		g.drawString("Other Views", 48, 43);
		g.setColor(Color.WHITE);
		g.drawString("Other Views", 45, 40);
		
		int colorX = 30, colorY = 50;
		for(int i = 0; i<4; i++)
		{
			if(i<3)
			{
				g.setColor(TRANSPARENTBLACK);//shadow
				g.fillRect(colorX+10, colorY+(i*(100+10))+10, 100, 100);
				
				//ACTUAL Color
				g.setColor(Color.DARK_GRAY);
				g.fillRect(colorX, colorY+(i*(100+10)), 100, 100);
			}
			else
			{
				g.setColor(TRANSPARENTBLACK);//shadow
				g.fillRect(colorX+10, colorY+(i*(100+10))+10, 100, 100);
				
				g.setColor(Color.DARK_GRAY);
				g.fillRect(colorX, colorY+(i*(100+10)), 100, 100);
				
				g.setFont(new Font("Berlin Sans FB", Font.PLAIN, 15));
				g.setColor(TRANSPARENTBLACK);
				g.drawString("MAP", colorX+38, colorY+(i*(100+10))+53);
				g.setColor(Color.WHITE);
				g.drawString("MAP", colorX+35, colorY+(i*(100+10))+50);
			}
		}
		
	}
	

	public void mousePressed(MouseEvent e) {
		System.out.println(e.getX() + ", " + e.getY()); // for debugging and testing
		if (MainMenu) {
			int boxW = 300;
			int boxL = 100;
			/*
			 * g.fillRect((1920-boxW)/2, 497, boxW, boxL); g.fillRect((1920-boxW)/2, 700,
			 * boxW, boxL);
			 */

			if (e.getX() >= (1920 - boxW) / 2 && e.getX() <= (1920 - boxW) / 2 + boxW && e.getY() >= 497
					&& e.getY() <= 597) // IF START
			{
				MainMenu = false;
				AUCTION = true;//should be find regions ~
			}
			if (e.getX() >= (1920 - boxW) / 2 && e.getX() <= (1920 - boxW) / 2 + boxW && e.getY() >= 700
					&& e.getY() <= 800) // IF QUIT
			{
				System.exit(0);

			}
		}
		else if(AUCTION)
		{

			if (e.getX() >= 1715 && e.getY() >= 990)  //temporary button for Switching UIS
			AUCTION = false;
			MAPUI = true;
		}

		repaint();

	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) 
	{
	}

	public void mouseExited(MouseEvent e) {
	}

	public void keyTyped(KeyEvent e) {
		if (e.getExtendedKeyCode() == KeyEvent.VK_BACK_SPACE || e.getExtendedKeyCode() == 8) {
			// System.out.println("backspace");
			if (keyInput.length() >= 1)
				keyInput = keyInput.substring(0, keyInput.length() - 1);
		} else if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER || e.getExtendedKeyCode() == 10) {
			// System.out.println("enter");
			if (keyInput.length() > 0)
				// BID
				keyInput = "";
		} else if (e.getExtendedKeyCode() >= 48 && e.getExtendedKeyCode() <= 57)
			keyInput += e.getKeyChar();

		repaint();

	}

	public void keyPressed(KeyEvent e) {

	}

	public void keyReleased(KeyEvent e) {

	}
}