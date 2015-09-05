package SpaceAndTimeHelpers;

import java.util.Comparator;

/**
 *
 * @author Artur
 */
public class DistanceToNavPointComparator implements Comparator<DistanceToNavPoint>
{

    @Override
    public int compare(DistanceToNavPoint o1, DistanceToNavPoint o2)
    {
        if(o1.distance < o2.distance)
            return -1;
        else
            return 1;
    }
    
}
