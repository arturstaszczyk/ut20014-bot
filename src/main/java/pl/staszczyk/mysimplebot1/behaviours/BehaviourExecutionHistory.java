package pl.staszczyk.mysimplebot1.behaviours;

import java.util.Comparator;
import java.util.PriorityQueue;
import pl.staszczyk.mysimplebot1.Timer;

/**
 *
 * @author Artur
 */
public class BehaviourExecutionHistory
{
    public class HistoryItem 
    {
        public HistoryItem(long timestamp, Class behaviour)
        {
            timestampEnded = timestamp;
            behaviourClass = behaviour;
        }
        
        public long timestampEnded;
        public Class behaviourClass;
    };
    
    private class HistoryItemCompataror implements Comparator<HistoryItem>
    {
        @Override
        public int compare(HistoryItem o1, HistoryItem o2)
        {
            // smallest timestamp (oldest behaviour) is lowest value
            if(o1.timestampEnded < o2.timestampEnded)
                return -1;
            else
                return 1;
        }
    }
    
    private static long FORGET_TIME_TRESHOLD = 10000;
    
    private PriorityQueue<HistoryItem> mBehaviourHistory;
    
    public BehaviourExecutionHistory()
    {
        mBehaviourHistory = new PriorityQueue<HistoryItem>(16, new HistoryItemCompataror());
    }
    
    public void clearHistory()
    {
        mBehaviourHistory.clear();
    }
    
    public void storeInHistory(Class behaviourClass, long timestampEnded)
    {
        HistoryItem item = new HistoryItem(timestampEnded, behaviourClass);
        mBehaviourHistory.add(item);
    }
    
    public boolean isBehaviourInHistory(Class behaviourClass)
    {
        boolean hasBehaviour = false;
        
        for(HistoryItem historyItem : mBehaviourHistory)
        {
            if(historyItem.behaviourClass == behaviourClass)
                hasBehaviour = true;
        }
        
        return hasBehaviour;
    }
    
    public void forgetOldBehaviours(Timer timer)
    {
        if(mBehaviourHistory.isEmpty())
            return;
        
        long oldestBehaviourTimestamp = mBehaviourHistory.peek().timestampEnded;
        while(!mBehaviourHistory.isEmpty() && timer.getMillisecondsSince(oldestBehaviourTimestamp) > FORGET_TIME_TRESHOLD)
        {
            mBehaviourHistory.poll();
        }
    }
}
