/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.roller.plugins.weblogentry.jspwiki;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;

import org.apache.roller.weblogger.business.plugins.entry.WeblogEntryPlugin;
import org.apache.roller.weblogger.pojos.Weblog;
import org.apache.roller.weblogger.pojos.WeblogEntry;
import org.apache.roller.weblogger.ui.core.RollerContext;
import org.apache.wiki.WikiContext;
import org.apache.wiki.WikiEngine;
import org.apache.wiki.WikiPage;

/**
 * Wiki plugin using the JSPWiki WikiEngine. If you want Wiki links to point to your JSPWiki, then
 * edit the jspwiki.properties fiel in Roller's WEB-INF directory and set the jspwiki.baseURL
 * appropriately. For example, if your Wiki is on your localhost in the Servlet Context named 'wiki'
 * then you'd set jspwiki.baseURL like so:<br />
 * <br />
 * jspwiki.baseURL=http://local:8080/wiki/<br />
 *
 * @author David M Johnson
 */
public class WikiPlugin implements WeblogEntryPlugin {

    protected String name = "JSPWiki Syntax";
    public String description = "Allows use of JSPWiki formatting to easily generate HTML. "
            + "See the <a href='http://www.jspwiki.org/Wiki.jsp?page=TextFormattingRules' target='jspwiki'>JSPWiki</a> site.";

    private static Log log
            = LogFactory.getFactory().getInstance(WikiPlugin.class);

    static WikiEngine wikiEngine = null;
    static WikiContext wikiContext = null;
    static WikiPage wikiPage = null;

    public WikiPlugin() {
        log.debug("JSPWiki WikiPlugin instantiated.");
    }

    public String toString() {
        return name;
    }

    /**
     * Initialize the JSPWiki Engine if not done so already. Put the Plugin into the VelocityContex
     * (is this still necessary?-Lance).
     */
    public void init(Weblog weblog) {
        try {
            if (WikiPlugin.wikiEngine == null) {
                Object context = RollerContext.getServletContext();
                if (context != null) {
                    // get wiki engine when running inside webapp
                    ServletContext servletContext = (ServletContext) context;
                    WikiPlugin.wikiEngine = WikiEngine.getInstance(servletContext, null);
                } else {
                    // get wiki engine when running from command-line
                    Properties wikiprops = new Properties();
                    wikiprops.load(getClass().getResourceAsStream("/jspwiki.properties"));
                    WikiPlugin.wikiEngine = new WikiEngine(wikiprops);
                }
            }
            if (WikiPlugin.wikiContext == null && WikiPlugin.wikiEngine != null) {
                WikiPlugin.wikiPage = new WikiPage(wikiEngine, "dummyPage");
                WikiPlugin.wikiContext = new WikiContext(WikiPlugin.wikiEngine, WikiPlugin.wikiPage);
            }
        } catch (Exception e) {
            log.error("ERROR initializing WikiPlugin", e);
        }
    }

    /**
     * Convert an input string that contains text that uses JSPWiki syntax to an output string in
     * HTML format.
     *
     * @param src Input string that uses JSPWiki syntax
     * @return Output string in HTML format.
     */
    public String render(String src) {
        String ret = null;
        try {
            ret = wikiContext.getEngine().getRenderingManager().textToHTML(wikiContext, src);
        } catch (Exception e) {
            log.error("ERROR rendering Wiki text", e);
        }
        return ret;
    }

    public String render(WeblogEntry entry, String str) {
        return render(str);
    }

    /**
     * Return URL to the Wiki page for a weblog entry, CamelCase style
     */
    public String makeCamelCaseWikiLink(WeblogEntry wd, String prefix) {
        StringBuilder sb = new StringBuilder();
        StringTokenizer toker = new StringTokenizer(wd.getAnchor(), "_");
        while (toker.hasMoreTokens()) {
            String token = toker.nextToken();
            sb.append(token.substring(0, 1).toUpperCase());
            sb.append(token.substring(1));
        }
        return wikiEngine.getBaseURL() + "Wiki.jsp?page=" + prefix + sb.toString();
    }

    /**
     * Return URL to the Wiki page for a weblog entry, spacey style
     */
    public String makeSpacedWikiLink(WeblogEntry wd, String prefix) {
        StringBuilder sb = new StringBuilder();
        StringTokenizer toker = new StringTokenizer(wd.getAnchor(), "_");
        while (toker.hasMoreTokens()) {
            sb.append(toker.nextToken());
            if (toker.hasMoreTokens()) {
                sb.append("%20");
            }
        }
        return wikiEngine.getBaseURL() + "Wiki.jsp?page=" + prefix + sb.toString();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return StringEscapeUtils.escapeJavaScript(description);
    }

    public boolean getSkipOnSingleEntry() {
        return false;
    }
}
