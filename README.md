# Overview
This is an example Android Eclipse project that creates an SQLite-backed scrolling list with checkboxes.
Clicking the checkboxes persist the changes to the database. 

<img style="float:right;" src="http://github.com/romanows/SQLiteChecklist/raw/master/screenshot.png" alt="Screenshot of running example app, showing list items and checkboxes" />

Clicking a list row either by touch or by using the trackpad will cause the checkbox row's "is_checked" field to toggle in the database.
Changes in the database are then propagated to the view, and the checkbox will checked or not as appropriate.
This design strongly separates the model and view, gaining reliability with a latency cost.

Some code is dedicated to maintaining the position of a non-touch list item selector after it is used to click a list row.

More than anything, this code is a starting point for creating new custom, database-backed lists.
It makes use of ListFragments, ContentProviders, which helps load data nicely and in the background.
These features require the android compatability library when used on early versions of Android, which is included for ease of running the example.


Brian Romanowski   
romanows@gmail.com   


# Possible Improvements
<ul>
  <li>On orientation/configuration change, ensure that the currently viewed list item is viewable in the new view.</li>
  <li>Optionally: only allow checking a checkbox by touch if the user touches the checkbox, while still allowing the user to check a checkbox using the trackball to click on a list item.</li>
</ul>


# Bugs
Please file bug reports and bug fixes in the GitHub issue tracker.
Feel free to shoot the author an email if you find this example useful, it would make his day.


# LICENSE
The android-support-v4.jar library is copyrighted by The Android Open Source Project and is re-distributed under the terms of the Apache 2.0 license, see LICENSE.txt.
Everything else is released under the Simplified BSD License, see LICENSE.TXT.   
