package SpaceAndTimeHelpers;

/**
 *
 * @author Artur
 */
public class TimeOfItem<ItemOccuranceType>
{

    public TimeOfItem(long timestamp, ItemOccuranceType item)
    {
        timestampEnded = timestamp;
        itemOccurance = item;
    }
    public long timestampEnded;
    public ItemOccuranceType itemOccurance;
}
