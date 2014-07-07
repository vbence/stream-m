
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bence
 */
public class MeasuredInputStream extends InputStream {
    
    protected final InputStream base;
    protected final Stream stream;
    
    public MeasuredInputStream(InputStream base, Stream stream) {
        this.base = base;
        this.stream = stream;
    }

    @Override
    public int read() throws IOException {
        byte[] buffer = new byte[1];
        int bytes = read(buffer, 0, 1);
        return buffer[0];
    }
    
    @Override
    public int read(byte[] buffer, int offset, int maxLength) throws IOException {

        // starting time of the transfer
        long transferStart = new Date().getTime();
        
        int numBytes = 0;
        while (numBytes == 0) {

            try {
                // reading data
                numBytes = base.read(buffer, offset, maxLength);
            } catch (SocketTimeoutException e) {
            }
            
        }

        // notification about the transfer
        stream.postEvent(new TransferEvent(this, stream, TransferEvent.STREAM_INPUT, numBytes, new Date().getTime() - transferStart));
        
        return numBytes;
    }
    
}
