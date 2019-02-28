package edu.coursera.distributed;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.*;

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
     * @param ncores The number of cores that are available to your
     *               multi-threaded file server. Using this argument is entirely
     *               optional. You are free to use this information to change
     *               how you create your threads, or ignore it.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs,
                    final int ncores) throws IOException {

        ExecutorService threadPool = Executors.newCachedThreadPool();
        /*
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */

        while (true) {

            // TODO Delete this once you start working on your solution.

            // TODO 1) Use socket.accept to get a Socket object

            /*
             * TODO 2) Now that we have a new Socket object, handle the parsing
             * of the HTTP message on that socket and returning of the requested
             * file in a separate thread. You are free to choose how that new
             * thread is created. Common approaches would include spawning a new
             * Java Thread or using a Java Thread Pool. The steps to complete
             * the handling of HTTP messages are the same as in MiniProject 2,
             * but are repeated below for convenience:
             *
             *   a) Using Socket.getInputStream(), parse the received HTTP
             *      packet. In particular, we are interested in confirming this
             *      message is a GET and parsing out the path to the file we are
             *      GETing. Recall that for GET HTTP packets, the first line of
             *      the received packet will look something like:
             *
             *          GET /path/to/file HTTP/1.1
             *   b) Using the parsed path to the target file, construct an
             *      HTTP reply and write it to Socket.getOutputStream(). If the
             *      file exists, the HTTP reply should be formatted as follows:
             *
             *        HTTP/1.0 200 OK\r\n
             *        Server: FileServer\r\n
             *        \r\n
             *        FILE CONTENTS HERE\r\n
             *
             *      If the specified file does not exist, you should return a
             *      reply with an error code 404 Not Found. This reply should be
             *      formatted as:
             *
             *        HTTP/1.0 404 Not Found\r\n
             *        Server: FileServer\r\n
             *        \r\n
             *
             * If you wish to do so, you are free to re-use code from
             * MiniProject 2 to help with completing this MiniProject.
             */

            Socket clientConn = socket.accept();
//            Thread thread = new Thread(
//                    () -> {
//                        try {
//                            BufferedReader in = new BufferedReader(new InputStreamReader(clientConn.getInputStream()));
//                            String line = in.readLine().trim();
//                            assert line != null;
//                            assert line.startsWith("GET");
//                            String[] requestParts = line.split("\\s+");
//                            String filePath = requestParts[1];
//
//                            String fileContents = fs.readFile(new PCDPPath(filePath));
//
//
//                            PrintWriter op = new PrintWriter(clientConn.getOutputStream());
//                            if (fileContents != null) {
//                                op.write("HTTP/1.0 200 OK\r\n");
//                                op.write("Server: FileServer\r\n");
//                                op.write("\r\n");
//                                op.write(fileContents + "\r\n");
//                            } else {
//                                op.write("HTTP/1.0 404 Not Found\r\n");
//                                op.write("Server: FileServer\r\n");
//                                op.write("\r\n");
//                            }
//                            op.close();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                    }
//            );
//            thread.start();
            Future<Void> task = threadPool.submit(new FileServerAction(clientConn, fs));
//            task.get();

        }

    }

    static class FileServerAction implements Callable<Void> {

        private Socket clientConn;
        private PCDPFilesystem fs;

        public FileServerAction(Socket clientConn, PCDPFilesystem fs) {
            Objects.requireNonNull(clientConn);
            this.clientConn = clientConn;
            this.fs = fs;
        }

        @Override
        public Void call() throws Exception {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientConn.getInputStream()));
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
//            throw new IOException("My exception");
            return null;
        }
    }
}
