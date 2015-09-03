package pl.staszczyk.mysimplebot1.behaviours.implementations;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import pl.staszczyk.mysimplebot1.behaviours.Behaviour;
import pl.staszczyk.mysimplebot1.behaviours.IBehaviourFinishedListener;

/**
 *
 * @author Artur
 */
public class RunToBehaviour extends Behaviour {

    protected NavPoint mTarget = null;
    
    public RunToBehaviour(UT2004BotModuleController bot) {
        super(bot);
    }
    
    public RunToBehaviour setTarget(NavPoint target){
        mTarget = target;
        return this;
    }
    
    @Override
    public void onBegin() {
        mBot.getNavigation().navigate(mTarget);
    }

    @Override
    public void execute(double dt) {
        IUT2004Navigation nav = mBot.getNavigation();
        
        nav.navigate(mTarget);
        
        Location botPosition = mBot.getBot().getLocation();
        
        if(Location.getDistanceSquare(mTarget.getLocation(), botPosition) < (200  * 200))
            for(IBehaviourFinishedListener listener : mListeners)
                listener.onBehaviourFinished(this);
    }

    @Override
    public void onEnd() {
        mBot.getNavigation().stopNavigation();
    }

    @Override
    public String toString() {
        return "RunToBehaviour";
    }
    
}
