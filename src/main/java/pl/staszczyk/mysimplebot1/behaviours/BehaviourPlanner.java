package pl.staszczyk.mysimplebot1.behaviours;

import SpaceAndTimeHelpers.DistanceToNavPoint;
import SpaceAndTimeHelpers.DistanceToNavPointComparator;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavPoints;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.logging.Level;
import pl.staszczyk.mysimplebot1.SpaceAwareness;
import pl.staszczyk.mysimplebot1.Timer;
import pl.staszczyk.mysimplebot1.behaviours.implementations.FightBehaviour;
import pl.staszczyk.mysimplebot1.behaviours.implementations.RunToBehaviour;
import pl.staszczyk.mysimplebot1.behaviours.implementations.StayBehaviour;

/**
 *
 * @author Artur
 */
public class BehaviourPlanner
{
    private UT2004BotModuleController mBot;
    private Random mRandom;
    private Timer mTimer;
    
    private static long HEALTH_TREASHOLD = 20;
    
    public BehaviourPlanner(UT2004BotModuleController bot)
    {
        mRandom = new Random();
        mTimer = new Timer();
        mBot = bot;
    }
    
    public void plan(BehaviourExecutor executor, SpaceAwareness spaceAwerness)
    {
        executor.getExecutionHistory().forgetOldBehaviours(mTimer);
        
        Behaviour behaviour = executor.getActiveBehaviour();
        if(behaviour.isUnbreakable())
            return;
        
        mBot.getLog().log(Level.INFO, "Searching for new state...");
        boolean enemyInRange = mBot.getPlayers().canSeeEnemies();
        
        if(!hasFightingHealth() && mBot.getWeaponry().hasLoadedWeapon())
        {
            if(!executor.isBehaviourExecutedRecently(RunToBehaviour.class) ||
                    executor.getActiveBehaviour().getClass() == StayBehaviour.class)
            {
                NavPoint healthPoint = findNotVisibleHealthPoint();
                if(healthPoint == null)
                    healthPoint = getNotVisibleRandomPoint();

                executor.replaceBehaviour(new RunToBehaviour(mBot, 
                        Behaviour.BehaviourCategory.FLEEING, false).setTarget(healthPoint));
            }
        }
        else if(!enemyInRange || !mBot.getWeaponry().hasLoadedWeapon())
        {
            NavPoint weaponPoint = findNotVisibleWeaponPoint();
            if(weaponPoint == null)
                weaponPoint = getNotVisibleRandomPoint();

            executor.replaceBehaviour(new RunToBehaviour(mBot, 
                    Behaviour.BehaviourCategory.FLEEING, 
                    true).setTarget(weaponPoint));
        }
        else if(enemyInRange)
        {
            if(!executor.hasFightBehaviourPlanned())
            {
                executor.replaceBehaviour(new FightBehaviour(mBot));
            }
        }
        else
        {
            if(executor.currentBehaviourClass() != RunToBehaviour.class)
            {
                NavPoints navPoints = mBot.getNavPoints();
                RunToBehaviour runBehaviour = new RunToBehaviour(mBot,
                        Behaviour.BehaviourCategory.NEUTRAL, false);
                runBehaviour.setTarget(navPoints.getRandomNavPoint());
                executor.queueBehaviour(runBehaviour);
            }
        }
    }
    
    private boolean hasFightingHealth()
    {
        boolean shoulFight = false;
        
        int armor = mBot.getInfo().getArmor();
        int health = mBot.getInfo().getHealth();
        
        double healthRunTreashold = HEALTH_TREASHOLD + mRandom.nextGaussian() * 2;
        double armorRunTreashold = HEALTH_TREASHOLD + mRandom.nextGaussian() * 2;
        
        if(health > healthRunTreashold)
            shoulFight = true;
        else if(health < healthRunTreashold && armor > armorRunTreashold)
            shoulFight = true;

        return shoulFight;
    }
    
    public NavPoint findNotVisibleWeaponPoint()
    {
        NavPoints navPoints = mBot.getNavPoints();
        NavPoint target = null;
        
        PriorityQueue<DistanceToNavPoint> weaponPoints = 
                new PriorityQueue<DistanceToNavPoint>(16, new DistanceToNavPointComparator());
        
        for(NavPoint navPoint : navPoints.getNavPoints().values())
        {
            if(navPoint.isInvSpot() && 
               (navPoint.getItemClass().getCategory() == ItemType.Category.WEAPON ||
                    navPoint.getItemClass().getCategory() == ItemType.Category.ARMOR))
            {
                double distance = mBot.getBot().getLocation().getDistance(navPoint.getLocation());
                weaponPoints.add(new DistanceToNavPoint(distance, navPoint));
            }
        }
        
        boolean found = false;
        while(!found && !weaponPoints.isEmpty())
        {
            NavPoint point = weaponPoints.poll().location;
            if(!point.isVisible() || point.isItemSpawned())
                target = point;
        }
        
        return target;
    }
    
    public NavPoint findNotVisibleHealthPoint()
    {
        NavPoints navPoints = mBot.getNavPoints();
        NavPoint target = null;
        
        for(NavPoint navPoint : navPoints.getNavPoints().values())
        {
            if(navPoint.isInvSpot() && 
               navPoint.getItemClass().getCategory() == ItemType.Category.HEALTH)
            {
                if(navPoint.isVisible() && navPoint.isItemSpawned())
                    target = navPoint;
                else if(!navPoint.isVisible())
                    target = navPoint;
                
                if(navPoint != null)
                    break;
            }
        }
        
        return target;
    }
    
    private NavPoint getNotVisibleRandomPoint()
    {
        NavPoints navPoints = mBot.getNavPoints();
        NavPoint target = null;
        
        for(NavPoint navPoint : navPoints.getNavPoints().values())
        {
            if(!navPoint.isVisible())
                target = navPoint;

            if(navPoint != null)
                break;
        }
        
        return target;
    }
}
