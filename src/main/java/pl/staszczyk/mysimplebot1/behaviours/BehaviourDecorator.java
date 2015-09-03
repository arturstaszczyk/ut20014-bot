package pl.staszczyk.mysimplebot1.behaviours;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;

/**
 *
 * @author Artur
 */
public abstract class BehaviourDecorator extends Behaviour {
    
    protected Behaviour mDecoratedBehaviour = null;

    public BehaviourDecorator(Behaviour behaviourToDecorate, UT2004BotModuleController bot)
    {
        super(bot);
        mDecoratedBehaviour = behaviourToDecorate;
    }
}
