I have implement an extended TFTP (Trivial File Transfer Protocol)
server and client. The communication between the server and the client(s) was
performed using a binary communication protocol, which will support the upload,
download and lookup of files. 

The implementation of the server was based on the Reactor and Thread-Per-Client
(TPC) servers . The servers, do not support bi-directional message passing.
Any time the server receives a message from a client it can replay back
to the client itself. But what if we want to send messages between clients, or broadcast
an announcment to all the clients?
The first part of the project was to replace some
of the current interfaces with new interfaces that will allow such a case. Note that this
part changes the servers pattern and must not know the specific protocol it is running.
The current server pattern also works that way (Generics and interfaces).

Once the server implementation has been extended i had implemented an
example protocol. The extended TFTP Specification The TFTP (Trivial File Transfer
Protocol) allows users to upload and download files from a given server. 
The extended version require a user to perform a passwordless server login as well as enable the
server to communicate broadcast messages to all users and support for directory listings.
It is a binary protocol (non-text-base). The commands are defined by an opcode that describes
the incoming command. For each command, a different length of data needs to be read
according to it’s specifications. 
