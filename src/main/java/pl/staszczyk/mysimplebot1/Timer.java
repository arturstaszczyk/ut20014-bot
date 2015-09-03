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
package pl.staszczyk.mysimplebot1;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import java.util.logging.Level;

/**
 *
 * @author Artur
 */
public class Timer {
    
    protected long mLastLogicTime = -1;
    protected long mLastDT = 0;
    
    public void update(LogCategory log)
    {
        long currTime = System.currentTimeMillis();
        if (mLastLogicTime > 0) 
            log.log(Level.CONFIG, 
                "Logic invoked after: {0} ms", (currTime - mLastLogicTime));
        
        mLastDT = currTime - mLastLogicTime;
        mLastLogicTime = currTime;
    }
    
    public long dt()
    {
        return mLastDT;
    }
    
}
