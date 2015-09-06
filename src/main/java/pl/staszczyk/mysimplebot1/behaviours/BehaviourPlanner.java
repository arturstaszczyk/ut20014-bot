package pl.staszczyk.mysimplebot1.behaviours;

import SpaceAndTimeHelpers.DistanceToNavPoint;
import SpaceAndTimeHelpers.DistanceToNavPointComparator;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavPoints;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;
import pl.staszczyk.mysimplebot1.HistoryAndPlanning.NavPointsVisitationHistory;
import pl.staszczyk.mysimplebot1.SpaceAwareness;
import pl.staszczyk.mysimplebot1.Timer;
import pl.staszczyk.mysimplebot1.behaviours.implementations.FightBehaviour;
import pl.staszczyk.mysimplebot1.behaviours.implementations.RunToBehaviour;

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

    public void plan(BehaviourExecutor executor,
                     NavPointsVisitationHistory navPointsHistory,
                     SpaceAwareness spaceAwerness)
    {
        Behaviour behaviour = executor.getActiveBehaviour();
        if (behaviour.isUnbreakable())
        {
            return;
        }

        boolean enemyInRange = mBot.getPlayers().canSeeEnemies();

        // low ammo - go for it
        if (!mBot.getWeaponry().hasLoadedWeapon())
        {
            if (!executor.hasFleeingBehaviourPlanned())
            {
                NavPoint weaponPoint = findNotVisibleWeaponPoint(navPointsHistory);
                if (weaponPoint == null)
                {
                    weaponPoint = mBot.getNavPoints().getRandomNavPoint();
                }

                executor.replaceBehaviour(new RunToBehaviour(mBot,
                                                             Behaviour.BehaviourCategory.FLEEING,
                                                             true).setTarget(weaponPoint));
            }
        }
        // low health / armor
        else if (!hasFightingHealth() && mBot.getWeaponry().hasLoadedWeapon())
        {
            if (!executor.hasFleeingBehaviourPlanned())
            {
                NavPoint healthPoint = findNotVisibleHealthPoint(navPointsHistory);
                if (healthPoint == null)
                {
                    healthPoint = mBot.getNavPoints().getRandomNavPoint();
                }

                executor.replaceBehaviour(new RunToBehaviour(mBot,
                                                             Behaviour.BehaviourCategory.FLEEING, true).setTarget(healthPoint));
            }
        }
        else if (enemyInRange)
        {
            if (!executor.hasFightBehaviourPlanned())
            {
                executor.replaceBehaviour(new FightBehaviour(mBot));
            }
        }
        else
        {
            if (!executor.hasNeutralBehaviourPlanned())
            {
                NavPoint runTarget = getNotVisibleRandomItemPoint();
                if (runTarget == null)
                {
                    runTarget = mBot.getNavPoints().getRandomNavPoint();
                }

                RunToBehaviour runBehaviour = new RunToBehaviour(mBot,
                                                                 Behaviour.BehaviourCategory.NEUTRAL,
                                                                 false);
                runBehaviour.setTarget(runTarget);
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

        if (health > healthRunTreashold)
        {
            shoulFight = true;
        }
        else if (health < healthRunTreashold && armor > armorRunTreashold)
        {
            shoulFight = true;
        }

        return shoulFight;
    }

    public NavPoint findNotVisibleWeaponPoint(NavPointsVisitationHistory history)
    {
        NavPoints navPoints = mBot.getNavPoints();
        NavPoint target = null;

        PriorityQueue<DistanceToNavPoint> weaponPoints =
                new PriorityQueue<DistanceToNavPoint>(16, new DistanceToNavPointComparator());

        for (NavPoint navPoint : navPoints.getNavPoints().values())
        {
            if (navPoint.isInvSpot()
                    && (navPoint.getItemClass().getCategory() == ItemType.Category.WEAPON
                        || navPoint.getItemClass().getCategory() == ItemType.Category.AMMO))
            {
                double distance = mBot.getBot().getLocation().getDistance(navPoint.getLocation());
                weaponPoints.add(new DistanceToNavPoint(distance, navPoint));
            }
        }

        Iterator<DistanceToNavPoint> iter = weaponPoints.iterator();
        while (iter.hasNext())
        {
            NavPoint navPoint = iter.next().location;
            if (!history.isInHistory(navPoint))
            {
                if (!navPoint.isVisible() || navPoint.isItemSpawned())
                {
                    target = navPoint;
                }
            }
        }

        return target;
    }

    public NavPoint findNotVisibleHealthPoint(NavPointsVisitationHistory history)
    {
        NavPoints navPoints = mBot.getNavPoints();
        NavPoint target = null;

        for (NavPoint navPoint : navPoints.getNavPoints().values())
        {
            if (navPoint.isInvSpot()
                    && navPoint.getItemClass().getCategory() == ItemType.Category.HEALTH
                    && !history.isInHistory(navPoint))
            {
                if (navPoint.isVisible() && navPoint.isItemSpawned())
                {
                    target = navPoint;
                }
                else if (!navPoint.isVisible())
                {
                    target = navPoint;
                }

                if (target != null)
                {
                    break;
                }
            }
        }

        return target;
    }

    private NavPoint getNotVisibleRandomItemPoint()
    {
        NavPoints navPoints = mBot.getNavPoints();
        NavPoint target = null;

        for (NavPoint navPoint : navPoints.getNavPoints().values())
        {
            if (navPoint.isInvSpot())
            {
                if (!navPoint.isVisible())
                {
                    target = navPoint;
                }
            }
            
            if (target != null)
            {
                break;
            }
        }

        return target;
    }
}
