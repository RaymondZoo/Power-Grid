import java.util.ArrayList;

public class Player {
	private ArrayList<PowerPlant> powerList;
	private int money;
	private String color;

	public Player() {
		powerList = new ArrayList<PowerPlant>();
		money = 50;
		color = "null";
	}

	public Player(String color) {
		powerList = new ArrayList<PowerPlant>();
		money = 50;
		this.color = color;
		
		//temp
		/*ArrayList<String>cost=new ArrayList<String>();
		cost.add("coal");
		cost.add("coal");
		PowerPlant p1=new PowerPlant(4, cost, 1);
		ArrayList<String>resources=new ArrayList<String>();
		resources.add("coal");
		resources.add("coal");
		p1.addResources(resources);
		addPowerPlant(p1);
		
		ArrayList<String>cost1=new ArrayList<String>();
		cost1.add("coal||oil");
		cost1.add("coal||oil");
		PowerPlant p2=new PowerPlant(5, cost1, 1);
		ArrayList<String>resources1=new ArrayList<String>();
		resources1.add("coal");
		resources1.add("coal");
		p2.addResources(resources1);
		addPowerPlant(p2);
		
		addPowerPlant(new PowerPlant(13, new ArrayList<String>(), 1));*/
	}

	public void addPowerPlant(PowerPlant p) {
		powerList.add(p);
		return;
	}

	public int getHighestPowerPlant() {
		int max = Integer.MIN_VALUE;
		for (PowerPlant plant : powerList) {
			if (plant.getMinBid() > max) {
				max = plant.getMinBid();
			}
		}
		return max;
	}

	public ArrayList<PowerPlant> getPowerList() {
		return powerList;
	}

	public void setPowerList(ArrayList<PowerPlant> powerList) {
		this.powerList = powerList;
	}

	public int getMoney() {
		return money;
	}

	public void subtractMoney(int amount) {
		money = money - amount;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void addMoney(int in) {
		money += in;
	}

	public int getMaxCoal() {
		int maxCoal = 0;
		for (PowerPlant p : powerList) {
			if (p.getCost().contains("coal") || p.getCost().get(0).contains("coal")) {
				maxCoal += p.getCost().size();
			}
		}
		return maxCoal * 2;
	}

	public int getMaxOil() {
		int maxOil = 0;
		for (PowerPlant p : powerList) {
			if (p.getCost().contains("oil") || p.getCost().get(0).contains("oil")) {
				maxOil += p.getCost().size();
			}
		}
		return maxOil * 2;
	}

	public int getMaxNuclear() {
		int maxNuclear = 0;
		for (PowerPlant p : powerList) {
			if (p.getCost().contains("nuclear") || p.getCost().get(0).contains("nuclear")) {
				maxNuclear += p.getCost().size();
			}
		}
		return maxNuclear * 2;
	}

	public int getMaxTrash() {
		int maxTrash = 0;
		for (PowerPlant p : powerList) {
			if (p.getCost().contains("trash") || p.getCost().get(0).contains("trash")) {
				maxTrash += p.getCost().size();
			}
		}
		return maxTrash * 2;
	}

	public String toString() {
		return this.color;
	}

}