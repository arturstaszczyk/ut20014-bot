/*
 * Copyright (C) 2015 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.staszczyk.mysimplebot1.behaviours.implementations;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.logging.Level;
import javax.vecmath.Vector3d;
import pl.staszczyk.mysimplebot1.SpaceAwareness;
import pl.staszczyk.mysimplebot1.behaviours.Behaviour;
import pl.staszczyk.mysimplebot1.behaviours.IBehaviourFinishedListener;

/**
 *
 * @author Artur
 */
public class DodgeBehaviour extends Behaviour
{
    private class DistanceToDirection
    {

        public DistanceToDirection(SpaceAwareness.Direction dir, double dist)
        {
            direction = dir;
            distance = dist;
        }
        public SpaceAwareness.Direction direction;
        public double distance;
    }

    private class DistanceToDirectionComparator implements Comparator<DistanceToDirection>
    {

        @Override
        public int compare(DistanceToDirection d1, DistanceToDirection d2)
        {
            if (d1.distance < d2.distance)
            {
                return -1;
            } else
            {
                return 1;
            }
        }
    }

    protected boolean dodgeMade = false;
    protected long mMillisecondsSinceLastDodge;
    protected int dodgesCount = 0;
    protected Player mDodgePlayer = null;
    protected SpaceAwareness mSpaceAwerness = null;
    PriorityQueue<DistanceToDirection> mPossibleDodges = null;

    public DodgeBehaviour(UT2004BotModuleController bot, SpaceAwareness spaceAwerness)
    {
        super(bot);

        mSpaceAwerness = spaceAwerness;
        mPossibleDodges = new PriorityQueue<DistanceToDirection>(5, new DistanceToDirectionComparator());
    }

    @Override
    public String toString()
    {
        return "DodgeBehaviour";
    }

    @Override
    public void onBegin()
    {
        mDodgePlayer = mBot.getPlayers().getNearestEnemy(5000);
    }

    @Override
    public void execute(double dt)
    {
        mMillisecondsSinceLastDodge =+ (long)dt;
        
        if (mDodgePlayer != null)
        {
            Location myPosition = mBot.getBot().getLocation();
            if (dodgeMade == false)
            {
                for (SpaceAwareness.Direction dirName : mSpaceAwerness.getDirections().keySet())
                {
                    Location dodgePoint = mSpaceAwerness.getLocationInDirection(dirName);
                    mPossibleDodges.add(new DistanceToDirection(dirName,
                            Location.getDistanceSquare(myPosition, dodgePoint)));
                }
                
                while(!dodgeMade && !mPossibleDodges.isEmpty())
                {
                    DistanceToDirection dist = mPossibleDodges.poll();
                    
                    double distToObst = mSpaceAwerness.sqrDistanceToObstacleInDirection(dist.direction);
                    if(distToObst > 300 * 300)
                    {
                        Location dirAsLocation = new Location(mSpaceAwerness.getDirections().get(dist.direction));
                        makeNormalRelativeDodge(dirAsLocation);
                        mBot.getLog().log(Level.INFO, "Making dodge in " + dist.direction);
                    }
                }
                
                if(!dodgeMade)
                {
                    Vector3d back = mSpaceAwerness.getDirections().get(SpaceAwareness.Direction.FRONT);
                    back.negate();
                    
                    makeNormalRelativeDodge(new Location(back));
                    
                    mBot.getLog().log(Level.INFO, "Making dodge back");
                }
            }
        }
        
        for(IBehaviourFinishedListener listener : mListeners)
                listener.onBehaviourFinished(this);
    }
    
    private void makeNormalRelativeDodge(Location location)
    {
        mBot.getMove().dodgeRelative(location, false);
        dodgeMade = true;
        mMillisecondsSinceLastDodge = 0;
    }

    @Override
    public void onEnd()
    {
    }
}
