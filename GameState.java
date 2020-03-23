import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

public class GameState {
	private Scanner input;
	private ArrayList<Player>playerOrder;
	private TreeMap<Integer, ArrayList<String>>market;
	private ArrayList<PowerPlant>deck;
	private ArrayList<PowerPlant>currentMarket;
	private ArrayList<PowerPlant>futureMarket;
	private int phase;
	private int step;
	private int maxHouseInCity;
	private PowerPlant auctionCard;
	private int numberOfPlayers;
	private TreeMap<Player, Integer>bids;
	private TreeMap<Player, Boolean>decision;
	private TreeSet<City>cities;
	private ArrayList<String> playableColors;
	private boolean endOfGame;
	private HashMap<Player, Integer>numCities;
	public static final String[] colors = { "blue", "black", "green", "purple", "red", "yellow" };
	
	public GameState() throws IOException {
		
		ArrayList<String>colorList=new ArrayList<String>();
		for (String s:colors) {
			colorList.add(s);
		}
		
		playerOrder=new ArrayList<Player>();
		market=new TreeMap<Integer, ArrayList<String>>();
		deck=new ArrayList<PowerPlant>();
		currentMarket=new ArrayList<PowerPlant>();
		futureMarket=new ArrayList<PowerPlant>();
		phase=1;
		step=1;
		maxHouseInCity=step;
		numberOfPlayers=4;
		bids=new TreeMap<Player, Integer>();
		decision=new TreeMap<Player, Boolean>();
		cities=new TreeSet<City>();
		playableColors=new ArrayList<String>();
		endOfGame=false;
		numCities=new HashMap<Player, Integer>();
		
		//adding in players
		playerOrder.add(new Player(colorList.remove((int)(Math.random()*colorList.size()))));
		playerOrder.add(new Player(colorList.remove((int)(Math.random()*colorList.size()))));
		playerOrder.add(new Player(colorList.remove((int)(Math.random()*colorList.size()))));
		playerOrder.add(new Player(colorList.remove((int)(Math.random()*colorList.size()))));
		
		//reading in PowerPlants unfinished broken
		Scanner PowerPlantReader=new Scanner(new File("PowerPlants.txt"));
		while(PowerPlantReader.hasNext()) {
			String line=PowerPlantReader.nextLine();
			String[] stats=line.split(" ");
			//deck.add(new PowerPlant MinBid ResourceCost CitiesPowered
		}
		
		
		
		
		
	}
}
