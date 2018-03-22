#  Java-based chat server & client

1. **What is this project?**
This project was an exercise in two important areas of programming: concurrency and network programming. A server of any
will most likely need to support multiple client connections simultaneously, and of course clients & servers are
oftentimes located on different machines.

2. **What did I learn?**
 1. **Networking in Java:** The Socket class in Java represents a TCP socket. Each socket has an input and output stream.

 2. **Concurrency in Java:** Submitting instances of classes that implement the Runnable interface to an ExecutorService.

 3. **Swing:** While I have worked with Swing in the past, I forgot about how Java handles GUI components. Swing components
are controlled in what is known as the Event Queue, which is a thread dedicated solely to dealing with the graphical
user interface of a program. One should be wary of calling code meant for the "main" thread within the Event Queue
since that may mean essentially "freezing" the GUI. For example, calling `reader.readLine()` (after passing the
output stream to a BufferedReader) within the `actionPerformed()` method will have this effect since `readLine()` will
block until input comes in.