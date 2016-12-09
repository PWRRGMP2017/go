package pwrrgmp2017.go.server.playerobserver;

public interface IPlayerObservable
{
	public void addObserverOn(IPlayerObserver observer, PlayerEvent event);

	public void removeObserverOn(IPlayerObserver observer, PlayerEvent event);
}
