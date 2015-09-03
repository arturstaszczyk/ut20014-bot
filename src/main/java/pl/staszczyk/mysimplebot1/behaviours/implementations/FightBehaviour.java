package pl.staszczyk.mysimplebot1.behaviours.implementations;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPrefs;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StopShooting;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import java.util.Random;
import pl.staszczyk.mysimplebot1.behaviours.Behaviour;

/**
 *
 * @author Artur
 */
public class FightBehaviour extends Behaviour
{
    Player mTarget = null;
    WeaponPrefs mWeaponPrefs = null;
    
    Random mRand = new Random();
    boolean mShooting;
    
    private static double DISTANCE = 700;
    
    public FightBehaviour(UT2004BotModuleController bot)
    {
        super(bot);
    }
    
    @Override
    public String toString()
    {
        return "FightBehaviour";
    }

    @Override
    public void onBegin()
    {
        mTarget = mBot.getPlayers().getNearestEnemy(2000);
        
        mWeaponPrefs = mBot.getWeaponPrefs();
        mWeaponPrefs.addGeneralPref(UT2004ItemType.LIGHTNING_GUN, true);                
        mWeaponPrefs.addGeneralPref(UT2004ItemType.SHOCK_RIFLE, true);
        mWeaponPrefs.addGeneralPref(UT2004ItemType.MINIGUN, false);
        mWeaponPrefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, true);        
        mWeaponPrefs.addGeneralPref(UT2004ItemType.ROCKET_LAUNCHER, true);
        mWeaponPrefs.addGeneralPref(UT2004ItemType.LINK_GUN, true);
        mWeaponPrefs.addGeneralPref(UT2004ItemType.ASSAULT_RIFLE, true);        
        mWeaponPrefs.addGeneralPref(UT2004ItemType.BIO_RIFLE, true);
        
        mShooting = false;
    }

    @Override
    public void execute(double dt)
    {
        if(mTarget != null)
        {        
            AgentInfo info = mBot.getInfo();
            if (!mTarget.isVisible()) 
            {
                if (info.isShooting() || info.isSecondaryShooting()) {
                    mBot.getAct().act(new StopShooting());
                    mShooting = false;
                }
            }
            else 
            {
                double distance = info.getLocation().getDistance(mTarget.getLocation());
                if (mBot.getShoot().shoot(mWeaponPrefs, mTarget) != null)
                {
                    mShooting = true;
                }
            }

            int decentDistance = Math.round(mRand.nextFloat() * 800) + 200;
            if (!mTarget.isVisible() || !mShooting || decentDistance < DISTANCE) 
            {  
                mBot.getNavigation().navigate(mTarget);
                mShooting = false;
            }         
            
        }
        else
        {
            endBehaviour();
        }
        

//        // 3) if enemy is far or not visible - run to him
//        int decentDistance = Math.round(random.nextFloat() * 800) + 200;
//        if (!enemy.isVisible() || !shooting || decentDistance < distance) {
//            if (!runningToPlayer) {
//                navigation.navigate(enemy);
//                runningToPlayer = true;
//            }
//        } else {
//            runningToPlayer = false;
//            navigation.stopNavigation();
//        }
    }

    @Override
    public void onEnd()
    {
    }
    
}
