package absolutelyaya.ultracraft.components.player;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.util.Identifier;

public interface ILevelStatsComponent extends ComponentV3
{
	
	void enterLevel(Identifier levelId, String instance);
	
	Identifier getCurrentLevel();
	
	String getCurrentLevelInstance();
	
	boolean isInCombat();
	
	boolean isTimerRunning();
	
	void startTimer();
	
	void stopTimer(boolean interrupted);
	
	/**
	 * Client only; this isn't networked
	 */
	void setTimerPaused(boolean v);
	
	/**
	 * Client only; this isn't networked
	 */
	boolean isTimerPaused();
	
	long getElapsedTimer();
	
	void setBestTime(Identifier levelId, boolean perfect, long time);
	
	long getLastStoppedTimer();
	
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
	
	void setInvalid();
	
	boolean isInvalid();
	
	void setCurLevelSoundTrackKey(String id);
	
	String getCurLevelSoundTrackKey();
	
	void setShouldMusicFade(boolean val);
	
	boolean shouldLevelMusicFade();
	
	void onFinishLevel();
}
