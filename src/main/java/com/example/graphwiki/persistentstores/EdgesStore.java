package com.example.graphwiki.persistentstores;


import com.example.graphwiki.graph.Edge;
import com.example.graphwiki.graph.GraphNode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class EdgesStore {
    // need to store weight, src, dst
    /**
     * Size of a long in bytes.
     */
    static final int LONG_WIDTH = 8;
    /**
     * Size of the weight (of the edge) array.
     */
    static final int WEIGHT_ARR_SIZE = LONG_WIDTH;
    /**
     * Size of the source node's ID array.
     */
    static final int SRC_ARR_SIZE = LONG_WIDTH;
    /**
     * Size of the destination node's ID array.
     */
    static final int DST_ARR_SIZE = LONG_WIDTH;
    /**
     * Offset of the source node's ID array.
     */
    static final int SRC_ARR_OFFSET = 0;
    /**
     * Offset of the destination node's ID array.
     */
    static final int DST_ARR_OFFSET = SRC_ARR_SIZE;
    /**
     * Offset of the edge's weight array.
     */
    static final int WEIGHT_ARR_OFFSET = DST_ARR_OFFSET + DST_ARR_SIZE;
    /**
     * Total size of an entry.
     */
    static final int TOTAL_SIZE = WEIGHT_ARR_OFFSET + WEIGHT_ARR_SIZE;
    /**
     * The random access file we will be storing the information to.
     */
    public RandomAccessFile file;
    /**
     * The file channel associated with the random access file.
     */
    public FileChannel channel;

    public EdgesStore(String rafTitle) throws FileNotFoundException {
        file = new RandomAccessFile("/Users/jamielafarr/Java/365/projects/thirdproject/src/thirdproject/rafs/" + rafTitle + ".txt", "rw");
        channel = file.getChannel();
    }

    /**
     * Method to write the fields on an edge to disk.
     * @param src the src node of the edge.
     * @param e the edge.
     * @throws IOException if file channel does.
     */
    public void diskWrite(GraphNode src, Edge e) throws IOException {
        channel.position(e.id);
        ByteBuffer b = ByteBuffer.allocate(LONG_WIDTH);
        b.putLong(e.src.id);
        b.flip();
        channel.write(b);
        channel.position(DST_ARR_OFFSET);
        b = ByteBuffer.allocate(LONG_WIDTH);
        b.putLong(e.dst.id);
        b.flip();
        channel.write(b);
        b = ByteBuffer.allocate(LONG_WIDTH);
        b.putDouble(e.weight);
        b.flip();
        channel.write(b);
    }

    /**
     * Method to read the fields of an edge on disk.
     * @param id the ID of the edge we are reading.
     * @return an edge with the fields of the edge we are reading from disk.
     * @throws IOException if file channel throws an IO exception.
     */
    public Edge diskRead(long id) throws IOException {
        channel.position(id * TOTAL_SIZE);
        ByteBuffer b = ByteBuffer.allocate(LONG_WIDTH);
        channel.read(b);
        b.flip();
        long src = b.getLong();
        channel.position(channel.position() + LONG_WIDTH);
        b = ByteBuffer.allocate(LONG_WIDTH);
        channel.read(b);
        b.flip();
        long dst = b.getLong();
        channel.position(channel.position() + LONG_WIDTH);
        b = ByteBuffer.allocate(LONG_WIDTH);
        channel.read(b);
        b.flip();
        double weight = b.getDouble();
        Edge e = new Edge();
        e.fakeSrc = src;
        e.fakeDst = dst;
        e.weight = weight;
        return e;
    }
}
