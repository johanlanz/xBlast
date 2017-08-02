# xBlast
Projet de prog 2016
To play need : 
- 1 client
- at most 4 players. 

Server can be run on a client. 
- Server
-> in the terminal input java -classpath lib/sq.jar:bin ch.epfl.xblast.server.main x where x is the number of player. 
- Client 
-> in the terminal input java -classpath lib/sq.jar:bin ch.epfl.xblast.client.main x where x is the ip address of the server

Notes : 
By default there is 4 players and the ip address is localhost. 
Might not run on the bigger scale for now because of NAT ( The addresses can't always be tracked back if the other players are not in the router. 
