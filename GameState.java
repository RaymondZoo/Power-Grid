import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

public class GameState {
	private Scanner input;
	private ArrayList<Player> playerOrder;
	private TreeMap<Integer, ArrayList<String>> coalMarket;
	private TreeMap<Integer, ArrayList<String>> oilMarket;
	private TreeMap<Integer, ArrayList<String>> trashMarket;
	private TreeMap<Integer, String> nuclearMarket;
	private ArrayList<PowerPlant> deck;
	private ArrayList<PowerPlant> currentMarket;
	private ArrayList<PowerPlant> futureMarket;
	private int phase;
	private int step;
	private int maxHouseInCity;
	private PowerPlant auctionCard;
	private int numberOfPlayers;
	private HashMap<Player, Integer> bids;
	private HashMap<Player, Boolean> decision;
	private TreeSet<City> cities;
	private ArrayList<String> playableColors;
	private boolean endOfGame;
	private boolean marketStep3;
	private HashMap<Player, Integer> numCities;
	public static final String[] colors = { "blue", "black", "green", "purple", "red", "yellow" };

	
	public static GameState gs = null;
	
	public static GameState getGamestate() {
		if ( gs == null) 
		{
			gs = new GameState();
		}
		return gs;
	}
	
	@SuppressWarnings({ "unchecked", "resource" })
	public GameState() {
			//throws IOException {
	
		try {
		marketStep3=false;
		playerOrder = new ArrayList<Player>();
		coalMarket = new TreeMap<Integer, ArrayList<String>>();
		oilMarket = new TreeMap<Integer, ArrayList<String>>();
		trashMarket = new TreeMap<Integer, ArrayList<String>>();
		nuclearMarket = new TreeMap<Integer, String>();
		//filling in markets
		for (int i = 8; i >= 0; i--) {
			for (int j = 0; j < 3; j++) {
				if (i >= 3) {
					ArrayList<String> storage;
					if (oilMarket.keySet().contains(i)) {
						storage = oilMarket.get(i);
						storage.add("oil");
					} else {
						storage = new ArrayList<String>();
						storage.add("oil");
					}
					oilMarket.put(i, storage);
				}
				if (i >= 6) {
					ArrayList<String> storage;
					if (trashMarket.keySet().contains(i)) {
						storage = trashMarket.get(i);
						storage.add("trash");
					} else {
						storage = new ArrayList<String>();
						storage.add("trash");
					}
					trashMarket.put(i, storage);
				}
				ArrayList<String> storage;
				if (coalMarket.keySet().contains(i)) {
					storage = coalMarket.get(i);
					storage.add("coal");
				} else {
					storage = new ArrayList<String>();
					storage.add("coal");
				}
				coalMarket.put(i, storage);
			}
		}
		for (int i = 16; i >= 12; i--) {
			nuclearMarket.put(i, "nuclear");
		}

		deck = new ArrayList<PowerPlant>();
		currentMarket = new ArrayList<PowerPlant>();
		futureMarket = new ArrayList<PowerPlant>();
		phase = 1;
		step = 1;
		maxHouseInCity = step;
		numberOfPlayers = 4;
		bids = new HashMap<Player, Integer>();
		decision = new HashMap<Player, Boolean>();
		cities = new TreeSet<City>();
		playableColors = new ArrayList<String>();
		endOfGame = false;
		numCities = new HashMap<Player, Integer>();

		// adding in players
		ArrayList<String> colorList = new ArrayList<String>();
		for (String s : colors) {
			colorList.add(s);
		}
		playerOrder.add(new Player(colorList.remove((int) (Math.random() * colorList.size()))));
		playerOrder.add(new Player(colorList.remove((int) (Math.random() * colorList.size()))));
		playerOrder.add(new Player(colorList.remove((int) (Math.random() * colorList.size()))));
		playerOrder.add(new Player(colorList.remove((int) (Math.random() * colorList.size()))));
		for (Player p : playerOrder) {
			decision.put(p, false);
			bids.put(p, 0);
		}

		// reading in PowerPlants please check
		ArrayList<PowerPlant> plug = new ArrayList<PowerPlant>();
		ArrayList<PowerPlant> socket = new ArrayList<PowerPlant>();
		Scanner PowerPlantReader = new Scanner(new File(C:\Users\risha\eclipse-workspace\Power Grid New\src));
		while (PowerPlantReader.hasNext()) {
			String line = PowerPlantReader.nextLine();
			String[] stats = line.split("/");
			int minBid = Integer.parseInt(stats[0]);
			int numCitiesPowered = Integer.parseInt(stats[2]);
			String costLine = stats[1];
			if (costLine.equals("green")) {

				if (minBid <= 15) {
					plug.add(new PowerPlant(minBid, new ArrayList<String>(), numCitiesPowered));
				} else {
					socket.add(new PowerPlant(minBid, new ArrayList<String>(), numCitiesPowered));
				}

			}
			String[] costStuff = costLine.split(" ");
			int num = Integer.parseInt(costStuff[0]);
			ArrayList<String> cost = new ArrayList<String>();
			for (int n = 0; n < num; n++) {
				cost.add(costStuff[1]);
			}

			if (minBid <= 15) {
				plug.add(new PowerPlant(minBid, cost, numCitiesPowered));
			} else {
				socket.add(new PowerPlant(minBid, cost, numCitiesPowered));
			}
			Collections.shuffle(plug);
			Collections.shuffle(socket);

			// setting up current and future market
			ArrayList<PowerPlant> tempList = new ArrayList<PowerPlant>();
			for (int i = 0; i < 8; i++) {
				tempList.add(plug.remove(i));
			}
			PowerPlant topCard = plug.remove(0);

			plug.remove(0);
			socket.remove(0);
			socket.remove(0);
			socket.remove(0);

			socket.addAll(plug);
			Collections.shuffle(socket);
			socket.add(0, topCard);
			socket.add(socket.size(), new PowerPlant(1234));
			deck.addAll(socket);

			Collections.sort(tempList);
			for (int i = 0; i < 4; i++) {
				currentMarket.add(tempList.remove(i));
			}
			for (int i = 0; i < 4; i++) {
				futureMarket.add(tempList.remove(i));
			}
			
		}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public Scanner getInput() {
		return input;
	}

	public void setInput(Scanner input) {
		this.input = input;
	}

	public ArrayList<Player> getPlayerOrder() {
		return playerOrder;
	}

	public void setPlayerOrder(ArrayList<Player> playerOrder) {
		this.playerOrder = playerOrder;
	}

	public TreeMap<Integer, ArrayList<String>> getCoalMarket() {
		return coalMarket;
	}

	public void setCoalMarket(TreeMap<Integer, ArrayList<String>> coalMarket) {
		this.coalMarket = coalMarket;
	}

	public TreeMap<Integer, ArrayList<String>> getOilMarket() {
		return oilMarket;
	}

	public void setOilMarket(TreeMap<Integer, ArrayList<String>> oilMarket) {
		this.oilMarket = oilMarket;
	}

	public TreeMap<Integer, ArrayList<String>> getTrashMarket() {
		return trashMarket;
	}

	public void setTrashMarket(TreeMap<Integer, ArrayList<String>> trashMarket) {
		this.trashMarket = trashMarket;
	}

	public TreeMap<Integer, String> getNuclearMarket() {
		return nuclearMarket;
	}

	public void setNuclearMarket(TreeMap<Integer, String> nuclearMarket) {
		this.nuclearMarket = nuclearMarket;
	}

	public ArrayList<PowerPlant> getDeck() {
		return deck;
	}

	public void setDeck(ArrayList<PowerPlant> deck) {
		this.deck = deck;
	}

	public ArrayList<PowerPlant> getCurrentMarket() {
		return currentMarket;
	}

	public void setCurrentMarket(ArrayList<PowerPlant> currentMarket) {
		this.currentMarket = currentMarket;
	}

	public ArrayList<PowerPlant> getFutureMarket() {
		return futureMarket;
	}

	public void setFutureMarket(ArrayList<PowerPlant> futureMarket) {
		this.futureMarket = futureMarket;
	}

	public int getPhase() {
		return phase;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getMaxHouseInCity() {
		return maxHouseInCity;
	}

	public void setMaxHouseInCity(int maxHouseInCity) {
		this.maxHouseInCity = maxHouseInCity;
	}

	public PowerPlant getAuctionCard() {
		return auctionCard;
	}

	public void setAuctionCard(PowerPlant auctionCard) {
		this.auctionCard = auctionCard;
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}

	public HashMap<Player, Integer> getBids() {
		return bids;
	}

	public void setBids(HashMap<Player, Integer> bids) {
		this.bids = bids;
	}

	public HashMap<Player, Boolean> getDecision() {
		return decision;
	}

	public void setDecision(TreeMap<Player, Boolean> decision) {
		this.decision = decision;
	}

	public TreeSet<City> getCities() {
		return cities;
	}

	public void setCities(TreeSet<City> cities) {
		this.cities = cities;
	}

	public ArrayList<String> getPlayableColors() {
		return playableColors;
	}

	public void setPlayableColors(ArrayList<String> playableColors) {
		this.playableColors = playableColors;
	}

	public boolean isEndOfGame() {
		return endOfGame;
	}

	public void setEndOfGame(boolean endOfGame) {
		this.endOfGame = endOfGame;
	}

	public HashMap<Player, Integer> getNumCities() {
		return numCities;
	}

	public void setNumCities(HashMap<Player, Integer> numCities) {
		this.numCities = numCities;
	}
	public boolean getMarketStep3() {
		return marketStep3;
	}
	public void nextPhase() {
		phase++;
		if (phase == 6) {
			phase = 1;
		}
	}
	public boolean phaseDone() {
		boolean b = true;
		for (Player p : decision.keySet()) {
			b = decision.get(p);
		}
		return b;
	}

	public void determinePlayerOrder() {
		for (int i = 1; i < playerOrder.size(); ++i) {
			Player key = playerOrder.get(i);
			int j = i - 1;
			while (j >= 0 && isGreater(playerOrder.get(j), key)) {
				playerOrder.set(j + 1, playerOrder.get(j));
				j = j - 1;
			}
			playerOrder.set(j + 1, key);
		}
	}

	public boolean isGreater(Player p1, Player p2) {
		int p1num = numCities.get(p1);
		int p2num = numCities.get(p2);
		if (p1num > p2num) {
			return true;
		} else if (p1num < p2num) {
			return false;
		} else {
			if (p1.getHighestPowerPlant() > p2.getHighestPowerPlant()) {
				return true;
			} else {
				return false;
			}
		}
	}

	public void nextStep() {
		if (step == 1) {
			maxHouseInCity++;
			currentMarket.remove(0);
			addPowerPlant();
		} else if (step == 2) {
			maxHouseInCity++;
			if (phase == 2) {
				this.marketStep3 = true;
			}
			if (phase == 5) {
				restructureMarket();
			}
		}
		step++;
	}

	public void addPowerPlant() {
		PowerPlant toAdd = deck.remove(0);
		if (step != 3) {
			if (toAdd.getMinBid() == 1234) {
				if (step == 1) {
					nextStep(); // 1-->2
					nextStep(); // 2-->3
				}
				if (step == 2) {
					nextStep(); // 2-->3
				}
			}
			futureMarket.add(toAdd);
			Collections.sort(futureMarket);
			currentMarket.add(futureMarket.remove(0));
			Collections.sort(currentMarket);
		} else {
			currentMarket.add(toAdd);
			Collections.sort(currentMarket);
		}
	}
	
	public void restructureMarket() {
		currentMarket.addAll(futureMarket);
		futureMarket.clear();
		currentMarket.remove(0);
		currentMarket.remove(new PowerPlant(1234));
	}
	
	public boolean isAuctionDone() {
		int countPositive=0;
		for (Player p:decision.keySet()) {
			if(!decision.get(p)) {
				if (bids.get(p)>0) {
					countPositive++;
				}
			}
		}
		return countPositive==1;
	}
}
