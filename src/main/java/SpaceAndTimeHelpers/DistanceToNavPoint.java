
package SpaceAndTimeHelpers;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

/**
 *
 * @author Artur
 */
public class DistanceToNavPoint
{
    public DistanceToNavPoint(double dist, NavPoint loc)
    {
        distance = dist;
        location = loc;
    }
    
    public double distance;
    public NavPoint location;
}
