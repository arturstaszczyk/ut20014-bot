package pl.staszczyk.mysimplebot1.HistoryAndPlanning;

import SpaceAndTimeHelpers.TimeOfItem;
import SpaceAndTimeHelpers.TimeOfItemComparator;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.logging.Level;
import pl.staszczyk.mysimplebot1.Timer;

/**
 *
 * @author Artur
 */
public class NavPointsVisitationHistory
{

    private PriorityQueue<TimeOfItem<NavPoint>> mNavPointsHistory;
    
    private Timer mTimer;
    private static double DISTANCE_TREASHOLD = 40;
    private static long HISTORY_REMEMBER_TIME_TREASHOLD = 15000;

    public NavPointsVisitationHistory()
    {
        mTimer = new Timer();
        mNavPointsHistory = new PriorityQueue<TimeOfItem<NavPoint>>(16, new TimeOfItemComparator());
    }

    public boolean isInHistory(NavPoint navPoint)
    {
        boolean isPresent = false;
        
        Iterator<TimeOfItem<NavPoint>> iter = mNavPointsHistory.iterator();
        while(iter.hasNext())
        {
            TimeOfItem<NavPoint> navPointHistory = iter.next();
            if(navPointHistory.itemOccurance.getId() == navPoint.getId())
            {
                isPresent = true;
                break;
            }
        }
        
        return isPresent;
    }
    
    public void updateNavigationHistory(UT2004BotModuleController botController)
    {
        checkForNewNavPoint(botController);
        removeOldNavPoint();
        logNavpointsHistory(botController.getLog());
    }

    private void checkForNewNavPoint(UT2004BotModuleController botController)
    {
        NavPoint nearestPoint = botController.getNavPoints().getNearestNavPoint();
        if (nearestPoint == null)
        {
            return;
        }

        if (!nearestPoint.isInvSpot())
        {
            return;
        }

        double distance = botController.getBot().getLocation().getDistance(nearestPoint.getLocation());
        if (distance < DISTANCE_TREASHOLD)
        {
            removeInstancesWithNavPoint(nearestPoint);
            navPointVisited(nearestPoint);
        }
    }
    
    private void removeInstancesWithNavPoint(NavPoint navPoint)
    {
        Iterator<TimeOfItem<NavPoint>> iter = mNavPointsHistory.iterator();
        while(iter.hasNext())
        {
            TimeOfItem<NavPoint> navPointHistory = iter.next();
            if(navPointHistory.itemOccurance.getId() == navPoint.getId())
                iter.remove();
        }
    }

    private void navPointVisited(NavPoint navPoint)
    {
        if (!navPoint.isInvSpot())
        {
            return;
        }

        TimeOfItem<NavPoint> occurence = new TimeOfItem<NavPoint>(mTimer.getTimestamp(), navPoint);
        mNavPointsHistory.add(occurence);
    }

    private void removeOldNavPoint()
    {
        Iterator<TimeOfItem<NavPoint>> iter = mNavPointsHistory.iterator();
        while(iter.hasNext())
        {
            TimeOfItem<NavPoint> navPointHistory = iter.next();
            double timeDiff = mTimer.getMillisecondsSince(navPointHistory.timestampEnded);
            
            if(timeDiff > HISTORY_REMEMBER_TIME_TREASHOLD)
                iter.remove();
        }
    }

    private void logNavpointsHistory(LogCategory log)
    {
        log.log(Level.INFO, "============ HISTORY ============");
        Iterator<TimeOfItem<NavPoint>> iter = mNavPointsHistory.iterator();
        while(iter.hasNext())
        {
            TimeOfItem<NavPoint> historyItem = iter.next();
            long timeDiff = mTimer.getMillisecondsSince(historyItem.timestampEnded);
            
            log.log(Level.INFO, "NavPoint: {0} ms ago: {1}",
                    new Object[]
                    {
                        historyItem.itemOccurance.getId().toString(), timeDiff
                    });
        }
        log.log(Level.INFO, "============== EOH ==============");
    }
}
