/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.czentral.util.stream;

/**
 *
 * @author bence
 */
public interface Processor {

    /**
     * Checks if this processor has finished its job.
     *
     * @return <code>true</code> if this processor extracted all the data.
     */
    boolean finished();

    /**
     * Process binary data from this buffer.
     *
     * @param buffer The buffer of data.
     * @param offset The offset of the first byte which needs to be processed within the buffer.
     * @param length The number of data bytes need to be processed.
     * @return Number of bytes successfully processed.
     */
    int process(byte[] buffer, int offset, int length);
    
}
