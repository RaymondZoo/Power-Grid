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
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PowerGridPanel extends JPanel implements MouseListener, KeyListener {

	private boolean hasSelected;
	private int fourthindex;
	private HashMap<Player, Integer> numCitiesPowered = new HashMap<Player, Integer>();
	private int width;
	private int index;
	private int height;
	private String keyInput;
	private int citiesPowered=0;
	private PowerPlant powerPlantforResource = null;
	HashMap<Player, Integer> endNumCitiesPowered = new HashMap<Player, Integer>();

	GameState gs;

	// Constants (tentative)~
	public static final int PPWIDTH = 210, PPHEIGHT = 200; // PP = powerplant //this is for map only //265*256 original
															// proportions
	public static final int MAPX = 1500, MAPY = 90; // Starting points for powerplants on mapUI
	public static final int MARKETX = 500, MARKETY = 130;

	private ArrayList<String> selectedRegions; // aka playableColors
	// Colors
	public static final Color GREEN = new Color(17, 59, 8);
	public static final Color TRANSPARENTBLACK = new Color(0, 0, 0, 150);

	// UIs
	private Player view;
	private boolean mapView;
	private boolean MainMenu, REGIONS, MAPUI, AUCTION, FOURTH, END;
	// FOURTH is when there is a fourth powerplant
	// gamestate's endOfGame will also be one

	// Current variables
	private int currPlayer;
	private int auctionPlayer;
	private ArrayList<Player> players;
	private boolean round1;
	private int auctionIndex;
	private int minBid;
	private String selectedCity;
	HashMap<String, ArrayList<Coord>> displayList;

	public PowerGridPanel(int width, int height) throws IOException // we should really be doing try catch statements
																	// instead
	{
		super();
		index = -1;
		hasSelected = false;
		fourthindex = -1;
		displayList = new HashMap<String, ArrayList<Coord>>();
		addMouseListener(this);
		addKeyListener(this);
		setFocusable(true);
		setLayout(null);
		setSize(width, height);

		keyInput = "";

		this.width = width;
		this.height = height;

		selectedRegions = new ArrayList<String>();

		// Initializing each UI
		MainMenu = true;
		MAPUI = false;
		AUCTION = false;
		REGIONS = false;
		FOURTH = false;
		view = null;
		mapView = false;
		END = false;

		gs = new GameState();
		players = gs.getPlayerOrder();
		//System.out.println(players);
		currPlayer = 0;
		round1 = true;
		auctionIndex = -1;
		selectedCity = "";

	}

	public void paint(Graphics g) {
		// Anti-aliases text so that it is smooth
		// try {
		// gs.setPhase(5);

		// DRAWVIEW SHOULD COME FIRST

		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		if (MainMenu) {
			drawMainMenu(g);
		} else if (REGIONS) {
			drawRegion(g);
		} else if (view != null || mapView) {
			drawView(g);
		} else if (AUCTION) {
			drawAUCTION(g);
		} else if (FOURTH) {
			drawFOURTH(g);
		} else if (MAPUI) {
			drawMAPUI(g);
		} else if (END) {
			drawEND(g);
		}
	}

	public void drawMainMenu(Graphics g) {
		try {
			BufferedImage mainMenuBackground = ImageIO.read(PowerGridPanel.class.getResource("UI/MainMenu.jpg"));
			// BufferedImage mainMenuBackground = ImageIO.read(new
			// File("src/UI/MainMenu.jpg"));
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

	public void drawRegion(Graphics g) {
		drawMAPONLY(g);

		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 30));
		g.setColor(TRANSPARENTBLACK);
		g.drawString("MESSAGE BOARD:", 1573, 48);
		g.setColor(Color.WHITE);
		g.drawString("MESSAGE BOARD:", 1570, 45);
		g.setFont(new Font("Berlin Sans FB", Font.PLAIN, 20));
		g.setColor(Color.WHITE);
		g.drawString("-Choose a region:", 1570, 80);

		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 30));
		g.drawString("PLAYERS:", 1570, 130);

		g.setFont(new Font("Berlin Sans FB", Font.PLAIN, 20));
		g.setColor(TRANSPARENTBLACK);
		g.fillRect(357, 158, 100, 50);
		g.setColor(Color.MAGENTA);
		g.fillRect(347, 148, 100, 50);
		g.setColor(Color.WHITE);
		g.drawString("Purple", 360, 180);
		if (selectedRegions.contains("purple")) {
			drawCheck(347, 148, g);
		}

		g.setColor(TRANSPARENTBLACK);
		g.fillRect(685, 140, 100, 50);
		g.setColor(Color.YELLOW);
		g.fillRect(675, 130, 100, 50);
		g.setColor(Color.BLACK);
		g.drawString("Yellow", 690, 160);
		if (selectedRegions.contains("yellow")) {
			drawCheck(675, 130, g);
		}

		g.setColor(TRANSPARENTBLACK);
		g.fillRect(480, 690, 100, 50);
		g.setColor(Color.BLUE);
		g.fillRect(470, 680, 100, 50);
		g.setColor(Color.WHITE);
		g.drawString("Blue", 485, 710);
		if (selectedRegions.contains("blue")) {
			drawCheck(470, 680, g);
		}

		g.setColor(TRANSPARENTBLACK);
		g.fillRect(1305, 315, 100, 50);
		g.setColor(Color.ORANGE);
		g.fillRect(1295, 305, 100, 50);
		g.setColor(Color.WHITE);
		g.drawString("Orange", 1310, 335);
		if (selectedRegions.contains("orange")) {
			drawCheck(1295, 305, g);
		}

		g.setColor(TRANSPARENTBLACK);
		g.fillRect(680, 810, 100, 50);
		g.setColor(Color.RED);
		g.fillRect(670, 800, 100, 50);
		g.setColor(Color.WHITE);
		g.drawString("Red", 685, 830);
		if (selectedRegions.contains("red")) {
			drawCheck(670, 800, g);
		}

		g.setColor(TRANSPARENTBLACK);
		g.fillRect(1180, 840, 100, 50);
		g.setColor(Color.GREEN);
		g.fillRect(1170, 830, 100, 50);
		g.setColor(Color.WHITE);
		g.drawString("Green", 1185, 860);
		if (selectedRegions.contains("green")) {
			drawCheck(1170, 830, g);
		}
		//System.out.println(currPlayer);

		int textX = 1570, textY = 190;
		for (int i = 0; i < players.size(); i++) {
			String temp = "";
			if (players.get(currPlayer).equals(players.get(i))) {
				temp = "(You)";
			}
			g.drawString("-" + temp + players.get(i).getColor().toUpperCase(), textX, textY + (i * 80));
			temp = "";
		}

	}

	public void drawMAPONLY(Graphics g) {
		try {
			// BufferedImage mainMenuBackground =
			// ImageIO.read(PowerGridPanel.class.getResource("UI/ALT2.jpg")); //pretty good
			// ImageIO.read(new File("src/UI/BestSoFar.jpg"));

			BufferedImage TopLeft = ImageIO.read(PowerGridPanel.class.getResource("UI/TopLeftALT.jpg"));
			BufferedImage TopRight = ImageIO.read(PowerGridPanel.class.getResource("UI/TopRightALT.jpg"));
			BufferedImage BottomLeft = ImageIO.read(PowerGridPanel.class.getResource("UI/BottomLeftALT.jpg"));
			BufferedImage BottomRight = ImageIO.read(PowerGridPanel.class.getResource("UI/BottomRightALT.jpg"));

			g.setColor(GREEN); // We could do the player's color here ~
			g.fillRect(0, 0, width, height);
			// g.drawImage(mainMenuBackground, 0, 0, 1468, 1033, null);
			// 1535, 1080 former width, height

			g.drawImage(TopLeft, 0, 0, 738, 516, null);
			g.drawImage(TopRight, 738, 0, 738, 516, null);
			g.drawImage(BottomLeft, 0, 516, 738, 516, null);
			g.drawImage(BottomRight, 738, 516, 738, 516, null);

		} catch (IOException e) {
			System.out.println("Cannot find Map image!");
		}
		/*
		 * BufferedImage mainMenuBackground =
		 * ImageIO.read(PowerGridPanel.class.getResource("UI/BestSoFar.jpg"));
		 * //ImageIO.read(new File("src/UI/BestSoFar.jpg")); g.setColor(GREEN); // We
		 * could do the player's color here g.fillRect(0, 0, width, height);
		 * g.drawImage(mainMenuBackground, 0, 0, 1468, 1033, null); //1535, 1080
		 */

		// CHECK ~
		try {
			BufferedImage coal = ImageIO.read(PowerGridPanel.class.getResource("UI/coal.PNG"));
			int sizeCoal = gs.countResource(gs.getCoalMarket());
			for (int i = 24 - sizeCoal + 1; i <= 24; i++) {
				g.drawImage(coal, -2 + 35 * i + 10 * ((i - 1) / 3), 908, 30, 30, null);
			}

			BufferedImage oil = ImageIO.read(PowerGridPanel.class.getResource("UI/oil.PNG"));
			int sizeOil = gs.countResource(gs.getOilMarket());
			for (int i = 24 - sizeOil + 1; i <= 24; i++) {
				g.drawImage(oil, 7 + 25 * i + 40 * ((i - 1) / 3), 945, 30, 30, null);
			}

			BufferedImage trash = ImageIO.read(PowerGridPanel.class.getResource("UI/trash.PNG"));
			int sizeTrash = gs.countResource(gs.getTrashMarket());
			for (int i = 24 - sizeTrash + 1; i <= 24; i++) {
				g.drawImage(trash, -2 + 35 * i + 10 * ((i - 1) / 3), 980, 30, 30, null);
			}

			BufferedImage nuclear = ImageIO.read(PowerGridPanel.class.getResource("UI/nuclear.PNG"));
			int sizeNuclear = gs.countResource(gs.getNuclearMarket());
			int num = sizeNuclear;
			if (num > 0) {
				g.drawImage(nuclear, 998, 967, 45, 45, null);
				num--;
			}
			if (num > 0) {
				g.drawImage(nuclear, 950, 967, 45, 45, null);
				num--;
			}
			if (num > 0) {
				g.drawImage(nuclear, 998, 905, 45, 45, null);
				num--;
			}
			if (num > 0) {
				g.drawImage(nuclear, 950, 905, 45, 45, null);
				num--;
			}
			int inv = 8 - num + 1;
			for (int i = inv; i <= 8; i++) {
				g.drawImage(nuclear, 113 + 114 * (i - 1), 948, 30, 30, null);
			}
		} catch (IOException e) {
			System.out.println("Cannot find Map image!");
		}
		try {

			for (String s : displayList.keySet()) {
				for (Coord c : displayList.get(s)) {
					BufferedImage house = ImageIO.read(PowerGridPanel.class.getResource("UI/" + s + ".PNG"));
					g.drawImage(house, c.getX(), c.getY(), 20, 20, null);
					//System.out.println(s + " at " + c.toString());
				}
			}
		} catch (IOException e) {
			System.out.println("Cannot find Map image!");
		}

	}

	public void drawMAPUI(Graphics g) /* throws IOException */ {

		drawMAPONLY(g);
		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 38));
		g.setColor(TRANSPARENTBLACK);
		g.drawString("MONEY: " + players.get(currPlayer).getMoney(), 1573, 48);
		g.setColor(Color.WHITE);
		g.drawString("MONEY: " + players.get(currPlayer).getMoney(), 1570, 45);

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
		/*
		 * try { BufferedImage coal =
		 * ImageIO.read(PowerGridPanel.class.getResource("UI/coal.PNG")); int
		 * sizeCoal=gs.countResource(gs.getCoalMarket()); for (int
		 * i=24-sizeCoal+1;i<=24;i++) { g.drawImage(coal, -2+35*i+10*((i-1)/3), 908, 30,
		 * 30, null); }
		 * 
		 * BufferedImage oil =
		 * ImageIO.read(PowerGridPanel.class.getResource("UI/oil.PNG")); int
		 * sizeOil=gs.countResource(gs.getOilMarket()); for (int
		 * i=24-sizeOil+1;i<=24;i++) { g.drawImage(oil, 7+25*i+40*((i-1)/3), 945, 30,
		 * 30, null); }
		 * 
		 * BufferedImage trash =
		 * ImageIO.read(PowerGridPanel.class.getResource("UI/trash.PNG")); int
		 * sizeTrash=gs.countResource(gs.getTrashMarket()); for (int
		 * i=24-sizeTrash+1;i<=24;i++) { g.drawImage(trash, -2+35*i+10*((i-1)/3), 980,
		 * 30, 30, null); }
		 * 
		 * BufferedImage nuclear =
		 * ImageIO.read(PowerGridPanel.class.getResource("UI/nuclear.PNG")); int
		 * sizeNuclear=gs.countResource(gs.getNuclearMarket()); int num=sizeNuclear; if
		 * (num>0) { g.drawImage(nuclear, 950, 967, 45, 45, null); num--; } if (num>0) {
		 * g.drawImage(nuclear, 998, 967, 45, 45, null); num--; } if (num>0) {
		 * g.drawImage(nuclear, 950, 905, 45, 45, null); num--; } if (num>0) {
		 * g.drawImage(nuclear, 998, 905, 45, 45, null); num--; } int inv=8-num+1; for
		 * (int i=inv;i<=8;i++) { g.drawImage(nuclear, 113+114*(i-1), 948, 30, 30,
		 * null); } } catch (IOException e) {
		 * System.out.println("Cannot find Map image!"); }
		 */
		if (gs.getPhase() == 3) {
			g.setColor(TRANSPARENTBLACK);
			g.fillRect(464, 772, 100, 50);
			g.setColor(Color.BLACK);
			g.fillRect(454, 762, 100, 50);
			g.setColor(Color.WHITE);
			g.drawString("Coal", 469, 799);

			g.setColor(TRANSPARENTBLACK);
			g.fillRect(603, 773, 100, 50);
			g.setColor(Color.BLACK);
			g.fillRect(593, 762, 100, 50);
			g.setColor(Color.WHITE);
			g.drawString("Oil", 608, 799);

			g.setColor(TRANSPARENTBLACK);
			g.fillRect(464, 841, 100, 50);
			g.setColor(Color.BLACK);
			g.fillRect(454, 831, 100, 50);
			g.setColor(Color.WHITE);
			g.drawString("Trash", 460, 868);

			g.setColor(TRANSPARENTBLACK);
			g.fillRect(603, 841, 100, 50);
			g.setColor(Color.BLACK);
			g.fillRect(593, 831, 100, 50);
			g.setColor(Color.WHITE);
			g.drawString("Nuke", 608, 868);
		}
		/*
		 * if(gs.getPhase() ==4) { g.setColor(TRANSPARENTBLACK); g.fillRect(55, 695,
		 * 100, 50); g.setColor(GREEN); g.fillRect(45, 685, 100, 50);
		 * g.setColor(Color.WHITE); g.drawString("BUY", 60, 722); }
		 */
		// Your powerplants

		// correct
		// TEST~
		/*
		 * if (players.get(currPlayer).getPowerList().size()<3) {
		 * ArrayList<String>cost1=new ArrayList<String>(); cost1.add("coal||oil");
		 * cost1.add("coal||oil"); players.get(currPlayer).getPowerList().add(new
		 * PowerPlant(5,cost1,1));
		 * 
		 * ArrayList<String>cost2=new ArrayList<String>(); cost2.add("coal||oil");
		 * players.get(currPlayer).getPowerList().add(new PowerPlant(29,cost2,4));
		 * 
		 * ArrayList<String>cost3=new ArrayList<String>(); cost3.add("coal||oil");
		 * cost3.add("coal||oil"); cost3.add("coal||oil");
		 * players.get(currPlayer).getPowerList().add(new PowerPlant(46,cost3,7));
		 * System.out.println(players.get(currPlayer).getPowerList().size()); }//remove
		 * this ~
		 */

		for (int i = 0; i < players.get(currPlayer).getPowerList().size(); i++) {
			g.setColor(TRANSPARENTBLACK);// shadow
			g.fillRect(MAPX + 10, MAPY + (i * (PPHEIGHT + 20)) + 10, PPWIDTH, PPHEIGHT);

			// ACTUAL PP
			try {
				BufferedImage card = ImageIO.read(PowerGridPanel.class
						.getResource("UI/" + players.get(currPlayer).getPowerList().get(i).getMinBid() + ".PNG"));
				g.drawImage(card, MAPX, MAPY + (i * (PPHEIGHT + 20)), PPWIDTH, PPHEIGHT, null);
				drawRes(MAPX, MAPY + (i * (PPHEIGHT + 20)), g, players.get(currPlayer).getPowerList().get(i));

				if (gs.getPhase() == 3) // resource selection check marks
				{
					if (index == i) {
						drawCheck(MAPX + 170, MAPY + (i * (PPHEIGHT + 20)) + 10, g);
					}
				}

				if (gs.getPhase() == 5) {
					if (players.get(currPlayer).getPowerList().get(i).isHybrid()) {
						g.setFont(new Font("Berlin Sans FB", Font.PLAIN, 15));
						// System.out.println(players.get(currPlayer).getPowerList().get(i).getMinBid()+"
						// is a hybrid");
						for (int j = 0; j < (players.get(currPlayer).getPowerList().get(i).getCost().size()) + 1; j++) {
							int temp = (players.get(currPlayer).getPowerList().get(i).getCost().size()) + 1;
							g.setColor(Color.GRAY);
							g.fillRect(MAPX + PPWIDTH,
									(MAPY + (i * (PPHEIGHT + 20))) + (j * (PPHEIGHT
											/ (players.get(currPlayer).getPowerList().get(i).getCost().size() + 1))),
									200,
									(PPHEIGHT / (players.get(currPlayer).getPowerList().get(i).getCost().size() + 1))
											- 10);
						}

						if (players.get(currPlayer).getPowerList().get(i).getCost().size() == 1) {
							ArrayList<String> temp = players.get(currPlayer).getPowerList().get(i).getCost();
							String res1 = temp.get(0).substring(0, temp.get(0).indexOf("||"));
							String res2 = temp.get(0).substring(temp.get(0).indexOf("||") + 2);
							g.setColor(Color.WHITE);
							g.drawString("1 " + res1, MAPX + PPWIDTH + 50, MAPY + (i * (PPHEIGHT + 20)) + 50);
							g.drawString("1 " + res2, MAPX + PPWIDTH + 50, MAPY + (i * (PPHEIGHT + 20)) + 150);
						}
						else if (players.get(currPlayer).getPowerList().get(i).getCost().size() == 2) {
							ArrayList<String> temp = players.get(currPlayer).getPowerList().get(i).getCost();
							String res1 = temp.get(0).substring(0, temp.get(0).indexOf("||"));
							String res2 = temp.get(0).substring(temp.get(0).indexOf("||") + 2);
							g.setColor(Color.WHITE);
							g.drawString("2 " + res1, MAPX + PPWIDTH + 50, MAPY + (i * (PPHEIGHT + 20)) + 30);
							g.drawString("1 " + res1 + " 1 " + res1, MAPX + PPWIDTH + 50,
									MAPY + (i * (PPHEIGHT + 20)) + 100);
							g.drawString("2 " + res2, MAPX + PPWIDTH + 50, MAPY + (i * (PPHEIGHT + 20)) + 170);
						}
						else if (players.get(currPlayer).getPowerList().get(i).getCost().size() == 3) {
							ArrayList<String> temp = players.get(currPlayer).getPowerList().get(i).getCost();
							String res1 = temp.get(0).substring(0, temp.get(0).indexOf("||"));
							String res2 = temp.get(0).substring(temp.get(0).indexOf("||") + 2);
							g.setColor(Color.WHITE);
							g.drawString("3 " + res1, MAPX + PPWIDTH + 50, MAPY + (i * (PPHEIGHT + 20)) + 20);
							g.drawString("1 " + res1 + " 2 " + res1, MAPX + PPWIDTH + 50,
									MAPY + (i * (PPHEIGHT + 20)) + 70);
							g.drawString("2 " + res1 + " 1 " + res1, MAPX + PPWIDTH + 50,
									MAPY + (i * (PPHEIGHT + 20)) + 120);
							g.drawString("3 " + res2, MAPX + PPWIDTH + 50, MAPY + (i * (PPHEIGHT + 20)) + 170);
						}
					}

					else {
						g.setFont(new Font("Berlin Sans FB", Font.BOLD, 30));
						g.setColor(Color.GRAY);
						g.fillRect(MAPX + PPWIDTH, MAPY + (i * (PPHEIGHT + 20)), 200, PPHEIGHT);
						g.setColor(Color.WHITE);
						g.drawString("BURN", MAPX + PPWIDTH + 50, MAPY + (i * (PPHEIGHT + 20)) + 100);
					}

				}
			} catch (IOException e) {
				System.out.println("Cannot find Map image!");
			}
		}

		// Other Players
		g.setColor(TRANSPARENTBLACK);
		g.fillRect(1300, 650, 150, 400);
		g.setColor(GREEN);
		g.fillRect(1290, 640, 150, 400);
		g.setColor(TRANSPARENTBLACK);
		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 20));
		g.drawString("Other Players", 1303, 663);
		g.setColor(Color.WHITE);
		g.drawString("Other Players", 1300, 660);

		g.setFont(new Font("Berlin Sans FB", Font.PLAIN, 15));
		// temp
		/*
		 * for (int i = 0; i < 3; i++) { g.setColor(TRANSPARENTBLACK);// shadow
		 * g.fillRect(MAPX + 10, MAPY + (i * (PPHEIGHT + 20)) + 10, PPWIDTH, PPHEIGHT);
		 * // ACTUAL PP if (i==0) { try { BufferedImage card =
		 * ImageIO.read(PowerGridPanel.class.getResource("UI/5.PNG")); g.drawImage(card,
		 * MAPX, MAPY + (i * (PPHEIGHT + 20)), PPWIDTH, PPHEIGHT, null); } catch
		 * (IOException e) { System.out.println("Cannot find Map image!"); } } if (i==1)
		 * { try { BufferedImage card =
		 * ImageIO.read(PowerGridPanel.class.getResource("UI/29.PNG"));
		 * g.drawImage(card, MAPX, MAPY + (i * (PPHEIGHT + 20)), PPWIDTH, PPHEIGHT,
		 * null); } catch (IOException e) {
		 * System.out.println("Cannot find Map image!"); } } if (i==2) { try {
		 * BufferedImage card =
		 * ImageIO.read(PowerGridPanel.class.getResource("UI/42.PNG"));
		 * g.drawImage(card, MAPX, MAPY + (i * (PPHEIGHT + 20)), PPWIDTH, PPHEIGHT,
		 * null); } catch (IOException e) {
		 * System.out.println("Cannot find Map image!"); } } }
		 */

		g.setFont(new Font("Berlin Sans FB", Font.PLAIN, 20));
		// End Turn
		g.setColor(TRANSPARENTBLACK);
		g.fillRect(537, 20, 260, 80);
		g.setColor(GREEN);
		g.fillRect(527, 10, 260, 80);
		g.setColor(TRANSPARENTBLACK);
		g.drawString("END TURN", 608, 48);
		g.setColor(Color.WHITE);
		g.drawString("END TURN", 605, 45);

		// System.out.println("top"+currPlayer);
		ArrayList<Player> Others = new ArrayList<Player>();
		for (int i = 0; i < players.size(); i++) {
			if (i != currPlayer) {
				Others.add(players.get(i));
				// System.out.println("top"+players.get(i).getColor());
			}
		}

		int colorX = 1310, colorY = 690;
		for (int i = 0; i < Others.size(); i++) {
			g.setColor(TRANSPARENTBLACK);// shadow
			g.fillRect(colorX + 10, colorY + (i * (100 + 10)) + 10, 100, 100);

			// ACTUAL Color

			if (Others.get(i).getColor().equals("purple")) {
				g.setColor(Color.MAGENTA);
				g.fillRect(colorX, colorY + (i * (100 + 10)), 100, 100);
			} else if (Others.get(i).getColor().equals("yellow")) {
				g.setColor(Color.YELLOW);
				g.fillRect(colorX, colorY + (i * (100 + 10)), 100, 100);
			} else if (Others.get(i).getColor().equals("blue")) {
				g.setColor(Color.BLUE);
				g.fillRect(colorX, colorY + (i * (100 + 10)), 100, 100);
			} else if (Others.get(i).getColor().equals("green")) {
				g.setColor(Color.GREEN);
				g.fillRect(colorX, colorY + (i * (100 + 10)), 100, 100);
			} else if (Others.get(i).getColor().equals("red")) {
				g.setColor(Color.RED);
				g.fillRect(colorX, colorY + (i * (100 + 10)), 100, 100);
			} else if (Others.get(i).getColor().equals("black")) {
				g.setColor(Color.BLACK);
				g.fillRect(colorX, colorY + (i * (100 + 10)), 100, 100);
			}

		}
		/*
		 * try {
		 * 
		 * for (String s:displayList.keySet()) { for (Coord c:displayList.get(s)) {
		 * BufferedImage house =
		 * ImageIO.read(PowerGridPanel.class.getResource("UI/"+s+".PNG"));
		 * g.drawImage(house, c.getX(), c.getY(), 20, 20, null);
		 * System.out.println(s+" at "+c.toString()); } } } catch (IOException e) {
		 * System.out.println("Cannot find Map image!"); }
		 */

		// Player color
		g.setColor(GREEN);
		g.fillRect(0, 0, 340, 112);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 30));
		g.drawString(players.get(currPlayer).getColor().toUpperCase() + " player", 30, 60);
		
		g.setColor(GREEN);
		g.fillRect(935, 0, 543, 125);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 30));
		g.setColor(TRANSPARENTBLACK);
		g.drawString("STEP "+gs.getStep()+", PHASE "+gs.getPhase(), 973, 60);
		g.setColor(Color.WHITE);
		g.drawString("STEP "+gs.getStep()+", PHASE "+gs.getPhase(), 970, 60);
		
		

	}

	public void drawAUCTIONBACKGROUND(Graphics g) {
		g.setColor(GREEN);
		g.fillRect(0, 0, width, height);
		/*
		 * // TEMPORARY BUTTON ~ g.setColor(Color.WHITE); g.fillRect(1715, 990, 205,
		 * 90); g.setFont(new Font("Berlin Sans FB", Font.PLAIN, 15));
		 * g.setColor(Color.BLACK); g.drawString("Switch UI", 1760, 1010);
		 */

		// Other Players
		System.out.println("AuctionBACKGROUND:" + currPlayer);
		System.out.println(players.get(currPlayer).getColor());

		ArrayList<Player> Others = new ArrayList<Player>();
		for (int i = 0; i < players.size(); i++) {
			if (i != currPlayer) {
				Others.add(players.get(i));
			}
		}

		g.setColor(TRANSPARENTBLACK);
		g.fillRect(10, 10, 150, 520);
		g.setColor(GREEN);
		g.fillRect(0, 0, 150, 520);
		g.setColor(TRANSPARENTBLACK);
		g.drawString("Other Views", 43, 43);
		g.setColor(Color.WHITE);
		g.drawString("Other Views", 40, 40);

		int colorX = 30, colorY = 50;
		for (int i = 0; i < 4; i++) {
			if (i < 3) {
				g.setColor(TRANSPARENTBLACK);// shadow
				g.fillRect(colorX + 10, colorY + (i * (100 + 10)) + 10, 100, 100);

				// ACTUAL Color ~
				/*
				 * g.setColor(Color.DARK_GRAY); g.fillRect(colorX, colorY+(i*(100+10)), 100,
				 * 100);
				 */
				if (Others.get(i).getColor().equals("purple")) {
					g.setColor(Color.MAGENTA);
					g.fillRect(colorX, colorY + (i * (100 + 10)), 100, 100);
				} else if (Others.get(i).getColor().equals("yellow")) {
					g.setColor(Color.YELLOW);
					g.fillRect(colorX, colorY + (i * (100 + 10)), 100, 100);
				} else if (Others.get(i).getColor().equals("blue")) {
					g.setColor(Color.BLUE);
					g.fillRect(colorX, colorY + (i * (100 + 10)), 100, 100);
				} else if (Others.get(i).getColor().equals("green")) {
					g.setColor(Color.GREEN);
					g.fillRect(colorX, colorY + (i * (100 + 10)), 100, 100);
				} else if (Others.get(i).getColor().equals("red")) {
					g.setColor(Color.RED);
					g.fillRect(colorX, colorY + (i * (100 + 10)), 100, 100);
				} else if (Others.get(i).getColor().equals("black")) {
					g.setColor(Color.BLACK);
					g.fillRect(colorX, colorY + (i * (100 + 10)), 100, 100);
				}
			} else {
				g.setColor(TRANSPARENTBLACK);// shadow
				g.fillRect(colorX + 10, colorY + (i * (100 + 10)) + 10, 100, 100);

				g.setColor(Color.DARK_GRAY);
				g.fillRect(colorX, colorY + (i * (100 + 10)), 100, 100);

				g.setFont(new Font("Berlin Sans FB", Font.PLAIN, 15));
				g.setColor(TRANSPARENTBLACK);
				g.drawString("MAP", colorX + 38, colorY + (i * (100 + 10)) + 53);
				g.setColor(Color.WHITE);
				g.drawString("MAP", colorX + 35, colorY + (i * (100 + 10)) + 50);
			}
		}
		if (gs.getStep() != 3) {
			g.setFont(new Font("Berlin Sans FB", Font.BOLD, 30));
			g.setColor(TRANSPARENTBLACK);
			g.drawString("MESSAGE BOARD:", 1573, 48);
			g.setColor(Color.WHITE);
			g.drawString("MESSAGE BOARD:", 1570, 45);
		} else {
			g.setFont(new Font("Berlin Sans FB", Font.BOLD, 30));
			g.setColor(TRANSPARENTBLACK);
			g.drawString("MESSAGE BOARD:", 1573, 412);
			g.setColor(Color.WHITE);
			g.drawString("MESSAGE BOARD:", 1570, 409);
		}
		
		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 30));
		g.setColor(TRANSPARENTBLACK);
		g.drawString("STEP "+gs.getStep()+", PHASE "+gs.getPhase(), 263, 103);
		g.setColor(Color.WHITE);
		g.drawString("STEP "+gs.getStep()+", PHASE "+gs.getPhase(), 260, 100);
		
	}

	public void drawAUCTION(Graphics g) {
		drawAUCTIONBACKGROUND(g);
		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 38));
		g.setColor(TRANSPARENTBLACK);
		g.drawString("AUCTION", 863, 48);
		g.setColor(Color.WHITE);
		g.drawString("AUCTION", 860, 45);

		System.out.println(players);

		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 20));
		g.setColor(TRANSPARENTBLACK);
		g.drawString("MONEY: " + players.get(currPlayer).getMoney(), 13, 558);
		g.setColor(Color.WHITE);
		g.drawString("MONEY: " + players.get(currPlayer).getMoney(), 10, 555);

		g.setColor(TRANSPARENTBLACK);
		g.drawString("YOUR POWERPLANTS:", 13, 583);
		g.setColor(Color.WHITE);
		g.drawString("YOUR POWERPLANTS:", 10, 580);

		int AUCTIONX = 10, AUCTIONY = 595;
		int side = 120; // only for this UI

		// Your powerplant
		for (int i = 0; i < players.get(currPlayer).getPowerList().size(); i++) {
			g.setColor(TRANSPARENTBLACK);// shadow
			g.fillRect(AUCTIONX + 10, AUCTIONY + (i * (side + 15)) + 10, side, side);

			// ACTUAL PP
			// g.setColor(Color.DARK_GRAY);
			// g.fillRect(AUCTIONX, AUCTIONY+(i*(side+15)), side, side);
			try {
				BufferedImage card = ImageIO.read(PowerGridPanel.class
						.getResource("UI/" + players.get(currPlayer).getPowerList().get(i).getMinBid() + ".PNG"));

				g.drawImage(card, AUCTIONX, AUCTIONY + (i * (side + 15)), side, side, null);
				drawSmallRes(AUCTIONX, AUCTIONY + (i * (side + 15)), g, players.get(currPlayer).getPowerList().get(i));

			} catch (IOException e) {
				System.out.println("Cannot find Map image!");
			}

		} /*
			 * for (int i = 0; i < 3; i++) { g.setColor(TRANSPARENTBLACK);// shadow
			 * g.fillRect(AUCTIONX + 10, AUCTIONY + (i * (side + 15)) + 10, side, side);
			 * 
			 * // ACTUAL PP // g.setColor(Color.DARK_GRAY); // g.fillRect(AUCTIONX,
			 * AUCTIONY+(i*(side+15)), side, side); try { BufferedImage card =
			 * ImageIO.read(PowerGridPanel.class.getResource("UI/35.jpg"));
			 * 
			 * g.drawImage(card, AUCTIONX, AUCTIONY + (i * (side + 15)), side, side, null);
			 * drawSmallRes(AUCTIONX, AUCTIONY + (i * (side + 15)), g , null);
			 * 
			 * } catch (IOException e) { System.out.println("Cannot find Map image!"); }
			 * 
			 * }
			 */

		// Market
		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 20));
		g.setColor(TRANSPARENTBLACK);
		g.drawString("CURRENT MARKET:", 268, 218);
		g.setColor(Color.WHITE);
		g.drawString("CURRENT MARKET:", 265, 215);
		for (int i = 0; i < gs.getCurrentMarket().size(); i++) // current Market
		{
			g.setColor(TRANSPARENTBLACK);// shadow
			g.fillRect(MARKETX + (i * (PPWIDTH + 20)) + 10, MARKETY + 10, PPWIDTH, PPHEIGHT);

			// ACTUAL PP
			// g.setColor(Color.DARK_GRAY);
			// g.fillRect(AUCTIONX, AUCTIONY+(i*(side+15)), side, side);
			try {
				BufferedImage card = ImageIO.read(
						PowerGridPanel.class.getResource("UI/" + gs.getCurrentMarket().get(i).getMinBid() + ".PNG")); // change
																														// this
																														// to

				g.drawImage(card, MARKETX + (i * (PPWIDTH + 20)), MARKETY, PPWIDTH, PPHEIGHT, null);

				if (auctionIndex == i) {
					drawCheck(MARKETX + (i * (PPWIDTH + 20)) + 170, MARKETY + 10, g);
				}

			} catch (IOException e) {
				System.out.println("Cannot find Map image!");
			}

		}
		if (gs.getStep() != 3) {
			g.setFont(new Font("Berlin Sans FB", Font.BOLD, 20));
			g.setColor(TRANSPARENTBLACK);
			g.drawString("FUTURE MARKET:", 268, 493);
			g.setColor(Color.WHITE);
			g.drawString("FUTURE MARKET:", 265, 490);
			int space = 265;
			for (int i = 0; i < gs.getFutureMarket().size(); i++) // future Market
			{
				g.setColor(TRANSPARENTBLACK);// shadow
				g.fillRect(MARKETX + (i * (PPWIDTH + 20)) + 10, (MARKETY + space) + 10, PPWIDTH, PPHEIGHT);

				// ACTUAL PP
				// g.setColor(Color.DARK_GRAY);
				// g.fillRect(AUCTIONX, AUCTIONY+(i*(side+15)), side, side);
				try {
					BufferedImage card = ImageIO.read(
							PowerGridPanel.class.getResource("UI/" + gs.getFutureMarket().get(i).getMinBid() + ".PNG"));
					g.drawImage(card, MARKETX + (i * (PPWIDTH + 20)), MARKETY + space, PPWIDTH, PPHEIGHT, null);

				} catch (IOException e) {
					System.out.println("Cannot find Map image!");
				}

			}
		}
		g.setColor(Color.WHITE);
		g.fillRect(940, 644, 300, 50);

		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 38));
		g.setColor(TRANSPARENTBLACK);
		g.drawString("Enter Bid", 725, 683);
		g.setColor(Color.WHITE);
		g.drawString("Enter Bid:", 722, 680);

		g.setColor(Color.BLACK);
		if (keyInput != null) {
			g.drawString(keyInput, 950, 680);
		}

		// Bid/Pass

		int bidX = 500, bidY = 810; // 360, 100
		for (int i = 0; i < 2; i++) {
			g.setColor(TRANSPARENTBLACK);// shadow
			g.fillRect(bidX + (i * (360 + 20)) + 10, bidY + 10, 360, 100);

			g.setColor(Color.DARK_GRAY);
			g.fillRect(bidX + (i * (360 + 20)), bidY, 360, 100);
		}
		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 38));
		g.setColor(Color.WHITE);
		g.drawString("BID", 640, 875);
		g.drawString("PASS", 1000, 875); // 880, 810

		System.out.println("Auction:" + currPlayer);
		System.out.println(players.get(currPlayer).getColor());
		g.drawString(players.get(currPlayer).getColor().toUpperCase() + " player", 295, 45); // 880, 810

		int highest = 0;
		for (int i = 0; i < players.size(); i++) {
			if (gs.getBids().get(players.get(i)) > highest) {
				highest = gs.getBids().get(players.get(i));
			}
		}

		g.drawString("Current Bid: " + highest, 1170, 45); // 880, 810
	}

	public void drawFOURTH(Graphics g) {
		drawAUCTIONBACKGROUND(g);
		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 38));
		g.setColor(TRANSPARENTBLACK);
		g.drawString("4th Powerplant", 843, 48);
		g.setColor(Color.WHITE);
		g.drawString("4th Powerplant", 840, 45);

		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 20));
		g.setColor(TRANSPARENTBLACK);
		g.drawString("MONEY: " + players.get(currPlayer).getMoney(), 13, 558);
		g.setColor(Color.WHITE);
		g.drawString("MONEY: " + players.get(currPlayer).getMoney(), 10, 555);
//FIX~
		// temp
		/*
		 * if (players.get(currPlayer).getPowerList().size()<3) { ArrayList<String>
		 * cost=new ArrayList<String>(); cost.add("coal||oil"); cost.add("coal||oil");
		 * players.get(currPlayer).getPowerList().add(new PowerPlant(12, cost, 2));
		 * 
		 * ArrayList<String> cost1=new ArrayList<String>();
		 * players.get(currPlayer).getPowerList().add(new PowerPlant(13, cost1, 1));
		 * 
		 * ArrayList<String> cost2=new ArrayList<String>(); cost2.add("coal");
		 * cost2.add("coal"); players.get(currPlayer).getPowerList().add(new
		 * PowerPlant(25, cost2, 5)); }
		 */

		for (int i = 0; i < players.get(currPlayer).getPowerList().size(); i++) // Powerplants
		{
			g.setColor(TRANSPARENTBLACK);// shadow
			g.fillRect(MARKETX + (i * (PPWIDTH + 20)) + 10, MARKETY + 10, PPWIDTH, PPHEIGHT);

			// ACTUAL PP
			// g.setColor(Color.DARK_GRAY);
			// g.fillRect(AUCTIONX, AUCTIONY+(i*(side+15)), side, side);
			try {
				BufferedImage card = ImageIO.read(PowerGridPanel.class
						.getResource("UI/" + players.get(currPlayer).getPowerList().get(i).getMinBid() + ".PNG"));
				g.drawImage(card, MARKETX + (i * (PPWIDTH + 20)), MARKETY, PPWIDTH, PPHEIGHT, null);
				drawRes(MARKETX + (i * (PPWIDTH + 20)), MARKETY, g, players.get(currPlayer).getPowerList().get(i));
				if (!(i == fourthindex)) {
					if (players.get(currPlayer).getPowerList().get(i).isHybrid()) {
						g.setFont(new Font("Berlin Sans FB", Font.PLAIN, 20));
						g.setColor(Color.GRAY);
						g.fillRect(MARKETX + (i * (PPWIDTH + 20)), MARKETY + PPHEIGHT + 20, PPWIDTH, 75);
						g.setColor(Color.WHITE);
						g.drawString("MOVE COAL", MARKETX + (i * (PPWIDTH + 20)) + 50, MARKETY + PPHEIGHT + 60);

						g.setColor(Color.GRAY);
						g.fillRect(MARKETX + (i * (PPWIDTH + 20)), MARKETY + PPHEIGHT + 20 + 75 + 10, PPWIDTH, 75);
						g.setColor(Color.WHITE);
						g.drawString("MOVE OIL", MARKETX + (i * (PPWIDTH + 20)) + 50,
								MARKETY + PPHEIGHT + 10 + 75 + 10 + 50);

					} else {
						g.setFont(new Font("Berlin Sans FB", Font.BOLD, 30));
						g.setColor(Color.GRAY);
						g.fillRect(MARKETX + (i * (PPWIDTH + 20)), MARKETY + PPHEIGHT + 20, PPWIDTH, 75);
						g.setColor(Color.WHITE);
						g.drawString("MOVE", MARKETX + (i * (PPWIDTH + 20)) + 50, MARKETY + PPHEIGHT + 60);
					}
				}

			} catch (IOException e) {
				System.out.println("Cannot find Map image!");
			}

		}

		g.setColor(TRANSPARENTBLACK);// shadow
		g.fillRect(825, 890, 360, 100);

		g.setColor(Color.DARK_GRAY);
		g.fillRect(815, 880, 360, 100);

		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 38));
		g.setColor(Color.WHITE);
		g.drawString("END TURN", 905, 935);

	}

	public void displayMessage(String line) {

	}

	public void drawView(Graphics g) {

		if (mapView) {
			drawMAPONLY(g);
		} else {

			g.setColor(GREEN);
			g.fillRect(0, 0, width, height);

			g.setFont(new Font("Berlin Sans FB", Font.BOLD, 38));
			g.setColor(TRANSPARENTBLACK);
			g.drawString(view.getColor().toUpperCase() + "'s situation", 843, 48);
			g.setColor(Color.WHITE);
			g.drawString(view.getColor().toUpperCase() + "'s situation", 840, 45);

			int viewX = 663, viewY = 315;

			for (int i = 0; i < view.getPowerList().size(); i++) // Powerplants
			{
				g.setColor(TRANSPARENTBLACK);// shadow
				g.fillRect(viewX + (i * (PPWIDTH + 20)) + 10, viewY + 10, PPWIDTH, PPHEIGHT);

				// ACTUAL PP
				// g.setColor(Color.DARK_GRAY);
				// g.fillRect(AUCTIONX, AUCTIONY+(i*(side+15)), side, side);
				try {
					BufferedImage card = ImageIO.read(
							PowerGridPanel.class.getResource("UI/" + view.getPowerList().get(i).getMinBid() + ".PNG"));
					g.drawImage(card, viewX + (i * (PPWIDTH + 20)), viewY, PPWIDTH, PPHEIGHT, null);

					drawRes(viewX + (i * (PPWIDTH + 20)), viewY, g, view.getPowerList().get(i));

				} catch (IOException e) {
					System.out.println("Cannot find Map image!");
				}

			}
			g.setFont(new Font("Berlin Sans FB", Font.BOLD, 38));
			g.setColor(Color.WHITE);
			g.drawString("MONEY: " + view.getMoney(), 880, 590);
			g.drawString("Number of Cities: " + gs.getNumCities().get(view), 800, 650);
		}
		g.setColor(Color.RED);
		g.fillRect(1720, 0, 200, 80);

		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 20));
		g.setColor(Color.WHITE);
		g.drawString("CLOSE", 1790, 50);

	}

	public void drawEND(Graphics g) {
		int n = players.size();
		for (int i = 1; i < n; ++i) {
			Player key = players.get(i);
			int j = i - 1;
			while (j >= 0 && playerComp(players.get(j), key, this.endNumCitiesPowered) > 0) {
				players.set(j + 1, players.get(j));
				j = j - 1;
			}
			players.set(j + 1, key);
		}
		try {
			BufferedImage end = ImageIO.read(PowerGridPanel.class.getResource("UI/END.png"));
			g.drawImage(end, 0, 0, width, height, null);

		} catch (IOException e) {
			System.out.println("Cannot find main menu image!");
		}

		// players last to first, color, amount of money,
		// endNumCitiesPowered.get(player), network
		g.setFont(new Font("Berlin Sans FB", Font.BOLD, 38));
		g.setColor(Color.BLACK);
		for (int i = 0; i < players.size(); i++) {
			g.drawString(i + 1 + "", 115, 540 + (i * 100));
		}
		int ENDX = 460, ENDY = 840;
		for (int i = 0; i < players.size(); i++) {

			g.drawString(""+players.get(i).getColor().toUpperCase(), ENDX, ENDY - (i * 100));
			g.drawString(""+endNumCitiesPowered.get(players.get(i)), ENDX + 350, ENDY - (i * 100));
			g.drawString(""+players.get(i).getMoney(), ENDX + 700, ENDY - (i * 100));
			g.drawString(""+gs.getNumCities().get(players.get(i)), ENDX + 1050, ENDY - (i * 100));

		}

	}

	public int playerComp(Player p1, Player p2, HashMap<Player, Integer> temp) {
		if (temp.get(p1) != temp.get(p2)) {
			return temp.get(p1) - temp.get(p2);
		} else {
			if (p1.getMoney() != p2.getMoney()) {
				return p1.getMoney() - p1.getMoney();
			} else {
				if (gs.getNumCities().get(p1) != gs.getNumCities().get(p2)) {
					return gs.getNumCities().get(p1) - gs.getNumCities().get(p2);
				} else {
					double d = Math.random();
					if (d > 0.5) {
						return 1;
					} else {
						return -1;
					}
				}
			}
		}
	}

	public void drawCheck(int x, int y, Graphics g) {
		try {
			BufferedImage mainMenuBackground = ImageIO.read(PowerGridPanel.class.getResource("UI/selected.png"));
			g.drawImage(mainMenuBackground, x, y, 50, 50, null);

		} catch (IOException e) {
			System.out.println("Cannot find main menu image!");
		}
	}

	public void drawRes(int x, int y, Graphics g, PowerPlant pp) {
		try {
			BufferedImage coal = ImageIO.read(PowerGridPanel.class.getResource("UI/coal.PNG"));
			BufferedImage oil = ImageIO.read(PowerGridPanel.class.getResource("UI/oil.PNG"));
			BufferedImage trash = ImageIO.read(PowerGridPanel.class.getResource("UI/trash.PNG"));
			BufferedImage nuclear = ImageIO.read(PowerGridPanel.class.getResource("UI/nuclear.PNG"));

			int resX = x + 25, resY = y + 50;
			int size = 30;
			int sep = 30;

			for (int i = 0; i < pp.getStorage().size(); i++) {
				if (pp.getStorage().get(i).equals("coal")) {
					g.drawImage(coal, resX + (i * sep), resY, size, size, null);
				} else if (pp.getStorage().get(i).equals("trash")) {
					g.drawImage(trash, resX + (i * sep), resY, size, size, null);
				} else if (pp.getStorage().get(i).equals("oil")) {
					g.drawImage(oil, resX + (i * sep), resY, size, size, null);
				} else if (pp.getStorage().get(i).equals("nuclear")) {
					g.drawImage(nuclear, resX + (i * sep), resY, size, size, null);
				}

			}

			/*
			 * for(int i = 0 ; i< 6;i++) //remove later ~ { g.drawImage(coal, resX+(i*sep),
			 * resY, size, size, null);
			 * 
			 * }
			 */

		} catch (IOException e) {
			System.out.println("Cannot find Map image!");
		}
	}

	public void drawSmallRes(int x, int y, Graphics g, PowerPlant pp) {
		try {
			BufferedImage coal = ImageIO.read(PowerGridPanel.class.getResource("UI/coal.PNG"));
			BufferedImage oil = ImageIO.read(PowerGridPanel.class.getResource("UI/oil.PNG"));
			BufferedImage trash = ImageIO.read(PowerGridPanel.class.getResource("UI/trash.PNG"));
			BufferedImage nuclear = ImageIO.read(PowerGridPanel.class.getResource("UI/nuclear.PNG"));

			int resX = x, resY = y + 35;
			int size = 25;
			int sep = 20;

			// if(pp != null)
			// {
			for (int i = 0; i < pp.getStorage().size(); i++) {
				if (pp.getStorage().get(i).equals("coal")) {
					g.drawImage(coal, resX + (i * sep), resY, size, size, null);
				} else if (pp.getStorage().get(i).equals("oil")) {
					g.drawImage(trash, resX + (i * sep), resY, size, size, null);
				} else if (pp.getStorage().get(i).equals("trash")) {
					g.drawImage(oil, resX + (i * sep), resY, size, size, null);
				} else if (pp.getStorage().get(i).equals("nuclear")) {
					g.drawImage(nuclear, resX + (i * sep), resY, size, size, null);
				}

			}
			// }
			/*
			 * else { for(int i = 0 ; i< 6;i++) //remove later ~ { g.drawImage(coal,
			 * resX+(i*sep), resY, size, size, null);
			 * 
			 * } }
			 */
		} catch (IOException e) {
			System.out.println("Cannot find Map image!");
		}
	}

	public void mousePressed(MouseEvent e) {
		System.out.println(gs.getDecision());
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
				REGIONS = true;// should be find regions ~

			}
			if (e.getX() >= (1920 - boxW) / 2 && e.getX() <= (1920 - boxW) / 2 + boxW && e.getY() >= 700
					&& e.getY() <= 800) // IF QUIT
			{
				System.exit(0);

			}
		} else if (view != null || mapView) {
			if (e.getX() >= 1720 && e.getY() <= 80) {
				view = null;
				mapView = false;
			}
		} else if (REGIONS) {
			String[] colors = { "purple", "orange", "blue", "green", "red", "yellow" };
			ArrayList thing = new ArrayList(Arrays.asList(colors));
			String region = findRegion(e);
			// System.out.println("region:" + region);
			if (thing.contains(region) && adjacent(region) && !selectedRegions.contains(region)) {
				currPlayer++;

				selectedRegions.add(region);
			}

			if (selectedRegions.size() == 4) {
				System.out.println("Sel Region List: "+selectedRegions);
				ArrayList<City> cityList = gs.getListOfCites();
				ArrayList<City> adjustedCityList = new ArrayList<City>();
				for (City c : cityList) {
					if (selectedRegions.contains(c.getZoneColor())) {
						adjustedCityList.add(c);
					}
				}
				gs.setListOfCites(adjustedCityList);
				System.out.println("Adj Region List: "+adjustedCityList);

				if (round1) {
					gs.randomizePlayerOrder();
				} else {
					gs.determinePlayerOrder();
				}

				players = gs.getPlayerOrder();

				gs.rearrangeMarket();

				AUCTION = true;
				REGIONS = false;

				currPlayer = 0;
			}

		} else if (AUCTION) {
			gs.setPhase(2);
			// if (e.getX() >= 1715 && e.getY() >= 990) // temporary button for Switching
			// UIs~
			// {
			// AUCTION = false;
			// FOURTH = true;
			// } //TEST ~

			// int colorX = 30, colorY = 50;
			// g.fillRect(0, 0, 150, 520);
			displayMessage("Select a card to bid on and then type up the bid amount ");
			ArrayList<Player> Others = new ArrayList<Player>();
			for (int i = 0; i < players.size(); i++) {
				if (i != currPlayer) {
					Others.add(players.get(i));
				}
			}
			// System.out.println("bug");
			if (e.getX() >= 0 && e.getX() <= 150 && e.getY() >= 0 && e.getY() <= 520) // if other player view
			{
				// System.out.println()

				int colorX = 30, colorY = 50;
				for (int i = 0; i < Others.size() + 1; i++) {// (colorX, colorY + (i * (100 + 10)), 100, 100);
					if (i < 3 && e.getX() >= colorX && e.getX() <= colorX + 100 && e.getY() >= colorY + (i * (100 + 10))
							&& e.getY() <= colorY + (i * (100 + 10)) + 100) {
						// System.out.println("bug");
						view = Others.get(i);
					} else if (i == 3 && e.getX() >= colorX && e.getX() <= colorX + 100
							&& e.getY() >= colorY + (i * (100 + 10)) && e.getY() <= colorY + (i * (100 + 10)) + 100) // map
																														// view
					{
						mapView = true;
						System.out.println("reached");
					}
					// System.out.println(i);
				}
			}

			if (gs.getAuctionCard() == null) {
				// ArrayList<Player> tempPlayers = gs.getPlayerOrder();
				// int minPrice = 0;
				for (int j = 0; j < gs.getCurrentMarket().size(); j++) {
					// MARKETX + (i * (PPWIDTH + 20)), MARKETY, PPWIDTH, PPHEIGHT,
					if (e.getX() >= MARKETX + (j * (PPWIDTH + 20))
							&& e.getX() <= MARKETX + (j * (PPWIDTH + 20)) + PPWIDTH && e.getY() >= MARKETY
							&& e.getY() <= MARKETY + PPHEIGHT) {
						if (auctionIndex == j) {
							auctionIndex = -1;
						} else {
							auctionIndex = j;
						}
					}
				}
				// boolean bidded = false;
				if (!keyInput.equals("") && e.getX() >= 500 && e.getX() <= (500 + 360) && e.getY() >= 810
						&& e.getY() <= (810 + 100)) // bid starting auction
				{
					// bidded = true;
					if (auctionIndex!=-1&&Integer.parseInt(keyInput) >= gs.getCurrentMarket().get(auctionIndex).getMinBid()) {
						gs.setAuctionCard(gs.getCurrentMarket().get(auctionIndex));

						minBid = Integer.parseInt(keyInput);

						gs.getBids().put(players.get(currPlayer), Integer.parseInt(keyInput));

						int nextIndex = nextIndex();

						if (nextIndex == -1) {

							System.out.println("Index is -1");
							Player winner = null;
							// give player powerplant
							for (int i = 0; i < players.size(); i++) {
								if (gs.getBids().get(players.get(i)) > 0) {
									winner = players.get(i);
								}
							}
							winner.subtractMoney(minBid); // CHECK ~
							gs.getCurrentMarket().remove(gs.getAuctionCard());
							gs.addPowerPlant();
							winner.addPowerPlant(gs.getAuctionCard());
							gs.getDecision().put(winner, true);
							gs.setAuctionCard(null);
							if (winner.getPowerList().size() == 4) {
								FOURTH = true;
								AUCTION = false;
							 }
							if (gs.getMarketStep3()) {
								gs.restructureMarket();
							}
							AUCTION = false;
							MAPUI = true;
							currPlayer = 3;
							index =-1;
							gs.nextPhase();
						} else {
							System.out.println(players.get(nextIndex).getColor());
							currPlayer = nextIndex;
							keyInput = "";
						}

					}
				}
				if (!round1 && e.getX() >= 880 && e.getX() <= (880 + 360) && e.getY() >= 810 && e.getY() <= (810 + 100)) // pass
																															// starting
																															// auction
				{
					// auctionIndex = -1;
					// gs.getDecision().put(tempPlayers.get(0), true);
					// tempPlayers.remove(tempPlayers.get(0));
					gs.getDecision().put(players.get(currPlayer), true);
					int nextIndex = -1;

					for (int i = 0; i < players.size(); i++) {
						if (gs.getDecision().get(players.get(i)) == false 
								&& gs.getBids().get(players.get(i)) <= 0) {
							nextIndex = i;
							break;
						}
					}

					if (nextIndex == -1) {
						gs.resetBid();
						minBid = 0;
						auctionIndex = -1;
						currPlayer = 3;
						AUCTION = false;
						MAPUI = true;
						if (gs.getMarketStep3()) {
							gs.restructureMarket();
						}
						index =-1;
						gs.nextPhase();
					} else {
						currPlayer = nextIndex;

					}
				}
			} else {
				displayMessage("It's round 1 so you need to bid for a powerplant");
				if (!keyInput.equals("") && e.getX() >= 500 && e.getX() <= (500 + 360) && e.getY() >= 810
						&& e.getY() <= (810 + 100)) // bid
				{
					if (Integer.parseInt(keyInput) >= minBid) {
						minBid = Integer.parseInt(keyInput);

						gs.getBids().put(players.get(currPlayer), Integer.parseInt(keyInput));

						int nextIndex = -1;

						for (int i = currPlayer; i < players.size(); i++) {
							if (gs.getDecision().get(players.get(i)) == false && i != currPlayer
									&& gs.getBids().get(players.get(i)) >= 0
									&& gs.getBids().get(players.get(i)) < minBid) {
								nextIndex = i;
								break;
							}
						}
						if (nextIndex == -1) {
							for (int i = 0; i < currPlayer; i++) {
								if (gs.getDecision().get(players.get(i)) == false && i != currPlayer
										&& gs.getBids().get(players.get(i)) >= 0
										&& gs.getBids().get(players.get(i)) < minBid) {
									nextIndex = i;
									break;
								}
							}
							currPlayer=nextIndex;
							keyInput="";
						} else {
							currPlayer = nextIndex;
							keyInput = "";
						}

					}
				} else if (e.getX() >= 880 && e.getX() <= (880 + 360) && e.getY() >= 810 && e.getY() <= (810 + 100)) // pass
				{
					gs.getBids().put(players.get(currPlayer), -1);
					System.out.println(gs.isAuctionDone());
					if (gs.isAuctionDone()) {
						System.out.println("Bids: "+gs.getBids());
						Player winner = null;
						// give player powerplant
						for (int i = 0; i < players.size(); i++) {
							if (gs.getBids().get(players.get(i)) > 0) {
								winner = players.get(i);
							}
						}
						System.out
								.println("Player " + winner.getColor() + " won power plant for" + minBid + " elektro");
						winner.subtractMoney(minBid); // CHECK ~
						gs.getCurrentMarket().remove(gs.getAuctionCard());
						gs.addPowerPlant();
						winner.addPowerPlant(gs.getAuctionCard());
						gs.getDecision().put(winner, true);
						System.out.println(gs.getDecision());
						gs.setAuctionCard(null);
						gs.resetBid();
						minBid = 0;
						auctionIndex = -1;
						if (winner.getPowerList().size() == 4) {
							FOURTH = true;
							AUCTION = false;
						}
						/*
						 * int nextIndex = -1;
						 * 
						 * for(int i = 0 ; i<players.size(); i++) {
						 * if(gs.getDecision().get(players.get(i)) == false && i != currPlayer) {
						 * nextIndex = i; } } currPlayer = nextIndex;
						 */
						int nextIndex = nextIndex();
						if (nextIndex == -1) {

//							System.out.println("Index is -1");
//							Player winner = null;
//							// give player powerplant
//							for (int i = 0; i < players.size(); i++) {
//								if (gs.getBids().get(players.get(i)) > -1) {
//									winner = players.get(i);
//								}
//							}
//							winner.subtractMoney(minBid); // CHECK ~
//							gs.getCurrentMarket().remove(gs.getAuctionCard());
//							gs.addPowerPlant();
//							winner.addPowerPlant(gs.getAuctionCard());
//							gs.getDecision().put(winner, true);
//							gs.setAuctionCard(null);
//							if (winner.getPowerList().size() == 4) {
//								FOURTH = true;
//								AUCTION = false;
							// }
							AUCTION = false;
							MAPUI = true;
							currPlayer = 3;
							index = -1;
							if (gs.getMarketStep3()) {
								gs.restructureMarket();
							}
							gs.nextPhase();
						} else {
							System.out.println(players.get(nextIndex).getColor());
							currPlayer = nextIndex;
							keyInput = "";
						}
					} else {
						int nextIndex = -1;

						for (int i = currPlayer; i < players.size(); i++) {
							if (gs.getDecision().get(players.get(i)) == false
									&& gs.getBids().get(players.get(i)) >= 0
									&& gs.getBids().get(players.get(i)) < minBid) {
								nextIndex = i;
								break;
							}
						}
						if (nextIndex == -1) {
							for (int i = 0; i < currPlayer; i++) {
								if (gs.getDecision().get(players.get(i)) == false
										&& gs.getBids().get(players.get(i)) >= 0
										&& gs.getBids().get(players.get(i)) < minBid) {
									nextIndex = i;
									break;
								}
							}
						}
						currPlayer = nextIndex;
						if(currPlayer == -1)
						{
							System.out.println("AUCTION FAIL AT LINE 1482");
						}
					}

				}
			}
			System.out.println(gs.getDecision());

			/*
			 * if (auctionIndex == -1) { gs.getDecision().put(tempPlayers.get(0), true);
			 * tempPlayers.remove(tempPlayers.get(0)); } else if (bidded) {
			 * 
			 * }
			 */
			/*
			 * if (gs.getMarketStep3()) { gs.restructureMarket(); }
			 * gs.determinePlayerOrder();
			 */

			/*
			 * if (round1) { gs.randomizePlayerOrder(); } else { gs.determinePlayerOrder();
			 * }
			 */
			
		} else if (FOURTH) {
			boolean hasSelectedAPowerPlant = false;
			for (int i = 0; i < 4; i++) {
				if (!hasSelectedAPowerPlant && e.getX() >= MARKETX + (i * (PPWIDTH + 20))
						&& e.getX() <= (MARKETX + (i * (PPWIDTH + 20)) + PPWIDTH) && e.getY() >= MARKETY
						&& e.getY() <= MARKETY + PPHEIGHT) {
					fourthindex = i;
					hasSelectedAPowerPlant = true;
				}
			}
			for (int i = 0; i < players.get(currPlayer).getPowerList().size(); i++) // Powerplants
			{

				// ACTUAL PP
				// g.setColor(Color.DARK_GRAY);
				// g.fillRect(AUCTIONX, AUCTIONY+(i*(side+15)), side, side);
				if (!(i == fourthindex)) {
					if (players.get(currPlayer).getPowerList().get(i).isHybrid()) {
						if (e.getX() >= MARKETX + (i * (PPWIDTH + 20))
								&& e.getX() <= MARKETX + (i * (PPWIDTH + 20)) + PPWIDTH
								&& e.getY() >= MARKETY + PPHEIGHT + 20 && e.getY() <= MARKETY + PPHEIGHT + 95) {
							if (!players.get(currPlayer).getPowerList().get(i).isFull()
									&& players.get(currPlayer).getPowerList().get(fourthindex).getStorage().size() > 0
									&& players.get(currPlayer).getPowerList().get(fourthindex).getCost().get(0)
											.equals("coal")) {
								ArrayList<String> test = new ArrayList<String>();
								test.add(
										players.get(currPlayer).getPowerList().get(fourthindex).getStorage().remove(0));
								players.get(currPlayer).getPowerList().get(i).addResources(test);
							}
						}

						if (e.getX() >= MARKETX + (i * (PPWIDTH + 20))
								&& e.getX() <= MARKETX + (i * (PPWIDTH + 20)) + PPWIDTH
								&& e.getY() >= MARKETY + PPHEIGHT + 20 + 75 + 10
								&& e.getY() <= MARKETY + PPHEIGHT + 20 + 75 + 10 + 75) {
							if (!players.get(currPlayer).getPowerList().get(i).isFull()
									&& players.get(currPlayer).getPowerList().get(fourthindex).getStorage().size() > 0
									&& players.get(currPlayer).getPowerList().get(fourthindex).getCost().get(0)
											.equals("oil")) {
								ArrayList<String> test = new ArrayList<String>();
								test.add(
										players.get(currPlayer).getPowerList().get(fourthindex).getStorage().remove(0));
								players.get(currPlayer).getPowerList().get(i).addResources(test);
							}
						}

					} else {
						if (e.getX() >= MARKETX + (i * (PPWIDTH + 20))
								&& e.getX() <= MARKETX + (i * (PPWIDTH + 20)) + PPWIDTH
								&& e.getY() >= MARKETY + PPHEIGHT + 20 && e.getY() <= MARKETY + PPHEIGHT + 95) {
							if (players.get(currPlayer).getPowerList().get(i).getCost().size() != 0) {
								if (!players.get(currPlayer).getPowerList().get(i).isFull()
										&& players.get(currPlayer).getPowerList().get(fourthindex).getStorage()
												.size() > 0
										&& players.get(currPlayer).getPowerList().get(fourthindex).getCost().get(0)
												.equals(players.get(currPlayer).getPowerList().get(i).getCost()
														.get(0))) {
									ArrayList<String> test = new ArrayList<String>();
									test.add(players.get(currPlayer).getPowerList().get(fourthindex).getStorage()
											.remove(0));
									players.get(currPlayer).getPowerList().get(i).addResources(test);
								}
							}
						}
					}
				}
			}
			if (e.getX() >= 815 && e.getX() <= 815 + 360 && e.getY() >= 880 && e.getY() <= 980) {
				players.get(currPlayer).getPowerList().remove(fourthindex);

				int nextIndex = 0;
				for (int i = 0; i < players.size(); i++) {
					if (gs.getDecision().get(players.get(i)) == false && i != currPlayer
							&& gs.getBids().get(players.get(i)) <= 0) {
						System.out.println("i and currPlayer" + i + " " + currPlayer);
						System.out.println(players.get(i).getColor() + " " + players.get(currPlayer).getColor());
						nextIndex = i;
						break;
					}
				}

				if (nextIndex == -1) {
					AUCTION = false;
					FOURTH = false;
					MAPUI = true;
					currPlayer = 3;
					index = -1;
					if (gs.getMarketStep3()) {
						gs.restructureMarket();
					}
					gs.nextPhase();
				} else {
					AUCTION = true;
					FOURTH = false;
					System.out.println("curr and next" + currPlayer + " " + nextIndex);
					currPlayer = nextIndex;
					keyInput = "";
				}
			}

		} else if (MAPUI) {
			// System.out.println("bug");
			// System.out.println(currPlayer);
			ArrayList<Player> Others = new ArrayList<Player>();
			for (int i = 0; i < players.size(); i++) {
				if (i != currPlayer) {
					Others.add(players.get(i));
					// System.out.println(players.get(i).getColor());
				}
			}
			// g.fillRect(1290, 640, 150, 400);
			if (e.getX() >= 1290 && e.getX() <= 1290 + 150 && e.getY() >= 640 && e.getY() <= 640 + 400) // if other
																										// player view
			{
				// System.out.println()

				int colorX = 1310, colorY = 690;
				for (int i = 0; i < Others.size(); i++) {// (colorX, colorY + (i * (100 + 10)), 100, 100);
					if (e.getX() >= colorX && e.getX() <= colorX + 100 && e.getY() >= colorY + (i * (100 + 10))
							&& e.getY() <= colorY + (i * (100 + 10)) + 100) {
						// System.out.println("bug");
						view = Others.get(i);
					}
				}
			} else if (gs.getPhase() == 3) {
				gs.resetDecision();
				boolean canCoal = false, canOil = false, canTrash = false, canNuclear = false;
				displayMessage("Please select the powerplant you want to place resources on and then select resources");
				if (this.powerPlantforResource != null) {
					canCoal = checkWhetherPossible(powerPlantforResource, "coal", 1, players.get(currPlayer));
					System.out.println("Coal: " + canCoal);
					canOil = checkWhetherPossible(powerPlantforResource, "oil", 1, players.get(currPlayer));
					System.out.println("Oil: " + canOil);
					canTrash = checkWhetherPossible(powerPlantforResource, "trash", 1, players.get(currPlayer));
					System.out.println("Trash: " + canTrash);
					canNuclear = checkWhetherPossible(powerPlantforResource, "nuclear", 1, players.get(currPlayer));
					System.out.println("Nuclear: " + canNuclear);
					System.out.println("HasSelected: " + hasSelected);
					System.out.println("Index: " + index);
				} /// g.drawImage(card, MAPX, MAPY + (i * (PPHEIGHT + 20)), PPWIDTH, PPHEIGHT,
					/// null);
				if ((e.getX() >= MAPX) && (e.getX() <= MAPX + PPWIDTH) && (e.getY() >= MAPY) 
						&& e.getY() <= MAPY + PPHEIGHT) {
					powerPlantforResource = players.get(currPlayer).getPowerList().get(0);
					if (index == 0) {
						index = -1;
						hasSelected = false;
					} else {
						System.out.println("Stuff");
						index = 0;
						hasSelected = true;
					}
				} else if ((e.getX() >= MAPX) && (e.getX() <= MAPX + PPWIDTH) && (e.getY() >= MAPY + PPHEIGHT + 20)
						&& e.getY() <= MAPY + PPHEIGHT + PPHEIGHT + 20) {
					powerPlantforResource = players.get(currPlayer).getPowerList().get(1);
					if (index == 1) {
						index = -1;
						hasSelected = false;
					} else {
						index = 1;
						hasSelected = true;
					}
				} else if ((e.getX() >= MAPX) && (e.getX() <= MAPX + PPWIDTH)
						&& (e.getY() >= MAPY + PPHEIGHT + PPHEIGHT + 20 + 20)
						&& e.getY() <= MAPY + PPHEIGHT + PPHEIGHT + PPHEIGHT + 20 + 20) {
					powerPlantforResource = players.get(currPlayer).getPowerList().get(2);
					if (index == 2) {
						index = -1;
						hasSelected = false;
					} else {
						index = 2;
						hasSelected = true;
					}
				} else if (canCoal && hasSelected && e.getX() >= 454 && e.getX() <= 554 && e.getY() >= 762
						&& e.getY() <= 762 + 50) {
					gs.moveResources(powerPlantforResource, "coal", players.get(currPlayer), 1);
				} else if (canOil && hasSelected && e.getX() >= 593 && e.getX() <= 693 && e.getY() >= 762
						&& e.getY() <= 762 + 50) {
					gs.moveResources(powerPlantforResource, "oil", players.get(currPlayer), 1);
				} else if (canTrash && hasSelected && e.getX() >= 454 && e.getX() <= 554 && e.getY() >= 831
						&& e.getY() <= 881) {
					gs.moveResources(powerPlantforResource, "trash", players.get(currPlayer), 1);
				} else if (canNuclear && hasSelected && e.getX() >= 593 && e.getX() <= 693 && e.getY() >= 831
						&& e.getY() <= 881) {
					gs.moveResources(powerPlantforResource, "nuclear", players.get(currPlayer), 1);
				} else if (e.getX() >= 527 && e.getX() <= 787 && e.getY() >= 10 && e.getY() <= 90) {
					gs.getDecision().put(players.get(currPlayer), true);
					int resindex = currPlayer-1;
					System.out.println("Index"+resindex);
					if (resindex == -1) {
						// go to city building
						gs.setPhase(4);
						currPlayer = 3;
						hasSelected = false;
						powerPlantforResource = null;
					} else {
						// System.out.println("bruh");
						index=-1;
						hasSelected = false;
						currPlayer = resindex;
						powerPlantforResource = null;
					}
				}
				// resource is removed from market
				// cost is added to a total cost for the player
				// added to powerplant
			} else if (gs.getPhase() == 4) // city building
			{
				gs.resetDecision();
				displayMessage("Select which city you want to build on");
				String name = findCity(e);
				if (!name.contentEquals("Not in ranges")) {
					if (selectedCity.contentEquals(name)) {
						selectedCity = "";
					}
					selectedCity = name;
					int cost = 0;
					City c = gs.findCity(selectedCity);
					if(c != null)
					{
					if (!(c.getPlayersAtCity().size() >= gs.getMaxHouseInCity())) {
						if (gs.getNumCities().get(players.get(currPlayer)) == 0) {
							cost = c.getCost();
						} else {
							cost = c.leastCost(players.get(currPlayer));// least cost algorithm
							cost += c.getCost();
						}
						if (players.get(currPlayer).getMoney() >= cost) {
							displayMessage(
									players.get(currPlayer).getColor() + " has bought " + c.getName() + " for " + cost);
							players.get(currPlayer).addMoney(cost * -1);// subtracting money

							// display
							ArrayList<Coord> list = displayList.get(players.get(currPlayer).getColor());
							if (list == null) {
								list = new ArrayList<Coord>();
							} else {
							}
							int numPlayers = c.getPlayersAtCity().size();
							Coord cityCoord = gs.getCityCoords().get(c.getName());
							cityCoord.setX(-14 + cityCoord.getX() + 30 * numPlayers);
							cityCoord.setY(-14 + cityCoord.getY() + 30 * numPlayers);
							list.add(cityCoord);
							displayList.put(players.get(currPlayer).getColor(), list);
							repaint();
							revalidate();
							System.out.println(displayList);

							c.addPlayer(players.get(currPlayer));
							gs.addCityBuilt(players.get(currPlayer));
							gs.checkPowerPlantSize();
						}
						selectedCity = "";
					} else {
						displayMessage("There is no more space in this city. Please choose another one");
					}
					}
				}
				// g.fillRect(527, 10, 260, 80);

				if (e.getX() >= 527 && e.getX() <= (527 + 260) && e.getY() >= 10 && e.getY() <= (10 + 80)) // IF END
																											// TURN
				{
					gs.getDecision().put(players.get(currPlayer), true);
					int cityindex = currPlayer-1;
					if (cityindex == -1) {
						gs.setPhase(5);
						currPlayer = 0;
					} else {
						currPlayer = cityindex;
					}
				}

			} else if (gs.getPhase() == 5) {
				gs.resetDecision();
				if (e.getX() >= 527 && e.getX() <= (527 + 260) && e.getY() >= 10 && e.getY() <= (10 + 80)) // END TURN
				{
					
					System.out.println(players.get(currPlayer).getColor()+" reached the END TURN");
					
					auctionIndex=-1;
					keyInput="";
					
					System.out.println("numCitiesPowered: "+numCitiesPowered);
					numCitiesPowered.put(players.get(currPlayer),
							Math.min(citiesPowered, gs.getNumCities().get(players.get(currPlayer))));
					System.out.println("Player: "+players.get(currPlayer).getColor()+". NumCities: "+Math.min(citiesPowered, gs.getNumCities().get(players.get(currPlayer))));
					System.out.println("NumCitiesPowered: "+citiesPowered);
					gs.getDecision().put(players.get(currPlayer), true);
					int bindex = currPlayer+1;
					if (bindex == 4) {
						// go to determinePlayerOrder
						if (gs.isEndOfGame()) {
							END = true;
							MAPUI = false;
							this.endNumCitiesPowered = numCitiesPowered;
						} else {
							gs.givingMoney(numCitiesPowered);
							gs.setRestock();
							gs.marketFix();
							gs.nextPhase();
							currPlayer = 0;
							numCitiesPowered.clear();
							citiesPowered=0;
							MAPUI=false;
							AUCTION=true;

							gs.determinePlayerOrder();
							players = gs.getPlayerOrder();

							if (round1) {
								round1 = false;
							}
						}
					} else {
						currPlayer = bindex;
					}
				}
				for (int i = 0; i < players.get(currPlayer).getPowerList().size(); i++) {
					// g.drawImage(card, MAPX, MAPY + (i * (PPHEIGHT + 20)), PPWIDTH, PPHEIGHT,
					// null);
					if (players.get(currPlayer).getPowerList().get(i).isHybrid()) {
						// System.out.println(players.get(currPlayer).getPowerList().get(i).getMinBid()+"
						// is a hybrid");
						for (int j = 0; j < (players.get(currPlayer).getPowerList().get(i).getCost().size()) + 1; j++) {
							int temp = (players.get(currPlayer).getPowerList().get(i).getCost().size()) + 1;
							// g.fillRect(MAPX+PPWIDTH, (MAPY + (i * (PPHEIGHT + 20)))
							// +(j*(PPHEIGHT/(players.get(currPlayer).getPowerList().get(i).getCost().size()+1))),
							// 200,
							// (PPHEIGHT/(players.get(currPlayer).getPowerList().get(i).getCost().size()+1))-10);
							int buttonY = (MAPY + (i * (PPHEIGHT + 20))) + (j * (PPHEIGHT
									/ (players.get(currPlayer).getPowerList().get(i).getCost().size() + 1)));
							int buttonH = (PPHEIGHT
									/ (players.get(currPlayer).getPowerList().get(i).getCost().size() + 1)) - 10;
							System.out.println("("+(MAPX + PPWIDTH)+","+buttonY+")");
							System.out.println("("+(MAPX + PPWIDTH+200)+","+(buttonY+buttonH)+")");
							displayMessage(
									"Choose the powerplant and the resources you want to burn by clicking on the button");
							if (e.getX() >= MAPX + PPWIDTH && e.getX() <= MAPX + PPWIDTH + 200 && e.getY() >= buttonY
									&& e.getY() <= buttonY + buttonH) {

								ArrayList<String> list = new ArrayList<String>();
								if (temp == 2) {
									if (j == 0) {
										list.add("coal");
										if (players.get(currPlayer).getPowerList().get(i).canBurnH(list)) {
											players.get(currPlayer).getPowerList().get(i).burnResources(list);
											citiesPowered += players.get(currPlayer).getPowerList().get(i)
													.getNumCitiesPowered();
										}
									}
									else if (j == 1) {

										list.add("oil");
										if (players.get(currPlayer).getPowerList().get(i).canBurnH(list)) {
											players.get(currPlayer).getPowerList().get(i).burnResources(list);
										}
										citiesPowered += players.get(currPlayer).getPowerList().get(i)
												.getNumCitiesPowered();
									}
								}
								else if (temp == 3) {
									if (j == 0) {

										System.out.println("Burning 2 coal");
										list.add("coal");
										list.add("coal");
										if (players.get(currPlayer).getPowerList().get(i).canBurnH(list)) {
											System.out.println("PreBurnResources");
											players.get(currPlayer).getPowerList().get(i).burnResources(list);
											System.out.println("PostBurnResources");
										}
										citiesPowered += players.get(currPlayer).getPowerList().get(i)
												.getNumCitiesPowered();
									}
									else if (j == 1) {

										list.add("coal");
										list.add("oil");
										if (players.get(currPlayer).getPowerList().get(i).canBurnH(list)) {
											players.get(currPlayer).getPowerList().get(i).burnResources(list);
										}
										citiesPowered += players.get(currPlayer).getPowerList().get(i)
												.getNumCitiesPowered();
									}
									else if (j == 2) {

										list.add("oil");
										list.add("oil");
										if (players.get(currPlayer).getPowerList().get(i).canBurnH(list)) {
											players.get(currPlayer).getPowerList().get(i).burnResources(list);
										}
										citiesPowered += players.get(currPlayer).getPowerList().get(i)
												.getNumCitiesPowered();
									}
								}
								else if (temp == 4) {
									if (j == 0) {

										list.add("coal");
										list.add("coal");
										list.add("coal");
										if (players.get(currPlayer).getPowerList().get(i).canBurnH(list)) {
											players.get(currPlayer).getPowerList().get(i).burnResources(list);
										}
										citiesPowered += players.get(currPlayer).getPowerList().get(i)
												.getNumCitiesPowered();
									}
									else if (j == 1) {

										list.add("coal");
										list.add("oil");
										list.add("oil");
										if (players.get(currPlayer).getPowerList().get(i).canBurnH(list)) {
											players.get(currPlayer).getPowerList().get(i).burnResources(list);
										}
										citiesPowered += players.get(currPlayer).getPowerList().get(i)
												.getNumCitiesPowered();
									}
									else if (j == 2) {

										list.add("coal");
										list.add("coal");
										list.add("oil");
										if (players.get(currPlayer).getPowerList().get(i).canBurnH(list)) {
											players.get(currPlayer).getPowerList().get(i).burnResources(list);
										}
										citiesPowered += players.get(currPlayer).getPowerList().get(i)
												.getNumCitiesPowered();
									}
									else if (j == 3) {

										list.add("oil");
										list.add("oil");
										list.add("oil");
										if (players.get(currPlayer).getPowerList().get(i).canBurnH(list)) {
											players.get(currPlayer).getPowerList().get(i).burnResources(list);
										}
										citiesPowered += players.get(currPlayer).getPowerList().get(i)
												.getNumCitiesPowered();
									}
								}
								gs.reSupplyAfterBurn(list, players.get(currPlayer).getPowerList().get(i));
							}

						}
						System.out.println(players.get(currPlayer).getColor()+" is after reSupply");
					} // g.fillRect(MAPX+PPWIDTH, MAPY + (i * (PPHEIGHT + 20)), 200, PPHEIGHT);
					else if (players.get(currPlayer).getPowerList().get(i).canBurn() && e.getX() >= MAPX + PPWIDTH
							&& e.getX() <= MAPX + PPWIDTH + 200 && e.getY() >= MAPY + (i * (PPHEIGHT + 20))
							&& e.getY() <= MAPY + (i * (PPHEIGHT + 20)) + PPHEIGHT) {
						players.get(currPlayer).getPowerList().get(i)
								.burnResources(players.get(currPlayer).getPowerList().get(i).getCost());
						citiesPowered += players.get(currPlayer).getPowerList().get(i).getNumCitiesPowered();
						gs.reSupplyAfterBurn(players.get(currPlayer).getPowerList().get(i).getCost(),
								players.get(currPlayer).getPowerList().get(i));
						System.out.println(players.get(currPlayer).getColor()+" is stuck in normal burn");
					}
				}
			}
		} else {
			System.out.println("FAIL");
		}
		// g.fillRect(45, 685, 100, 50); //Buy Button
		repaint();

	}

	private int nextIndex() {
		int nextIndex = -1;

		int numtrue=0;
		for (Player p:gs.getDecision().keySet()) {
			if (gs.getDecision().get(p)) {
				numtrue++;
			}
		}
		
		for (int i = 0; i < players.size(); i++) {
			if (gs.getDecision().get(players.get(i)) == false) {
				//System.out.println(players.get(i).getColor() + " made no decision.");
				if (1==1) {
					//System.out.println(players.get(i).getColor() + " isn't current player.");
					if (gs.getBids().get(players.get(i)) <= 0) {
						//System.out.println(players.get(i).getColor() + " bid is pass");
						nextIndex = i;
						break;
					}
				}
			}
		}
		return nextIndex;
	}

	public void keyTyped(KeyEvent e) {
		if (e.getExtendedKeyCode() == KeyEvent.VK_BACK_SPACE || e.getExtendedKeyCode() == 8 || e.getKeyChar() == '') {
			//System.out.println("backspace");
			if (keyInput.length() >= 1)
				keyInput = keyInput.substring(0, keyInput.length() - 1);
			/*
			 * } else if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER ||
			 * e.getExtendedKeyCode() == 10) { System.out.println("enter"); if
			 * (keyInput.length() > 0) // BID ~ keyInput = "";
			 */
		} else if (isNumeric("" + e.getKeyChar())) {
			keyInput += e.getKeyChar();
		}

		repaint();

	}

	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			int d = Integer.parseInt(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public boolean adjacent(String str) {
		String[] purple = { "yellow", "red", "blue" };
		String[] blue = { "purple", "red" };
		String[] red = { "yellow", "purple", "blue", "green" };
		String[] green = { "orange", "yellow", "red" };
		String[] yellow = { "orange", "green", "red", "purple" };
		String[] orange = { "green", "yellow" };

		ArrayList<String> last = new ArrayList<String>();

		if (str.contentEquals("purple")) {
			last = new ArrayList<String>(Arrays.asList(purple));
		} else if (str.equals("blue")) {
			last = new ArrayList<String>(Arrays.asList(blue));
		} else if (str.equals("green")) {
			last = new ArrayList<String>(Arrays.asList(green));
		} else if (str.equals("red")) {
			last = new ArrayList<String>(Arrays.asList(red));
		} else if (str.equals("yellow")) {
			last = new ArrayList<String>(Arrays.asList(yellow));
		} else if (str.equals("orange")) {
			last = new ArrayList<String>(Arrays.asList(orange));
		}

		if (selectedRegions.isEmpty())
			return true;

		for (int i = 0; i < selectedRegions.size(); i++) {
			if (last.contains(selectedRegions.get(i))) {
				return true;
			}
		}
		return false;
	}

	public String findRegion(MouseEvent e) {
		// System.out.println(e.getX()+""+e.getY());
		if (e.getX() >= 347 && e.getX() <= 447 && e.getY() >= 148 && e.getY() <= 198) {
			return "purple";
		}
		if (e.getX() >= 675 && e.getX() <= 775 && e.getY() >= 130 && e.getY() <= 180) {
			return "yellow";
		}
		if (e.getX() >= 470 && e.getX() <= 570 && e.getY() >= 680 && e.getY() <= 730) {
			return "blue";
		}
		if (e.getX() >= 1295 && e.getX() <= 1395 && e.getY() >= 305 && e.getY() <= 355) {
			return "orange";
		}
		if (e.getX() >= 670 && e.getX() <= 770 && e.getY() >= 800 && e.getY() <= 850) {
			return "red";
		}
		if (e.getX() >= 1170 && e.getX() <= 1270 && e.getY() >= 830 && e.getY() <= 880) {
			return "green";
		}
		return "no region found";

	}

	public String findCity(MouseEvent e) {
		if (e.getX() >= 85 && e.getX() <= 135 && e.getY() >= 148 && e.getY() <= 198) {
			return "Seattle";
		}
		if (e.getX() >= 53 && e.getX() <= 103 && e.getY() >= 238 && e.getY() <= 288) {
			return "Portland";
		}
		if (e.getX() >= 57 && e.getX() <= 107 && e.getY() >= 509 && e.getY() <= 559) {
			return "San_Francisco";
		}
		if (e.getX() >= 145 && e.getX() <= 195 && e.getY() >= 623 && e.getY() <= 673) {
			return "Los_Angeles";
		}
		if (e.getX() >= 200 && e.getX() <= 250 && e.getY() >= 688 && e.getY() <= 738) {
			return "San_Diego";
		}
		if (e.getX() >= 343 && e.getX() <= 393 && e.getY() >= 652 && e.getY() <= 702) {
			return "Phoenix";
		}
		if (e.getX() >= 257 && e.getX() <= 307 && e.getY() >= 555 && e.getY() <= 605) {
			return "Las_Vegas";
		}
		if (e.getX() >= 346 && e.getX() <= 396 && e.getY() >= 424 && e.getY() <= 474) {
			return "Salt_Lake_City";
		}
		if (e.getX() >= 235 && e.getX() <= 285 && e.getY() >= 309 && e.getY() <= 359) {
			return "Boise";
		}
		if (e.getX() >= 488 && e.getX() <= 538 && e.getY() >= 579 && e.getY() <= 629) {
			return "Santa_Fe";
		}
		if (e.getX() >= 521 && e.getX() <= 571 && e.getY() >= 445 && e.getY() <= 495) {
			return "Denver";
		}
		if (e.getX() >= 536 && e.getX() <= 586 && e.getY() >= 377 && e.getY() <= 427) {
			return "Cheyenne";
		}
		if (e.getX() >= 447 && e.getX() <= 497 && e.getY() >= 248 && e.getY() <= 298) {
			return "Billings";
		}
		if (e.getX() >= 755 && e.getX() <= 805 && e.getY() >= 750 && e.getY() <= 808) {
			return "Houston";
		}
		if (e.getX() >= 745 && e.getX() <= 795 && e.getY() >= 674 && e.getY() <= 724) {
			return "Dallas";
		}
		if (e.getX() >= 722 && e.getX() <= 772 && e.getY() >= 578 && e.getY() <= 628) {
			return "Oklahoma_City";
		}
		if (e.getX() >= 773 && e.getX() <= 823 && e.getY() >= 481 && e.getY() <= 531) {
			return "Kansas_City";
		}
		if (e.getX() >= 746 && e.getX() <= 796 && e.getY() >= 392 && e.getY() <= 442) {
			return "Omaha";
		}
		if (e.getX() >= 814 && e.getX() <= 864 && e.getY() >= 265 && e.getY() <= 315) {
			return "Minneapolis";
		}
		if (e.getX() >= 723 && e.getX() <= 773 && e.getY() >= 217 && e.getY() <= 267) {
			return "Fargo";
		}
		if (e.getX() >= 831 && e.getX() <= 881 && e.getY() >= 188 && e.getY() <= 238) {
			return "Duluth";
		}
		if (e.getX() >= 938 && e.getX() <= 988 && e.getY() >= 378 && e.getY() <= 428) {
			return "Chicago";
		}
		if (e.getX() >= 892 && e.getX() <= 942 && e.getY() >= 477 && e.getY() <= 527) {
			return "St._Louis";
		}
		if (e.getX() >= 898 && e.getX() <= 948 && e.getY() >= 592 && e.getY() <= 642) {
			return "Memphis";
		}
		if (e.getX() >= 895 && e.getX() <= 945 && e.getY() >= 749 && e.getY() <= 799) {
			return "New_Orleans";
		}
		if (e.getX() >= 978 && e.getX() <= 1028 && e.getY() >= 647 && e.getY() <= 697) {
			return "Birmingham";
		}
		if (e.getX() >= 1068 && e.getX() <= 1118 && e.getY() >= 648 && e.getY() <= 698) {
			return "Atlanta";
		}
		if (e.getX() >= 1059 && e.getX() <= 1109 && e.getY() >= 559 && e.getY() <= 609) {
			return "Knoxville";
		}
		if (e.getX() >= 1049 && e.getX() <= 1099 && e.getY() >= 465 && e.getY() <= 515) {
			return "Cincinnati";
		}
		if (e.getX() >= 1055 && e.getX() <= 1105 && e.getY() >= 346 && e.getY() <= 396) {
			return "Detroit";
		}
		if (e.getX() >= 1181 && e.getX() <= 1231 && e.getY() >= 880 && e.getY() <= 930) {
			return "Miami";
		}
		if (e.getX() >= 1094 && e.getX() <= 1144 && e.getY() >= 818 && e.getY() <= 868) {
			return "Tampa";
		}
		if (e.getX() >= 1151 && e.getX() <= 1201 && e.getY() >= 736 && e.getY() <= 786) {
			return "Jacksonville";
		}
		if (e.getX() >= 1150 && e.getX() <= 1200 && e.getY() >= 672 && e.getY() <= 722) {
			return "Savannah";
		}
		if (e.getX() >= 1220 && e.getX() <= 1270 && e.getY() >= 582 && e.getY() <= 632) {
			return "Raleigh";
		}
		if (e.getX() >= 1288 && e.getX() <= 1338 && e.getY() >= 536 && e.getY() <= 586) {
			return "Norfolk";
		}
		if (e.getX() >= 1234 && e.getX() <= 1284 && e.getY() >= 472 && e.getY() <= 522) {
			return "Washington_D.C";
		}
		if (e.getX() >= 1164 && e.getX() <= 1214 && e.getY() >= 424 && e.getY() <= 474) {
			return "Pittsburgh";
		}
		if (e.getX() >= 1202 && e.getX() <= 1252 && e.getY() >= 333 && e.getY() <= 383) {
			return "Buffalo";
		}
		if (e.getX() >= 1304 && e.getX() <= 1354 && e.getY() >= 439 && e.getY() <= 489) {
			return "Philadelphia";
		}
		if (e.getX() >= 1337 && e.getX() <= 1387 && e.getY() >= 391 && e.getY() <= 441) {
			return "New_York";
		}
		if (e.getX() >= 1393 && e.getX() <= 1443 && e.getY() >= 331 && e.getY() <= 381) {
			return "Boston";
		}

		return "Not in ranges";
	}

	public boolean checkWhetherPossible(PowerPlant plant, String typeOfResource, int numReq, Player player) {
		// the system.outs will be replaced by adding them to the message board
		ArrayList<String> cost = plant.getCost();

		if (!plant.isHybrid()) {
			if (!cost.contains(typeOfResource)) {// if the cost doesn't match with the resources they're trying to place
				displayMessage("You can't place " + typeOfResource + " on this card");
				return false;

			} else if (plant.getStorage().size() + numReq > cost.size() * 2) {// they are overfilling the powerplant
				displayMessage("You can't place " + typeOfResource + " on this card");
				return false;
			}
		} else {
			// this whole block of code is figuring out how much of each type of resource
			// are there in the
			// powerplant storage
			String firstElement = cost.get(0);
			String firstResource = firstElement.substring(0, firstElement.indexOf("||"));
			String secondResource = firstElement.substring(firstElement.indexOf("||") + 2, firstElement.length());
			int numOfFirstResource = 0;
			int numOfSecondResource = 0;
			for (String x : plant.getStorage()) {
				if (x.equalsIgnoreCase(firstResource)) {
					numOfFirstResource++;
				} else if (x.equalsIgnoreCase(secondResource)) {
					numOfSecondResource++;
				}
			}
			if (!(firstElement.contains(typeOfResource))) {
				displayMessage("You can't place " + typeOfResource + " on this card");
				return false;
			}

			else {
				if (typeOfResource.equalsIgnoreCase(firstResource))// if the resource selected is the first resource
				{
					if (plant.getStorage().size()+1  > plant.getCost().size() * 2)// if trying to store greater than the
																					// amount allowed
					{
						displayMessage("Sorry you don't have space to put " + typeOfResource + " on this card");
						return false;
					}
				} else if (typeOfResource.equalsIgnoreCase(secondResource)) {
					if (plant.getStorage().size()+1 > plant.getCost().size() * 2) {

						displayMessage("Sorry you don't have space to put " + typeOfResource + " on this card");
						return false;

					}
				}
			}
			
		}
		TreeMap<Integer, ArrayList<String>> market= new TreeMap<Integer, ArrayList<String>>();
		
		if(typeOfResource.equalsIgnoreCase("coal"))
		{
			market = gs.getCoalMarket();
		}
		else if(typeOfResource.equalsIgnoreCase("oil"))
		{
			market = gs.getOilMarket();
		}
		else if(typeOfResource.equalsIgnoreCase("trash"))
		{
			market = gs.getTrashMarket();
		}
		else if(typeOfResource.equalsIgnoreCase("nuclear"))
		{
			market = gs.getNuclearMarket();
		}
		Iterator<Integer> iter = market.keySet().iterator();// iterating through the keys of the market
		int numCollectedResources = 0;
		int totalCost = 0;
		while (iter.hasNext()) {
			int key = iter.next();
			int size = market.get(key).size();
			if (size > 0) {
				for (int w = 0; w < size; w++) {
					ArrayList<String> tempList = market.get(key);// each key has an array list of resources like:(cost 1
																	// = coal, coal, coal)
					//tempList.remove(0);// remove from arraylist
					numCollectedResources++;
					totalCost+=key;
					if (numCollectedResources == numReq)// if the number of collected resources equals the number		// required stop removing from market
						break;
				}
			}
			if (numCollectedResources == numReq)// coming out from from the while loop as well
				break;
		}
		if(totalCost>player.getMoney())
			return false;
		return true;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void keyPressed(KeyEvent e) {

	}

	public void keyReleased(KeyEvent e) {

	}
}
