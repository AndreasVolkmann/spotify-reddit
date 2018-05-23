<#include "header.ftl" parse=true>
<div class="hero-body">
    <div class="container has-text-centered">
        <h1 class="title">
            Checkout
        </h1>
        <h2 class="subtitle">
            Review the actions below
        </h2>

        <#list resultList as result>

        </#list>

        <div class="box">
            <article class="media">
                <div class="media-left">
                    <figure class="image is-64x64">
                        <img src="https://bulma.io/images/placeholders/128x128.png" alt="Image">
                    </figure>
                </div>
                <div class="media-content">
                    <div class="content">
                        <p>
                            <strong>Tracks to Add</strong>
                            <small></small>
                            <small>(${resultsAdd.total})</small>
                            <br>
                            <#include "track_table.ftl" parse=true>
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

        <div class="box">
            <article class="media">
                <div class="media-left">
                    <figure class="image is-64x64">
                        <img src="https://bulma.io/images/placeholders/128x128.png" alt="Image">
                    </figure>
                </div>
                <div class="media-content">
                    <div class="content">
                        <p>
                            <strong>Tracks to Add</strong>
                            <small></small>
                            <small>(${resultsDel.total})</small>
                            <br>
                            <#include "track_table.ftl" parse=true>
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
    </div>
</div>
<#include "footer.ftl" parse=true>