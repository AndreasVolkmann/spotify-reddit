# Spotify + Reddit = Spottit
![Spottit Logo](./spottit-logo-drawn-cropped.png?raw=true "Spottit Logo")
[![Build Status](https://travis-ci.com/AndreasVolkmann/spotify-reddit.svg?branch=master)](https://travis-ci.com/AndreasVolkmann/spotify-reddit)
[![GitHub release](https://img.shields.io/badge/Version-0.6.0-blue.svg)](https://github.com/AndreasVolkmann/spotify-reddit/releases/)
[![Deploy](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

Dynamic Spotify playlists, based on Reddit. Easily set up your own subreddit playlist.  

Follow [Reddit Electronic Music's Spotify](https://open.spotify.com/user/8j1md7p5ntsj62xu2yeapolfi?si=qj_fqiZYTYeXKUzN9mUxCA)
 to see some of the generated playlists.

### Table of Contents  
* [How to use](#how-to-use) 
* [Configuration](#configuration) 
* [Deployment](#deployment) 

### How to use
To use the program, define your own `config.yml` and run the program with `-c ${PATH_TO_YOUR_CONFIG.YML}`.
The following assumes that your config is called config.yml and is located in the same directory as the jar.

Before running for the first time, you need to authorize the application. 

Run with `-ma`: `java -jar spottit-${version}.jar -c config.yml -ma`

This will obtain a refresh token, so that you do not need to authorize again. 

Once this is done, you can omit the `-ma`. The application should now update the playlists.
 
It is also possible to specify the refresh token via an environment variable called `REFRESH_TOKEN`. 


### Configuration
To customize the application, edit the 
[example_config.yml](https://github.com/AndreasVolkmann/spotify-reddit/blob/master/example_config.yml)
 and fill in your own information.

```yaml
#Spotify user ID
userId: YOUR_USER_ID

# Add an entry for each playlist you want to update
playlists:
    # First Playlist, based on the top tracks from last week
  - id: YOUR_PLAYLIST_ID

    # How many tracks should the list contain at max?
    maxSize: 10

    # What subreddit do you want to target?
    subreddit: trance

    # can be either of: HOT, NEW, RISING, CONTROVERSIAL, TOP
    sort: TOP

    # can be either of: HOUR, DAY, WEEK, MONTH, YEAR, ALL
    # Only applies when sort is set to CONTROVERSIAL or TOP
    timePeriod: WEEK

    # Minimum number of upvotes that a reddit post needs to have before being considered
    minUpvotes: 10

    # Whether to look for the exact mix or allow other versions
    isStrictMix: true

    # Second Playlist, based on the top tracks from all time
  - id: YOUR_OTHER_PLAYLIST_ID
    maxSize: 10
    subreddit: trance
    sort: TOP
    timePeriod: ALL
    # Omit minUpvotes to allow any reddit post to be added
    
    # set this to true, if the playlist is private
    isPrivate: true

# When a reddit post has any of the following flairs, it will be excluded
flairsToExclude:
  - Mix
  - Liveset
  - Radio
  - Show
  - Album
  - Upcoming
  - AMA
  - Concluded
  - RIP

# minimum length of tracks in seconds in order to be added to the playlists
minimumLength: 100
```


##### Spotify User ID
To find your Spotify user ID, click your profile, click the `...` and select `Share` > `Copy Spotify URI`.
The last numerical part is your user ID.

##### Spotify Playlist ID
To find the ID of your playlist, right click it, select `Share` > `Copy Spotify URI`.
The last part is your playlist's ID.


#### Advanced Configuration


##### Tag Filtering
Some subreddits make extensive use of tags, which basically means anything that is captured in parenthesis `()` `[]`.

The `tagFilter` can be declared at playlist level and is used to filter out reddit posts based on their tags. 

Example config:
```yaml
playlists:
  - id: xyz
    ...
    tagFilter:
      # Tag must be equal
      # Example post that would be included: [FRESH] Luca Brasi - Clothes I Slept In
      includeExact:
        - FRESH
        
      # Tag contains
      # Example post that would be included: Foghorns - All Glands on Deck (indie/folk/punk)
      include:
        - punk
        
      # Tag must not be equal
      # Example post that would be excluded: [Album] Maps & Atlases - Lightlessness Is Nothing New
      excludeExact:
        - Album
        
      # Tag must not include
      # Example post that would be excluded: [FRESH VIDEO] Preoccupations - Decompose 
      exclude: 
        - video
```

##### Date Filtering
Sometimes you want to only add tracks that match a certain date criteria. 

The `dateFilter` can be declared at playlist level and supports different ways of checking a track's release date.

Example config:
```yaml
playlists:
  - id: xyz
    ...
    dateFilter:
      # Tracks that have been released since the beginning of 2018
      startingFrom: 2018-01-01
    
      # The release date can not exceed the current date minus the specified distance
      # Given a maxDistance of 1 month and 1 year, a track may not be older than 1 month and 1 year
      # Given the current date of 2018-06-16, include only tracks that are released since 2017-05-16
      maxDistance:
        month: 1
        year: 1
```

### Deployment
In order to run the jobs automatically, the application can be deployed to a server, using Heroku, for example.
Steps:
1. Obtain a refresh token locally.
2. Deploy the jar and your `config.yml`
3. Set the `REFRESH_TOKEN` environment variable to your obtained refresh token from step 1
4. Run the app as usual

More details about deploying to Heroku below.

##### Deploy to Heroku
* Use the `Deploy to Heroku` button or do it manually from Heroku.
* Give your app a name and choose a region
* Click the `Deploy app` button
* Once the app is deployed, click the `Manage App` button
* Click the `Deploy` tab
* In the `Deployment method` section, select `Heroku Git` and follow the instructions
* After cloning from Heroku, add this repository as a remote using `git remote add origin https://github.com/AndreasVolkmann/spotify-reddit`
* Then use `git pull origin master` to fetch the code


##### Yaml Configuration
* Add your `config.yml` files, commit and `git push heroku`.  You should be able to see Heroku building the project.
* In Heroku, navigate to your app's settings and add a config var: Key = `REFRESH_TOKEN`, Value should be your refresh token obtained from the manual auth step.


##### Scheduling
* In either `Overview` or `Resources`, click on `Heroku Scheduler`, and then click on `Add new job`
* Start by verifying that the program can be found and executed. In the command field enter `java -jar build/libs/spottit-0.6.0.jar --help`
* Set the frequency to `Every 10 minutes` and save
* In order to see the output, use `heroku logs --ps scheduler` from your local project. See more [here](https://devcenter.heroku.com/articles/scheduler#inspecting-output)
* Currently, the Heroku Scheduler has limited frequency options. I am working on a custom solution to this.
* Once everything works, edit your job and pass your config `java -jar build/libs/spottit-0.6.0.jar -c config.yml`




