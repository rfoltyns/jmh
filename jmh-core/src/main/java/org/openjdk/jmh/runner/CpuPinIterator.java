/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.openjdk.jmh.runner;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public final class CpuPinIterator implements Iterator<CpuPin> {

    public static int[] NO_PINS = new int[0];

    private final AtomicInteger current = new AtomicInteger();
    private final int[] cpuIds;
    private final CpuPin[] cpuPins;

    public CpuPinIterator(final int[] cpuIdConfigs) {
        cpuIds = cpuIdConfigs;
        cpuPins = cpuIdConfigs.length > 0 ? getPins(cpuIds) : new CpuPin[0];
    }

    public static CpuPinIterator from(final int[] cpus) {
        return new CpuPinIterator(cpus);
    }

    private CpuPin[] getPins(final int[] cpuIds) {

        final CpuPin[] result = new CpuPin[cpuIds.length];
        for (int i = 0; i < cpuIds.length; i++) {
            result[i] = CpuPinFactory.cpuId(cpuIds[i]);
        }
        return result;

    }

    @Override
    public boolean hasNext() {
        return cpuIds.length > 0;
    }

    @Override
    public CpuPin next() {
        if (!hasNext()) {
            return CpuPin.NOOP;
        }
        return cpuPins[Math.max(0, current.getAndIncrement() % cpuIds.length)];
    }

    public CpuPin peekNext() {
        if (!hasNext()) {
            return CpuPin.NOOP;
        }
        return cpuPins[Math.max(0, current.get() + 1 % cpuIds.length)];
    }

    public CpuPin current() {
        if (!hasNext()) {
            return CpuPin.NOOP;
        }
        return cpuPins[Math.max(0, current.get() % cpuIds.length)];
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove not supported");
    }
}
