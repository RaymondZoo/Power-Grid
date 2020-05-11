import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class City {
	private ArrayList<Player> playersAtCity;
	private String zoneColor;
	private HashMap<City, Integer> edges;
	private String name;

	public City(String name, String zoneColor) {
		this.name = name;
		this.zoneColor = zoneColor;
		playersAtCity = new ArrayList<Player>();
		edges = new HashMap<City, Integer>();
	}

	public City() {
		playersAtCity = new ArrayList<Player>();
		zoneColor = "";
		edges = new HashMap<City, Integer>();
		name = "";
	}

	public int leastCost(Player p) {
		HashMap<City, Integer> dist = new HashMap<City, Integer>();
		HashSet<City> visited = new HashSet<City>();
		dist.put(this, 0);
		City current = this;
		while (!current.hasPlayer(p)) {
			for (City c : current.getEdges().keySet()) {
				if (!visited.contains(c)) {
					int tentDist = dist.get(current) + current.getEdges().get(c);
					if (!dist.keySet().contains(c)) {
						dist.put(c, tentDist);
					} else {
						if (dist.get(c) > tentDist)
							dist.put(c, tentDist);
					}
				}
			}
			visited.add(current);
			current = getLowest(dist, visited);
			//System.out.println(current.getName());
			//System.out.println(dist.get(current));
		}
		return dist.get(current);
	}

	public City getLowest(HashMap<City, Integer> dist, HashSet<City> visited) {
		ArrayList<City>list=new ArrayList<City>();
		list.addAll(dist.keySet());
		list.removeAll(visited);
		City lowest=null;
		for (City c : list) {
			if (lowest == null) {
				if (!visited.contains(c)) {
					lowest = c;
				}
			} else if (!visited.contains(c) && dist.get(c) < dist.get(lowest)) {
				lowest = c;
			}
		}
		return lowest;
	}

	public ArrayList<Player> getPlayersAtCity() {
		return playersAtCity;
	}

	public void setPlayersAtCity(ArrayList<Player> playersAtCity) {
		this.playersAtCity = playersAtCity;
	}

	public String getZoneColor() {
		return zoneColor;
	}

	public void setZoneColor(String zoneColor) {
		this.zoneColor = zoneColor;
	}

	public HashMap<City, Integer> getEdges() {
		return edges;
	}

	public void setEdges(HashMap<City, Integer> edges) {
		this.edges = edges;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean hasPlayer(Player p) {
		return getPlayersAtCity().contains(p);
	}

	public void addPlayer(Player p) {
		playersAtCity.add(p);
	}

	public int getCost() {
		return 10 + (5 * playersAtCity.size());
	}
	
	public String toString() {
		return name;
	}
}