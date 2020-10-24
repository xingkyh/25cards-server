package model;

public class Poker {
	private int points;
	private String kind;
	public Poker(int points, String kind) {
		super();
		this.points = points;
		this.kind = kind;
	}
	
	public static String[] kinds = {"方块", "梅花", "红桃", "黑桃"};
	@Override
	public String toString() {
		return kind+"aa"+String.valueOf(points);
	}
}
