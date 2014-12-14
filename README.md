# RSS News Reader Application for Android handheld devices v1.4
### This application is created for learning purposes only.

## 0. Update Log

###v1.4 updates

+ ConcurrentModificationException fixed
+ invalid RSS URL error fixed
+ using SharedPreferences to store and retrieve the update interval
+ ExtendableListView remembers last expanded group

###v1.3 updates

+ updates using a service
+ using expandable listview to group feeds by category
+ only mobile phone support, for now

not used classes inside the .legacy package


###v1.15 updates

+ checks if new RSS feed's url starts with 'http://' or 'https://'
+ html parser no longer displays img, a and strong tags.
+ Read More appears instead of complete link.
+ manual update button on action bar (doesn't recreate the list)!!! that is meant to be used when new rss feed is added to the main list

## 1. Introduction
This application implements a simple RSS News feed reader. I started with a lot of auto-generated code and step by step I wrote my own code to fit the problem. A simple example is the implementation of list views. I started with auto-generated type and continue coding until I transformed it to one I liked.

This application has been tested on phone not on tablets, so I don't know what really happens when executed on one ;-).

This application was my introduction to the world of Android devices programming.

## 2. Use cases

1. Choose news feed and read it.
2. Manage the news feeds by choosing which to display.
3. Add more news feeds.
4. Setting the time interval between updates.

## 3. Design
The application consists of several activities:

1. The MainActivity draws an extendable list view of RSS feeds and the DetailsActivity draws the RSS items held by the selected RSS feed.
2. ManageActivity, NewsFeedActivity, SettingsActivity are simple activities, children of the MainActivity.

The application contains two model classes:

1. RssFeed.java represents the information of an RSS feed.
2. RssItem.java represents the information of a single RSS feed item.

On the background an SQLite database is running with the help of DatabaseHanlder.java

Static content is Helper.java and the communication with the servers is facilitated with the RetrieveFeedTask.java

Lastly a Service (UpdateService.java) is implemented to facilitate the update schedule.


## 4. TODO

1. Make it prettier.
2. Make main list of feeds redrawn after every update.
3. Add more RSS feeds at initialization.

## 5. Contact information
*Romanos Trechlis* @r_trechlis

[www.romanostrechlis.com](http://www.romanostrechlis.com)