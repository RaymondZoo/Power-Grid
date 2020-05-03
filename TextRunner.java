import java.io.*;

import java.util.Scanner;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Iterator;

public class TextRunner {

	static GameState gs = null;

	static Scanner input = new Scanner(System.in); // player input

	public static void main(String[] args) throws IOException {
		boolean turn1 = true;
		gs = GameState.getGamestate();
		ArrayList<String> playableColors = new ArrayList<String>();
		// System.out.println("Enter Playable Colors (4), seperate lines");

		// for (int i = 0; i < 4; i++) {
		// playableColors.add(input.nextLine());
		// }

		System.out.println("Temp: Playable Colors are green, blue, purple, red");
		playableColors.add("green");
		playableColors.add("red");
		playableColors.add("purple");
		playableColors.add("blue");

		while (!gs.isEndOfGame()) {
			// phase 1
			if (turn1) {
				gs.randomizePlayerOrder();
			} else {
				gs.determinePlayerOrder();
			}

			// phase 2
			// auction
			gs.rearrangeMarket();
			System.out.println("Current Market: ");
			for(int x = 1; x<=gs.getCurrentMarket().size(); x++)
			{
				System.out.println(x + ". " + gs.getCurrentMarket().get(x-1));
			}
			System.out.println("Future Market: ");
			for(int x = 1; x<=gs.getFutureMarket().size(); x++)
			{
				System.out.println(x + ". " + gs.getCurrentMarket().get(x-1));
			}
			
			ArrayList<Player> tempPlayers = new ArrayList<Player>();
			tempPlayers.addAll(gs.getPlayerOrder());

			// System.out.println(tempPlayers);
			System.out.println();
			System.out.println(tempPlayers.get(0).getColor() + "'s money: " + tempPlayers.get(0).getMoney());
			int minPrice = 0;
			while (!tempPlayers.isEmpty()) {
				System.out.println(tempPlayers.get(0).getColor()
						+ ", choose the index of the powerPlant to start auction on (1-4), 0 to pass");
				int index = Integer.parseInt(input.nextLine())-1;
				if (index == -1 && turn1) {
					while (index == -1) {
						System.out.println(tempPlayers.get(0).getColor()
								+ ", choose the index of the powerPlant to start auction on (1-4). You cannot pass b/c it's turn 1");
						index = Integer.parseInt(input.nextLine())-1;
					}
				}
				if (index == -1) {
					gs.getDecision().put(tempPlayers.get(0), true);
					tempPlayers.remove(tempPlayers.get(0));
				} else {
					gs.setAuctionCard(gs.getCurrentMarket().get(index));
					minPrice = gs.getCurrentMarket().get(index).getMinBid();
					int i = 0;
					ArrayList<Player> auctionPlayers = new ArrayList<Player>();
					Boolean firstBid = true;
					auctionPlayers.addAll(tempPlayers);
					while (!(auctionPlayers.size() == 1)) {
						// System.out.println(auctionPlayers);
						if (firstBid) {
							System.out.println(auctionPlayers.get(i).getColor() + " starts bidding at " + minPrice);
							firstBid = false;
						} else {
							System.out.println(auctionPlayers.get(i).getColor()
									+ ", how much do you want to bid on this powerplant? (anything less or equal to current bid to pass) "
									+ gs.getAuctionCard().toString() + ". Current Bid is " + minPrice);
							int bid = Integer.parseInt(input.nextLine());
							if (bid > auctionPlayers.get(i).getMoney()) {
								while (bid > auctionPlayers.get(i).getMoney()) {
									System.out.println(auctionPlayers.get(i).getColor()
											+ "how much do you want to bid on this powerplant? (anything less or equal to current bid to pass) "
											+ gs.getAuctionCard().toString() + ". Current Bid is " + minPrice
											+ ". You don't have enough money for the previous bid");
									bid = Integer.parseInt(input.nextLine());
								}
							}
							if (bid <= minPrice) {
								auctionPlayers.remove(i);
								i--;
								if (i == -1) {
									i = auctionPlayers.size() - 1;
								}
							} else {
								minPrice = bid;
								System.out.println(auctionPlayers.get(i).getColor() + " bids " + minPrice);
							}
						}
						i++;
						if (i == auctionPlayers.size()) {
							i = 0;
						}
					}
					System.out.println(auctionPlayers.get(0).getColor() + " has won the auction for "
							+ gs.getAuctionCard().toString() + " for " + minPrice + ".");
					auctionPlayers.get(0).subtractMoney(minPrice);
					tempPlayers.removeAll(auctionPlayers);
					gs.getCurrentMarket().remove(gs.getAuctionCard());
					gs.addPowerPlant();
					auctionPlayers.get(0).addPowerPlant(gs.getAuctionCard());
					gs.rearrangeMarket();
					System.out.println("Current Market: " + gs.getCurrentMarket());
					System.out.println("Future Market: " + gs.getFutureMarket());
					gs.setAuctionCard(null);
				}

			}
			if (gs.getMarketStep3()) {
				gs.restructureMarket();
			}

			// Phase 3
			// Resource Buying

			System.out.println("Now it's time for resource selection");
			for (int i = 0; i <= 3; i++) {

				System.out.print("Coal Market:");
				System.out.println(gs.getCoalMarket());
				System.out.print("Oil Market");
				System.out.println(gs.getOilMarket());
				System.out.print("Nuclear Market");
				System.out.println(gs.getNuclearMarket());
				System.out.print("Trash Market");
				System.out.println(gs.getTrashMarket());
				resourceSelection(i);
				System.out.println("Are You Done with Purchasing Resources?");
				input = new Scanner(System.in);
				String answer = input.nextLine();
				if (answer.equals("yes")) {
					if (i != 3)
						System.out.println("Ok. Moving on to next Player");
				} else if (answer.equals("no")) {
					resourceSelection(i);

				}
			}

			// phase 4
			// citybuilding
			System.out.println("Phase 4");
			for (int i = gs.getPlayerOrder().size() - 1; i >= 0; i--) {
				System.out.println(
						gs.getPlayerOrder().get(i).getColor() + ": Which city do you want to build to (-1 for none)");
				Player p = gs.getPlayerOrder().get(i);
				String cmd = input.nextLine();
				while (!cmd.equals("-1")) {
					if (cmd.equals("-1")) {

					} else {
						City c = gs.findCity(cmd);
						int cost = 0;
						if (c != null) {
							if (gs.getNumCities().get(p) == 0) {
								cost = c.getCost();
							} else {
								cost = c.leastCost(p);
								cost += c.getCost();
								if (p.getMoney() > cost) {
									p.addMoney(cost * -1);
									c.addPlayer(p);
									gs.addCityBuilt(p);
								}
							}
							System.out.println("Cost: " + cost);
							gs.checkPowerPlantSize();
							System.out.println(gs.getPlayerOrder().get(i).getColor()
									+ ": Which city do you want to build to (-1 for none)");
							cmd = input.nextLine();
						}
					}
				}

			}

			// phase 5
			// poweringstuffs
			TreeMap<Player, Integer> numCitiesPowered = new TreeMap<Player, Integer>();
			for (int i = 0; i < gs.getPlayerOrder().size(); i++) {
				Player current = gs.getPlayerOrder().get(i);
				System.out.println(current.getColor()
						+ ": choose a powerplant to burn resources from for power. (index 0-2). -1 to quit");
				int index = Integer.parseInt(input.nextLine());
				int totalCitiesPowered = 0;
				int numCitiesOwned = gs.getNumCities().get(current);
				while (index != -1) {
					if (current.getPowerList().get(index).burnResources(current.getPowerList().get(index).getCost())) {
						totalCitiesPowered += Math.min(current.getPowerList().get(index).getNumCitiesPowered(),
								numCitiesOwned);
						numCitiesOwned = numCitiesOwned
								- Math.min(current.getPowerList().get(index).getNumCitiesPowered(), numCitiesOwned);
						System.out.println(
								"Powerplant successfully powered. " + totalCitiesPowered + " cities powered in total.");
					} else {
						System.out.println("Unsuccessful.");
					}
					System.out.println(current.getColor() + ": choose a powerplant to burn. (index 0-2). -1 to quit");
					index = Integer.parseInt(input.nextLine());
				}
				numCitiesPowered.put(current, totalCitiesPowered);
			}
			gs.givingMoney(numCitiesPowered);

			gs.restockResources();

			gs.marketFix();

			if (turn1) {
				turn1 = false;
			}
		}

	}

	@SuppressWarnings("unchecked")
	public static void resourceSelection(int playerNum) {

		int originalMoney = gs.getPlayerOrder().get(playerNum).getMoney();
		String[] resources = { "coal", "oil", "trash", "nuclear" };
		System.out.println(gs.getPlayerOrder().get(playerNum).getColor() + "'s turn. You have " + originalMoney
				+ " dollars. Enter 0 for coal, 1 for oil, 2 trash, 3 for nuclear, -1 to pass");
		int numResource = input.nextInt();
		if (numResource !=-1) {
		System.out.println("How much of " + resources[numResource] + " do you want");
		int numReq = input.nextInt();
		TreeMap<Integer, ArrayList<String>> market = new TreeMap<Integer, ArrayList<String>>();
		String resource = resources[numResource];

		market = gs.getMarket(resource);

		TreeMap<Integer, ArrayList<String>> originalMarket;
		originalMarket = copyMarket(market);

		int numCollectedResources = 0;
		Iterator<Integer> iter = market.keySet().iterator();
		while (iter.hasNext()) {
			int key = iter.next();
			int size = market.get(key).size();
			if (size > 0) {
				for (int w = 0; w < size; w++) {
					ArrayList<String> tempList = market.get(key);
					tempList.remove(0);
					numCollectedResources++;
					gs.getPlayerOrder().get(playerNum).subtractMoney(key);
					if (numCollectedResources == numReq)
						break;
				}
			}
			if (numCollectedResources == numReq)
				break;
		}
		if (numCollectedResources < numReq || gs.getPlayerOrder().get(playerNum).getMoney() < 0) {
			if (numCollectedResources < numReq)
				System.out.println("Sorry Market Doesn't have enough resources");
			else if (gs.getPlayerOrder().get(playerNum).getMoney() < 0)
				System.out.println("Sorry You don't have that much money");
			else if (numCollectedResources < numReq && gs.getPlayerOrder().get(playerNum).getMoney() < 0)
				System.out.println("Sorry You don't have that much money & the market doesn't have enough resource");
			else if (numResource == 0 || gs.getPlayerOrder().get(playerNum).getMaxCoal() < numReq) {
				System.out.println("You don't have enough space for this resource buy");
			} else if (numResource == 1 || gs.getPlayerOrder().get(playerNum).getMaxOil() < numReq) {
				System.out.println("You don't have enough space for this resource buy");
			} else if (numResource == 2 || gs.getPlayerOrder().get(playerNum).getMaxTrash() < numReq) {
				System.out.println(gs.getPlayerOrder().get(playerNum).getMaxTrash());
				System.out.println("You don't have enough space for this resource buy");
			} else if (numResource == 3 || gs.getPlayerOrder().get(playerNum).getMaxNuclear() < numReq) {
				System.out.println("You don't have enough space for this resource buy");
			} else if (numResource == 0)
				gs.getPlayerOrder().get(playerNum).setMoney(originalMoney);
			gs.setMarket(resource, originalMarket);
		}

		System.out
				.println("The amount of you have now is " + gs.getPlayerOrder().get(playerNum).getMoney() + " dollars");
		}
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
}
