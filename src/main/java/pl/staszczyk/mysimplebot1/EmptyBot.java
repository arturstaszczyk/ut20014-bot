package pl.staszczyk.mysimplebot1;

import pl.staszczyk.mysimplebot1.behaviours.Behaviour;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectClassEventListener;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectAppearedEvent;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.*;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.*;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import pl.staszczyk.mysimplebot1.behaviours.BehaviourExecutor;
import pl.staszczyk.mysimplebot1.behaviours.IBehaviourChangeListener;
import pl.staszczyk.mysimplebot1.behaviours.implementations.DodgeBehaviour;
import pl.staszczyk.mysimplebot1.behaviours.implementations.FightBehaviour;
import pl.staszczyk.mysimplebot1.behaviours.implementations.RunToBehaviour;
import pl.staszczyk.mysimplebot1.behaviours.implementations.StayBehaviour;

@AgentScoped
public class EmptyBot extends UT2004BotModuleController implements IBehaviourChangeListener {
    
    private Timer mTimer = new Timer();
    private BehaviourExecutor mBehaviourExecutor = null;
    private SpaceAwareness mSpaceAwareness = null;

    @Override
    public Initialize getInitializeCommand() {    	
    	return new Initialize().setName("[Stasiu]SuperOsomKillerBot").setSkin("Dominator");        
    }
    
    @Override
    public void botInitialized(GameInfo info, ConfigChange currentConfig, InitedMessage init) {

    }
    
    @Override
    public void botFirstSpawn(GameInfo gameInfo, ConfigChange config, InitedMessage init, Self self) {
        body.getCommunication().sendGlobalTextMessage("Hello world! I am alive!");
        act.act(new SendMessage().setGlobal(true).setText("And I can speak! Hurray!"));
        log.setLevel(Level.INFO);
    }
    
    @Override
    public void beforeFirstLogic() {
        mBehaviourExecutor = new BehaviourExecutor(log);
        mBehaviourExecutor.addBehaviourChangeListener(this);
        
        Behaviour initialBehaviour = null;
        initialBehaviour = new RunToBehaviour(this).setTarget(navPoints.getRandomNavPoint());
        //initialBehaviour = new StayBehaviour(this);
        mBehaviourExecutor.replaceBehaviour(initialBehaviour);
        
        mSpaceAwareness = new SpaceAwareness(bot);
        
        act.act(new RemoveRay("All"));
        mSpaceAwareness.prepareRays(raycasting);
        act.act(new Configuration().setDrawTraceLines(true).setAutoTrace(true));
    }
    
    @Override
    public void botKilled(BotKilled event) {
    }
    
    @EventListener(eventClass=BotDamaged.class)
    public void botDamaged(BotDamaged event) {  
        mBehaviourExecutor.replaceBehaviour(new DodgeBehaviour(this, mSpaceAwareness));
    }
    
    @ObjectClassEventListener(eventClass = WorldObjectAppearedEvent.class, objectClass = Player.class)
    protected void noticedAnyPlayer(WorldObjectAppearedEvent<Player> event)
    {
        Player tmpPlayer = event.getObject();
        if(tmpPlayer.isSpectator())
            return;
        
        log.log(Level.INFO, "Found player: " + event.getObject().getName());
        Player player = event.getObject();
        mBehaviourExecutor.replaceBehaviour(new FightBehaviour(this));
    }

    @Override
    public void logic() throws PogamutException {
        mTimer.update(log);
        mBehaviourExecutor.execute(mTimer.dt());
    }
    
    public static void main(String args[]) throws PogamutException {
        new UT2004BotRunner(EmptyBot.class, "EmptyBot").setMain(true).startAgent();
    }

    @Override
    public void onBehaviourChange(Behaviour previois, Behaviour next) {
        log.log(Level.INFO, "Bechaviour changed to: {0}", next.toString());
    }

    @Override
    public void onNoMoreBehaviours() {
        Behaviour newBehaviour = null;
        
        newBehaviour = new RunToBehaviour(this).setTarget(navPoints.getRandomNavPoint());
        //newBehaviour = new StayBehaviour(this);
        
        mBehaviourExecutor.replaceBehaviour(newBehaviour);
    }

}
