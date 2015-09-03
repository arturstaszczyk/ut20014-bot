package pl.staszczyk.mysimplebot1.behaviours;

/**
 *
 * @author Artur
 */
public interface IBehaviourChangeListener {
    
    void onBehaviourChange(Behaviour previois, Behaviour next);
    void onNoMoreBehaviours();
}
