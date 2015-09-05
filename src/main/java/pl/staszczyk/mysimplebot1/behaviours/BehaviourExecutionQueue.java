package pl.staszczyk.mysimplebot1.behaviours;

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Artur
 */
public class BehaviourExecutionQueue
{
    private Queue<Behaviour> mQueuedBehaviour;

    public BehaviourExecutionQueue()
    {
        mQueuedBehaviour = new LinkedList<Behaviour>();
    }

    public void clearQueue()
    {
        mQueuedBehaviour.clear();
    }
    
    public void addToQueue(Behaviour behaviour)
    {
        mQueuedBehaviour.add(behaviour);
    }
    
    public Behaviour getQueuedBehaviour()
    {
        return mQueuedBehaviour.poll();
    }

    public boolean hasBehaviourCategory(Behaviour.BehaviourCategory category)
    {
        boolean hasCategory = false;

        for (Behaviour behaviour : mQueuedBehaviour)
        {
            if (behaviour.getCategory() == category)
            {
                hasCategory = true;
            }
        }

        return hasCategory;
    }
}
