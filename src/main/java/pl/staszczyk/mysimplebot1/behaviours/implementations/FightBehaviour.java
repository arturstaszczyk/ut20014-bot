package pl.staszczyk.mysimplebot1.behaviours.implementations;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPref;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StopShooting;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import java.util.Random;
import pl.staszczyk.mysimplebot1.LogHelpers;
import pl.staszczyk.mysimplebot1.WeaponPrefsChooser;
import pl.staszczyk.mysimplebot1.behaviours.Behaviour;

/**
 *
 * @author Artur
 */
public class FightBehaviour extends Behaviour
{

    Player mEnemy = null;
    Random mRand = new Random();
    RandomBotCommand mRandomCommand = new RandomBotCommand(1000);
    private static double DISTANCE = 150;

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
        findNewTarget();
        WeaponPrefsChooser.configureDefaultPrefs(mBot.getWeaponPrefs());
    }

    private void findNewTarget()
    {
        mEnemy = mBot.getPlayers().getNearestVisibleEnemy();
        if (mEnemy == null)
        {
            mEnemy = mBot.getPlayers().getRandomVisibleEnemy();
        }
    }

    @Override
    public void execute(double dt)
    {
        boolean endBehaviour = false;

        if (mEnemy == null)
        {
            endBehaviour = true;
        }
        else if (mEnemy != null)
        {
            double distanceToTarget = mBot.getBot().getLocation().getDistance(mEnemy.getLocation());
            
            if (!mEnemy.isVisible())
            {
                stopShooting();
                findNewTarget();
            }
            else //visible
            {
                shoot();
                followIfFurtherThan(distanceToTarget, DISTANCE);
                if (mRandomCommand.canProduceCommand())
                {
                    mBot.getAct().act(mRandomCommand.produceCommand());
                }
            }
        }

        if (endBehaviour)
        {
            endBehaviour();
        }

        LogHelpers.setBotDebugName(this, mBot);
    }

    private void followIfFurtherThan(double distanceToTarget, double distanceTreshold)
    {
        if (distanceToTarget > distanceTreshold)
        {
            mBot.getNavigation().navigate(mEnemy);
        }
        else
        {
            mBot.getNavigation().stopNavigation();
        }
    }

    private void shoot()
    {
        WeaponPref preferredWeapon = mBot.getWeaponPrefs().getWeaponPreference();
        boolean shotFired = mBot.getShoot().shoot(preferredWeapon, mEnemy.getId());
        LogHelpers.logShoot(mBot.getLog(), preferredWeapon, shotFired, mEnemy);
    }

    private void stopShooting()
    {
        AgentInfo info = mBot.getInfo();
        if (info.isPrimaryShooting() || info.isSecondaryShooting())
        {
            mBot.getAct().act(new StopShooting());
        }
    }

    @Override
    public void onEnd()
    {
        mBot.getAct().act(new StopShooting());
        mBot.getNavigation().stopNavigation();
    }
}
