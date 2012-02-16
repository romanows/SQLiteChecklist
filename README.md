# Overview
An eclipse project that is a minimal example of an SQLite-backed   
scrolling list with checkboxes for each list item.  Clicking the   
checkboxes persist the changes to the database.   

<img style="float:right;" src="http://github.com/romanows/SQLiteChecklist/raw/master/screenshot.png" alt="Screenshot of running example app, showing list items and checkboxes" />

This code works, but the major downside is that all database   
operations are performed in the UI-thread.  Each time the checkbox   
state is changed, a new cursor is created, rerunning the list query.   
This can cause slowdowns and "application not responding" warnings.   

Brian Romanowski   
romanows@gmail.com   


# Explanation
The main activity is <tt><a href="http://github.com/romanows/SQLiteChecklist/raw/master/src/com/pwnetics/example/SQLiteChecklistActivity.java">SQLiteChecklistActivity.java</a></tt>.  The list row   
layout is specified (fairly trivially) in <tt><a href="http://github.com/romanows/SQLiteChecklist/raw/master/res/layout/list_text_checkbox.xml">list_text_checkbox.xml</a></tt>.     

The SQLiteChecklistActivity's <code>onCreate()</code> method starts by getting a   
dummy database.  The <code>DatabaseHelper</code> class exists to initialize this    
dummy database on the applications first run.   

The SQL query that populates the list is obtained from a private   
method <code>getCursor()</code> because we will need to re-run this query whenever   
the user toggles a checkbox. This is poor practice, and some   
workarounds are presented in the next section.   

A <code>SimpleCursorAdapter</code> is used to connect the cursor to the list row.   
A <code>ViewBinder</code> handles setting the checkbox to the correct state.  It    
also sets the <code>OnCheckedChangeListener</code> that is used to update the    
database on checkbox change and then requery the cursor.   

One odd thing is that <code>onCheckedChanged()</code> is called when a checkbox    
scrolls out of view. In this case, the checked state of the row's    
checkbox has not changed.  It would be inefficent to query or update   
the database each time a row scrolls off screen, so we use the   
<code>isShown()</code> method to determine whether the checkbox is on-   
screen when the listener is notified.  This <em>seems</em> to provide the   
expected behavior.


# Possible Improvements
The correct way to obtain data from a database is, I believe, to make   
use of content providers.  Failing that, it may be useful to cache the   
checked value for row _id's that are changed by the user in a <code>HashMap</code>.   


# Bugs
Please file bug reports and bug fixes in the GitHub issue tracker.   
Feel free to shoot the author an email if you find this example   
useful, it would make his day.  


# LICENSE
This software is released under the Simplified BSD License, see   
LICENSE.TXT.   
