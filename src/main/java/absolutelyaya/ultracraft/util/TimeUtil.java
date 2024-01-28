package absolutelyaya.ultracraft.util;

import absolutelyaya.ultracraft.Ultracraft;

public class TimeUtil
{
	public static long parseToMilli(String string)
	{
		try
		{
			String[] segments = string.split(":");
			long parTime = 0;
			for (int i = Math.min(segments.length - 1, 3); i >= 0; i--)
			{
				long ms = Long.parseLong(segments[i]);
				if(i > 2)
					ms *= 60; //hours to minutes
				if(i > 1)
					ms *= 60; //minutes to seconds
				if(i > 0)
					ms *= 1000; //seconds to millisecond
				parTime += ms;
			}
			return parTime;
		}
		catch (NumberFormatException e)
		{
			Ultracraft.LOGGER.warn("Couldn't parse par-time '" + string + "'; Number Format Exception");
			return -1;
		}
	}
	
	public static String milliToString(long time)
	{
		long milli = time % 1000, sec = time / 1000, min = sec / 60, hour = min / 60;
		StringBuilder builder = new StringBuilder();
		if(hour > 0)
			builder.append(String.format("%d:", hour));
		builder.append(String.format("%d:", min % 60));
		builder.append(String.format("%02d.", sec % 60));
		builder.append(String.format("%03d", milli));
		return builder.toString();
	}
}
