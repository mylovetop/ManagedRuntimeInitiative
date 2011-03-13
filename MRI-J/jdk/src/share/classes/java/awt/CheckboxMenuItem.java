/*
 * Copyright 1995-2006 Sun Microsystems, Inc.  All Rights Reserved.
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
package java.awt;

import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.event.*;
import java.util.EventListener;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import javax.accessibility.*;


/**
 * This class represents a check box that can be included in a menu.
 * Selecting the check box in the menu changes its state from
 * "on" to "off" or from "off" to "on."
 * <p>
 * The following picture depicts a menu which contains an instance
 * of <code>CheckBoxMenuItem</code>:
 * <p>
 * <img src="doc-files/MenuBar-1.gif"
 * alt="Menu labeled Examples, containing items Basic, Simple, Check, and More Examples. The Check item is a CheckBoxMenuItem instance, in the off state."
 * ALIGN=center HSPACE=10 VSPACE=7>
 * <p>
 * The item labeled <code>Check</code> shows a check box menu item
 * in its "off" state.
 * <p>
 * When a check box menu item is selected, AWT sends an item event to
 * the item. Since the event is an instance of <code>ItemEvent</code>,
 * the <code>processEvent</code> method examines the event and passes
 * it along to <code>processItemEvent</code>. The latter method redirects
 * the event to any <code>ItemListener</code> objects that have
 * registered an interest in item events generated by this menu item.
 *
 * @author      Sami Shaio
 * @see         java.awt.event.ItemEvent
 * @see         java.awt.event.ItemListener
 * @since       JDK1.0
 */
public class CheckboxMenuItem extends MenuItem implements ItemSelectable, Accessible {

    static {
        /* ensure that the necessary native libraries are loaded */
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
    }

   /**
    * The state of a checkbox menu item
    * @serial
    * @see #getState()
    * @see #setState(boolean)
    */
    boolean state = false;

    transient ItemListener itemListener;

    private static final String base = "chkmenuitem";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID
     */
     private static final long serialVersionUID = 6190621106981774043L;

    /**
     * Create a check box menu item with an empty label.
     * The item's state is initially set to "off."
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @since   JDK1.1
     */
    public CheckboxMenuItem() throws HeadlessException {
        this("", false);
    }

    /**
     * Create a check box menu item with the specified label.
     * The item's state is initially set to "off."

     * @param     label   a string label for the check box menu item,
     *                or <code>null</code> for an unlabeled menu item.
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public CheckboxMenuItem(String label) throws HeadlessException {
        this(label, false);
    }

    /**
     * Create a check box menu item with the specified label and state.
     * @param      label   a string label for the check box menu item,
     *                     or <code>null</code> for an unlabeled menu item.
     * @param      state   the initial state of the menu item, where
     *                     <code>true</code> indicates "on" and
     *                     <code>false</code> indicates "off."
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @since      JDK1.1
     */
    public CheckboxMenuItem(String label, boolean state)
        throws HeadlessException {
        super(label);
        this.state = state;
    }

    /**
     * Construct a name for this MenuComponent.  Called by getName() when
     * the name is null.
     */
    String constructComponentName() {
        synchronized (CheckboxMenuItem.class) {
            return base + nameCounter++;
        }
    }

    /**
     * Creates the peer of the checkbox item.  This peer allows us to
     * change the look of the checkbox item without changing its
     * functionality.
     * Most applications do not call this method directly.
     * @see     java.awt.Toolkit#createCheckboxMenuItem(java.awt.CheckboxMenuItem)
     * @see     java.awt.Component#getToolkit()
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
            if (peer == null)
                peer = Toolkit.getDefaultToolkit().createCheckboxMenuItem(this);
            super.addNotify();
        }
    }

    /**
     * Determines whether the state of this check box menu item
     * is "on" or "off."
     *
     * @return      the state of this check box menu item, where
     *                     <code>true</code> indicates "on" and
     *                     <code>false</code> indicates "off"
     * @see        #setState
     */
    public boolean getState() {
        return state;
    }

    /**
     * Sets this check box menu item to the specifed state.
     * The boolean value <code>true</code> indicates "on" while
     * <code>false</code> indicates "off."
     *
     * <p>Note that this method should be primarily used to
     * initialize the state of the check box menu item.
     * Programmatically setting the state of the check box
     * menu item will <i>not</i> trigger
     * an <code>ItemEvent</code>.  The only way to trigger an
     * <code>ItemEvent</code> is by user interaction.
     *
     * @param      b   <code>true</code> if the check box
     *             menu item is on, otherwise <code>false</code>
     * @see        #getState
     */
    public synchronized void setState(boolean b) {
        state = b;
        CheckboxMenuItemPeer peer = (CheckboxMenuItemPeer)this.peer;
        if (peer != null) {
            peer.setState(b);
        }
    }

    /**
     * Returns the an array (length 1) containing the checkbox menu item
     * label or null if the checkbox is not selected.
     * @see ItemSelectable
     */
    public synchronized Object[] getSelectedObjects() {
        if (state) {
            Object[] items = new Object[1];
            items[0] = label;
            return items;
        }
        return null;
    }

    /**
     * Adds the specified item listener to receive item events from
     * this check box menu item.  Item events are sent in response to user
     * actions, but not in response to calls to setState().
     * If l is null, no exception is thrown and no action is performed.
     * <p>Refer to <a href="doc-files/AWTThreadIssues.html#ListenersThreads"
     * >AWT Threading Issues</a> for details on AWT's threading model.
     *
     * @param         l the item listener
     * @see           #removeItemListener
     * @see           #getItemListeners
     * @see           #setState
     * @see           java.awt.event.ItemEvent
     * @see           java.awt.event.ItemListener
     * @since         JDK1.1
     */
    public synchronized void addItemListener(ItemListener l) {
        if (l == null) {
            return;
        }
        itemListener = AWTEventMulticaster.add(itemListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified item listener so that it no longer receives
     * item events from this check box menu item.
     * If l is null, no exception is thrown and no action is performed.
     * <p>Refer to <a href="doc-files/AWTThreadIssues.html#ListenersThreads"
     * >AWT Threading Issues</a> for details on AWT's threading model.
     *
     * @param         l the item listener
     * @see           #addItemListener
     * @see           #getItemListeners
     * @see           java.awt.event.ItemEvent
     * @see           java.awt.event.ItemListener
     * @since         JDK1.1
     */
    public synchronized void removeItemListener(ItemListener l) {
        if (l == null) {
            return;
        }
        itemListener = AWTEventMulticaster.remove(itemListener, l);
    }

    /**
     * Returns an array of all the item listeners
     * registered on this checkbox menuitem.
     *
     * @return all of this checkbox menuitem's <code>ItemListener</code>s
     *         or an empty array if no item
     *         listeners are currently registered
     *
     * @see           #addItemListener
     * @see           #removeItemListener
     * @see           java.awt.event.ItemEvent
     * @see           java.awt.event.ItemListener
     * @since 1.4
     */
    public synchronized ItemListener[] getItemListeners() {
        return (ItemListener[])(getListeners(ItemListener.class));
    }

    /**
     * Returns an array of all the objects currently registered
     * as <code><em>Foo</em>Listener</code>s
     * upon this <code>CheckboxMenuItem</code>.
     * <code><em>Foo</em>Listener</code>s are registered using the
     * <code>add<em>Foo</em>Listener</code> method.
     *
     * <p>
     * You can specify the <code>listenerType</code> argument
     * with a class literal, such as
     * <code><em>Foo</em>Listener.class</code>.
     * For example, you can query a
     * <code>CheckboxMenuItem</code> <code>c</code>
     * for its item listeners with the following code:
     *
     * <pre>ItemListener[] ils = (ItemListener[])(c.getListeners(ItemListener.class));</pre>
     *
     * If no such listeners exist, this method returns an empty array.
     *
     * @param listenerType the type of listeners requested; this parameter
     *          should specify an interface that descends from
     *          <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     *          <code><em>Foo</em>Listener</code>s on this checkbox menuitem,
     *          or an empty array if no such
     *          listeners have been added
     * @exception ClassCastException if <code>listenerType</code>
     *          doesn't specify a class or interface that implements
     *          <code>java.util.EventListener</code>
     *
     * @see #getItemListeners
     * @since 1.3
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        EventListener l = null;
        if  (listenerType == ItemListener.class) {
            l = itemListener;
        } else {
            return super.getListeners(listenerType);
        }
        return AWTEventMulticaster.getListeners(l, listenerType);
    }

    // REMIND: remove when filtering is done at lower level
    boolean eventEnabled(AWTEvent e) {
        if (e.id == ItemEvent.ITEM_STATE_CHANGED) {
            if ((eventMask & AWTEvent.ITEM_EVENT_MASK) != 0 ||
                itemListener != null) {
                return true;
            }
            return false;
        }
        return super.eventEnabled(e);
    }

    /**
     * Processes events on this check box menu item.
     * If the event is an instance of <code>ItemEvent</code>,
     * this method invokes the <code>processItemEvent</code> method.
     * If the event is not an item event,
     * it invokes <code>processEvent</code> on the superclass.
     * <p>
     * Check box menu items currently support only item events.
     * <p>Note that if the event parameter is <code>null</code>
     * the behavior is unspecified and may result in an
     * exception.
     *
     * @param        e the event
     * @see          java.awt.event.ItemEvent
     * @see          #processItemEvent
     * @since        JDK1.1
     */
    protected void processEvent(AWTEvent e) {
        if (e instanceof ItemEvent) {
            processItemEvent((ItemEvent)e);
            return;
        }
        super.processEvent(e);
    }

    /**
     * Processes item events occurring on this check box menu item by
     * dispatching them to any registered <code>ItemListener</code> objects.
     * <p>
     * This method is not called unless item events are
     * enabled for this menu item. Item events are enabled
     * when one of the following occurs:
     * <p><ul>
     * <li>An <code>ItemListener</code> object is registered
     * via <code>addItemListener</code>.
     * <li>Item events are enabled via <code>enableEvents</code>.
     * </ul>
     * <p>Note that if the event parameter is <code>null</code>
     * the behavior is unspecified and may result in an
     * exception.
     *
     * @param       e the item event
     * @see         java.awt.event.ItemEvent
     * @see         java.awt.event.ItemListener
     * @see         #addItemListener
     * @see         java.awt.MenuItem#enableEvents
     * @since       JDK1.1
     */
    protected void processItemEvent(ItemEvent e) {
        ItemListener listener = itemListener;
        if (listener != null) {
            listener.itemStateChanged(e);
        }
    }

    /*
     * Post an ItemEvent and toggle state.
     */
    void doMenuEvent(long when, int modifiers) {
        setState(!state);
        Toolkit.getEventQueue().postEvent(
            new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED,
                          getLabel(),
                          state ? ItemEvent.SELECTED :
                                  ItemEvent.DESELECTED));
    }

    /**
     * Returns a string representing the state of this
     * <code>CheckBoxMenuItem</code>. This
     * method is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not be
     * <code>null</code>.
     *
     * @return     the parameter string of this check box menu item
     */
    public String paramString() {
        return super.paramString() + ",state=" + state;
    }

    /* Serialization support.
     */

    /*
     * Serial Data Version
     * @serial
     */
    private int checkboxMenuItemSerializedDataVersion = 1;

    /**
     * Writes default serializable fields to stream.  Writes
     * a list of serializable <code>ItemListeners</code>
     * as optional data.  The non-serializable
     * <code>ItemListeners</code> are detected and
     * no attempt is made to serialize them.
     *
     * @param s the <code>ObjectOutputStream</code> to write
     * @serialData <code>null</code> terminated sequence of
     *  0 or more pairs; the pair consists of a <code>String</code>
     *  and an <code>Object</code>; the <code>String</code> indicates
     *  the type of object and is one of the following:
     *  <code>itemListenerK</code> indicating an
     *    <code>ItemListener</code> object
     *
     * @see AWTEventMulticaster#save(ObjectOutputStream, String, EventListener)
     * @see java.awt.Component#itemListenerK
     * @see #readObject(ObjectInputStream)
     */
    private void writeObject(ObjectOutputStream s)
      throws java.io.IOException
    {
      s.defaultWriteObject();

      AWTEventMulticaster.save(s, itemListenerK, itemListener);
      s.writeObject(null);
    }

    /*
     * Reads the <code>ObjectInputStream</code> and if it
     * isn't <code>null</code> adds a listener to receive
     * item events fired by the <code>Checkbox</code> menu item.
     * Unrecognized keys or values will be ignored.
     *
     * @param s the <code>ObjectInputStream</code> to read
     * @serial
     * @see removeActionListener()
     * @see addActionListener()
     * @see #writeObject
     */
    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException
    {
      s.defaultReadObject();

      Object keyOrNull;
      while(null != (keyOrNull = s.readObject())) {
        String key = ((String)keyOrNull).intern();

        if (itemListenerK == key)
          addItemListener((ItemListener)(s.readObject()));

        else // skip value for unrecognized key
          s.readObject();
      }
    }

    /**
     * Initialize JNI field and method IDs
     */
    private static native void initIDs();


/////////////////
// Accessibility support
////////////////

    /**
     * Gets the AccessibleContext associated with this CheckboxMenuItem.
     * For checkbox menu items, the AccessibleContext takes the
     * form of an AccessibleAWTCheckboxMenuItem.
     * A new AccessibleAWTCheckboxMenuItem is created if necessary.
     *
     * @return an AccessibleAWTCheckboxMenuItem that serves as the
     *         AccessibleContext of this CheckboxMenuItem
     * @since 1.3
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTCheckboxMenuItem();
        }
        return accessibleContext;
    }

    /**
     * Inner class of CheckboxMenuItem used to provide default support for
     * accessibility.  This class is not meant to be used directly by
     * application developers, but is instead meant only to be
     * subclassed by menu component developers.
     * <p>
     * This class implements accessibility support for the
     * <code>CheckboxMenuItem</code> class.  It provides an implementation
     * of the Java Accessibility API appropriate to checkbox menu item
     * user-interface elements.
     * @since 1.3
     */
    protected class AccessibleAWTCheckboxMenuItem extends AccessibleAWTMenuItem
        implements AccessibleAction, AccessibleValue
    {
        /*
         * JDK 1.3 serialVersionUID
         */
        private static final long serialVersionUID = -1122642964303476L;

        /**
         * Get the AccessibleAction associated with this object.  In the
         * implementation of the Java Accessibility API for this class,
         * return this object, which is responsible for implementing the
         * AccessibleAction interface on behalf of itself.
         *
         * @return this object
         */
        public AccessibleAction getAccessibleAction() {
            return this;
        }

        /**
         * Get the AccessibleValue associated with this object.  In the
         * implementation of the Java Accessibility API for this class,
         * return this object, which is responsible for implementing the
         * AccessibleValue interface on behalf of itself.
         *
         * @return this object
         */
        public AccessibleValue getAccessibleValue() {
            return this;
        }

        /**
         * Returns the number of Actions available in this object.
         * If there is more than one, the first one is the "default"
         * action.
         *
         * @return the number of Actions in this object
         */
        public int getAccessibleActionCount() {
            return 0;  //  To be fully implemented in a future release
        }

        /**
         * Return a description of the specified action of the object.
         *
         * @param i zero-based index of the actions
         */
        public String getAccessibleActionDescription(int i) {
            return null;  //  To be fully implemented in a future release
        }

        /**
         * Perform the specified Action on the object
         *
         * @param i zero-based index of actions
         * @return true if the action was performed; otherwise false.
         */
        public boolean doAccessibleAction(int i) {
            return false;    //  To be fully implemented in a future release
        }

        /**
         * Get the value of this object as a Number.  If the value has not been
         * set, the return value will be null.
         *
         * @return value of the object
         * @see #setCurrentAccessibleValue
         */
        public Number getCurrentAccessibleValue() {
            return null;  //  To be fully implemented in a future release
        }

        /**
         * Set the value of this object as a Number.
         *
         * @return true if the value was set; otherwise false
         * @see #getCurrentAccessibleValue
         */
        public boolean setCurrentAccessibleValue(Number n) {
            return false;  //  To be fully implemented in a future release
        }

        /**
         * Get the minimum value of this object as a Number.
         *
         * @return Minimum value of the object; null if this object does not
         * have a minimum value
         * @see #getMaximumAccessibleValue
         */
        public Number getMinimumAccessibleValue() {
            return null;  //  To be fully implemented in a future release
        }

        /**
         * Get the maximum value of this object as a Number.
         *
         * @return Maximum value of the object; null if this object does not
         * have a maximum value
         * @see #getMinimumAccessibleValue
         */
        public Number getMaximumAccessibleValue() {
            return null;  //  To be fully implemented in a future release
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.CHECK_BOX;
        }

    } // class AccessibleAWTMenuItem

}