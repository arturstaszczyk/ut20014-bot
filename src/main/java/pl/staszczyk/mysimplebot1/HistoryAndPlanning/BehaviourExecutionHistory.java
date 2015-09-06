package pl.staszczyk.mysimplebot1.HistoryAndPlanning;

import SpaceAndTimeHelpers.TimeOfItem;
import SpaceAndTimeHelpers.TimeOfItemComparator;
import java.util.PriorityQueue;
import pl.staszczyk.mysimplebot1.Timer;

/**
 *
 * @author Artur
 */
public class BehaviourExecutionHistory
{
    private static long FORGET_TIME_TRESHOLD = 10000;
    
    private PriorityQueue<TimeOfItem<Class>> mBehaviourHistory;
    
    public BehaviourExecutionHistory()
    {
        mBehaviourHistory = new PriorityQueue<TimeOfItem<Class>>(16, new TimeOfItemComparator());
    }
    
    public void clearHistory()
    {
        mBehaviourHistory.clear();
    }
    
    public void storeInHistory(Class behaviourClass, long timestampEnded)
    {
        TimeOfItem item = new TimeOfItem(timestampEnded, behaviourClass);
        mBehaviourHistory.add(item);
    }
    
    public boolean isBehaviourInHistory(Class behaviourClass)
    {
        boolean hasBehaviour = false;
        
        for(TimeOfItem historyItem : mBehaviourHistory)
        {
            if(historyItem.itemOccurance == behaviourClass)
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
