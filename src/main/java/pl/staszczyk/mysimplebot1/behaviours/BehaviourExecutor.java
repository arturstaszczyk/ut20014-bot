package pl.staszczyk.mysimplebot1.behaviours;

import pl.staszczyk.mysimplebot1.HistoryAndPlanning.BehaviourExecutionHistory;
import pl.staszczyk.mysimplebot1.HistoryAndPlanning.BehaviourExecutionQueue;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import java.util.*;
import java.util.logging.Level;
import pl.staszczyk.mysimplebot1.Timer;

/**
 *
 * @author Artur
 */
public class BehaviourExecutor implements IBehaviourFinishedListener
{

    private List<IBehaviourChangeListener> mListeners;
    private Behaviour mActiveBehaviour;
    private BehaviourExecutionHistory mHistory;
    private BehaviourExecutionQueue mQueue;
    private Timer mTimer;
    private LogCategory mLog;

    public Behaviour getActiveBehaviour()
    {
        return mActiveBehaviour;
    }
    
    public BehaviourExecutionHistory getExecutionHistory()
    {
        return mHistory;
    }

    public BehaviourExecutor(LogCategory log, Behaviour initialBehaviour)
    {
        mHistory = new BehaviourExecutionHistory();
        mQueue = new BehaviourExecutionQueue();

        mListeners = new LinkedList<IBehaviourChangeListener>();
        mLog = log;

        mTimer = new Timer();

        startBehaviour(initialBehaviour);
    }

    public void addBehaviourChangeListener(IBehaviourChangeListener listener)
    {
        if (!mListeners.contains(listener))
        {
            mListeners.add(listener);
        }
    }

    public void removeBehaviourChangeListener(IBehaviourChangeListener listener)
    {
        if (mListeners.contains(listener))
        {
            mListeners.remove(listener);
        }
    }

    public void clearMindSet()
    {
        mHistory.clearHistory();
        mQueue.clearQueue();
    }

    public boolean isBehaviourExecutedRecently(Class behaviourClass)
    {
        boolean hasBehaviour = mHistory.isBehaviourInHistory(behaviourClass);
        if (mActiveBehaviour.getClass() == behaviourClass)
        {
            hasBehaviour = true;
        }

        return hasBehaviour;
    }

    public void execute(double dt)
    {
        mHistory.forgetOldBehaviours(mTimer);
        mActiveBehaviour.execute(dt);
    }

    public Class currentBehaviourClass()
    {
        Class currentBehaviourClass = null;
        if (mActiveBehaviour != null)
        {
            currentBehaviourClass = mActiveBehaviour.getClass();
        }

        return currentBehaviourClass;
    }

    public void queueBehaviour(Behaviour behaviour)
    {
        mLog.log(Level.INFO, "Queued behaviour {0}", behaviour);
        mQueue.addToQueue(behaviour);
    }
    
    public boolean hasFightBehaviourPlanned()
    {
        return mQueue.hasBehaviourCategory(Behaviour.BehaviourCategory.FLEEING) ||
                mQueue.hasBehaviourCategory(Behaviour.BehaviourCategory.ATTACKING) ||
                mActiveBehaviour.getCategory() == Behaviour.BehaviourCategory.FLEEING ||
                mActiveBehaviour.getCategory() == Behaviour.BehaviourCategory.ATTACKING;
    }
    
    public boolean hasFleeingBehaviourPlanned()
    {
        return mQueue.hasBehaviourCategory(Behaviour.BehaviourCategory.FLEEING) ||
                mActiveBehaviour.getCategory() == Behaviour.BehaviourCategory.FLEEING;
    }
    
    public boolean hasNeutralBehaviourPlanned()
    {
        return mQueue.hasBehaviourCategory(Behaviour.BehaviourCategory.NEUTRAL) ||
                mActiveBehaviour.getCategory() == Behaviour.BehaviourCategory.NEUTRAL;
    }

    public void replaceBehaviour(Behaviour behaviour)
    {
        mLog.log(Level.INFO, "Replaced behaviour with {0}", behaviour);
        
        stopBehaviour(mActiveBehaviour);
        notifyChangeListeners(mActiveBehaviour, behaviour);
        startBehaviour(behaviour);

        mQueue.clearQueue();
    }
    
    private void startBehaviour(Behaviour behaviour)
    {
        mActiveBehaviour = behaviour;
        behaviour.addOnFinishedListener(this);
        behaviour.onBegin();
    }

    private void stopBehaviour(Behaviour behaviour)
    {
        if (behaviour == null)
        {
            return;
        }

        behaviour.removeOnFinishedListener(this);
        behaviour.onEnd();

        mHistory.storeInHistory(behaviour.getClass(), mTimer.getTimestamp());
    }

    private void notifyChangeListeners(Behaviour active, Behaviour next)
    {
        for (IBehaviourChangeListener listener : mListeners)
        {
            listener.onBehaviourChange(active, next);
        }
    }

    private void notifyEndListeners()
    {
        for (IBehaviourChangeListener listener : mListeners)
        {
            listener.onNoMoreBehaviours();
        }
    }
    
    // IBehaviourFinishedListener
    @Override
    public void onBehaviourFinished(Behaviour behaviour)
    {
        mLog.log(Level.INFO, "FINISHED: {0}", behaviour.toString());

        Behaviour queuedBehaviour = mQueue.getQueuedBehaviour();
        if (queuedBehaviour == null)
        {
            stopBehaviour(behaviour);
            notifyEndListeners();
        } else
        {
            replaceBehaviour(queuedBehaviour);
        }
    }
}
