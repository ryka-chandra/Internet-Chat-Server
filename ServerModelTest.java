package org.cis1200;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
//import java.util.Set;


public class ServerModelTest {
    private ServerModel model;

    /**
     * Before each test, we initialize model to be
     * a new ServerModel (with all new, empty state)
     */
    @BeforeEach
    public void setUp() {
        // We initialize a fresh ServerModel for each test
        model = new ServerModel();
    }

    /**
     * Here is an example test that checks the functionality of your
     * changeNickname error handling. Each line has commentary directly above
     * it which you can use as a framework for the remainder of your tests.
     */
    @Test
    public void testInvalidNickname() {
        // A user must be registered before their nickname can be changed,
        // so we first register a user with an arbitrarily chosen id of 0.
        model.registerUser(0);

        // We manually create a Command that appropriately tests the case
        // we are checking. In this case, we create a NicknameCommand whose
        // new Nickname is invalid.
        Command command = new NicknameCommand(0, "User0", "!nv@l!d!");

        // We manually create the expected Broadcast using the Broadcast
        // factory methods. In this case, we create an error Broadcast with
        // our command and an INVALID_NAME error.
        Broadcast expected = Broadcast.error(
                command, ServerResponse.INVALID_NAME
        );

        // We then get the actual Broadcast returned by the method we are
        // trying to test. In this case, we use the updateServerModel method
        // of the NicknameCommand.
        Broadcast actual = command.updateServerModel(model);

        // The first assertEquals call tests whether the method returns
        // the appropriate Broadcast.
        assertEquals(expected, actual, "Broadcast");

        // We also want to test whether the state has been correctly
        // changed.In this case, the state that would be affected is
        // the user's Collection.
        Collection<String> users = model.getRegisteredUsers();

        // We now check to see if our command updated the state
        // appropriately. In this case, we first ensure that no
        // additional users have been added.
        assertEquals(1, users.size(), "Number of registered users");

        // We then check if the username was updated to an invalid value
        // (it should not have been).
        assertTrue(users.contains("User0"), "Old nickname still registered");

        // Finally, we check that the id 0 is still associated with the old,
        // unchanged nickname.
        assertEquals(
                "User0", model.getNickname(0),
                "User with id 0 nickname unchanged"
        );
    }
/*
    @Test
    public void testRegisterUser() {
        // Test basic registration.
        Broadcast expected = Broadcast.connected("User0");
        Broadcast actual = model.registerUser(0);
        assertEquals(expected, actual, "Broadcast");

        // Test registering multiple users.
        expected = Broadcast.connected("User1");
        actual = model.registerUser(1);
        assertEquals(expected, actual, "Broadcast");
        
        // Test registering users with different IDs.
        expected = Broadcast.connected("User2");
        actual = model.registerUser(2);
        assertEquals(expected, actual, "Broadcast");

        // Test registering users with large IDs.
        expected = Broadcast.connected("User1000");
        actual = model.registerUser(1000);
        assertEquals(expected, actual, "Broadcast");

        // Test registering users with zero ID.
        expected = Broadcast.connected("User0");
        actual = model.registerUser(0);
        assertEquals(expected, actual, "Broadcast");

        // Test state of registered users after multiple registrations.
        Collection<String> users = model.getRegisteredUsers();
        assertEquals(5, users.size(), "Number of registered users");

        // Test nicknames are correctly associated with respective IDs.
        assertEquals("User0", model.getNickname(0), "User with id 0 registered");
        assertEquals("User1", model.getNickname(1), "User with id 1 registered");
        assertEquals("User2", model.getNickname(2), "User with id 2 registered");
        assertEquals("User-1", model.getNickname(-1), "User with id -1 registered");
        assertEquals("User1000", model.getNickname(1000), "User with id 1000 registered");
    }

   @Test
   public void testDeregisterUser() {
       // Register users for testing deregistration.
       model.registerUser(0);
       model.registerUser(1);
       model.registerUser(2);


       // Test basic deregistration.
       Broadcast expected = Broadcast.disconnected("User0", Set.of("User1", "User2"));
       Broadcast actual = model.deregisterUser(0);
       assertEquals(expected, actual, "Broadcast");


       // Test deregistration of a user not in any channel.
       expected = Broadcast.disconnected("User2", Set.of("User1"));
       actual = model.deregisterUser(2);
       assertEquals(expected, actual, "Broadcast");


       // Test deregistration of the last user.
       expected = Broadcast.disconnected("User1", Collections.emptySet());
       actual = model.deregisterUser(1);
       assertEquals(expected, actual, "Broadcast");


       // Test deregistration of a non-existent user.
       expected = Broadcast.disconnected("NonExistentUser", Collections.emptySet());
       actual = model.deregisterUser(100);
       assertEquals(expected, actual, "Broadcast");


       // Test state of registered users after deregistrations.
       Collection<String> users = model.getRegisteredUsers();
       assertEquals(0, users.size(), "Number of registered users");
   }


   @Test
   public void testChangeNickname() {
       // Register users for testing nickname changes.
       model.registerUser(0);
       model.registerUser(1);
       model.registerUser(2);


       // Test valid nickname change.
       NicknameCommand validChange = new NicknameCommand(0, "User0", "NewUser");
       Broadcast expected = Broadcast.okay(validChange, Set.of("NewUser", "User1", "User2"));
       Broadcast actual = model.changeNickname(validChange);
       assertEquals(expected, actual, "Broadcast");


       // Test invalid nickname change (contains special characters).
       NicknameCommand invalidChange = new NicknameCommand(1, "User1", "!nv@l!d!");
       expected = Broadcast.error(invalidChange, ServerResponse.INVALID_NAME);
       actual = model.changeNickname(invalidChange);
       assertEquals(expected, actual, "Broadcast");


       // Test nickname change to an existing nickname.
       NicknameCommand existingChange = new NicknameCommand(2, "User2", "User0");
       expected = Broadcast.error(existingChange, ServerResponse.NAME_ALREADY_IN_USE);
       actual = model.changeNickname(existingChange);
       assertEquals(expected, actual, "Broadcast");


       // Test nickname change for a non-existent user.
       NicknameCommand nonExistentChange = new NicknameCommand(100, "NonExistentUser", "NewUser");
       expected = Broadcast.error(nonExistentChange, ServerResponse.NO_SUCH_USER);
       actual = model.changeNickname(nonExistentChange);
       assertEquals(expected, actual, "Broadcast");


       // Test state of registered users after nickname changes.
       Collection<String> users = model.getRegisteredUsers();
       assertEquals(3, users.size(), "Number of registered users");
       assertTrue(users.contains("NewUser"), "NewUser registered");
       assertTrue(users.contains("User1"), "User1 still registered");
       assertTrue(users.contains("User2"), "User2 still registered");
   }


   @Test
   public void testCreateChannelInvalidName() {
       Command command = new CreateCommand(0, "Invalid@Channel", false);
       Broadcast expected = Broadcast.error(command, ServerResponse.INVALID_NAME);
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");


       // Ensure that no channel has been created.
       assertEquals(0, model.getChannels().size(), "Number of channels");
   }


   @Test
   public void testCreateChannelExistingChannel() {
       model.createChannel(new CreateCommand(0, "ExistingChannel", false));
       Command command = new CreateCommand(1, "ExistingChannel", false);
       Broadcast expected = Broadcast.error(command, ServerResponse.CHANNEL_ALREADY_EXISTS);
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");


       // Ensure that no additional channel has been created.
       assertEquals(1, model.getChannels().size(), "Number of channels");
   }


   @Test
   public void testCreateChannelValidName() {
       Command command = new CreateCommand(0, "NewChannel", false);
       Broadcast expected = Broadcast.okay(command, Collections.singleton("User0"));
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");


       // Ensure that the new channel has been created.
       assertEquals(1, model.getChannels().size(), "Number of channels");
       assertTrue(model.getChannels().contains("NewChannel"), "New channel exists");
   }


   @Test
   public void testCreateChannelEmptyName() {
       Command command = new CreateCommand(0, "", false);
       Broadcast expected = Broadcast.error(command, ServerResponse.INVALID_NAME);
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");


       // Ensure that no channel has been created.
       assertEquals(0, model.getChannels().size(), "Number of channels");
   }


   @Test
   public void testCreateChannelNullName() {
       Command command = new CreateCommand(0, null, false);
       Broadcast expected = Broadcast.error(command, ServerResponse.INVALID_NAME);
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");


       // Ensure that no channel has been created.
       assertEquals(0, model.getChannels().size(), "Number of channels");
   }


   @Test
   public void testCreateChannelOwnerNotRegistered() {
       Command command = new CreateCommand(1, "NewChannel", false);
       Broadcast expected = Broadcast.error(command, ServerResponse.NO_SUCH_USER);
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");


       // Ensure that no channel has been created.
       assertEquals(0, model.getChannels().size(), "Number of channels");
   }


   @Test
   public void testCreateChannelValidPrivateChannel() {
       Command command = new CreateCommand(0, "PrivateChannel", true);
       Broadcast expected = Broadcast.okay(command, Collections.singleton("User0"));
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");


       // Ensure that the new private channel has been created.
       assertEquals(1, model.getChannels().size(), "Number of channels");
       assertTrue(model.getChannels().contains("PrivateChannel"), "New private channel exists");
       assertTrue(model.getChannel("PrivateChannel").isPrivate(), "New channel is private");
   }


   @Test
   public void testJoinChannelNoSuchChannel() {
       Command command = new JoinCommand(0, "NonExistentChannel");
       Broadcast expected = Broadcast.error(command, ServerResponse.NO_SUCH_CHANNEL);
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");


       // Ensure that the user is not added to any channel.
       assertTrue(model.getChannels().isEmpty(), "No channels exist");
   }


   @Test
   public void testJoinChannelValid() {
       model.createChannel(new CreateCommand(0, "PublicChannel", false));


       Command command = new JoinCommand(1, "PublicChannel");
       Broadcast expected = Broadcast.names(command, Collections.singleton("User0"), "User0");
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");


       // Ensure that the user is added to the public channel.
       assertTrue(model.getUsersInChannel(
           "PublicChannel").contains("User1"), "User joined public channel");
   }


   @Test
   public void testJoinNonexistentChannel() {
       // Attempt to join a channel that does not exist.
       Command command = new JoinCommand(0, "NonexistentChannel");
       Broadcast expected = Broadcast.error(
               command, ServerResponse.NO_SUCH_CHANNEL
       );
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");
   }


   @Test
   public void testJoinPrivateChannel() {
       // Attempt to join a private channel.
       model.createChannel(new CreateCommand(0, "PrivateChannel", false));
       Command command = new JoinCommand(1, "PrivateChannel");
       Broadcast expected = Broadcast.error(
               command, ServerResponse.JOIN_PRIVATE_CHANNEL
       );
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");
   }


   @Test
   public void testJoinChannelTwice() {
       // Successfully join a channel and then attempt to join it again.
       model.createChannel(new CreateCommand(0, "PublicChannel", false));
       model.joinChannel(new JoinCommand(1, "PublicChannel"));
       Command command = new JoinCommand(1, "PublicChannel");
       Set<String> expectedRecipients = new TreeSet<>(Arrays.asList("User0", "User1"));
       Broadcast expected = Broadcast.names(command, expectedRecipients, "User0");
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");
   }


   @Test
   public void testJoinInvalidChannelName() {
       // Attempt to join a channel with an invalid name.
       Command command = new JoinCommand(0, "!InvalidChannelName");
       Broadcast expected = Broadcast.error(
               command, ServerResponse.NO_SUCH_CHANNEL
       );
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");
   }


   @Test
   public void testSendMessageToNonexistentChannel() {
       // Attempt to send a message to a channel that does not exist.
       Command command = new MessageCommand(0, "NonexistentChannel", "Hello, World!");
       Broadcast expected = Broadcast.error(
               command, ServerResponse.NO_SUCH_CHANNEL
       );
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");
   }


   @Test
   public void testSendMessageNotInChannel() {
       // Attempt to send a message to a channel without being a member.
       model.createChannel(new CreateCommand(0, "PublicChannel", false));
       Command command = new MessageCommand(1, "PublicChannel", "Hello, World!");
       Broadcast expected = Broadcast.error(
               command, ServerResponse.USER_NOT_IN_CHANNEL
       );
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");
   }


   @Test
   public void testSendMessageValid() {
       // Successfully send a message to a valid channel.
       model.createChannel(new CreateCommand(0, "PublicChannel", false));
       model.joinChannel(new JoinCommand(1, "PublicChannel"));
       Command command = new MessageCommand(1, "PublicChannel", "Hello, World!");
       Set<String> expectedRecipients = new TreeSet<>(Arrays.asList("User0", "User1"));
       Broadcast expected = Broadcast.okay(command, expectedRecipients);
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");
   }


   @Test
   public void testSendMessageToInvalidChannelName() {
       // Attempt to send a message to a channel with an invalid name.
       Command command = new MessageCommand(0, "!InvalidChannelName", "Hello, World!");
       Broadcast expected = Broadcast.error(
               command, ServerResponse.NO_SUCH_CHANNEL
       );
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");
   }


   @Test
   public void testSendMessageToPrivateChannel() {
       // Attempt to send a message to a private channel without being invited.
       model.createChannel(new CreateCommand(0, "PrivateChannel", true));
       model.joinChannel(new JoinCommand(1, "PrivateChannel"));
       Command command = new MessageCommand(1, "PrivateChannel", "Hello, World!");
       Broadcast expected = Broadcast.error(
               command, ServerResponse.JOIN_PRIVATE_CHANNEL
       );
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");
   }


   @Test
   public void testSendMessageWithEmptyMessage() {
       // Attempt to send an empty message to a channel.
       model.createChannel(new CreateCommand(0, "EmptyMessageChannel", false));
       model.joinChannel(new JoinCommand(1, "EmptyMessageChannel"));
       Command command = new MessageCommand(1, "EmptyMessageChannel", "");
       Set<String> expectedRecipients = new TreeSet<>(Collections.singletonList("User1"));
       Broadcast expected = Broadcast.okay(command, expectedRecipients);
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");
   }


   @Test
   public void testSendMessageToNonAlphanumericChannel() {
       // Attempt to send a message to a channel with a non-alphanumeric name.
       Command command = new MessageCommand(0, "!@#InvalidChannelName", "Hello, World!");
       Broadcast expected = Broadcast.error(
               command, ServerResponse.NO_SUCH_CHANNEL
       );
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");
   }


   @Test
   public void testLeaveNonexistentChannel() {
       // Attempt to leave a channel that doesn't exist.
       Command command = new LeaveCommand(0, "NonexistentChannel");
       Broadcast expected = Broadcast.error(
               command, ServerResponse.NO_SUCH_CHANNEL
       );
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");
   }


   @Test
   public void testLeaveChannelNotIn() {
       // Attempt to leave a channel that the user is not a member of.
       model.createChannel(new CreateCommand(0, "NewChannel", false));
       Command command = new LeaveCommand(1, "NewChannel");
       Broadcast expected = Broadcast.error(
               command, ServerResponse.USER_NOT_IN_CHANNEL
       );
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");
   }


   @Test
   public void testLeaveChannelSuccessfully() {
       // Successfully leave a channel.
       model.createChannel(new CreateCommand(0, "LeaveChannel", false));
       model.joinChannel(new JoinCommand(1, "LeaveChannel"));
       Command command = new LeaveCommand(1, "LeaveChannel");
       Set<String> expectedRecipients = new TreeSet<>(Collections.singletonList("User1"));
       Broadcast expected = Broadcast.okay(command, expectedRecipients);
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");
   }


   @Test
   public void testLeaveChannelAsOwner() {
       // Attempt to leave a channel where the user is the owner.
       model.createChannel(new CreateCommand(0, "OwnerChannel", false));
       Command command = new LeaveCommand(0, "OwnerChannel");
       Broadcast expected = Broadcast.error(
               command, ServerResponse.USER_NOT_IN_CHANNEL
       );
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");
   }


   @Test
   public void testLeaveChannelWithSingleUser() {
       // Attempt to leave a channel with only one user (the owner).
       model.createChannel(new CreateCommand(0, "SingleUserChannel", false));
       Command command = new LeaveCommand(0, "SingleUserChannel");
       Set<String> expectedRecipients = new TreeSet<>(Collections.singletonList("User0"));
       Broadcast expected = Broadcast.okay(command, expectedRecipients);
       Broadcast actual = command.updateServerModel(model);
       assertEquals(expected, actual, "Broadcast");
   }


   /*
    * Your TAs will be manually grading the tests that you write below this
    * comment block. Don't forget to test the public methods you have added to
    * your ServerModel class, as well as the behavior of the server in
    * different scenarios.
    * You might find it helpful to take a look at the tests we have already
    * provided you with in Task4Test, Task3Test, and Task5Test.
    */
}

