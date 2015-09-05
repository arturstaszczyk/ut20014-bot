package pl.staszczyk.mysimplebot1.behaviours.implementations;

import cz.cuni.amis.pogamut.base.communication.messages.CommandMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Dodge;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Jump;
import java.util.Random;
import pl.staszczyk.mysimplebot1.Timer;


/**
 *
 * @author Artur
 */
public class RandomBotCommand
{
    protected long mLastCommandTimestamp;
    protected long mTimeTrashold;
    
    protected Timer mTimer;
    protected Random mRandom;
    
    public RandomBotCommand(long timeTreashold)
    {
        mTimeTrashold = timeTreashold;
        mTimer = new Timer();
        mRandom = new Random();
        
        mLastCommandTimestamp = mTimer.getTimestamp();
    }
    
    public boolean canProduceCommand()
    {
        if(mTimer.getTimestamp() - mLastCommandTimestamp > mTimeTrashold)
            return true;
        
        return false;
    }
    
    public CommandMessage produceCommand()
    {
        CommandMessage command = null;
        if(canProduceCommand())
        {
            switch(mRandom.nextInt(4))
            {
                case 0:
                case 1:
                case 2:
                    command = new Jump();
                    break;
                case 3:
                    command =  new Dodge();
                    break;
                default:
                    break;

            }
        }
        
        mLastCommandTimestamp = mTimer.getTimestamp();
        return command;
    }
        
}
