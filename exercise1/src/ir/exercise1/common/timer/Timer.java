package ir.exercise1.common.timer;

/**
 * @author Florian Eckerstorfer (florian@eckerstorfer.co)
 */
public class Timer
{
    private long startTime;
    private long endTime;

    /**
     * Rests the timer.
     *
     * @return
     */
    public Timer reset()
    {
        startTime = 0;
        endTime   = 0;

        return this;
    }

    /**
     * Starts the timer.
     *
     * @return
     */
    public Timer start()
    {
        startTime = System.currentTimeMillis();

        return this;
    }

    /**
     * Stops the timer.
     *
     * @return
     */
    public Timer stop()
    {
        endTime = System.currentTimeMillis();

        return this;
    }

    /**
     * Returns the stime.
     *
     * @return
     * @throws RuntimeException when the timer has not been started
     * @throws RuntimeException when the timer has not been started
     */
    public long getTime()
        throws RuntimeException
    {
        if (0 == startTime) {
            throw new RuntimeException("Timer has to be started before time can be retrieved.");
        }
        if (0 == endTime) {
            throw new RuntimeException("Timer has to be stopped before time can be retrieved.");
        }

        return endTime - startTime;
    }
}
