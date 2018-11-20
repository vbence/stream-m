/*
 * Copyright 2018 Bence Varga
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.czentral.minirtmp;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Varga Bence
 */
public class RTMPCommand {
    
    protected String name;
    
    protected double txid;
    
    protected Map<String, Object> commandObject;
    
    protected List<Object> arguments;

    public RTMPCommand(String name, double txid, Map<String, Object> commandObject, List<Object> arguments) {
        this.name = name;
        this.txid = txid;
        this.commandObject = commandObject;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public double getTxid() {
        return txid;
    }

    public Map<String, Object> getCommandObject() {
        return commandObject;
    }

    public List<Object> getArguments() {
        return arguments;
    }
    
}
