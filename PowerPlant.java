import java.util.ArrayList;

//Note: Resources are fully lowercase (oil, coal, nuclear, trash)
public class PowerPlant implements Comparable {
	private ArrayList<String> cost;
	private ArrayList<String> storage;
	int minBid, numCitiesPowered;

	public PowerPlant() {
		cost = new ArrayList<String>();
		storage = new ArrayList<String>();
		minBid = 0;
		numCitiesPowered = 0;
	}

	public PowerPlant(int minBid) {
		cost = new ArrayList<String>();
		storage = new ArrayList<String>();
		this.minBid = minBid;
		numCitiesPowered = 0;
	}

	public PowerPlant(ArrayList<String> cost, ArrayList<String> storage, int minBid, int numCitiesPowered) {
		this.cost = cost;
		this.storage = storage;
		this.minBid = minBid;
		this.numCitiesPowered = numCitiesPowered;
	}

	public PowerPlant(int minBid, ArrayList<String> cost, int numCitiesPowered) {
		this.cost = cost;
		this.storage = new ArrayList<String>();
		this.minBid = minBid;
		this.numCitiesPowered = numCitiesPowered;
	}

	public boolean isHybrid() {
		if (cost.get(0).contains("||")) {
			return true;
		}
		return false;
	}

	public void addResources(ArrayList<String> resources) {

		// if the plant is green
		if (cost.size() == 0) {
			return;
		}
		storage.addAll(resources);
		/*if (cost.get(0).contains("||")) {
			String firstResource = cost.get(0).substring(0, cost.get(0).indexOf("||"));
			String secondResource = cost.get(0).substring(cost.get(0).indexOf("||") + 1, cost.get(0).length());
			int index = 0;
			while (!isFull()) {
				if (resources.get(index).equals(firstResource) || resources.get(index).equals(secondResource)) {
					storage.add(resources.get(index));
				}
				++index;
			}
		}
		int index = 0;
		while (!isFull()) {
			while (storage.size() <= cost.size() * 2) {
				if (cost.get(0).contains(resources.get(index))) {
				}
				storage.add(resources.get(index));
			}
			++index;
		}
		*/
	}

	public boolean isFull() {
		return storage.size() >= (cost.size() * 2);
	}

	public boolean burnResources(ArrayList<String> resources) {
		// green PowerPlant
		if (cost.size() == 0) {
			return true;
		}
		if(!this.isHybrid())
		{
			if(storage.size()>=cost.size())
			{
				actualRemoval(cost.get(0));
				return true;
			}
			return false;
		}
		else if(this.isHybrid())
		{
			if(resources.size()==0)
				return false;
			if (canBurnH(resources)) {
				for (String str:resources) {
					storage.remove(str);
				}
			}
			else {
				return false;
			}
		}
		return false;
		/*
		ArrayList<String> costCopy = new ArrayList<String>();
		for (String s : cost) {
			costCopy.add(s);
		}
		for (String s : costCopy) {
			if (!specialRemove(resources, s))
				return false;
		}
		for (String s : cost) {
			specialRemove(storage, s);
		}
		return true;
		*/
	}
	public void actualRemoval(String word)
	{
		for(int i = 1;i<=cost.size();i++)
		{
			storage.remove(word);
		}
	}

	// used for hybrid plants
	public boolean specialRemove(ArrayList<String> resources, String s) {
		for (int i = 0; i < resources.size(); i++) {
			if (s.contains(resources.get(i))) {
				resources.remove(i);
				return true;
			}
		}
		return false;
	}

	public ArrayList<String> getCost() {
		return cost;
	}

	public void setCost(ArrayList<String> cost) {
		this.cost = cost;
	}

	public ArrayList<String> getStorage() {
		return storage;
	}

	public void setStorage(ArrayList<String> storage) {
		this.storage = storage;
	}

	public int getMinBid() {
		return minBid;
	}

	public void setMinBid(int minBid) {
		this.minBid = minBid;
	}

	public int getNumCitiesPowered() {
		return numCitiesPowered;
	}

	public void setNumCitiesPowered(int numCitiesPowered) {
		this.numCitiesPowered = numCitiesPowered;
	}

	public int compareTo(Object arg0) {
		PowerPlant toComp = (PowerPlant) arg0;
		return getMinBid()- toComp.getMinBid();
	}

	public String toString() {
		return "Number: " + minBid + ". Cost" + cost.toString() + " powers " + numCitiesPowered + " cities.";
	}
	public boolean canBurn() {
		return storage.size()>=cost.size();
	}
	public boolean canBurnH(ArrayList<String>toBurn) {
		ArrayList<String>costCopy=new ArrayList<String>();
		costCopy.addAll(cost);
		for (String str:toBurn) {
			for (int k=0;k<costCopy.size();k++) {
				if (costCopy.get(k).contains(str)) {
					costCopy.remove(k);
					break;
				}
			}
			if (costCopy.size()==0) {
				return true;
			}
		}
		return (costCopy.size()==0);
	}
}