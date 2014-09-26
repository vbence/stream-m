package org.czentral.minirtmp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bence
 */
public interface ChunkProcessor {
    
    public boolean alive();
    
    public void processChunk(MessageInfo mi, byte[] buffer, int payloadOffset, int payloadLength);
    
}
