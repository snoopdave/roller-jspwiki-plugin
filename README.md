Roller JSP Wiki Plugin 
====
This Roller Weblog Entry plugin allows you to use JSPWiki syntax in Roller blog entries. 
This is the old Roller JSPWiki plugin updated to work with the latest Apache Roller and Apache JSPWiki codebases. 

How to build
---
Since the latest Roller and JSPWiki builds are not in Maven Central, you will have to first build Roller and JSPWiki.
You can do so like this.

First, get and build Apache Roller:

    $ git clone https://github.com/apache/roller.git
    $ cd roller
    $ mvn clean install
    
Next, get and build Apache JSPWiki:

    $ git clone https://github.com/apache/jspwiki.git
    $ cd jspwiki
    $ mvn clean install
    
And finally, get an build this plugin:

    $ git clone https://github.com/snoopdave/roller-jspwiki-plugin.git
    $ cd roller-jspwiki-plugin
    $ mvn clean install
    
Once that is complete you will find the JSPWiki plugin jar in the target directory.

How to install and use with Roller
---

Copy the JSPWiki plugin jar file into the WEB-INF/lib directory of your Roller installation.

Include the JSPWiki plugin in the list of plugins in your roller-custom.properties file:

    plugins.page=\
    org.apache.roller.plugins.weblogentry.jspwiki.WikiPlugin,\
    org.apache.roller.weblogger.business.plugins.entry.TopicTagPlugin,\
    org.apache.roller.weblogger.business.plugins.entry.SmileysPlugin,\
    org.apache.roller.weblogger.business.plugins.entry.ConvertLineBreaksPlugin

Add a JSPWiki properties file at WEB-INF/classes/jspwiki-custom.properties:

    # set this to point to your JSPWiki instance if you have one
    jspwiki.baseURL=http://rollerweblogger.org/wiki/

    # these are needed to run JSPWiki embedded inside Roller, no need to change them!
    jspwiki.pageProvider =org.apache.roller.plugins.weblogentry.jspwiki.RollerPageProvider
    jspwiki.basicAttachmentProvider.storageDir = .
    jspwiki.fileSystemProvider.pageDir = .
    jspwiki.referenceStyle=absolute
    jspwiki.authorizer=org.apache.wiki.auth.authorize.WebContainerAuthorizer
    jspwiki.translatorReader.allowHTML = true
    jspwiki.translatorReader.camelCaseLinks = true
    jspwiki.translatorReader.inlinePattern.1 = *.jpg
    jspwiki.translatorReader.inlinePattern.2 = *.png
    jspwiki.translatorReader.inlinePattern.3 = *.gif
    
Restart Roller and you should see the JSPWiki as one of the options in the New Entry page of Roller.
    
