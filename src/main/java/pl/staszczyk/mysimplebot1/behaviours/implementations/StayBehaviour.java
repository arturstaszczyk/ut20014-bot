package pl.staszczyk.mysimplebot1.behaviours.implementations;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import pl.staszczyk.mysimplebot1.behaviours.Behaviour;

/**
 *
 * @author Artur
 */
public class StayBehaviour extends Behaviour {

    public StayBehaviour(UT2004BotModuleController bot) {
        super(bot, Behaviour.BehaviourCategory.NEUTRAL);
    }
    
    @Override
    public void onBegin() {
        mBot.getConfig().setName("Stasiu [" + toString() + "]");
    }

    @Override
    public void execute(double dt) {
        endBehaviour();
    }

    @Override
    public void onEnd() {
    }

    @Override
    public String toString() {
        return "++STAY++";
    }
    
}
