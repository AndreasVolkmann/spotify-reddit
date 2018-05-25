<#import "track_table.ftl" as t>

<#macro trackBox type>
    <#if type == "add">
        <#assign results=resultsAdd name="Add">
    <#else>
        <#assign results=resultsDel name="Delete">
    </#if>

<div class="box">
    <article class="media">
        <div class="media-left">
            <figure class="image is-64x64">
    <#if type == "add">

        <i class="fas fa-plus fa-2x"></i>
    <#else>
                <i class="far fa-trash-alt fa-2x"></i>
    </#if>
            </figure>
        </div>
        <div class="media-content">
            <div class="content">
                <p>
                    <strong>Tracks to ${name}</strong>
                    <small></small>
                    <small>(${results.total})</small>
                    <br>
                            <@t.trackTable type=type/>
                </p>
            </div>

            <nav class="level is-mobile">
                <div class="level-left">
                    <a class="level-item" aria-label="reply">
            <span class="icon is-small">
              <i class="fas fa-reply" aria-hidden="true"></i>
            </span>
                    </a>
                    <a class="level-item" aria-label="retweet">
            <span class="icon is-small">
              <i class="fas fa-retweet" aria-hidden="true"></i>
            </span>
                    </a>
                    <a class="level-item" aria-label="like">
            <span class="icon is-small">
              <i class="fas fa-heart" aria-hidden="true"></i>
            </span>
                    </a>
                </div>
            </nav>
        </div>
    </article>
</div>
</#macro>