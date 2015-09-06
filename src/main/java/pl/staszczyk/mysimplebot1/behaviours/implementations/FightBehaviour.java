package pl.staszczyk.mysimplebot1.behaviours.implementations;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPrefs;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StopShooting;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import java.util.Random;
import pl.staszczyk.mysimplebot1.Timer;
import pl.staszczyk.mysimplebot1.behaviours.Behaviour;

/**
 *
 * @author Artur
 */
public class FightBehaviour extends Behaviour
{
    Player mTarget = null;
    WeaponPrefs mWeaponPrefs = null;
    long mLastVisibleTimestamp;
    
    Random mRand = new Random();
    RandomBotCommand mRandomCommand = new RandomBotCommand(1500);
    boolean mShooting;
    private Timer mTimer = new Timer();
    
    private static double DISTANCE = 200;
    private static long LAST_SEEN_TREASHOLD = 10000;
    
    public FightBehaviour(UT2004BotModuleController bot)
    {
        super(bot, Behaviour.BehaviourCategory.ATTACKING);
    }
    
    @Override
    public String toString()
    {
        return "++FIGHT++";
    }
    
    @Override
    public void onBegin()
    {
        mBot.getConfig().setName("Stasiu [" + toString() + "]");
        
        mTarget = mBot.getPlayers().getNearestVisibleEnemy();
        if(mTarget == null)
            mTarget = mBot.getPlayers().getRandomVisibleEnemy();
        
        mShooting = false;
    }

    @Override
    public void execute(double dt)
    {
        boolean endBehaviour = false;
        double distanceToTarget = mBot.getBot().getLocation().getDistance(mTarget.getLocation());
        
        if(mTarget != null)
        {        
            if (!mTarget.isVisible()) 
            {
                stopShooting();
                
                mBot.getNavigation().navigate(mTarget);
                if(mTimer.getTimestamp() - mLastVisibleTimestamp > LAST_SEEN_TREASHOLD)
                    endBehaviour = true;
            }
            else //visible
            {
                mLastVisibleTimestamp = mTimer.getTimestamp();
                
                shoot();
                followIfFurther(distanceToTarget, DISTANCE);
                if(mRandomCommand.canProduceCommand())
                    mBot.getAct().act(mRandomCommand.produceCommand());
            }
        }
        else
        {
            endBehaviour = true;
        }
        
        if(endBehaviour)
            endBehaviour();
    }
    
    private void followIfFurther(double distanceToTarget, double distanceTreshold)
    {
        if(distanceToTarget > distanceTreshold)
            mBot.getNavigation().navigate(mTarget);
        else
            mBot.getNavigation().stopNavigation();
    }
    
    private void shoot()
    {
        mWeaponPrefs = mBot.getWeaponPrefs();
        mWeaponPrefs.addGeneralPref(UT2004ItemType.LIGHTNING_GUN, false);                
        mWeaponPrefs.addGeneralPref(UT2004ItemType.SHOCK_RIFLE, false);
        mWeaponPrefs.addGeneralPref(UT2004ItemType.MINIGUN, false);
        mWeaponPrefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, false);        
        //mWeaponPrefs.addGeneralPref(UT2004ItemType.ROCKET_LAUNCHER, false);
        mWeaponPrefs.addGeneralPref(UT2004ItemType.LINK_GUN, false);
        mWeaponPrefs.addGeneralPref(UT2004ItemType.ASSAULT_RIFLE, false);        
        mWeaponPrefs.addGeneralPref(UT2004ItemType.BIO_RIFLE, false);
        
        if (mBot.getShoot().shoot(mWeaponPrefs, mTarget) != null && !mShooting)
        {
            mShooting = true;
        }
    }
    
    private void stopShooting()
    {
        AgentInfo info = mBot.getInfo();
        if (mShooting || info.isSecondaryShooting())
        {
            mBot.getAct().act(new StopShooting());
            mShooting = false;
        }
    }

    @Override
    public void onEnd()
    {
        mBot.getAct().act(new StopShooting());
        mBot.getNavigation().stopNavigation();
    }
    
}
