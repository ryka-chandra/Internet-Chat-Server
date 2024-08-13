package org.cis1200;

import java.util.*;

/**
 * This is a communication channel class with channel name, owner ID, user IDs, and privacy status.
 */
public final class Channel {
    
    private String channelName;
    private int ownerId;
    private Set<Integer> idSet;
    private boolean isPrivate;

    /**
     * Constructs a new Channel object with the specified name, owner ID, and privacy status.
     *
     * @param name   The name of the channel.
     * @param id     The ID of the owner of the channel.
     * @param priv   A boolean indicating whether the channel is private or not.
     */
    public Channel(String name, Integer id, boolean priv) {
        channelName = name;
        ownerId = id;
        idSet = new TreeSet<Integer>();
        isPrivate = priv;
    }

    /**
     * Adds a user to the channel by their ID.
     *
     * @param id The ID of the user to be added to the channel.
     */
    public void addUser(Integer id) {
        idSet.add(id);
    }

    /**
     * Removes a user from the channel by their ID.
     *
     * @param id The ID of the user to be removed from the channel.
     */
    public void removeUser(Integer id) {
        idSet.remove(id);
    }

    /**
     * Gets a copy of the set of user IDs currently in the channel.
     *
     * @return A Set containing the user IDs in the channel.
     */
    public Set<Integer> getIdSet() {
        Set<Integer> result = new TreeSet<Integer>();
        result.addAll(idSet);
        return result;
    }

    /**
     * Gets the ID of the owner of the channel.
     *
     * @return The ID of the channel owner.
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * Gets the name of the channel.
     *
     * @return The name of the channel.
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * Checks if the channel is private.
     *
     * @return true if the channel is private, false otherwise.
     */
    public boolean isPrivate() {
        return isPrivate;
    }

}
