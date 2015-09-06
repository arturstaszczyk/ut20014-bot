package pl.staszczyk.mysimplebot1;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPrefs;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;

/**
 *
 * @author Artur
 */
public class WeaponPrefsChooser
{
    public static void configureDefaultPrefs(WeaponPrefs prefs)
    {
        prefs.clearAllPrefs();

        prefs.addGeneralPref(UT2004ItemType.SHOCK_RIFLE, true);
        prefs.addGeneralPref(UT2004ItemType.LIGHTNING_GUN, false);  
        prefs.addGeneralPref(UT2004ItemType.LINK_GUN, true);
        prefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, true);
        prefs.addGeneralPref(UT2004ItemType.MINIGUN, false);
        prefs.addGeneralPref(UT2004ItemType.ASSAULT_RIFLE, true); 
        prefs.addGeneralPref(UT2004ItemType.SHIELD_GUN, false);
    }
}
