# Spotify + Reddit = Spottit
![Spottit Logo](/spottit-logo-drawn-cropped.png?raw=true "Spottit Logo")

Dynamic Spotify playlists, based on Reddit.
  
Easily set up your own subreddit playlist. 

Originally this was made for /r/trance but it can be used for any subreddit. 

Current test playlists can be found here: 
* [Weekly](https://open.spotify.com/user/113258637/playlist/7eoppWh6XjOIIp10DKpSAs)
* [All Time](https://open.spotify.com/user/113258637/playlist/6cQk28LM0k3ogiCNEH0d9e?si=zFxqpxt6T2C9NhWFTwbDfw)


### How to use
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

    # Second Playlist, based on the top tracks from all time
  - id: YOUR_OTHER_PLAYLIST_ID
    maxSize: 10
    subreddit: trance
    sort: TOP
    timePeriod: ALL
    
# You can also add a list of flairs which is used to exclude posts from Reddit
flairsToExclude:
  - Mix
  - Liveset
  - Radio
  
# minimum length of tracks in seconds in order to be added to the playlists
minimumLength: 100
```

To find your Spotify user ID, click your profile, click the `...` and select `Share` > `Copy Spotify URI`.
The last numerical part is your user ID.

To find the ID of your playlist, right click it, select `Share` > `Copy Spotify URI`.
The last part is your playlist's ID.

Run the program with `-c ${PATH_TO_YOUR_CONFIG.YML}`.


### Reference
* [Reddit API](https://www.reddit.com/dev/api/)
* [Spotify Java SDK](https://github.com/thelinmichael/spotify-web-api-java)


### Todo
* Fine tune track selection (There are still some hardcoded rules that should be delegated to the config.yml)
