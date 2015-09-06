package pl.staszczyk.mysimplebot1;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPref;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import java.util.logging.Level;
import pl.staszczyk.mysimplebot1.behaviours.Behaviour;

/**
 *
 * @author Artur
 */
public class LogHelpers
{

    public static void setBotDebugName(NavPoint targetNavPoint,
                                       Behaviour behaviour,
                                       UT2004BotModuleController botController)
    {
        String nodeName = targetNavPoint.getId().toString();
        if (targetNavPoint.isInvSpot())
        {
            nodeName = targetNavPoint.getItemClass().getName();
        }

        WeaponPref preferredWeapon = botController.getWeaponPrefs().getWeaponPreference();
        String activeWeapon = preferredWeapon.getWeapon().getName();
        int primaryAmmoLeft = botController.getWeaponry().getCurrentPrimaryAmmo();
        int secondaryAmmoLeft = botController.getWeaponry().getCurrentAlternateAmmo();

        botController.getConfig().setName("[" + behaviour.toString() + "]-["
                + behaviour.getCategory().toString() + "]-["
                + nodeName + "]-["
                + activeWeapon + ":" + primaryAmmoLeft + "+" + secondaryAmmoLeft + "]");
    }

    public static void setBotDebugName(Behaviour behaviour,
                                       UT2004BotModuleController botController)
    {
        WeaponPref preferredWeapon = botController.getWeaponPrefs().getWeaponPreference();
        String activeWeapon = preferredWeapon.getWeapon().getName();
        int primaryAmmoLeft = botController.getWeaponry().getCurrentPrimaryAmmo();
        int secondaryAmmoLeft = botController.getWeaponry().getCurrentAlternateAmmo();

        botController.getConfig().setName("[" + behaviour.toString() + "]-["
                + behaviour.getCategory().toString() + "]-["
                + activeWeapon + ":" + primaryAmmoLeft + "+" + secondaryAmmoLeft + "]");
    }

    public static void logShoot(LogCategory log,
                                WeaponPref preferredWeapon,
                                boolean shotFired,
                                Player enemy)
    {
        if (shotFired)
        {
            String weapoMode = preferredWeapon.isPrimary() ? "PRIMARY" : "SECONDARY";
            log.log(Level.INFO, "Shooting with {0} at: {1}",
                    new Object[]
                    {
                        weapoMode, enemy.getName().toString()
                    });
        }
        else
        {
            log.log(Level.WARNING, "Bot cannot shoot");
        }
    }
}
