package org.cis1200;

import java.util.*;

/*
 * Make sure to write your own tests in ServerModelTest.java.
 * The tests we provide for each task are NOT comprehensive!
 */

/**
 * The {@code ServerModel} is the class responsible for tracking the
 * state of the server, including its current users and the channels
 * they are in.
 * This class is used by subclasses of {@link Command} to:
 * 1. handle commands from clients, and
 * 2. handle commands from {@link ServerBackend} to coordinate
 * client connection/disconnection.
 */
public final class ServerModel {

    private TreeMap<String, Channel> channelMap;
    private TreeMap<Integer, String> userMap;

    /**
     * Constructs a {@code ServerModel}. Make sure to initialize any collections
     * used to model the server state here.
     */
    public ServerModel() {
        channelMap = new TreeMap<String, Channel>();
        userMap = new TreeMap<Integer, String>();
    }

    // =========================================================================
    // == Task 2: Basic Server model queries
    // == These functions provide helpful ways to test the state of your model.
    // == You may also use them in later tasks.
    // =========================================================================

    /**
     * Gets the user ID currently associated with the given
     * nickname. The returned ID is -1 if the nickname is not
     * currently in use.
     *
     * @param nickname The nickname for which to get the associated user ID
     * @return The user ID of the user with the argued nickname if
     *         such a user exists, otherwise -1
     */
    public int getUserId(String nickname) {
        Set<Integer> setUsers = userMap.keySet();
        for (Integer i: setUsers) {
            if (userMap.get(i).equals(nickname)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the nickname currently associated with the given user
     * ID. The returned nickname is null if the user ID is not
     * currently in use.
     *
     * @param userId The user ID for which to get the associated
     *               nickname
     * @return The nickname of the user with the argued user ID if
     *         such a user exists, otherwise null
     */
    public String getNickname(int userId) {
        return userMap.get(userId);
    }

    /**
     * Gets a collection of the nicknames of all users who are
     * registered with the server. Changes to the returned collection
     * should not affect the server state.
     * 
     * This method is provided for testing.
     *
     * @return The collection of registered user nicknames
     */
    public Collection<String> getRegisteredUsers() {
        Set<String> copy = new TreeSet<String>();
        copy.addAll(userMap.values());
        return copy;
    }

    /**
     * Gets a collection of the names of all the channels that are
     * present on the server. Changes to the returned collection
     * should not affect the server state.
     * 
     * This method is provided for testing.
     *
     * @return The collection of channel names
     */
    public Collection<String> getChannels() {
        Set<String> result = new TreeSet<String>();
        result.addAll(channelMap.keySet());
        return result;
    }

    /**
     * Gets a collection of the nicknames of all the users in a given
     * channel. The collection is empty if no channel with the given
     * name exists. Modifications to the returned collection should
     * not affect the server state.
     *
     * This method is provided for testing.
     *
     * @param channelName The channel for which to get member nicknames
     * @return A collection of all user nicknames in the channel
     */
    public Collection<String> getUsersInChannel(String channelName) {
        Channel c = channelMap.get(channelName);
        if (c == null) {
            return new TreeSet<String>();
        }
        TreeSet<String> set1 = new TreeSet<String>();
        for (Integer i : c.getIdSet()) {
            set1.add(userMap.get(i));
        }
        return set1;
    }

    /**
     * Gets the nickname of the owner of the given channel. The result
     * is {@code null} if no channel with the given name exists.
     *
     * This method is provided for testing.
     *
     * @param channelName The channel for which to get the owner nickname
     * @return The nickname of the channel owner if such a channel
     *         exists; otherwise, return null
     */
    public String getOwner(String channelName) {
        Channel c = channelMap.get(channelName);
        if (c == null) {
            return null;
        }
        int ownerId = c.getOwnerId();
        return userMap.get(ownerId);
    }

    // ===============================================
    // == Task 3: Connections and Setting Nicknames ==
    // ===============================================

    public Set<String> convertSetToName(Set<Integer> setOfIds) {
        Set<String> setOfNames = new TreeSet<String>();
        for (Integer i : setOfIds) {
            setOfNames.add(userMap.get(i));
        }
        return setOfNames;
    }

    /**
     * This method is automatically called by the backend when a new client
     * connects to the server. It should generate a default nickname with
     * {@link #generateUniqueNickname()}, store the new user's ID and username
     * in your data structures for {@link ServerModel} state, and construct
     * and return a {@link Broadcast} object using
     * {@link Broadcast#connected(String)}}.
     *
     * @param userId The new user's unique ID (automatically created by the
     *               backend)
     * @return The {@link Broadcast} object generated by calling
     *         {@link Broadcast#connected(String)} with the proper parameter
     */
    public Broadcast registerUser(int userId) {
        String nickname = generateUniqueNickname();
        // We have taken care of generating the nickname and returning
        // the Broadcast for you. You need to modify this method to
        // store the new user's ID and username in this model's internal state.
        userMap.put(userId, nickname);
        return Broadcast.connected(nickname);
    }

    /**
     * Helper for {@link #registerUser(int)}. (Nothing to do here.)
     *
     * Generates a unique nickname of the form "UserX", where X is the
     * smallest non-negative integer that yields a unique nickname for a user.
     * 
     * @return The generated nickname
     */
    private String generateUniqueNickname() {
        int suffix = 0;
        String nickname;
        Collection<String> existingUsers = getRegisteredUsers();
        do {
            nickname = "User" + suffix++;
        } while (existingUsers.contains(nickname));
        return nickname;
    }

    /**
     * This method is automatically called by the backend when a client
     * disconnects from the server. This method should take the following
     * actions, not necessarily in this order:
     *
     * (1) All users who shared a channel with the disconnected user should be
     * notified that they left
     * (2) All channels owned by the disconnected user should be deleted
     * (3) The disconnected user's information should be removed from
     * {@link ServerModel}'s internal state
     * (4) Construct and return a {@link Broadcast} object using
     * {@link Broadcast#disconnected(String, Collection)}.
     *
     * @param userId The unique ID of the user to deregister
     * @return The {@link Broadcast} object generated by calling
     *         {@link Broadcast#disconnected(String, Collection)} with the proper
     *         parameters
     */
    public Broadcast deregisterUser(int userId) {
        TreeSet<Integer> set1 = new TreeSet<Integer>();
        List<String> channelsToRemove = new LinkedList<>();
        for (String i : channelMap.keySet()) {
            Channel c = channelMap.get(i);
            if (userId == c.getOwnerId()) {
                channelsToRemove.add(i);
            }
            if (c.getIdSet().contains(userId)) {
                set1.addAll(c.getIdSet());
                c.removeUser(userId);
            }
        }
        for (String channelId : channelsToRemove) {
            channelMap.remove(channelId);
        }
        String userName = userMap.remove(userId);
        set1.remove(userId);
        return Broadcast.disconnected(userName, convertSetToName(set1));
    }

    /**
     * This method is called when a user wants to change their nickname.
     * 
     * @param nickCommand The {@link NicknameCommand} object containing
     *                    all information needed to attempt a nickname change
     * @return The {@link Broadcast} object generated by
     *         {@link Broadcast#okay(Command, Collection)} if the nickname
     *         change is successful. The command should be the original nickCommand
     *         and the collection of recipients should be any clients who
     *         share at least one channel with the sender, including the sender.
     *
     *         If an error occurs, use
     *         {@link Broadcast#error(Command, ServerResponse)} with either:
     *         (1) {@link ServerResponse#INVALID_NAME} if the proposed nickname
     *         is not valid according to
     *         {@link ServerModel#isValidName(String)}
     *         (2) {@link ServerResponse#NAME_ALREADY_IN_USE} if there is
     *         already a user with the proposed nickname
     */
    public Broadcast changeNickname(NicknameCommand nickCommand) {
        if (isValidName(nickCommand.getNewNickname())) {
            if (getRegisteredUsers().contains(nickCommand.getNewNickname())) {
                return Broadcast.error(nickCommand, ServerResponse.NAME_ALREADY_IN_USE);
            }
            userMap.replace(nickCommand.getSenderId(), nickCommand.getNewNickname());
            TreeSet<Integer> set1 = new TreeSet<Integer>();
            for (String i : channelMap.keySet()) {
                Channel c = channelMap.get(i);
                if (c.getIdSet().contains(nickCommand.getSenderId())) {
                    set1.addAll(c.getIdSet());
                }
            }
            return Broadcast.okay(nickCommand, convertSetToName(set1));
        } else {
            return Broadcast.error(nickCommand, ServerResponse.INVALID_NAME);
        }
    }

    /**
     * Determines if a given nickname is valid or invalid (contains at least
     * one alphanumeric character, and no non-alphanumeric characters).
     * (Nothing to do here.)
     * 
     * @param name The channel or nickname string to validate
     * @return true if the string is a valid name
     */
    public static boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        for (char c : name.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    // ===================================
    // == Task 4: Channels and Messages ==
    // ===================================

    /**
     * This method is called when a user wants to create a channel.
     * You can ignore the privacy aspect of this method for task 4, but
     * make sure you come back and implement it in task 5.
     * 
     * @param createCommand The {@link CreateCommand} object containing all
     *                      information needed to attempt channel creation
     * @return The {@link Broadcast} object generated by
     *         {@link Broadcast#okay(Command, Collection)} if the channel
     *         creation is successful. The only recipient should be the new
     *         channel's owner.
     *
     *         If an error occurs, use
     *         {@link Broadcast#error(Command, ServerResponse)} with either:
     *         (1) {@link ServerResponse#INVALID_NAME} if the proposed
     *         channel name is not valid according to
     *         {@link ServerModel#isValidName(String)}
     *         (2) {@link ServerResponse#CHANNEL_ALREADY_EXISTS} if there is
     *         already a channel with the proposed name
     */
    public Broadcast createChannel(CreateCommand createCommand) {
        if (isValidName(createCommand.getChannel())) {
            if (getChannels().contains(createCommand.getChannel())) {
                return Broadcast.error(createCommand, ServerResponse.CHANNEL_ALREADY_EXISTS);
            }
            Channel c = new Channel(
                createCommand.getChannel(), 
                createCommand.getSenderId(), 
                createCommand.isInviteOnly());
            c.addUser(createCommand.getSenderId());
            channelMap.put(createCommand.getChannel(), c);
            Set<String> result = new TreeSet<String>();
            result.add(createCommand.getSender());
            return Broadcast.okay(createCommand, result);
        } else {
            return Broadcast.error(createCommand, ServerResponse.INVALID_NAME);
        }
    }

    /**
     * This method is called when a user wants to join a channel.
     * You can ignore the privacy aspect of this method for task 4, but
     * make sure you come back and implement it in task 5.
     * 
     * @param joinCommand The {@link JoinCommand} object containing all
     *                    information needed for the user's join attempt
     * @return The {@link Broadcast} object generated by
     *         {@link Broadcast#names(Command, Collection, String)} if the user
     *         joins the channel successfully. The recipients should be all
     *         people in the joined channel (including the sender).
     *
     *         If an error occurs, use
     *         {@link Broadcast#error(Command, ServerResponse)} with either:
     *         (1) {@link ServerResponse#NO_SUCH_CHANNEL} if there is no
     *         channel with the specified name
     *         (2) (after Task 5) {@link ServerResponse#JOIN_PRIVATE_CHANNEL} if
     *         the sender is attempting to join a private channel
     */
    public Broadcast joinChannel(JoinCommand joinCommand) {
        String channelName = joinCommand.getChannel();
        Channel c = channelMap.get(channelName);
        if (c == null) {
            return Broadcast.error(joinCommand, ServerResponse.NO_SUCH_CHANNEL); 
        }
        if (c.isPrivate()) {
            return Broadcast.error(joinCommand, ServerResponse.JOIN_PRIVATE_CHANNEL);
        }
        c.addUser(joinCommand.getSenderId());
        return Broadcast.names(joinCommand, convertSetToName(
            c.getIdSet()), userMap.get(c.getOwnerId()));
    }

    /**
     * This method is called when a user wants to send a message to a channel.
     * 
     * @param messageCommand The {@link MessageCommand} object containing all
     *                       information needed for the messaging attempt
     * @return The {@link Broadcast} object generated by
     *         {@link Broadcast#okay(Command, Collection)} if the message
     *         attempt is successful. The recipients should be all clients
     *         in the channel.
     *
     *         If an error occurs, use
     *         {@link Broadcast#error(Command, ServerResponse)} with either:
     *         (1) {@link ServerResponse#NO_SUCH_CHANNEL} if there is no
     *         channel with the specified name
     *         (2) {@link ServerResponse#USER_NOT_IN_CHANNEL} if the sender is
     *         not in the channel they are trying to send the message to
     */
    public Broadcast sendMessage(MessageCommand messageCommand) {
        String channelName = messageCommand.getChannel();
        Channel c = channelMap.get(channelName);
        if (c == null) {
            return Broadcast.error(messageCommand, ServerResponse.NO_SUCH_CHANNEL); 
        }
        if (getUsersInChannel(channelName).contains(messageCommand.getSender())) {
            return Broadcast.okay(messageCommand, convertSetToName(c.getIdSet()));
        } else {
            return Broadcast.error(messageCommand, ServerResponse.USER_NOT_IN_CHANNEL); 
        }
    }

    /**
     * This method is called when a user wants to leave a channel.
     * 
     * @param leaveCommand The {@link LeaveCommand} object containing all
     *                     information about the user's leave attempt
     * @return The {@link Broadcast} object generated by
     *         {@link Broadcast#okay(Command, Collection)} if the user leaves
     *         the channel successfully. The recipients should be all clients
     *         who were in the channel, including the user who left.
     * 
     *         If an error occurs, use
     *         {@link Broadcast#error(Command, ServerResponse)} with either:
     *         (1) {@link ServerResponse#NO_SUCH_CHANNEL} if there is no
     *         channel with the specified name
     *         (2) {@link ServerResponse#USER_NOT_IN_CHANNEL} if the sender is
     *         not in the channel they are trying to leave
     */
    public Broadcast leaveChannel(LeaveCommand leaveCommand) {
        String channelName = leaveCommand.getChannel();
        Channel c = channelMap.get(channelName);
        if (c == null) {
            return Broadcast.error(leaveCommand, ServerResponse.NO_SUCH_CHANNEL); 
        }
        if (getUsersInChannel(channelName).contains(leaveCommand.getSender())) {
            Set<String> recip = convertSetToName(c.getIdSet());
            c.removeUser(leaveCommand.getSenderId());
            return Broadcast.okay(leaveCommand, recip);
        } else {
            return Broadcast.error(leaveCommand, ServerResponse.USER_NOT_IN_CHANNEL); 
        }
    }

    // =============================
    // == Task 5: Channel Privacy ==
    // =============================

    // Go back to createChannel and joinChannel and add
    // all privacy-related functionalities, then delete this when you're done.

    /**
     * This method is called when a channel's owner adds a user to that channel.
     * 
     * @param inviteCommand The {@link InviteCommand} object containing all
     *                      information needed for the invite attempt
     * @return The {@link Broadcast} object generated by
     *         {@link Broadcast#names(Command, Collection, String)} if the user
     *         joins the channel successfully as a result of the invite.
     *         The recipients should be all people in the joined channel
     *         (including the new user).
     *
     *         If an error occurs, use
     *         {@link Broadcast#error(Command, ServerResponse)} with either:
     *         (1) {@link ServerResponse#NO_SUCH_USER} if the invited user
     *         does not exist
     *         (2) {@link ServerResponse#NO_SUCH_CHANNEL} if there is no channel
     *         with the specified name
     *         (3) {@link ServerResponse#INVITE_TO_PUBLIC_CHANNEL} if the
     *         invite refers to a public channel
     *         (4) {@link ServerResponse#USER_NOT_OWNER} if the sender is not
     *         the owner of the channel
     */
    public Broadcast inviteUser(InviteCommand inviteCommand) {
        String channelName = inviteCommand.getChannel();
        Channel c = channelMap.get(channelName);
        if (c == null) {
            return Broadcast.error(inviteCommand, ServerResponse.NO_SUCH_CHANNEL); 
        }
        if (getRegisteredUsers().contains(inviteCommand.getSender())) {
            if (c.isPrivate()) {
                if (inviteCommand.getSenderId() == c.getOwnerId()) {
                    c.addUser(getUserId(inviteCommand.getUserToInvite()));
                    return Broadcast.names(
                        inviteCommand, 
                        convertSetToName(c.getIdSet()), 
                        inviteCommand.getSender());
                } else {
                    return Broadcast.error(inviteCommand, ServerResponse.USER_NOT_OWNER); 
                }
            } else {
                return Broadcast.error(inviteCommand, ServerResponse.INVITE_TO_PUBLIC_CHANNEL); 
            }
        } else {
            return Broadcast.error(inviteCommand, ServerResponse.NO_SUCH_USER); 
        }
    }

    /**
     * This method is called when a channel's owner removes a user from
     * that channel.
     * 
     * @param kickCommand The {@link KickCommand} object containing all
     *                    information needed for the kick attempt
     * @return The {@link Broadcast} object generated by
     *         {@link Broadcast#okay(Command, Collection)} if the user is
     *         successfully kicked from the channel. The recipients should be
     *         all clients who were in the channel, including the user
     *         who was kicked.
     *
     *         If an error occurs, use
     *         {@link Broadcast#error(Command, ServerResponse)} with either:
     *         (1) {@link ServerResponse#NO_SUCH_USER} if the user being kicked
     *         does not exist
     *         (2) {@link ServerResponse#NO_SUCH_CHANNEL} if there is no channel
     *         with the specified name
     *         (3) {@link ServerResponse#USER_NOT_IN_CHANNEL} if the
     *         user being kicked is not a member of the channel
     *         (4) {@link ServerResponse#USER_NOT_OWNER} if the sender is not
     *         the owner of the channel
     */
    public Broadcast kickUser(KickCommand kickCommand) {
        String channelName = kickCommand.getChannel();
        Channel c = channelMap.get(channelName);
        if (c == null) {
            return Broadcast.error(kickCommand, ServerResponse.NO_SUCH_CHANNEL); 
        }
        if (getRegisteredUsers().contains(kickCommand.getSender())) {
            if (convertSetToName(c.getIdSet()).contains(kickCommand.getUserToKick())) {
                if (kickCommand.getSenderId() == c.getOwnerId()) {
                    Set<String> set1 = new TreeSet<String>();
                    set1.addAll(convertSetToName(c.getIdSet()));
                    c.removeUser(getUserId(kickCommand.getUserToKick()));
                    return Broadcast.okay(kickCommand, set1);
                } else {
                    return Broadcast.error(kickCommand, ServerResponse.USER_NOT_OWNER); 
                }
            } else {
                return Broadcast.error(kickCommand, ServerResponse.USER_NOT_IN_CHANNEL); 
            }
        } else {
            return Broadcast.error(kickCommand, ServerResponse.NO_SUCH_USER); 
        }
    }

}
