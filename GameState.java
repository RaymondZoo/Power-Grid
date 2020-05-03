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
	private HashMap<City, ArrayList<String>> listOfLinks;
	private ArrayList<City> listOfCities;
	public static final String[] colors = { "blue", "black", "green", "purple", "red", "yellow" };
	public static final int[] rewards = { 10, 22, 33, 44, 54, 64, 73, 82, 90, 98, 105, 112, 118, 124, 129, 134, 138,
			142, 145, 148, 150 };
	public static final int[] step1Restock = { 5, 3, 2, 1 };
	public static final int[] step2Restock = { 6, 4, 3, 2 };
	public static final int[] step3Restock = { 4, 5, 4, 2 };

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

		try {
			marketStep3 = false;
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
				if(i==14||i==16)
				{
					nuclearWord = new ArrayList<String>();
					nuclearWord.add("nuclear");
					nuclearMarket.put(i, nuclearWord);
				}
			}
			ArrayList<String> blank = new ArrayList<String>();
			for(int i = 1;i<=6;i++)
			{
				oilMarket.put(i, blank);
			}
			for(int i = 1;i<=2;i++)
			{
				trashMarket.put(i, blank);
			}
			nuclearMarket.put(10, blank);
			nuclearMarket.put(12, blank);
			coalSupply-=24;
			trashSupply-=6;
			oilSupply-=18;
			nuclearSupply-=2;
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
			}

			// reading in cities
			Scanner cityReader = new Scanner(new File("Cities.txt"));
			cityReader.nextLine();
			cityReader.nextLine();

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
			while (iter.hasNext()) {
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
						if (listOfCities.get(i).getName().equalsIgnoreCase(toSearchFor)) {
							linked = listOfCities.get(i);
						}
					}
					current.getEdges().put(linked, cost);
				}
			}
			// reading in PowerPlants 
			ArrayList<PowerPlant> plug = new ArrayList<PowerPlant>();
			ArrayList<PowerPlant> socket = new ArrayList<PowerPlant>();
			Scanner PowerPlantReader = new Scanner(new File("PowerPlants.txt"));
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
			//System.out.println(tempList);
			for (int i = 0; i < 4; i++) {
				currentMarket.add(tempList.remove(0));
			}
			for (int i = 0; i < 4; i++) {
				futureMarket.add(tempList.remove(0));
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
			if (phase == 2 || phase == 4) {
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

	public City findCity(String name) {// incomplete
		for (City c : cities) {
			if (name.equals(c.getName()))
				return c;
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

	public void marketFix() {
		if (step != 3) {
			deck.add(deck.size(), futureMarket.remove(futureMarket.size() - 1));
			addPowerPlant();
		} else {
			currentMarket.remove(0);
			addPowerPlant();
		}
	}

	public void givingMoney(TreeMap<Player, Integer> numCitiesPowered) {
		for (Player p : numCitiesPowered.keySet()) {
			p.addMoney(rewards[numCitiesPowered.get(p)]);
		}
	}

	public void setRestock() {
		int coalToBeSupplied = 0;
		int oilToBeSupplied =  0;
		int trashToBeSupplied = 0;
		int nuclearToBeSupplied = 0;
		if(step==1)
		{
			coalToBeSupplied = 5;
			oilToBeSupplied = 3;
			trashToBeSupplied = 2;
			nuclearToBeSupplied = 1;
		}
		else if(step==2)
		{
			coalToBeSupplied = 6;
			oilToBeSupplied = 4;
			trashToBeSupplied = 3;
			nuclearToBeSupplied = 2;
		}
		else if(step==3)
		{
			coalToBeSupplied = 4;
			oilToBeSupplied = 5;
			trashToBeSupplied = 4;
			nuclearToBeSupplied = 2;
		}
		if(coalToBeSupplied>coalSupply)
		{
			coalToBeSupplied = coalSupply;
		}
		else if(oilToBeSupplied>oilSupply)
		{
			oilToBeSupplied = oilSupply;
		}
		else if(trashToBeSupplied>trashSupply)
		{
			trashToBeSupplied = trashSupply;
		}
		else if(nuclearToBeSupplied>nuclearSupply)
		{
			nuclearToBeSupplied = nuclearSupply;
		}
		setCoalMarket(restock(coalMarket, coalToBeSupplied));
		coalSupply-=coalToBeSupplied;
		setOilMarket(restock(oilMarket, oilToBeSupplied));
		oilSupply-=oilToBeSupplied;
		setTrashMarket(restock(trashMarket, trashToBeSupplied));
		trashSupply-=trashToBeSupplied;
		int lowestKeyNuclear = findLowestKeyAvailable(nuclearMarket);
		for(int i = 1;i<=nuclearToBeSupplied;i++)
		{
			for(int x = lowestKeyNuclear;x>=10;x-=2)
			{
				ArrayList<String> nuclearWord = new ArrayList<String>();
				nuclearWord.add("nuclear");
				nuclearMarket.put(lowestKeyNuclear, nuclearWord);
			}
		}
		nuclearSupply-=nuclearToBeSupplied;
	}
	public int findLowestKeyAvailable(TreeMap<Integer, ArrayList<String>> market)
	{
		Iterator<Integer> marketIter = market.keySet().iterator();
		int lowestKeyAvailable = 0;
		while(marketIter.hasNext())
		{
			int key = marketIter.next();
			if(market.get(key).size()>0)
			{
				lowestKeyAvailable = key;
			}
		}
		return lowestKeyAvailable;
	}
	public TreeMap<Integer, ArrayList<String>> restock(TreeMap<Integer, ArrayList<String>> market, int numToBeSupplied)
	{
		
		int lowestKeyAvailable = findLowestKeyAvailable(market);	
		String wordToPut = market.get(lowestKeyAvailable).get(0);
		TreeSet<Integer> costs = (TreeSet)market.keySet();
		Iterator<Integer> descend = costs.descendingIterator();
		while(descend.hasNext())
		{
			int key = descend.next();
			if(key==lowestKeyAvailable)
			{
				if(numToBeSupplied>3)
				{
					for(int i = market.get(lowestKeyAvailable).size();i<=3;i++)
					{
						market.get(lowestKeyAvailable).add(wordToPut);
						numToBeSupplied--;
					}
				}
			}
			else if(key<lowestKeyAvailable&&numToBeSupplied%3==0)
			{
				for(int i = 1;i<=3;i++)
				{
					market.get(key).add(wordToPut);
					numToBeSupplied--;
				}
			}
			else if(numToBeSupplied%3!=0)
			{
				for(int i = 1;i<=numToBeSupplied;i++)
				{
					market.get(key).add(wordToPut);
				}
				break;
			}
		}
		if(numToBeSupplied!=0)
		{
			printIfMarketDoesNotHaveSpace(numToBeSupplied);
		}
		return market;
	}

	public void addCityBuilt(Player p) {
		for (Player t : numCities.keySet()) {
			if (t.getColor().equals(p.getColor())) {
				numCities.put(t, numCities.get(t) + 1);
			}
		}
	}
	public void printIfMarketDoesNotHaveSpace(int numToBeSupplied)
	{
		System.out.println(numToBeSupplied + " could not restocked because market ran out of space");
	}

	public void randomizePlayerOrder() {
		Collections.shuffle(playerOrder);
	}
	
	public TreeMap<Integer,ArrayList<String>> getMarket(String resourceType) 
	{
		if ("coal".equalsIgnoreCase(resourceType)) 
			return coalMarket;
		else if ("oil".equalsIgnoreCase(resourceType))
			return oilMarket;
		else if ("trash".equalsIgnoreCase(resourceType))
			return trashMarket;
		else if ("nuclear".equalsIgnoreCase(resourceType))
			return nuclearMarket;	
		
		return null;
	}
	public void rearrangeMarket()
	{
		currentMarket.addAll(futureMarket);
		futureMarket.clear();
		Collections.sort(currentMarket);
		for(int i = 4;i<currentMarket.size();i++)
		{
			futureMarket.add(currentMarket.get(i));
		}
		currentMarket.removeAll(futureMarket);
		
	}
	public void setMarket(String resourceType, TreeMap<Integer,ArrayList<String>> newMarket) 
	{
		if ("coal".equalsIgnoreCase(resourceType)) 
			this.setCoalMarket(newMarket);
		else if ("oil".equalsIgnoreCase(resourceType))
			this.setOilMarket(newMarket);
		else if ("trash".equalsIgnoreCase(resourceType))
			this.setTrashMarket(newMarket);
		else if ("nuclear".equalsIgnoreCase(resourceType))
			this.setNuclearMarket(newMarket);	
	}
}
