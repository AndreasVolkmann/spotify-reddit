<#import "track_table.ftl" as t>
<#import "trackBox.ftl" as b>

<#include "header.ftl" parse=true>
<div class="hero-body">
    <div class="container has-text-centered">
        <h1 class="title">
            Checkout
        </h1>
        <h2 class="subtitle">
            Review the actions below
        </h2>

        <@b.trackBox type="add"></@b.trackBox>
        <@b.trackBox type="del"></@b.trackBox>


    </div>
</div>
<#include "footer.ftl" parse=true>