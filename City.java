import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class City {
	private ArrayList<Player> playersAtCity;
	private String zoneColor;
	private TreeMap<City, Integer> edges;
	private String name;
	
	public City(String name, String zoneColor) {
		this.name=name;
		this.zoneColor=zoneColor;
		playersAtCity=new ArrayList<Player>();
		edges=new TreeMap<City, Integer>();
	}
	public City() {
		playersAtCity=new ArrayList<Player>();
		zoneColor="";
		edges=new TreeMap<City, Integer>();
		name="";
	}
	public int leastCost(Player p) {
		HashMap<City, Integer>dist=new HashMap<City, Integer>();
		HashSet<City>visited=new HashSet<City>();
		visited.add(this);
		dist.put(this, 0);
		City current=this;
		while (!current.hasPlayer(p)) {
			for (City c:edges.keySet()) {
				if (!visited.contains(c)) {
					int tentDist=dist.get(current)+edges.get(c);
					if (!dist.keySet().contains(c)) {
						dist.put(c, tentDist);
					}
					else {
						if (dist.get(c)>tentDist)
							dist.replace(c,tentDist);
					}
				}
			}
			current=getLowest(dist, visited);
		}
		return dist.get(current);
	}
	public City getLowest(HashMap<City, Integer> dist, HashSet<City>visited) {
		City lowest=this;
		for (City c:dist.keySet()) {
			if (!visited.contains(c)&&dist.get(c)<dist.get(lowest)) {
				lowest=c;
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
	public TreeMap<City, Integer> getEdges() {
		return edges;
	}
	public void setEdges(TreeMap<City, Integer> edges) {
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
}
