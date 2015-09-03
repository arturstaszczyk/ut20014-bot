package pl.staszczyk.mysimplebot1;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import javax.vecmath.Vector3d;

/**
 *
 * @author Artur
 */
public class MathHelper {
    public static double cosBetweenPoints(Location middle, Location a, Location b)
    {
        Location vecA = Location.sub(a, middle);
        Location vecB = Location.sub(b, middle);
        double cos = MathHelper.dot(vecA.asVector3d(), vecB.asVector3d());
        
        return cos;
    }
    
    public static double dot(Vector3d v1, Vector3d v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }
}
