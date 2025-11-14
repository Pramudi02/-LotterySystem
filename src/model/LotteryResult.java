package model;

import java.util.Map;

public class LotteryResult {
    private int drawId;
    private int winningNumber;
    private long drawTime;
    private Map<String, Double> prizes; // username -> prize amount

    public LotteryResult(int drawId, int winningNumber) {
        this.drawId = drawId;
        this.winningNumber = winningNumber;
        this.drawTime = System.currentTimeMillis();
    }

    public int getDrawId() { return drawId; }
    public int getWinningNumber() { return winningNumber; }
    public long getDrawTime() { return drawTime; }
    public Map<String, Double> getPrizes() { return prizes; }
    public void setPrizes(Map<String, Double> prizes) { this.prizes = prizes; }
}
