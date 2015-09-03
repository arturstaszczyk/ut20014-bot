package pl.staszczyk.mysimplebot1.behaviours;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Artur
 */
public abstract class Behaviour {
    
    protected List<IBehaviourFinishedListener> mListeners = new LinkedList<IBehaviourFinishedListener>();

    protected UT2004BotModuleController mBot;
    
    public Behaviour(UT2004BotModuleController bot) {
        mBot = bot;
    }
    
    @Override
    public abstract String toString();
    
    public abstract void onBegin();
    public abstract void execute(double dt);
    public abstract void onEnd();
    
    public void addOnFinishedListener(IBehaviourFinishedListener listener)
    {
        if(!mListeners.contains(listener))
            mListeners.add(listener);
    }
    
    public void removeOnFinishedListener(IBehaviourFinishedListener listener)
    {
        if(mListeners.contains(listener))
            mListeners.remove(listener);
    }
    
    protected void endBehaviour()
    {
        for(IBehaviourFinishedListener listener : mListeners)
                listener.onBehaviourFinished(this);
    }
}
