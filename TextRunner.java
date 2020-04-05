import java.io.*;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.ArrayList;

public class TextRunner {
	public static void main(String[] args) throws IOException {
		Scanner input = new Scanner(System.in); // player input
		GameState gs = new GameState();
		int turn = 0;

		System.out.println("Enter Playable Colors (4)");
		ArrayList<String> playableColors = new ArrayList<String>();
		playableColors.add(input.nextLine());

		while (!gs.isEndOfGame()) {
			// phase 1
			gs.determinePlayerOrder();

			// phase 2
			// auction
			System.out.println("Current Market: " + gs.getCurrentMarket());
			System.out.println("Future Market: " + gs.getFutureMarket());
			ArrayList<Player> tempPlayers = new ArrayList<Player>();
			tempPlayers.addAll(gs.getPlayerOrder());
			int minPrice = 0;
			while (!gs.phaseDone()) {
				System.out.println(tempPlayers.get(0).getColor()
						+ ", choose the index of the powerPlant to start auction on (0-3)");
				int index = Integer.parseInt(input.nextLine());
				gs.setAuctionCard(gs.getCurrentMarket().get(index));
				minPrice = gs.getCurrentMarket().get(index).getMinBid();
				int i = 0;
				while (!gs.isAuctionDone()) {
					i++;
					if (i == 4) {
						i = 0;
					}
					System.out.println(tempPlayers.get(i).getColor()
							+ ", how much do you want to bid on this powerplant (-1 to pass)?"
							+ gs.getAuctionCard().toString());
					System.out.println("Current Bid: " + minPrice);
					int bid = Integer.parseInt(input.nextLine());
					if (bid <= minPrice) {
						gs.getBids().put(tempPlayers.get(i), -1);
					} else {
						minPrice = bid;
						gs.getBids().put(tempPlayers.get(i), bid);
					}
				}
				Player auctionWinner = null;
				for (Player p : gs.getBids().keySet()) {
					if (gs.getBids().get(p) != -1) {
						auctionWinner = p;
					}
				}
				System.out.println(auctionWinner.getColor() + " has won the auction for "
						+ gs.getAuctionCard().toString() + " for " + minPrice + ".");
				gs.setAuctionCard(null);
				gs.playerDecision(auctionWinner);
				gs.getBids().clear();
				gs.addPowerPlant();
			}
			if (gs.getMarketStep3()) {
				gs.restructureMarket();
			}

			// phase 3
			for (int i = gs.getPlayerOrder().size() - 1; i >= 0; i--) {
//				System.out.println("Type in how many coal you wish to buy (0 for none)");
//				int numCoal=Integer.parseInt(input.nextLine());
//				if (numCoal>0) {
//					System.out.println("Choose which powerplant to move these resources to (index 0-");
//				}
//				
//				System.out.println("Type in how many oil you wish to buy (0 for none)");
//				int numOil=Integer.parseInt(input.nextLine());
//				
//				System.out.println("Type in how many trash you wish to buy (0 for none)");
//				int numTrash=Integer.parseInt(input.nextLine());
//				
//				System.out.println("Type in how many nuclear you wish to buy (0 for none)");
//				int numNuclear=Integer.parseInt(input.nextLine());
//				
//				ArrayList<String>resources=new ArrayList<String>();
//				while(numCoal>0) {
//					resources.add("coal");
//					int price=gs.removeCoal();
//				}
//				//index refers to the powerplant the player has chosen the resources to go to
//				gs.getPlayerOrder().get(i).getPowerList().get(index).addResources()
			}

			// phase 4
			// citybuilding
			for (int i = gs.getPlayerOrder().size() - 1; i >= 0; i--) {
				System.out.println("Which city do you want to build to (-1 for none)");
				Player p = gs.getPlayerOrder().get(i);
				String cmd = input.nextLine();
				while (!cmd.equals("-1")) {
					City c = gs.findCity(cmd);
					int cost = 0;
					if (c != null) {
						cost = c.leastCost(p);
						cost += c.getCost();
						if (p.getMoney() > cost) {
							p.addMoney(cost * -1);
							c.addPlayer(p);
							gs.addCityBuilt(p);
						}
					}
					gs.checkPowerPlantSize();
					System.out.println("Which city do you want to build to (-1 for none)");
					cmd = input.nextLine();
				}
			}

			// phase 5
			// poweringstuffs
			TreeMap<Player, Integer> numCitiesPowered = new TreeMap<Player, Integer>();
			for (int i = 0; i < gs.getPlayerOrder().size(); i++) {
				Player current = gs.getPlayerOrder().get(i);
				System.out.println(current.getColor() + ": choose a powerplant to burn. (index 0-2). -1 to quit");
				int index = Integer.parseInt(input.nextLine());
				int totalCitiesPowered = 0;
				while (index != -1) {
					if (current.getPowerList().get(index).burnResources(current.getPowerList().get(index).getCost())) {
						totalCitiesPowered += current.getPowerList().get(index).getNumCitiesPowered();
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
		}
	}
}
