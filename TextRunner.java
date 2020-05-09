import java.io.*;

import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;

public class TextRunner {

	static GameState gs = null;

	static Scanner input = new Scanner(System.in); // player input

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		boolean turn1 = true;
		gs = GameState.getGamestate();
		ArrayList<String> playableColors = new ArrayList<String>();
		// System.out.println("Enter Playable Colors (4), separate lines");

		// for (int i = 0; i < 4; i++) {
		// playableColors.add(input.nextLine());
		// }

		// temp
		System.out.println("Temp: Playable Colors are green, blue, purple, red");
		playableColors.add("green");
		playableColors.add("red");
		playableColors.add("purple");
		playableColors.add("blue");
		ArrayList<City> cityList = gs.getListOfCites();
		ArrayList<City> adjustedCityList = new ArrayList<City>();
		for (City c : cityList) {
			if (playableColors.contains(c.getZoneColor())) {
				adjustedCityList.add(c);
			}
		}
		gs.setListOfCites(adjustedCityList);

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
			for (int x = 1; x <= gs.getCurrentMarket().size(); x++) {
				System.out.println(x + ". " + gs.getCurrentMarket().get(x - 1));
			}
			System.out.println("Future Market: ");
			for (int x = 1; x <= gs.getFutureMarket().size(); x++) {
				System.out.println(x + ". " + gs.getFutureMarket().get(x - 1));
			}

			ArrayList<Player> tempPlayers = new ArrayList<Player>();
			tempPlayers.addAll(gs.getPlayerOrder());

			// System.out.println(tempPlayers);

			int minPrice = 0;
			while (!tempPlayers.isEmpty()) {
				System.out.println();
				System.out.println(tempPlayers.get(0).getColor() + "'s money: " + tempPlayers.get(0).getMoney());
				System.out.println(tempPlayers.get(0).getColor()
						+ ", choose the index of the powerPlant to start auction on (1-4), 0 to pass");
				int index = Integer.parseInt(input.nextLine()) - 1;
				if (index == -1 && turn1) {
					while (index == -1) {
						System.out.println(tempPlayers.get(0).getColor()
								+ ", choose the index of the powerPlant to start auction on (1-4). You cannot pass b/c it's turn 1");
						index = Integer.parseInt(input.nextLine()) - 1;
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
					System.out.println();
					System.out.println("Current Market: ");
					for (int x = 1; x <= gs.getCurrentMarket().size(); x++) {
						System.out.println(x + ". " + gs.getCurrentMarket().get(x - 1));
					}
					System.out.println("Future Market: ");
					for (int x = 1; x <= gs.getFutureMarket().size(); x++) {
						System.out.println(x + ". " + gs.getFutureMarket().get(x - 1));
					}
					gs.setAuctionCard(null);
				}

			}
			if (gs.getMarketStep3()) {
				gs.restructureMarket();
			}

			// Phase 3
			// Resource Buying
			System.out.println();
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
				askResource(i);
				System.out.println("Are You Done with Purchasing Resources?");
				input = new Scanner(System.in);
				String answer = input.nextLine();
				if (answer.equals("yes")) {
					if (i != 3)
						System.out.println("Ok. Moving on to next Player");
				} else if (answer.equals("no")) {
					askResource(i);

				}
			}

			// phase 4
			// citybuilding
			System.out.println();
			System.out.println("Phase 4");
			for (int i = gs.getPlayerOrder().size() - 1; i >= 0; i--) {

				City c;
				System.out.println("Playable Regions: " + playableColors.toString());
				System.out.println("Max number of Players at a city is " + gs.getMaxHouseInCity());
				System.out.println(
						gs.getPlayerOrder().get(i).getColor() + ": Which city do you want to build to (-1 for none)");
				Player p = gs.getPlayerOrder().get(i);
				String cmd = input.nextLine();
				if (turn1 && cmd.equals("-1")) {
					while (cmd.equals("-1")) {
						System.out.println(gs.getPlayerOrder().get(i).getColor()
								+ ": Which city do you want to build to. You cannot pass b/c it's turn 1");
						cmd = input.nextLine();
					}
				}
				while (!cmd.equals("-1")) {
					if (cmd.equals("-1")) {
					} else {
						c = gs.findCity(cmd);
						int cost = 0;
						if (c == null) {
							System.out.println("City is unavailable to be built at");
						} else {
							// System.out.println("Inputed Phrase was " + cmd);
							// System.out.println("City Found was " + c.getName());
							// System.out.println("City has " + c.getPlayersAtCity().size() + " players");

							if (c.getPlayersAtCity().size() >= gs.getMaxHouseInCity()) {
								System.out.println(
										"There is no more space for a house at that city. Please choose another (-1 to quit)");
							} else if (gs.getNumCities().get(p) == 0) {
								cost = c.getCost();
								System.out.println("First City");
							} else {
								cost = c.leastCost(p);
								cost += c.getCost();
							}

							if (p.getMoney() > cost) {
								p.addMoney(cost * -1);
								c.addPlayer(p);
								gs.addCityBuilt(p);
							} else {
								System.out.println("You don't have enough money to build to there.");
							}

							System.out.println("Cost: " + cost);
							System.out.println("You now have $" + p.getMoney());
						}

						gs.checkPowerPlantSize();
						System.out.println("Playable Regions: " + playableColors.toString());
						System.out.println(gs.getPlayerOrder().get(i).getColor()
								+ ": Which city do you want to build to (-1 for none)");
						cmd = input.nextLine();
					}
				}
				if (gs.getNumCities().get(gs.getPlayerOrder().get(i)) >= 7&&gs.getStep()==1)
					gs.nextStep();

			}

			// phase 5
			// poweringstuffs
			if (!gs.isEndOfGame()) {
				HashMap<Player, Integer> numCitiesPowered = new HashMap<Player, Integer>();
				for (int i = 0; i < gs.getPlayerOrder().size(); i++) {
					Player current = gs.getPlayerOrder().get(i);
					System.out.println(current.getColor()
							+ ": choose a powerplant to burn resources from for power. (index 0-2). -1 to quit");
					System.out.println("Here are your Powerplants: " + gs.getPlayerOrder().get(i).getPowerList());
					int index = Integer.parseInt(input.nextLine());
					int totalCitiesPowered = 0;
					int numCitiesOwned = gs.getNumCities().get(current);
					while (index != -1) {
						if (!current.getPowerList().get(index).isHybrid() && current.getPowerList().get(index)
								.burnResources(current.getPowerList().get(index).getCost())) {

							totalCitiesPowered += current.getPowerList().get(index).getNumCitiesPowered();
							System.out.println(
									"Powerplant can power  " + current.getPowerList().get(index).getNumCitiesPowered()
											+ " cities powered in total.");
							PowerPlant plant = current.getPowerList().get(index);
							reSupplyAfterBurn(plant.getCost().get(0), plant);
						}

						else if (current.getPowerList().get(index).isHybrid()) {

							System.out.println(
									"You've chosen a hybrid power plant. You have two choices of resources to burn(Enter 1 or 2)");
							String firstElement = current.getPowerList().get(index).getCost().get(0);
							String firstResource = firstElement.substring(0, firstElement.indexOf("||"));
							String secondResource = firstElement.substring(firstElement.indexOf("||") + 2,
									firstElement.length());
							System.out.println("1) First Choice: " + firstResource);
							System.out.println("2) Second Choice: " + secondResource);
							int choice = input.nextInt();
							String wordToBurn = "";
							if (choice == 1)
								wordToBurn = firstResource;
							else if (choice == 2)
								wordToBurn = secondResource;
							ArrayList<String> resourcesToBurn = new ArrayList<String>();
							PowerPlant plant = current.getPowerList().get(index);
							for (String x : plant.getStorage()) {
								if (x.equalsIgnoreCase(wordToBurn))
									resourcesToBurn.add(wordToBurn);
							}
							if (plant.burnResources(resourcesToBurn)) {
								totalCitiesPowered += current.getPowerList().get(index).getNumCitiesPowered();
								System.out.println("Powerplant can power " + totalCitiesPowered + " cities in total.");
								reSupplyAfterBurn(wordToBurn, plant);
							} else {
								System.out.println("Unsuccessful");
							}

						} else {
							System.out.println("Unsuccessful.");
						}
						System.out
								.println(current.getColor() + ": choose a powerplant to burn. (index 0-2). -1 to quit");
						input = new Scanner(System.in);
						index = Integer.parseInt(input.nextLine());

					}
					System.out.println("All of the power plant burned succesfully powered "
							+ Math.min(totalCitiesPowered, numCitiesOwned) + " cities");
					numCitiesPowered.put(current, Math.min(totalCitiesPowered, numCitiesOwned));
				}
				gs.givingMoney(numCitiesPowered);
				gs.setRestock();
				gs.marketFix();
				System.out.print("Coal Market:");
				System.out.println(gs.getCoalMarket());
				System.out.print("Oil Market");
				System.out.println(gs.getOilMarket());
				System.out.print("Nuclear Market");
				System.out.println(gs.getNuclearMarket());
				System.out.print("Trash Market");
				System.out.println(gs.getTrashMarket());
				gs.givingMoney(numCitiesPowered);
				gs.setRestock();
				gs.marketFix();
			}

			if (turn1) {
				turn1 = false;
			}
		}
		System.out.println("End of the Game!");
		ArrayList<Player> list = gs.getPlayerOrder();
		HashMap<Player, Integer> numCitiesPowered = new HashMap<Player, Integer>();
		for (int i = 0; i < gs.getPlayerOrder().size(); i++) {
			Player current = gs.getPlayerOrder().get(i);
			System.out.println(current.getColor()
					+ ": choose a powerplant to burn resources from for power. (index 0-2). -1 to quit");
			System.out.println("Here are your Powerplants: " + gs.getPlayerOrder().get(i).getPowerList());
			int index = Integer.parseInt(input.nextLine());
			int totalCitiesPowered = 0;
			int numCitiesOwned = gs.getNumCities().get(current);
			while (index != -1) {
				if (!current.getPowerList().get(index).isHybrid() && current.getPowerList().get(index)
						.burnResources(current.getPowerList().get(index).getCost())) {

					totalCitiesPowered += current.getPowerList().get(index).getNumCitiesPowered();
					System.out.println(
							"Powerplant can power  " + current.getPowerList().get(index).getNumCitiesPowered()
									+ " cities powered in total.");
					PowerPlant plant = current.getPowerList().get(index);
					reSupplyAfterBurn(plant.getCost().get(0), plant);
				}

				else if (current.getPowerList().get(index).isHybrid()) {

					System.out.println(
							"You've chosen a hybrid power plant. You have two choices of resources to burn(Enter 1 or 2)");
					String firstElement = current.getPowerList().get(index).getCost().get(0);
					String firstResource = firstElement.substring(0, firstElement.indexOf("||"));
					String secondResource = firstElement.substring(firstElement.indexOf("||") + 2,
							firstElement.length());
					System.out.println("1) First Choice: " + firstResource);
					System.out.println("2) Second Choice: " + secondResource);
					int choice = input.nextInt();
					String wordToBurn = "";
					if (choice == 1)
						wordToBurn = firstResource;
					else if (choice == 2)
						wordToBurn = secondResource;
					ArrayList<String> resourcesToBurn = new ArrayList<String>();
					PowerPlant plant = current.getPowerList().get(index);
					for (String x : plant.getStorage()) {
						if (x.equalsIgnoreCase(wordToBurn))
							resourcesToBurn.add(wordToBurn);
					}
					if (plant.burnResources(resourcesToBurn)) {
						totalCitiesPowered += current.getPowerList().get(index).getNumCitiesPowered();
						System.out.println("Powerplant can power " + totalCitiesPowered + " cities in total.");
						reSupplyAfterBurn(wordToBurn, plant);
					} else {
						System.out.println("Unsuccessful");
					}

				} else {
					System.out.println("Unsuccessful.");
				}
				System.out
						.println(current.getColor() + ": choose a powerplant to burn. (index 0-2). -1 to quit");
				input = new Scanner(System.in);
				index = Integer.parseInt(input.nextLine());

			}
			System.out.println("All of the power plant burned succesfully powered "
					+ Math.min(totalCitiesPowered, numCitiesOwned) + " cities");
			numCitiesPowered.put(current, Math.min(totalCitiesPowered, numCitiesOwned));
		}
		for (int i = 0; i < list.size() - 1; i++) {
			int min_idx = i;
			for (int j = i + 1; j < list.size(); j++)
				if (numCitiesPowered.get(list.get(j)) < numCitiesPowered.get(list.get(min_idx)))
					min_idx = j;

			Player temp = list.get(min_idx);
			list.set(min_idx, list.get(i));
			list.set(i, temp);
		}
		System.out.println("Final Results:");
		for (int i=list.size()-1;i>=0;i--) {
			System.out.println(i+":"+list.get(i).getColor()+" with "+numCitiesPowered.get(list.get(i)));
		}
	}

	public static void reSupplyAfterBurn(String wordBurned, PowerPlant plant) {
		if (wordBurned.equals("coal")) {
			int newCoal = gs.getCoalSupply() + plant.getCost().size();
			gs.setCoalSupply(newCoal);
		} else if (wordBurned.equals("trash")) {
			int newTrash = gs.getTrashSupply() + plant.getCost().size();
			gs.setTrashSupply(newTrash);
		} else if (wordBurned.equals("oil")) {
			int newOil = gs.getTrashSupply() + plant.getCost().size();
			gs.setOilSupply(newOil);
		} else if (wordBurned.equals("nuclear")) {
			int newNuclear = gs.getTrashSupply() + plant.getCost().size();
			gs.setNuclearSupply(newNuclear);
		}
	}

	public static void askResource(int playerNum) {
		int originalMoney = gs.getPlayerOrder().get(playerNum).getMoney();
		String[] resources = { "coal", "oil", "trash", "nuclear" };

		System.out.println(gs.getPlayerOrder().get(playerNum).getColor() + "'s turn. You have " + originalMoney
				+ " dollars. Enter 0 for coal, 1 for oil, 2 trash, 3 for nuclear, -1 to pass");

		System.out.println("The Powerplants you have: " + gs.getPlayerOrder().get(playerNum).getPowerList());
		int numResource = input.nextInt();
		if (numResource != -1) {
			System.out.println("How much of " + resources[numResource] + " do you want");
			int numReq = input.nextInt();
			resourceMovement(playerNum, numResource, numReq);
		}
	}

	@SuppressWarnings("unchecked")
	public static void resourceMovement(int playerNum, int numResource, int numReq) {
		int originalMoney = gs.getPlayerOrder().get(playerNum).getMoney();
		String[] resources = { "coal", "oil", "trash", "nuclear" };
		System.out.println("Please chose the card you want to place these resources on(0 to 3)");
		System.out.println(gs.getPlayerOrder().get(playerNum).getPowerList());
		int indexOfCard = input.nextInt();
		PowerPlant plant = gs.getPlayerOrder().get(playerNum).getPowerList().get(indexOfCard);
		ArrayList<String> cost = plant.getCost();
		boolean tryAgain = false;
		if (!plant.isHybrid()) {
			if (!cost.contains(resources[numResource])) {
				System.out.println("You can't place " + resources[numResource] + " on this card");
				tryAgain = true;

			} else if (plant.getStorage().size() + numReq > cost.size() * 2) {
				System.out.println("Sorry you don't have space to put " + resources[numResource] + " on this card");
				tryAgain = true;
			}
		} else {
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
			if (!(firstElement.contains(resources[numResource]))) {
				System.out.println("You can't place " + resources[numResource] + " on this card");
				tryAgain = true;
			}

			else if (numOfFirstResource + numReq > plant.getCost().size() * 2
					|| numOfSecondResource + numReq > plant.getCost().size() * 2) {
				System.out.println("Sorry you don't have space to put " + resources[numResource] + " on this card");
				tryAgain = true;
			}
		}
		if (tryAgain) {
			System.out.println("Do you want to try again or not");
			input = new Scanner(System.in);
			String yesOrNo = input.nextLine();
			if (yesOrNo.equalsIgnoreCase("yes")) {
				askResource(playerNum);
				return;
			} else
				return;
		}
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
			if (numCollectedResources < numReq && gs.getPlayerOrder().get(playerNum).getMoney() < 0)
				System.out.println(
						"Sorry you don't have that much much money and the market doesn't have enough resources");
			else if (numCollectedResources < numReq)
				System.out.println("Sorry Market Doesn't have enough resources");
			else if (gs.getPlayerOrder().get(playerNum).getMoney() < 0)
				System.out.println("Sorry You don't have that much money");
			gs.getPlayerOrder().get(playerNum).setMoney(originalMoney);
			gs.setMarket(resource, originalMarket);

		}
		System.out
				.println("The amount of you have now is " + gs.getPlayerOrder().get(playerNum).getMoney() + " dollars");
		ArrayList<String> attainedResources = new ArrayList<String>();
		for (int i = 1; i <= numReq; i++) {
			attainedResources.add(resource);
		}
		gs.getPlayerOrder().get(playerNum).getPowerList().get(indexOfCard).addResources(attainedResources);
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
/*
 * if
 * (current.getPowerList().get(index).burnResources(current.getPowerList().get(
 * index).getCost())) { totalCitiesPowered +=
 * Math.min(current.getPowerList().get(index).getNumCitiesPowered(),
 * numCitiesOwned); numCitiesOwned = numCitiesOwned -
 * Math.min(current.getPowerList().get(index).getNumCitiesPowered(),
 * numCitiesOwned); System.out.println( "Powerplant successfully powered. " +
 * totalCitiesPowered + " cities powered in total.");
 */