/*
 *  Message.java
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

/**
 * A POJO that represents a typed message
 * @author Joe Walker [joe at getahead dot ltd dot uk]
 */
public class Message
{
    /**
     * @param newtext the new message text
     */
    public Message(String newtext)
    {
        text = newtext;

        if (text.length() > 256)
        {
            text = text.substring(0, 256);
        }

    }

    /**
     * @return the message id
     */
    public long getId()
    {
        return id;
    }

    /**
     * @return the message itself
     */
    public String getText()
    {
        return text;
    }

    /**
     * When the message was created
     */
    private long id = System.currentTimeMillis();

    /**
     * The text of the message
     */
    private String text;
}
