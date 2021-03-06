/*
 * Copyright 2004 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
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
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */
package sun.management.snmp.jvminstr;

// java imports
//
import java.io.Serializable;

// jmx imports
//
import com.sun.jmx.snmp.SnmpStatusException;

// jdmk imports
//
import com.sun.jmx.snmp.agent.SnmpMib;

import sun.management.snmp.jvmmib.JvmRTClassPathEntryMBean;

/**
 * The class is used for implementing the "JvmRTClassPathEntry" group.
 */
public class JvmRTClassPathEntryImpl implements JvmRTClassPathEntryMBean,
                                                Serializable {

    private final String item;
    private final int index;

    /**
     * Constructor for the "JvmRTClassPathEntry" group.
     */
    public JvmRTClassPathEntryImpl(String item, int index) {
        this.item = validPathElementTC(item);
        this.index = index;
    }

    private String validPathElementTC(String str) {
        return JVM_MANAGEMENT_MIB_IMPL.validPathElementTC(str);
    }

    /**
     * Getter for the "JvmRTClassPathItem" variable.
     */
    public String getJvmRTClassPathItem() throws SnmpStatusException {
        return item;
    }

    /**
     * Getter for the "JvmRTClassPathIndex" variable.
     */
    public Integer getJvmRTClassPathIndex() throws SnmpStatusException {
        return new Integer(index);
    }

}
