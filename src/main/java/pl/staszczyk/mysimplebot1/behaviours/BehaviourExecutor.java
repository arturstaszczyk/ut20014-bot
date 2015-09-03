package pl.staszczyk.mysimplebot1.behaviours;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Artur
 */
public class BehaviourExecutor implements IBehaviourFinishedListener {
    
    private List<IBehaviourChangeListener> mListeners = new LinkedList<IBehaviourChangeListener>();
    private Behaviour mActiveBehaviour = null;
    LogCategory mLog = null;
    
    public BehaviourExecutor(LogCategory log)
    {
        mLog = log;
    }
    
    public void execute(double dt)
    {
        mActiveBehaviour.execute(dt);
    }
    
    public void replaceBehaviour(Behaviour behaviour)
    {
        stopActivBehaviour();
        notifyListeners(mActiveBehaviour, behaviour);
        mActiveBehaviour = behaviour;
        startActiveBehaviour();
    }
    
    private void stopActivBehaviour() {
        if(mActiveBehaviour == null)
            return;
        
        mActiveBehaviour.removeOnFinishedListener(this);
        mActiveBehaviour.onEnd();
    }

    private void startActiveBehaviour() {
        mActiveBehaviour.addOnFinishedListener(this);
        mActiveBehaviour.onBegin();
    }
    
    private void notifyListeners(Behaviour active, Behaviour next) {
        for(IBehaviourChangeListener listener : mListeners)
            listener.onBehaviourChange(active, next);
    }
    
    public void addBehaviourChangeListener(IBehaviourChangeListener listener)
    {
        if(!mListeners.contains(listener))
            mListeners.add(listener);
    }
    
    public void removeBehaviourChangeListener(IBehaviourChangeListener listener)
    {
        if(mListeners.contains(listener))
            mListeners.remove(listener);
    }

    @Override
    public void onBehaviourFinished(Behaviour behaviour) {
        mLog.log(Level.INFO, "FINISHED: {0}", behaviour.toString());

        behaviour.onEnd();//moze byc replace
        
        for(IBehaviourChangeListener listener : mListeners)
            listener.onNoMoreBehaviours();
        
    }

}
