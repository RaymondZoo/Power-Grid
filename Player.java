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
	}

	public void addPowerPlant(PowerPlant p, int cost) {
		if (powerList.size() >= 3) {
			// call method to force the player to remove a powerplant
			addPowerPlant(p, cost);
		}
		if (powerList.size() < 3 && money > cost) {
			powerList.add(p);
			return;
		}
	}
	public int getHighestPowerPlant()
	{
		int max = Integer.MIN_VALUE;
		for(PowerPlant plant : powerList)
		{
			if(plant.getMinBid()>max)
			{
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
	public void subtractMoney(int amount)
	{
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
}
