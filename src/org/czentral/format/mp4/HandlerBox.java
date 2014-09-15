/*
 * This file is part of the "stream-m" software. An HTML5 compatible live
 * streaming and broadcasting server.
 * Copyright (C) 2014 Varga Bence
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
