import java.util.ArrayList;

public class PowerPlant {
	private ArrayList<String> cost;
	private ArrayList<String> storage;
	int minBid, numCitiesPowered;

	public PowerPlant() {
		cost = new ArrayList<String>();
		storage = new ArrayList<String>();
		minBid = 0;
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

	public void addResources(ArrayList<String> resources) {
		// if the plant is green
		if (cost.size() == 0) {
			return;
		}
		int index = 0;
		while (storage.size() <= cost.size()) {
			if (cost.get(0).contains(resources.get(index))) {
				storage.add(resources.get(index));
			}
			++index;
		}
	}

	public boolean isFull() {
		return storage.size() >= (cost.size() * 2);
	}

	public boolean burnResources(ArrayList<String> resources) {
		// green PowerPlant
		if (cost.size() == 0) {
			return true;
		}

		ArrayList<String> costCopy = new ArrayList<String>();
		for (String s : cost) {
			costCopy.add(s);
		}

		for (String s : costCopy) {
			if (!specialRemove(resources, s))
				return false;
		}
		for(String s: cost) {
			specialRemove(storage, s);
		}
		return true;
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

}
