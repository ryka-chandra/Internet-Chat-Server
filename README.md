# Internet-Chat-Server

## Overview

This project involves building an internet chat server in Java. The server manages communication between multiple clients, who can join channels, send messages, change nicknames, and manage user interactions. The server maintains the internal state of connected users and channels using a `TreeMap` data structure.

## Features

- **Client-Server Communication**: Clients connect to the server and send commands to interact with other users.
- **Nickname Management**: Users can change their nicknames, and the server broadcasts the change to other users in the same channel.
- **Channel Creation**: Users can create new public or private channels.
- **User Management**: Users can invite others to private channels, kick users from public channels, and leave channels.
- **Server State Management**: The server keeps track of all connected users, their IDs, nicknames, and the channels they are in using a `TreeMap`.

## Project Structure

- **ServerModel.java**: Stores information about all connected users, their IDs, usernames, and channels.
- **ServerMain.java**: The main class to start the server and handle client connections.
- **Client**: Represents a user connected to the server. Clients can send commands to interact with the server.

## Data Structures

- **TreeMap**: Used to store user data, with user IDs as keys and nicknames as values. This ensures that nickname changes do not affect user identification.

## How It Works

1. **Starting the Server**: Run `ServerMain.java` to start the server.
2. **Client Connection**: Users connect to the server by launching the client application (`hw07-client.jar`).
3. **User Interaction**: Clients can:
   - Change their nickname
   - Create new channels (public or private)
   - Invite users to private channels
   - Leave a channel
   - Kick users from a public channel
4. **Server State Management**: The server uses `ServerModel.java` to keep track of all active users and channels.

## Running the Project

1. **Start the Server**:
   - Open your Java IDE (e.g., IntelliJ IDEA).
   - Run `ServerMain.java` to start the chat server.

2. **Launch Clients**:
   - Right-click on `hw07-client.jar` and select "Run" to start a client instance.
   - To launch multiple client instances, use the command line:
     ```bash
     java -jar path-to-client.jar
     ```

## Example Commands

- **Change Nickname**: `/nick new_nickname`
- **Create Channel**: `/create channel_name`
- **Invite to Private Channel**: `/invite user_nickname channel_name`
- **Leave Channel**: `/leave channel_name`
- **Kick User**: `/kick user_nickname channel_name`

## Future Improvements

- **Enhanced Security**: Implement authentication and encryption for secure communication.
- **User Interface**: Develop a graphical user interface (GUI) for a more user-friendly experience.
- **Logging**: Add server-side logging for better monitoring and debugging.
