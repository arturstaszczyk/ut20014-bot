package pl.staszczyk.mysimplebot1.behaviours.implementations;

import cz.cuni.amis.pogamut.base.communication.messages.CommandMessage;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPref;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StopShooting;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import java.util.logging.Level;
import pl.staszczyk.mysimplebot1.WeaponPrefsChooser;
import pl.staszczyk.mysimplebot1.behaviours.Behaviour;

/**
 *
 * @author Artur
 */
public class RunToBehaviour extends Behaviour
{

    protected NavPoint mTarget = null;
    protected RandomBotCommand mRandomCommand;
    private static long DELAY_BETWEEN_RANDOM_COMMANDS = 1500;

    public RunToBehaviour(UT2004BotModuleController bot,
                          Behaviour.BehaviourCategory category,
                          boolean unbreakable)
    {
        super(bot, category, unbreakable);
        mRandomCommand = new RandomBotCommand(DELAY_BETWEEN_RANDOM_COMMANDS);
    }

    public RunToBehaviour setTarget(NavPoint target)
    {
        mTarget = target;
        return this;
    }

    @Override
    public String toString()
    {
        return "++RUNTO++";
    }

    @Override
    public void onBegin()
    {
        preparePreferredWeapons();
    }

    private void preparePreferredWeapons()
    {
        WeaponPrefsChooser.configureDefaultPrefs(mBot.getWeaponPrefs());
    }

    @Override
    public void execute(double dt)
    {
        IUT2004Navigation nav = mBot.getNavigation();
        nav.navigate(mTarget);

        handleRandomBehaviour();
        handleVisibleEnemies(nav);
        handleEndBehaviourCondition();
        setBotDebugName();
    }

    private void handleVisibleEnemies(IUT2004Navigation nav)
    {
        Player enemy = mBot.getPlayers().getNearestVisibleEnemy();
        if (enemy != null)
        {
            nav.setFocus(enemy);

            WeaponPref preferredWeapon = mBot.getWeaponPrefs().getWeaponPreference();
            boolean shotFired = mBot.getShoot().shoot(preferredWeapon, enemy.getId());
            logShoot(shotFired, preferredWeapon, enemy);
        }
        else
        {
            nav.setFocus(null);
            mBot.getAct().act(new StopShooting());
        }
    }

    private void logShoot(boolean shotFired, WeaponPref preferredWeapon, Player enemy)
    {
        if (shotFired)
        {
            String weapoMode = preferredWeapon.isPrimary() ? "PRIMARY" : "SECONDARY";
            mBot.getLog().log(Level.INFO, "Shooting with {0} at: {1}",
                              new Object[]
                    {
                        weapoMode, enemy.getName().toString()
                    });
        }
        else
        {
            mBot.getLog().log(Level.WARNING, "Bot cannot shoot");
        }
    }

    private void handleRandomBehaviour()
    {
        if (mRandomCommand.canProduceCommand())
        {
            CommandMessage command = mRandomCommand.produceCommand();
            mBot.getAct().act(command);
            mBot.getLog().log(Level.INFO, "Executing random command: {0}", command.toString());
        }
    }

    private void handleEndBehaviourCondition()
    {
        Location botPosition = mBot.getBot().getLocation();

        if (botPosition.getDistance(mTarget.getLocation()) < 30)
        {
            endBehaviour();
        }
    }

    private void setBotDebugName()
    {
        String nodeName = mTarget.getId().toString();
        if (mTarget.isInvSpot())
        {
            nodeName = mTarget.getItemClass().getName();
        }

        WeaponPref preferredWeapon = mBot.getWeaponPrefs().getWeaponPreference();
        String activeWeapon = preferredWeapon.getWeapon().getName();
        int primaryAmmoLeft = mBot.getWeaponry().getCurrentPrimaryAmmo();
        int secondaryAmmoLeft = mBot.getWeaponry().getCurrentAlternateAmmo();

        mBot.getConfig().setName("[" + toString() + "]-["
                + getCategory().toString() + "]-["
                + nodeName + "]-["
                + activeWeapon + ":" + primaryAmmoLeft + "+" + secondaryAmmoLeft + "]");
    }

    @Override
    public void onEnd()
    {
        mBot.getAct().act(new StopShooting());
        mBot.getNavigation().stopNavigation();
    }
}
