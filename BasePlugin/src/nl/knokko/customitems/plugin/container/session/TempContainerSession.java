package nl.knokko.customitems.plugin.container.session;

import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.plugin.container.ContainerInstance;

/**
 * A session of a player using a custom container with a non-persistent inventory.
 * All items in the slots of the container will be dropped as soon as the player
 * closes this session (by pressing escape).
 */
public class TempContainerSession implements ContainerSession {

	private final ContainerInstance containerState;
	
	public TempContainerSession(CustomContainer containerType) {
		this.containerState = new ContainerInstance(containerType);
	}
	
	public ContainerInstance getState() {
		return containerState;
	}
}
