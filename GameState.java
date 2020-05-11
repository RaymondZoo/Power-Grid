import java.io.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;

public class GameState {
	private Scanner input;
	private ArrayList<Player> playerOrder;
	private TreeMap<Integer, ArrayList<String>> coalMarket;
	private TreeMap<Integer, ArrayList<String>> oilMarket;
	private TreeMap<Integer, ArrayList<String>> trashMarket;
	private TreeMap<Integer, ArrayList<String>> nuclearMarket;
	private ArrayList<PowerPlant> deck;
	private ArrayList<PowerPlant> currentMarket;
	private ArrayList<PowerPlant> futureMarket;
	private int coalSupply = 24;
	private int oilSupply = 24;
	private int nuclearSupply = 12;
	private int trashSupply = 24;
	private int phase = 1;
	private int step = 1;
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
	private HashMap<City, ArrayList<String>> listOfLinks;
	private ArrayList<City> listOfCities;
	public static final String[] colors = { "blue", "black", "green", "purple", "red", "yellow" };
	public static final int[] rewards = { 10, 22, 33, 44, 54, 64, 73, 82, 90, 98, 105, 112, 118, 124, 129, 134, 138,
			142, 145, 148, 150 };
	public static final int[] step1Restock = { 5, 3, 2, 1 };
	public static final int[] step2Restock = { 6, 4, 3, 2 };
	public static final int[] step3Restock = { 4, 5, 4, 2 };
	private TreeMap<String, Coord>cityCoords;
 
	public static GameState gs = null;

	public static GameState getGamestate() {
		if (gs == null) {
			gs = new GameState();
		}
		return gs;
	}

	@SuppressWarnings({ "unchecked", "resource" })
	public GameState() {
		// throws IOException {
		cityCoords=new TreeMap<String, Coord>();
		setCityCoord();
		try {
			marketStep3 = false;
			playerOrder = new ArrayList<Player>();
			coalMarket = new TreeMap<Integer, ArrayList<String>>();
			oilMarket = new TreeMap<Integer, ArrayList<String>>();
			trashMarket = new TreeMap<Integer, ArrayList<String>>();
			nuclearMarket = new TreeMap<Integer, ArrayList<String>>();
			// filling in markets
			for (int i = 8; i >= 1; i--) {
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
					if (i >= 7) {
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
			ArrayList<String> nuclearWord = null;

			for (int i = 16; i >= 14; i--) {
				if (i == 14 || i == 16) {
					nuclearWord = new ArrayList<String>();
					nuclearWord.add("nuclear");
					nuclearMarket.put(i, nuclearWord);
				}
			}
			for (int i = 1; i <= 6; i++) {
				ArrayList<String> blank = new ArrayList<String>();
				trashMarket.put(i, blank);
			}
			for (int i = 1; i <= 2; i++) {
				ArrayList<String> blank = new ArrayList<String>();

				oilMarket.put(i, blank);
			}
			ArrayList<String> blank = new ArrayList<String>();
			nuclearMarket.put(10, blank);
			ArrayList<String> blank2 = new ArrayList<String>();
			nuclearMarket.put(12, blank2);
			for (int i = 1; i <= 8; i++) {
				ArrayList<String> stuff = new ArrayList<String>();
				nuclearMarket.put(i, stuff);
			}
			coalSupply -= 24;
			trashSupply -= 6;
			oilSupply -= 18;
			nuclearSupply -= 2;
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
			listOfLinks = new HashMap<City, ArrayList<String>>();
			listOfCities = new ArrayList<City>();

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
				numCities.put(p, 0);
			}

			// reading in cities
			InputStream is = getClass().getResourceAsStream("Cities.txt");
			InputStreamReader isr = new InputStreamReader(is);
			Scanner cityReader = new Scanner(isr);
			cityReader.nextLine();
			// cityReader.nextLine();

			while (cityReader.hasNextLine()) {
				String line = cityReader.nextLine();
				if (line == null || line.isEmpty()) {
					continue;
				}
				String[] citiesAndLinks = null;
				City c = null;

				try {
					// line = cityReader.nextLine();
					citiesAndLinks = line.split(" ");
					c = new City(citiesAndLinks[0], citiesAndLinks[1]);
				} catch (Exception e) {
					System.out.println(e);
				}
				ArrayList<String> links = new ArrayList<String>();
				for (int i = 2; i < citiesAndLinks.length; i++) {
					links.add(citiesAndLinks[i].substring(0, citiesAndLinks[i].length()));
				}
				listOfLinks.put(c, links);
				listOfCities.add(c);

			}
			// setting up cities
			Set<City> keySet = listOfLinks.keySet();
			Iterator<City> iter = keySet.iterator();
			int ctr = 0;
			while (iter.hasNext()) {
				if (ctr == 24) {
					// System.out.println ("At 23rd row");
				}
				City current = iter.next();
				ArrayList<String> links = listOfLinks.get(current);
				for (int i = 0; i < links.size(); i++) {
					String val = links.get(i);
					String toSearchFor = val.substring(0, val.indexOf("/"));
					int cost = 0;
					try {

						val = val.replace(",", "");

						cost = Integer.parseInt(val.substring(val.indexOf("/") + 1, val.length()));
					} catch (Exception e) {
						System.out.println(e);
					}
					City linked = null;
					for (int x = 0; x < listOfCities.size(); x++) {
						if (listOfCities.get(x).getName().equalsIgnoreCase(toSearchFor)) {
							linked = listOfCities.get(x);
							break;
						}
					}
					current.getEdges().put(linked, cost);
				}
				ctr++;
			}
			// reading in PowerPlants
			ArrayList<PowerPlant> plug = new ArrayList<PowerPlant>();
			ArrayList<PowerPlant> socket = new ArrayList<PowerPlant>();
			InputStream pp = getClass().getResourceAsStream("PowerPlants.txt");
			InputStreamReader ppr = new InputStreamReader(pp);
			Scanner PowerPlantReader = new Scanner(ppr);
			if (PowerPlantReader.hasNext()) {
				PowerPlantReader.next();
			}
			while (PowerPlantReader.hasNextLine()) {
				String line = PowerPlantReader.nextLine();
				if (line == null || line.isEmpty()) {
					continue;
				}

				String[] stats = line.split("/");
				int minBid = Integer.parseInt(stats[0]);
				int numCitiesPowered = Integer.parseInt(stats[2]);
				String costLine = stats[1];

				// adding if no cost
				if (costLine.contains("green")) {
					if (minBid <= 15) {
						plug.add(new PowerPlant(minBid, new ArrayList<String>(), numCitiesPowered));
					} else {
						socket.add(new PowerPlant(minBid, new ArrayList<String>(), numCitiesPowered));
					}
				}

				else {
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
				}
			}
			Collections.shuffle(plug);
			Collections.shuffle(socket);

			// setting up current and future market
			ArrayList<PowerPlant> tempList = new ArrayList<PowerPlant>();
			for (int i = 0; i < 8; i++) {
				if (plug != null && !plug.isEmpty()) {
					tempList.add(plug.remove(0));
				}
			}
			PowerPlant topCard = null;
			if (plug != null && !plug.isEmpty()) {
				topCard = plug.remove(0);
			}

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
			// System.out.println(tempList);
			for (int i = 0; i < 4; i++) {
				currentMarket.add(tempList.remove(0));
			}
			for (int i = 0; i < 4; i++) {
				futureMarket.add(tempList.remove(0));
			}
			for (Player p : playerOrder) {
				numCities.put(p, 0); //temp
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public ArrayList<City> getListOfCities() {
		return listOfCities;
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

	public TreeMap<Integer, ArrayList<String>> getNuclearMarket() {
		return nuclearMarket;
	}

	public void setNuclearMarket(TreeMap<Integer, ArrayList<String>> nuclearMarket) {
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

	public void setDecision(HashMap<Player, Boolean> decision) {
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
		for (Player p : numCities.keySet()) {
			if (numCities.get(p) > 16) {
				endOfGame = true;
				return true;
			}
		}
		endOfGame = false;
		return false;
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

	public int getCoalSupply() {
		return coalSupply;
	}

	public void setCoalSupply(int coalSupply) {
		this.coalSupply = coalSupply;
	}

	public int getOilSupply() {
		return oilSupply;
	}

	public void setOilSupply(int oilSupply) {
		this.oilSupply = oilSupply;
	}

	public int getNuclearSupply() {
		return nuclearSupply;
	}

	public void setNuclearSupply(int nuclearSupply) {
		this.nuclearSupply = nuclearSupply;
	}

	public int getTrashSupply() {
		return trashSupply;
	}

	public void setTrashSupply(int trashSupply) {
		this.trashSupply = trashSupply;
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
			if (p1.getHighestPowerPlant() < p2.getHighestPowerPlant()) {
				return true;
			} else if (p1.getHighestPowerPlant() > p2.getHighestPowerPlant()) {
				return false;
			} else {
				return Math.random() >= 0.5;
			}
		}
	}

	public void nextStep() {
		if (step == 1) {
			maxHouseInCity++;
			currentMarket.remove(0);
			addPowerPlant();
			System.out.println("Current Market after Step 1: "+currentMarket);
			System.out.println("Future Market after Step 1: "+futureMarket);
		} else if (step == 2) {
			maxHouseInCity++;
			if (phase == 2 || phase == 4) {
				this.marketStep3 = true;
			}
			if (phase == 5) {
				restructureMarket();
			}
		}
		System.out.println("Current Market after Step 2: "+currentMarket);
		System.out.println("Current Market size after Step 2: "+currentMarket.size());
		step++;
	}
	@SuppressWarnings("unchecked")
	public void addPowerPlant() {
		PowerPlant toAdd = deck.remove(0);
		System.out.println(step);
		if (step != 3) {
			/*if (toAdd.getMinBid() == 1234) {
				if (step == 1) {
					nextStep(); // 1-->2
					nextStep(); // 2-->3
				}
				if (step == 2) {
					nextStep(); // 2-->3
				}
			}*/
			if (currentMarket.size()==3) {
				futureMarket.add(toAdd);
				Collections.sort(futureMarket);
				//System.out.println(futureMarket);
				currentMarket.add(futureMarket.remove(0));
				Collections.sort(currentMarket);
				
				//System.out.println(currentMarket);
			}
			else {
				currentMarket.add(toAdd);
				Collections.sort(currentMarket);
				//System.out.println(futureMarket);
				futureMarket.add(currentMarket.remove(currentMarket.size()-1));
				Collections.sort(futureMarket);
				//System.out.println(currentMarket);
			}
		} else {
			currentMarket.add(toAdd);
			currentMarket.addAll(futureMarket);
			Collections.sort(currentMarket);
			System.out.println(currentMarket);
		}
	}

	public void restructureMarket() { // step 3 conversion
		currentMarket.addAll(futureMarket);
		futureMarket.clear();
		currentMarket.remove(0);
		for (PowerPlant p:getCurrentMarket()) {
			if (p.getMinBid()==1234) {
				currentMarket.remove(p);
				break;
			}
		}
		System.out.println("Current Market: "+currentMarket);
	}

	public boolean isAuctionDone() {
		int countPositive = 0;
		for (Player p : decision.keySet()) {
			if (!decision.get(p)) {
				if (p != null && bids.get(p) >= 0) {
					countPositive++;
				}
			}
		}
		return countPositive == 1;
	}

	public void playerDecision(Player p) {
		decision.put(p, true);
	}

	public int totalSize(TreeMap<Integer, ArrayList<String>> market) {
		int size = 0;
		for (int key : market.keySet()) {
			size += market.get(key).size();
		}
		return size;
	}

	public City findCity(String name) {
		for (City c : listOfCities) {
			if (name.equals(c.getName())) {
				System.out.println(c.getName() + " found.");
				return c;
			}
		}
		return null;
	}

	public void checkPowerPlantSize() {
		int maxNumCities = 0;
		for (Player p : numCities.keySet()) {
			if (numCities.get(p) > maxNumCities) {
				maxNumCities = numCities.get(p);
			}
		}
		boolean b = false;
		for (PowerPlant p : currentMarket) {
			if (p.getMinBid() <= maxNumCities) {
				b = true;
				currentMarket.remove(p);
				addPowerPlant();
			}
		}
		if (b) {
			for (PowerPlant p : currentMarket) {
				if (p.getMinBid() <= maxNumCities) {
					b = true;
					currentMarket.remove(p);
					addPowerPlant();
				}
			}
		}
	}

	public void marketFix() { // Phase 5
		if (step != 3) {
			deck.add(deck.size(), futureMarket.remove(futureMarket.size() - 1));
			addPowerPlant();
		} else {
			currentMarket.remove(0);
			addPowerPlant();
		}
	}

	public void givingMoney(HashMap<Player, Integer> numCitiesPowered) {
		for (Player p : numCitiesPowered.keySet()) {
			p.addMoney(rewards[numCitiesPowered.get(p)]);
		}
	}

	public void setRestock() {
		int coalToBeSupplied = 0;
		int oilToBeSupplied = 0;
		int trashToBeSupplied = 0;
		int nuclearToBeSupplied = 0;
		if (step == 1) {
			coalToBeSupplied = 5;
			oilToBeSupplied = 3;
			trashToBeSupplied = 2;
			nuclearToBeSupplied = 1;
		} else if (step == 2) {
			coalToBeSupplied = 6;
			oilToBeSupplied = 4;
			trashToBeSupplied = 3;
			nuclearToBeSupplied = 2;
		} else if (step == 3) {
			coalToBeSupplied = 4;
			oilToBeSupplied = 5;
			trashToBeSupplied = 4;
			nuclearToBeSupplied = 2;
		}
		if (coalToBeSupplied > coalSupply) {
			coalToBeSupplied = coalSupply;
		}
		if (oilToBeSupplied > oilSupply) {
			oilToBeSupplied = oilSupply;
		}
		if (trashToBeSupplied > trashSupply) {
			trashToBeSupplied = trashSupply;
		}
		if (nuclearToBeSupplied > nuclearSupply) {
			nuclearToBeSupplied = nuclearSupply;
		}
		restock(coalMarket, coalToBeSupplied, "coal");
		coalSupply -= coalToBeSupplied;
		restock(oilMarket, oilToBeSupplied, "oil");
		oilSupply -= oilToBeSupplied;
		restock(trashMarket, trashToBeSupplied, "trash");
		trashSupply -= trashToBeSupplied;
		int lowestKeyNuclear = findLowestKeyAvailable(nuclearMarket);
		String wordToPut = "nuclear";
		TreeSet<Integer> costs = new TreeSet<Integer>(nuclearMarket.keySet());
		Iterator<Integer> descend = costs.descendingIterator();
		if(lowestKeyNuclear!=0)
		{
		while (descend.hasNext()) {
			int key = descend.next();
			if (key < lowestKeyNuclear) {
				nuclearMarket.get(key).add(wordToPut);
				nuclearToBeSupplied--;
			}
			if (nuclearToBeSupplied == 0)
				break;
		}
		nuclearSupply -= nuclearToBeSupplied;
		}
		else
		{
			while (descend.hasNext()) {
				int key = descend.next();
				if (key <= nuclearMarket.keySet().size()) {
					nuclearMarket.get(key).add(wordToPut);
					nuclearToBeSupplied--;
				}
				if (nuclearToBeSupplied == 0)
					break;
			}
			nuclearSupply -= nuclearToBeSupplied;
		}
	}

	public int findLowestKeyAvailable(TreeMap<Integer, ArrayList<String>> market) {
		Iterator<Integer> marketIter = market.keySet().iterator();
		int lowestKeyAvailable = 0;
		while (marketIter.hasNext()) {
			int key = marketIter.next();
			if (market.get(key).size() > 0) {
				lowestKeyAvailable = key;
				return lowestKeyAvailable;
			}
		}
		return 0;
	}

	
	public void restock(TreeMap<Integer, ArrayList<String>> market, int numToBeSupplied, String wordToPut) {

		int lowestKeyAvailable = findLowestKeyAvailable(market);
		if(lowestKeyAvailable!=0)
		{
			TreeSet<Integer> costs = new TreeSet<Integer>(market.keySet());
			Iterator<Integer> descend = costs.descendingIterator();
			while (descend.hasNext()) {
				int key = descend.next();
				if (key == lowestKeyAvailable) {
					for (int i = market.get(lowestKeyAvailable).size(); i < 3; i++) {
						market.get(lowestKeyAvailable).add(wordToPut);
						numToBeSupplied--;
	
					}
				} else if (key < lowestKeyAvailable && numToBeSupplied % 3 == 0) {
					for (int i = 1; i <= 3; i++) {
						market.get(key).add(wordToPut);
						numToBeSupplied--;
					}
				} else if (numToBeSupplied % 3 != 0 && numToBeSupplied < 3 && key < lowestKeyAvailable) {
					int originalNumTobeSupplied = numToBeSupplied;
					for (int i = 1; i <= originalNumTobeSupplied; i++) {
						market.get(key).add(wordToPut);
						numToBeSupplied--;
					}
					break;
				}
				if (numToBeSupplied == 0)
					break;
			}
			if (numToBeSupplied != 0) {
				printIfMarketDoesNotHaveSpace(numToBeSupplied);
			}
		}
		else
		{
			TreeSet<Integer> costs = new TreeSet<Integer>(market.keySet());
			Iterator<Integer> descend = costs.descendingIterator();
			while (descend.hasNext()) {
				int key = descend.next();
				if (key <= market.keySet().size()) {
					for(int i = 1;i<=3;i++)
					{
						market.get(i).add(wordToPut);
					}
				}
				if (numToBeSupplied == 0)
					break;
			}
		}
	}

	public void addCityBuilt(Player p) {
		for (Player t : numCities.keySet()) {
			if (t.getColor().equals(p.getColor())) {
				numCities.put(t, numCities.get(t) + 1);
			}
			if (step==1&&numCities.get(t)>6) {
				nextStep(); //1-->2
			}
		}
	}

	public void printIfMarketDoesNotHaveSpace(int numToBeSupplied) {
		System.out.println(numToBeSupplied + " could not restocked because market ran out of space");
	}

	public void randomizePlayerOrder() {
		Collections.shuffle(playerOrder);
	}

	public TreeMap<Integer, ArrayList<String>> getMarket(String resourceType) {
		if ("coal".equalsIgnoreCase(resourceType)) 
			return coalMarket;
		else if ("oil".equalsIgnoreCase(resourceType))
			return oilMarket;
		else if ("trash".equalsIgnoreCase(resourceType))
			return trashMarket;
		else if ("nuclear".equalsIgnoreCase(resourceType))
			return nuclearMarket;
		else {
			System.out.println("returned null");
			return null;
		}
	}

	public void rearrangeMarket() { // Sort
		currentMarket.addAll(futureMarket);
		futureMarket.clear();
		Collections.sort(currentMarket);
		for (int i = 4; i < currentMarket.size(); i++) {
			futureMarket.add(currentMarket.get(i));
		}
		currentMarket.removeAll(futureMarket);

	}

	public void setMarket(String resourceType, TreeMap<Integer, ArrayList<String>> newMarket) {
		if ("coal".equalsIgnoreCase(resourceType))
			this.setCoalMarket(newMarket);
		else if ("oil".equalsIgnoreCase(resourceType))
			this.setOilMarket(newMarket);
		else if ("trash".equalsIgnoreCase(resourceType))
			this.setTrashMarket(newMarket);
		else if ("nuclear".equalsIgnoreCase(resourceType))
			this.setNuclearMarket(newMarket);
	}

	public void setListOfCites(ArrayList<City> list) {
		this.listOfCities = list;
	}

	public ArrayList<City> getListOfCites() {
		return listOfCities;
	}

	public Player nextPlayer(int i) {
		if (phase == 2 || phase == 5) {
			i++;
			if (i == 4) {
				i = 0;
			}
		} else {
			i--;
			if (i == 0) {
				i = 4;
			}
		}
		return playerOrder.get(i);
	}

	public void resetBid() {
		for (Player p : bids.keySet()) {
			bids.put(p, 0);
		}
	}

	public int countResource(TreeMap<Integer, ArrayList<String>> map) {
		int size = 0;
		for (int i : map.keySet()) {
			size += map.get(i).size();
		}
		return size;
	}

	public void moveResources(PowerPlant plant, String typeOfResource, Player player, int numReq) {
		int originalMoney = player.getMoney();
		TreeMap<Integer, ArrayList<String>> market = new TreeMap<Integer, ArrayList<String>>();
		market = getMarket(typeOfResource);
		System.out.println(market==null);// getting the correct market based on the resource the player selected
		TreeMap<Integer, ArrayList<String>> originalMarket;
		originalMarket = copyMarket(market);// saving original market in case transaction of resources is not possible
											// because the player can't afford it
		int numCollectedResources = 0;
		Iterator<Integer> iter = market.keySet().iterator();// iterating through the keys of the market
		while (iter.hasNext()) {
			int key = iter.next();
			int size = market.get(key).size();
			if (size > 0) {
				for (int w = 0; w < size; w++) {
					ArrayList<String> tempList = market.get(key);// each key has an array list of resources like:(cost 1
																	// = coal, coal, coal)
					tempList.remove(0);// remove from arraylist
					numCollectedResources++;
					player.subtractMoney(key);
					if (numCollectedResources == numReq)// if the number of collected resources equals the number
														// required stop removing from market
						break;
				}
			}
			if (numCollectedResources == numReq)// coming out from from the while loop as well
				break;
		}
		// pretty self-explanatory
		System.out.println("The amount of you have now is " + player.getMoney() + " dollars");
		ArrayList<String> attainedResources = new ArrayList<String>();
		for (int i = 1; i <= numReq; i++) {
			attainedResources.add(typeOfResource);
		}
		plant.addResources(attainedResources);// adding all the resources the player
																				// gained
	}
	public static TreeMap<Integer, ArrayList<String>> copyMarket(TreeMap<Integer, ArrayList<String>> market) {
		TreeMap<Integer, ArrayList<String>> newMarket = new TreeMap<Integer, ArrayList<String>>();
		Iterator<Integer> itr = market.keySet().iterator();
		while (itr.hasNext()) {

			int key = itr.next();
			ArrayList<String> list = new ArrayList<String>();
			ArrayList<String> listFromMarket = market.get(key);
			for (int i = 0; i < listFromMarket.size(); i++) {
				list.add(listFromMarket.get(i));
			}
			newMarket.put(key, list);
		}

		return newMarket;
	}
	public void setCityCoord() {
		cityCoords.put("Seattle",new Coord(85,148));
		cityCoords.put("Portland",new Coord(53,238));
		cityCoords.put("San_Francisco",new Coord(57,509));
		cityCoords.put("Los_Angeles",new Coord(145,623));
		cityCoords.put("San_Diego",new Coord(200,688));
		cityCoords.put("Phoenix",new Coord(343,652));
		cityCoords.put("Las_Vegas",new Coord(257,555));
		cityCoords.put("Salt_Lake_City",new Coord(346,424));
		cityCoords.put("Boise",new Coord(235,309));
		cityCoords.put("Santa_Fe",new Coord(488,579));
		cityCoords.put("Denver",new Coord(521,445));
		cityCoords.put("Cheyenne",new Coord(536,377));
		cityCoords.put("Billings",new Coord(447,248));
		cityCoords.put("Houston",new Coord(755,758));
		cityCoords.put("Dallas",new Coord(745,674));
		cityCoords.put("Oklahoma_City",new Coord(722,578));
		cityCoords.put("Kansas_City",new Coord(773,481));
		cityCoords.put("Omaha",new Coord(746,392));
		cityCoords.put("Minneapolis",new Coord(814,265));
		cityCoords.put("Fargo",new Coord(723,217));
		cityCoords.put("Duluth",new Coord(831,188));
		cityCoords.put("Chicago",new Coord(938,378));
		cityCoords.put("St._Louis",new Coord(892,477));
		cityCoords.put("Memphis",new Coord(898,592));
		cityCoords.put("New_Orleans",new Coord(895,749));
		cityCoords.put("Birmingham",new Coord(978,647));
		cityCoords.put("Atlanta",new Coord(1068,648));
		cityCoords.put("Knoxville",new Coord(1059,559));
		cityCoords.put("Cincinnati",new Coord(1049,465));
		cityCoords.put("Detroit",new Coord(1055,346));
		cityCoords.put("Miami",new Coord(1181,880));
		cityCoords.put("Tampa",new Coord(1094,818));
		cityCoords.put("Jacksonville",new Coord(1151,736));
		cityCoords.put("Savannah",new Coord(1150,672));
		cityCoords.put("Raleigh",new Coord(1220,582));
		cityCoords.put("Norfolk",new Coord(1288,536));
		cityCoords.put("Washington_D.C.",new Coord(1234,472));
		cityCoords.put("Pittsburgh",new Coord(1164,424));
		cityCoords.put("Buffalo",new Coord(1202,333));
		cityCoords.put("Philadelphia",new Coord(1304,439));
		cityCoords.put("New_York",new Coord(1337,391));
		cityCoords.put("Boston",new Coord(1393,331));
	}
	public TreeMap<String,Coord> getCityCoords(){
		return cityCoords;
	}
	public void reSupplyAfterBurn(ArrayList<String>list, PowerPlant plant) {
		int[]countResource=new int[4];
		for(int i = 0;i<countResource.length;i++)
		{
			countResource[i] = 0;
		}
		for (String str:list) {
			if (str.equals("coal")) {
				countResource[0]=countResource[0]+1;
			}
			if (str.equals("oil")) {
				countResource[1]=countResource[1]+1;
			}
			if (str.equals("trash")) {
				countResource[2]=countResource[2]+1;
			}
			if (str.equals("nuclear")) {
				countResource[3]=countResource[3]+1;
			}
		}
		
		if (list.contains("coal")) {
			int newCoal = getCoalSupply() + countResource[0];
			setCoalSupply(newCoal);
		} else if (list.contains("trash")) {
			int newTrash = getTrashSupply() + countResource[2];
			setTrashSupply(newTrash);
		} else if (list.contains("oil")) {
			int newOil = getTrashSupply() + countResource[1];
			setOilSupply(newOil);
		} else if (list.contains("nuclear")) {
			int newNuclear = getTrashSupply() + countResource[3];
			setNuclearSupply(newNuclear);
		}
	}
	public void resetDecision() {
		for (Player p:this.decision.keySet()) {
			decision.put(p, false);
		}
	}

	public void setMarketStep3(boolean b) {
		marketStep3=b;
	}
}