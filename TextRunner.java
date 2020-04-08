import java.io.*;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Iterator;
public class TextRunner {
	
	static 	GameState gs = null;
	

	
	static Scanner input = new Scanner(System.in); // player input
	public static void main(String[] args) throws IOException 
	{
		gs = GameState.getGamestate();
		
		int turn = 0;

		System.out.println("Enter Playable Colors (4)");
		ArrayList<String> playableColors = new ArrayList<String>();
		playableColors.add(input.nextLine());

		while (!gs.isEndOfGame()) {
			// phase 1
			gs.determinePlayerOrder();

			// phase 2
			// auction
			System.out.println("Current Market: "+gs.getCurrentMarket());
			System.out.println("Future Market: "+gs.getFutureMarket());
			ArrayList<Player> tempPlayers = new ArrayList<Player>();
			tempPlayers.addAll(gs.getPlayerOrder());
			int minPrice=0;
			while (!gs.phaseDone()) {
				System.out.println(tempPlayers.get(0).getColor()+", choose the index of the powerPlant to start auction on (0-3)");
				int index=Integer.parseInt(input.nextLine());
				gs.setAuctionCard(gs.getCurrentMarket().get(index));
				minPrice=gs.getCurrentMarket().get(index).getMinBid();
				int i=0;
				while (!gs.isAuctionDone()) {
					i++;
					if(i==4) {
						i=0;
					}
					System.out.println(tempPlayers.get(i).getColor()+", how much do you want to bid on this powerplant (-1 to pass)?"+gs.getAuctionCard().toString());
					System.out.println("Current Bid: "+minPrice);
					int bid=Integer.parseInt(input.nextLine());
					if (bid<=minPrice) {
						gs.getBids().put(tempPlayers.get(i), -1);
					}
					else {
						minPrice=bid;
					}
				}
				Player auctionWinner=null;
				for (Player p:gs.getBids().keySet()) {
					if (gs.getBids().get(p)!=-1) {
						auctionWinner=p;
					}
				}
				System.out.println(auctionWinner.getColor()+" has won the auction for "+gs.getAuctionCard().toString()+ " for "+minPrice);
				gs.setAuctionCard(null);
			
				
			}
			System.out.println("Now it's time for resource selection");
			for(int i = 1;i<=4;i++)
			{
				
				resourceSelection(i);
				System.out.println("Are You Done with Purchasing Reosurces?");
				String answer = input.nextLine();
				if(answer.equals("yes"))
				{
					System.out.println("Ok...Moving on to next Player");
				}
				else if(answer.equals("no"))
				{
					resourceSelection(i);

				}		
			}
			
		}
		
			// phase 3
			// resources

			// phase 4
			// citybuilding

			// phase 5
			// poweringstuffs
			if (gs.getMarketStep3()) {
				gs.restructureMarket();
			}
		}
	public static void resourceSelection( int playerNum)
	{
		String[] resources = {"coal", "oil", "trash", "nuclear"};	
		System.out.println("Player " + playerNum + "'s turn. Enter 0 for coal, 1 for oil, 2 trash, 3 for nuclear");
		int numResource = input.nextInt();
		System.out.println("How much of " + resources[numResource] + "do you want");
		int numReq = input.nextInt();
		TreeMap <Integer, ArrayList<String>> market = new TreeMap<Integer, ArrayList<String>>();
		TreeMap <Integer,String> nuclearMarket = new TreeMap<Integer,String>();
		String resource = resources[numResource];
		if(resource.equals("coal")){
			market = gs.getCoalMarket();
			}
		else if(resource.equals("oil")){
			market = gs.getOilMarket();
			}	
		else if(resource.equals("trash")){
			market = gs.getTrashMarket();
		}
		else if(resource.equals("nuclear")) {
			nuclearMarket = gs.getNuclearMarket();
		}
		int numCollectedResources=0;
		
		while(numCollectedResources!=numReq) 
		{
				if(resource.equals("coal")||resource.equals("oil")
						||resource.equals("nuclear"))
				{		
					Iterator<Integer> iter = market.keySet().iterator();
						while(iter.hasNext())
						{			
							int key = iter.next();
							int size = market.get(key).size();
							if(size>0)
							{
								String req = market.get(key).get(0);
								for(int w = 0;w<=size;w++)
								{
									
									market.get(key).remove(req);
									numCollectedResources++;
									int originalMoney = gs.getPlayerOrder().get(playerNum).getMoney();
									gs.getPlayerOrder().get(playerNum).subtractMoney(key);
									if(gs.getPlayerOrder().get(playerNum).getMoney()<0)
									{
										System.out.println("Sorry Can't Afford These Resources");
										gs.getPlayerOrder().get(playerNum).setMoney(originalMoney);
										break;
									}
									if(numCollectedResources==numReq)
										break;
								}
							}
						}	
					}
				else if(resource.equals("nuclear")) 
				{
					Iterator<Integer> iter = nuclearMarket.keySet().iterator();
					if(nuclearMarket.keySet().size()>=numReq)
					{
						while(iter.hasNext())
						{
							int key = iter.next();
							nuclearMarket.put(key, "");
							numCollectedResources++;
							int originalMoney = gs.getPlayerOrder().get(playerNum).getMoney();
							gs.getPlayerOrder().get(playerNum).subtractMoney(key);
							if(gs.getPlayerOrder().get(playerNum).getMoney()<0)
							{
								System.out.println("Sorry Can't Afford These Resources");
								gs.getPlayerOrder().get(playerNum).setMoney(originalMoney);
								break;
							}
							if(numCollectedResources==numReq)
								break;
						}
					}
				}
				if(numCollectedResources<numReq)
				{
					System.out.println("Sorry Market Doesn't have enough resources");
					break;
				}
		}
		
	}
}
