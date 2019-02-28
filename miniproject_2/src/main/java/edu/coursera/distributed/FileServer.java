package edu.coursera.distributed;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
public final class FileServer {
    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs     A proxy filesystem to serve files from. See the PCDPFilesystem
     *               class for more detailed documentation of its usage.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs)
            throws IOException {
        /*
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
        while (true) {

            // TODO Delete this once you start working on your solution.


            // TODO 1) Use socket.accept to get a Socket object
            Socket clientConn = socket.accept();

            BufferedReader in = new BufferedReader(new InputStreamReader(clientConn.getInputStream()));
            // closing the input stream closes the socket
            // To just close the input stream without closing the socket (stop inputs from client) use socket.shutdownInput();
            // you can still send to the output socket
            // To close socket completely use, socket.close();
            String line = in.readLine().trim();
            assert line != null;
            assert line.startsWith("GET");
            String[] requestParts = line.split("\\s+");
            String filePath = requestParts[1];

            String fileContents = fs.readFile(new PCDPPath(filePath));


            // Closing the returned OutputStream will close the associated socket
            PrintWriter op = new PrintWriter(clientConn.getOutputStream());
            if (fileContents != null) {
                op.write("HTTP/1.0 200 OK\r\n");
                op.write("Server: FileServer\r\n");
                op.write("\r\n");
//                op.write("\\r\\n");
                op.write(fileContents + "\r\n");
            } else {
                op.write("HTTP/1.0 404 Not Found\r\n");
                op.write("Server: FileServer\r\n");
                op.write("\r\n");
//                op.write("\\r\\n");
            }
            op.close();


            /*
             * TODO 2) Using Socket.getInputStream(), parse the received HTTP
             * packet. In particular, we are interested in confirming this
             * message is a GET and parsing out the path to the file we are
             * GETing. Recall that for GET HTTP packets, the first line of the
             * received packet will look something like:
             *
             *     GET /path/to/file HTTP/1.1
             */

            /*
             * TODO 3) Using the parsed path to the target file, construct an
             * HTTP reply and write it to Socket.getOutputStream(). If the file
             * exists, the HTTP reply should be formatted as follows:
             *
             *   HTTP/1.0 200 OK\r\n
             *   Server: FileServer\r\n
             *   \r\n
             *   FILE CONTENTS HERE\r\n
             *
             * If the specified file does not exist, you should return a reply
             * with an error code 404 Not Found. This reply should be formatted
             * as:
             *
             *   HTTP/1.0 404 Not Found\r\n
             *   Server: FileServer\r\n
             *   \r\n
             *
             * Don't forget to close the output stream.
             */
        }
    }
}
