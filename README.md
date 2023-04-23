## Simple Trading Simulator

This repository contains code that models a simple trading exchange. It is written in Java and tested with openjdk version 1.8.0. The exchange allows trading of two fictitious stocks "ABC" and "XYZ" and supports any number of clients. THe price of each stock is dynamically determined by supply and demand in the market. Each client runs in its own thread and the exchange is run in the main thread.

To run the code, compile the java files using: 

    javac *.java -d ./

The class files will be created in a directory called market located in the current folder. Then run the application using:

    java market.TestExchange


