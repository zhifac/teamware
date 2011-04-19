/*
 *  JavascriptChat.java
 *
 *  Copyright (c) 2006-2011, The University of Sheffield.
 *
 *  This file is part of GATE Teamware (see http://gate.ac.uk/teamware/), 
 *  and is free software, licenced under the GNU Affero General Public License,
 *  Version 3, November 2007 (also included with this distribution as file 
 *  LICENCE-AGPL3.html).
 *
 *  A commercial licence is also available for organisations whose business
 *  models preclude the adoption of open source and is subject to a licence
 *  fee charged by the University of Sheffield. Please contact the GATE team
 *  (see http://gate.ac.uk/g8/contact) if you require a commercial licence.
 *
 *  $Id$
 */
package gleam.executive.webapp.chat;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.util.Logger;

/**
 * @author Joe Walker [joe at getahead dot ltd dot uk]
 */
public class JavascriptChat
{
    /**
     * @param text The new message text to add
     */
    public void addMessage(String text)
    {
        if (text != null && text.trim().length() > 0)
        {
            messages.addFirst(new Message(text));
            while (messages.size() > 10)
            {
                messages.removeLast();
            }
            log.debug("messages size "+ messages.size());
        }

        WebContext wctx = WebContextFactory.get();
        String currentPage = wctx.getCurrentPage();
        log.debug("currentPage " + currentPage);
        ScriptBuffer script = new ScriptBuffer();
        script.appendScript("receiveMessages(")
              .appendData(messages)
              .appendScript(");");

        // Loop over all the users on the current page
        Collection pages = wctx.getScriptSessionsByPage(currentPage);
        for (Iterator it = pages.iterator(); it.hasNext();)
        {
            ScriptSession otherSession = (ScriptSession) it.next();
            otherSession.addScript(script);
            log.debug("added script to session " + script.toString());
        }
    }

    /**
     * The current set of messages
     */
    private LinkedList messages = new LinkedList();

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(JavascriptChat.class);
}
