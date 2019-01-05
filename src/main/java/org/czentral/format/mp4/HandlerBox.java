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

package org.czentral.format.mp4;

import org.czentral.data.binary.annotation.BitField;
import org.czentral.data.binary.annotation.BitTable;

/**
 *
 * @author Varga Bence
 */
@BitTable(propOrder = {"preDefined"
        , "handlerType"
        , "reserved"
        , "name"})
public class HandlerBox extends FullBox {
    
    @BitField(bitLength = 32)
    public final int preDefined = 0;
    
    private String handlerType;
    
    @BitField(bitLength = 32)
    private final int[] reserved = {0, 0, 0};
    
    // ToDo: implement with null-terminated string
    @BitField(bitLength = 8)
    private final int name = 0;
    
    public HandlerBox() {
        super("hdlr", 0, 0);
    }

    @BitField(bitLength = 32)
    public String getHandlerType() {
        return handlerType;
    }

    public void setHandlerType(String handlerType) {
        this.handlerType = handlerType;
    }

    public String getName() {
        return "";
    }

    public void setName(String name) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    
    public enum HandlerType {
        VIDEO("vide"),
        AUDIO("soun"),
        HINT("hint"),
        META("meta");

        private final String code;

        private HandlerType(final String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }
    }
    
}
