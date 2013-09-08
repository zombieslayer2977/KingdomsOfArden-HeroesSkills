package net.kingdomsofarden.andrew2060.heroes.skills;

import java.util.ArrayDeque;
import java.util.Deque;

public class KillData {
    
    private Deque<Long> killTimes;
    
    public KillData() {
        this.killTimes = new ArrayDeque<Long>(5);
    }
    
    public void add() {
        while(killTimes.peek() != null && killTimes.peek() < System.currentTimeMillis() - 3600000) {
            killTimes.removeFirst();
        }
        killTimes.addLast(System.currentTimeMillis());
    }
    
    public boolean checkSize() {
        while(killTimes.peek() != null && killTimes.peek() < System.currentTimeMillis() - 3600000) {
            killTimes.removeFirst();
        }
        return killTimes.size() > 5;
    }
    
}
