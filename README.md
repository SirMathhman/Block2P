# Block2P
In real-time peer to peer applications using blockchain technology.

## The Problem
Nowadays, communication between two users via a server is extremely difficult. When a user wants to a make a change to the server contents, the server has to tell all the connected clients to update if it is in real-time. Additionally, a server may face downtime and other concerns.

### Peer to Peer Networks
But if you build a peer to peer network, then the users can connect directly with no regulation between two parties, AKA the server.

### But what about Blockchains? Aren't they used for cryptography?
Well, yes. However, blockchains are also effective for keeping track of user changes. But how does this apply to blockchain technology? It's simple - every user, or peer, contains a set of the data shared amongst the whole network.

 If a peer wishes to make a change to that data, then the peer will send out it's change to all of its connected peers. That change will be then recorded by every peer connected in their internal ledgers. Then, the peer can group certain changes together once they are complete or satisfied and create a block, which if version control is used, such as Git, can be used to create a commit.

 If a peer wishes to join a network, then it simply has to connect to a single peer. Any updates that are broadcasted by peers within the network will eventually be relayed to the peer connecting.

### Wouldn't it be more effective to use a Server?
It depends on the application, and specifically what task you wish to do.

**Benefits:**

1) Decentralization - not having a central server makes activity difficult to regulate. It also makes the network less susceptible to failure. Additionally, if a server requires maintenance, valuable downtime is spent working on repairs.

2) Cost - using a server network sometimes requires you to have a location to keep the server. If you don't have access for a location for your server, or you have limited funds, then Block2P is perfect for you.

3) Ease of use - Starter code for using Block2P is less than 100 lines to complete a simple program.

4) Location - other than cost, having a lack of location can also benefit peers in the sense that the update time will only be limited by the peer's internet speed, and not by the presence of the server on the Internet.

## Usage
### Requirements
- Gradle, version 4.0 minimum
- IntelliJ IDEA, Eclipse, or any other IDE that builds Gradle projects
### Cloning
```
$ git clone https://github.com/Meticuli-Technologies/Nexus.git
```
