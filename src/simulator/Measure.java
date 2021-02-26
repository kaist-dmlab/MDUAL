package simulator;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * This class is from Luan's work (http://infolab.usc.edu/Luan/Outlier/)
 * @author Luan
 */

/**
 * Some utilities.
 */
public final class Measure {
    public static long peakUsedMemory = 0;
    private static final int MegaBytes = 1024*1024;

    /**
     * Don't let anyone instantiate this class.
     */
	private Measure() {}

    public static long getCPUTime(){
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported()? bean.getCurrentThreadCpuTime(): 0L;
    }
    
    public static long getMemory() {
        Runtime.getRuntime().gc();
        long used = Runtime.getRuntime().totalMemory()- Runtime.getRuntime().freeMemory();
        return used/MegaBytes;
    }
}
