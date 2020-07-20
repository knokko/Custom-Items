package nl.knokko.customitems.plugin.container.session;

import java.util.Collection;
import java.util.HashSet;

import org.bukkit.entity.Player;

import nl.knokko.customitems.plugin.container.ContainerInstance;

public class PersistentContainerSession implements ContainerSession {

	private final ContainerInstance containerState;
	
	private final Collection<Player> viewers;
	
	public PersistentContainerSession(ContainerInstance containerState, Player firstViewer) {
		this.containerState = containerState;
		this.viewers = new HashSet<>();
		this.viewers.add(firstViewer);
	}
	
	public ContainerInstance getContainerState() {
		return containerState;
	}
	
	public Iterable<Player> getViewers() {
		return viewers;
	}
	
	public void addViewer(Player newViewer) {
		viewers.add(newViewer);
	}
	
	public void removeViewer(Player stoppedViewing) {
		viewers.remove(stoppedViewing);
		// TODO Optionally save and unload memory
	}
}
