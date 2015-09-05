package pl.staszczyk.mysimplebot1.behaviours.implementations;

import cz.cuni.amis.pogamut.base.communication.messages.CommandMessage;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPref;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPrefs;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StopShooting;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import java.util.logging.Level;
import pl.staszczyk.mysimplebot1.behaviours.Behaviour;

/**
 *
 * @author Artur
 */
public class RunToBehaviour extends Behaviour {

    protected NavPoint mTarget = null;
    protected  RandomBotCommand mRandomCommand;
    private static long DELAY_BETWEEN_RANDOM_COMMANDS = 1500;
      
    public RunToBehaviour(UT2004BotModuleController bot,
            Behaviour.BehaviourCategory category,
            boolean unbreakable)
    {
        super(bot, category, unbreakable);
        mRandomCommand = new RandomBotCommand(DELAY_BETWEEN_RANDOM_COMMANDS);
    }
    
    public RunToBehaviour setTarget(NavPoint target){
        mTarget = target;
        return this;
    }
    
    @Override
    public String toString() {
        return "++RUNTO++";
    }
    
    @Override
    public void onBegin() {
        mBot.getNavigation().navigate(mTarget);
        mBot.getConfig().setName("Stasiu [" + toString() + "]");
    }

    @Override
    public void execute(double dt) {
        IUT2004Navigation nav = mBot.getNavigation();
        
        nav.navigate(mTarget);
        
        handleRandomBehaviour();
        handleVisibleEnemies(nav);
        handleEndBehaviourCondition();
    }
    
    private void handleVisibleEnemies(IUT2004Navigation nav)
    {
        Player enemy = mBot.getPlayers().getNearestEnemy(5000);
        if(enemy != null)
        {
            nav.setFocus(enemy);
//            mBot.getAct().act(new Shoot(enemy.getLocation(), 
//                    enemy.getId(), false));
            
        WeaponPrefs mWeaponPrefs = mBot.getWeaponPrefs();
        mWeaponPrefs.addGeneralPref(UT2004ItemType.LIGHTNING_GUN, false);                
        mWeaponPrefs.addGeneralPref(UT2004ItemType.SHOCK_RIFLE, false);
        mWeaponPrefs.addGeneralPref(UT2004ItemType.MINIGUN, false);
        mWeaponPrefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, false);        
        //mWeaponPrefs.addGeneralPref(UT2004ItemType.ROCKET_LAUNCHER, false);
        mWeaponPrefs.addGeneralPref(UT2004ItemType.LINK_GUN, false);
        mWeaponPrefs.addGeneralPref(UT2004ItemType.ASSAULT_RIFLE, false);        
        mWeaponPrefs.addGeneralPref(UT2004ItemType.BIO_RIFLE, false);
            mBot.getShoot().shoot(mWeaponPrefs, enemy.getLocation());
        }
        else
        {
            nav.setFocus(null);
            mBot.getAct().act(new StopShooting());
        }
    }
    
    private void handleEndBehaviourCondition()
    {
        Location botPosition = mBot.getBot().getLocation();
        
        if(botPosition.getDistance(mTarget.getLocation()) < 30)
            endBehaviour();
    }

    private void handleRandomBehaviour()
    {
        if(mRandomCommand.canProduceCommand())
        {
            CommandMessage command = mRandomCommand.produceCommand();
            mBot.getAct().act(command);
            mBot.getLog().log(Level.INFO, "Executing random command: {0}", command.toString());
        }
    }
    
    @Override
    public void onEnd() {
        mBot.getAct().act(new StopShooting());
        mBot.getNavigation().stopNavigation();
    }
}
