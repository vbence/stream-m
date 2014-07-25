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

/**
 *
 * @author Varga Bence
 */
public class ApplicationNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>ApplicationNoFoundException</code>
     * without detail message.
     */
    public ApplicationNotFoundException() {
    }

    /**
     * Constructs an instance of <code>ApplicationNoFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ApplicationNotFoundException(String msg) {
        super(msg);
    }
}
