package pl.staszczyk.mysimplebot1;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Raycasting;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.RemoveRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import cz.cuni.amis.utils.flag.FlagListener;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Vector3d;

/**
 *
 * @author Artur
 */
public class SpaceAwareness {
    
    public enum Direction {
     LEFT {
        public String toString() {
            return "LEFT";
        }
     },
     
     LEFT_FRONT {
        public String toString() {
            return "LEFT_FRONT";
        }
     },
     
     FRONT {
         public String toString() {
             return "FRONT";
         }
     },
     
     RIGHT_FRONT {
         public String toString() {
             return "RIGHT_FRONT";
         }
     },
     
     RIGHT {
         public String toString() {
             return "RIGHT";
         }
     }
    }
    
    protected Map<Direction, AutoTraceRay> mRays = new HashMap<Direction, AutoTraceRay>();
    protected Map<Direction, Vector3d> mUnitDirectionVectors = new HashMap<Direction, Vector3d>();
    protected UT2004Bot mBot = null;
    
    public Map<Direction, Vector3d> getDirections() {
        return mUnitDirectionVectors;
    }
    
    public SpaceAwareness(UT2004Bot bot)
    {
        mBot = bot;
    }
    
    public void prepareRays(final Raycasting raycasting) {
        mBot.getAct().act(new RemoveRay("All"));
        
        // initialize rays for raycasting
        final int rayLength = (int) (UnrealUtils.CHARACTER_COLLISION_RADIUS * 20);
        // settings for the rays
        boolean fastTrace = false;        // perform only fast trace == we just need true/false information
        boolean floorCorrection = false; // provide floor-angle correction for the ray (when the bot is running on the skewed floor, the ray gets rotated to match the skew)
        boolean traceActor = true;      // whether the ray should collid with other actors == bots/players as well
        
        mUnitDirectionVectors.put(Direction.LEFT, new Vector3d(0, -1, 0));
        mUnitDirectionVectors.put(Direction.LEFT_FRONT, new Vector3d(1, -1, 0));
        mUnitDirectionVectors.put(Direction.FRONT, new Vector3d(1, 0, 0));
        mUnitDirectionVectors.put(Direction.RIGHT_FRONT, new Vector3d(1, 1, 0));
        mUnitDirectionVectors.put(Direction.RIGHT, new Vector3d(0, 1, 0));
        
        for(Direction rayName : mUnitDirectionVectors.keySet()) {
            raycasting.createRay(rayName.toString(), mUnitDirectionVectors.get(rayName),
                    rayLength, fastTrace, floorCorrection, traceActor);
        }
        
        raycasting.getAllRaysInitialized().addListener(new FlagListener<Boolean>() {

            @Override
                public void flagChanged(Boolean changedValue) {
                    for(Direction rayName : mUnitDirectionVectors.keySet()) {
                        mRays.put(rayName, raycasting.getRay(rayName.toString()));
                    }
                }
            });
        
        raycasting.endRayInitSequence();
        
        mBot.getAct().act(new Configuration().setDrawTraceLines(true).setAutoTrace(true));
    }
    
    public Location getLocationInDirection(Direction dir)
    {
        Location destPoint = mRays.get(dir).getTo();
        Location myPosition = mBot.getLocation();
        
        Vector3d direction = Location.sub(destPoint, mBot.getLocation()).asVector3d();
        direction.normalize();
        
        Location pointInDir = Location.add(myPosition, new Location(direction));
        return pointInDir;
    }
    
    public double sqrDistanceToObstacleInDirection(Direction direction) {
        double sqrDistToObstacle = -1.0;
        
        AutoTraceRay ray = mRays.get(direction);
        if(ray != null && ray.isResult())
            sqrDistToObstacle = Location.getDistanceSquare(mBot.getLocation(),
                    ray.getHitLocation());
        
        return sqrDistToObstacle; 
    }
    
    
    
    public Location getSafestDirectionFrom(Location location)
    {
        double angleCos = -10e5;
        Location safestPoint = null;
        
        for(Direction key : mRays.keySet())
        {
            AutoTraceRay ray = mRays.get(key);
            if(ray.isResult())
            {
                double cos = MathHelper.cosBetweenPoints(mBot.getLocation(), location, ray.getHitLocation());
                if(cos > angleCos)
                {
                    angleCos = cos;
                    safestPoint = ray.getHitLocation();
                }
            }
//            double sqrDist = sqrDistanceToRayHit(ray);
//            if(sqrDist > 0)
//                log.log(Level.INFO, "Distance to {0} is: {1}", new Object[]{key, sqrDist});
        }
        
        if(safestPoint == null)
        {
             Location toSub = Location.sub(mBot.getLocation(), location);
             safestPoint = Location.add(mBot.getLocation(), toSub);             
        }
        
        return safestPoint;
    }
    
}
