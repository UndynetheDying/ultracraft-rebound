package absolutelyaya.ultracraft.components.player;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.util.Identifier;

public interface ILevelStatsComponent extends ComponentV3
{
	
	void enterLevel(Identifier levelId, String instance);
	
	Identifier getCurrentLevel();
	
	String getCurrentLevelInstance();
	
	boolean isInFight();
	
	boolean isTimerRunning();
	
	void startTimer();
	
	void stopTimer(boolean interrupted);
	
	void setBestTime(Identifier levelId, boolean perfect, long time);
	
	long getElapsedTimer();
	
	long getLastStoppedTimer();
	
	boolean isPerfect();
	
	void onDeath();
	
	int getDeaths();
	
	int getKills();
	
	void setKills(int val);
	
	int getStyle();
	
	void onStyle(float score);
	
	void onDamage();
	
	boolean isUndamaged();
	
	long getBestTime(Identifier levelId, boolean perfect);
	
	void resetBestTime(Identifier levelId);
	
	int getLastPlayedLevelVersion(Identifier levelId);
	
	int getBestRank(Identifier levelId);
	
	void setBestRank(Identifier levelId, int rank);
}
