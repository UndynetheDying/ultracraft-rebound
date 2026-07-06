package absolutelyaya.ultracraft.util;

import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class WeightedList <T>
{
	static Random rand = Random.create();
	List<T> list = new ArrayList<>();
	List<Integer> weights = new ArrayList<>();
	
	public void add(T item, int weight)
	{
		list.add(item);
		for (int i = 0; i < weight; i++)
			weights.add(list.size() - 1);
	}
	
	public T getRandomItem()
	{
		return list.get(weights.get(rand.nextInt(weights.size())));
	}
}
