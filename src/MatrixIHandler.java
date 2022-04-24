import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MatrixIHandler implements IHandler {
    private Matrix matrix;
    private Index start,end;

    /*
    to clear data members between clients (if same instance is shared among clients/tasks)
     */
    private void resetParams(){
        this.matrix = null;
        this.start = null;
        this.end = null;
    }

    @Override
    public void handle(InputStream fromClient, OutputStream toClient)
            throws IOException, ClassNotFoundException {

        // In order to read either objects or primitive types we can use ObjectInputStream
        ObjectInputStream objectInputStream = new ObjectInputStream(fromClient);
        // In order to write either objects or primitive types we can use ObjectOutputStream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(toClient);
        this.resetParams(); // in order to use same handler between tasks/clients

        boolean doWork = true;
        while(doWork){
            /*
             Use switch-case in order to get commands from client
             - client sends a 2D array
             - client send start index
             - client send end index
             - client sends an index and wished to get neighbors
             - client sends an index and wished to get reachable indices
             */

            // client send a verbal command
            switch(objectInputStream.readObject().toString()){
                case "matrix":{
                    // client will send a 2d array. handler will create a new Matrix object
                    int[][] primitiveMatrix = (int[][])objectInputStream.readObject();
                    System.out.println("Server: Got 2d array from client");
                    this.matrix = new Matrix(primitiveMatrix);
                    this.matrix.printMatrix();
                    break;
                }

                case "neighbors":{
                    Index findNeighborsIndex = (Index)objectInputStream.readObject();
                    List<Index> neighbors = new ArrayList<>();
                    if(this.matrix!=null){
                        neighbors.addAll(this.matrix.getNeighbors(findNeighborsIndex));
                        // print result in server
                        System.out.println("neighbors of " + findNeighborsIndex + ": " + neighbors);
                        // send to socket's OutputStream
                        objectOutputStream.writeObject(neighbors);
                    }
                    break;
                }

                case "reachables":{
                    Index findNeighborsIndex = (Index)objectInputStream.readObject();
                    List<Index> reachables = new ArrayList<>();
                    if(this.matrix!=null){
                        reachables.addAll(this.matrix.getReachable(findNeighborsIndex));
                        // print result in server
                        System.out.println("reachables of " + findNeighborsIndex + ": " + reachables);
                        // send to socket's OutputStream
                        objectOutputStream.writeObject(reachables);
                    }
                    break;
                }

                case "start index":{
                    this.start = (Index)objectInputStream.readObject();
                    break;
                }

                case "end index":{
                    this.end = (Index)objectInputStream.readObject();
                    break;
                }

                case "getAllSCC":{
                    List<HashSet<Index>> setOfSCCs = new ArrayList<>();
                    if(this.matrix != null){
                        setOfSCCs.addAll(this.matrix.getAllSCCs());
                    }
                    System.out.println("List of SCCs:\n" + setOfSCCs);
                    objectOutputStream.writeObject(setOfSCCs);
                    break;
                }

                case "stop":{
                    doWork = false;
                    break;
                }
            }
        }
    }
}