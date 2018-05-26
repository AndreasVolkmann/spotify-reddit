<#include "header.ftl" parse=true>
<div class="hero-body">
    <div class="container has-text-centered">
        <div class="fa-10x">
                <#include "logo.ftl">
        </div>

        <h1 class="title">
            Spotify authorization succesfull!
        </h1>
        <h2 class="subtitle">
            Here are your access and refresh tokens.
        </h2>

        <#include "credential_box.ftl">

    </div>
</div>
<#include "footer.ftl" parse=true>