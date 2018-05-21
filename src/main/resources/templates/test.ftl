<#include "header.ftl" parse=true>
<div class="container has-text-centered">
    <div class="tile is-ancestor">
        <div class="tile is-vertical is-8">
            <div class="tile">
                <#list playlists as pl>
                    <div class="tile is-parent is-vertical">
                        <article class="tile is-child notification is-info">
                            <p class="title">${pl.subreddit} Playlist</p>
                            <p class="subtitle">Time Period: ${pl.timePeriod}</p>
                            <p class="subtitle">Sort: ${pl.sort}</p>
                            <p class="subtitle">Max size: ${pl.maxSize}</p>
                        </article>
                    </div>
                <#else>
                    <p>No Playlists</p>
                </#list>

            </div>
        </div>
    </div>

</div>
<#include "footer.ftl" parse=true>