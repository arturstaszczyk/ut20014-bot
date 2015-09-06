package pl.staszczyk.mysimplebot1;

import cz.cuni.amis.introspection.java.JProp;
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
import pl.staszczyk.mysimplebot1.HistoryAndPlanning.NavPointsVisitationHistory;
import pl.staszczyk.mysimplebot1.behaviours.BehaviourExecutor;
import pl.staszczyk.mysimplebot1.behaviours.BehaviourPlanner;
import pl.staszczyk.mysimplebot1.behaviours.IBehaviourChangeListener;
import pl.staszczyk.mysimplebot1.behaviours.implementations.StayBehaviour;

@AgentScoped
public class EmptyBot extends UT2004BotModuleController
{

    private Timer mTimer = new Timer();
    private BehaviourPlanner mPlanner;
    private BehaviourExecutor mBehaviourExecutor;
    NavPointsVisitationHistory mNavPointsHistory;
    private SpaceAwareness mSpaceAwareness;

    @Override
    public Initialize getInitializeCommand()
    {
        return new Initialize().setName("Stasiu").setSkin("Dominator").setTeam(0).setDesiredSkill(5);
    }

    @Override
    public void botInitialized(GameInfo info, ConfigChange currentConfig, InitedMessage init)
    {
    }

    @Override
    public void botFirstSpawn(GameInfo gameInfo, ConfigChange config, InitedMessage init, Self self)
    {
        body.getCommunication().sendGlobalTextMessage("Hello world! I am alive!");
        log.setLevel(Level.INFO);
    }

    @Override
    public void beforeFirstLogic()
    {
        Behaviour initialBehaviour = new StayBehaviour(this);
        mBehaviourExecutor = new BehaviourExecutor(log, initialBehaviour);
        mBehaviourExecutor.addBehaviourChangeListener(new IBehaviourChangeListener()
        {

            @Override
            public void onBehaviourChange(Behaviour previois, Behaviour next)
            {
                log.log(Level.INFO, "Bechaviour changed to: {0}", next.toString());
            }

            @Override
            public void onNoMoreBehaviours()
            {
                mBehaviourExecutor.clearMindSet();
                mBehaviourExecutor.replaceBehaviour(new StayBehaviour(EmptyBot.this));
            }
        });

        mPlanner = new BehaviourPlanner(this);

        mSpaceAwareness = new SpaceAwareness(bot);
        mSpaceAwareness.prepareRays(raycasting);
        
        mNavPointsHistory = new NavPointsVisitationHistory();
    }

    @Override
    public void botKilled(BotKilled event)
    {
    }

    @EventListener(eventClass = BotDamaged.class)
    public void botDamaged(BotDamaged event)
    {
    }

    @ObjectClassEventListener(eventClass = WorldObjectAppearedEvent.class, objectClass = Player.class)
    protected void noticedAnyPlayer(WorldObjectAppearedEvent<Player> event)
    {
//        Player tmpPlayer = event.getObject();
//        if(tmpPlayer.isSpectator())
//            return;
//        
//        log.log(Level.INFO, "Found player: " + event.getObject().getName());
//        Player player = event.getObject();
//        mBehaviourExecutor.replaceBehaviour(new FightBehaviour(this));
    }

    @Override
    public void logic() throws PogamutException
    {
        mTimer.update(log);

        mNavPointsHistory.updateNavigationHistory(this);

        mPlanner.plan(mBehaviourExecutor, mNavPointsHistory, mSpaceAwareness);
        mBehaviourExecutor.execute(mTimer.getDT());
    }

    public static void main(String args[]) throws PogamutException
    {
        new UT2004BotRunner(EmptyBot.class, "EmptyBot").setHost("localhost").setMain(true).startAgents(5);
    }
}
