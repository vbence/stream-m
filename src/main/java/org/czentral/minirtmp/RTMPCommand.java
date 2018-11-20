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
