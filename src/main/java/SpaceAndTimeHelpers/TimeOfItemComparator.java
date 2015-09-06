package SpaceAndTimeHelpers;

import java.util.Comparator;

/**
 *
 * @author Artur
 */
public class TimeOfItemComparator implements Comparator<TimeOfItem>
{

    @Override
    public int compare(TimeOfItem o1, TimeOfItem o2)
    {
        // smallest timestamp (oldest behaviour) is lowest value
        if (o1.timestampEnded < o2.timestampEnded)
        {
            return -1;
        }
        else
        {
            return 1;
        }
    }
}
