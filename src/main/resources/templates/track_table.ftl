<table class="table">
    <thead>
    <tr>
        <#include "track_table_headers.ftl">
    </tr>
    </thead>
    <tfoot>
    <tr>
        <#include "track_table_headers.ftl">
    </tr>
    </tfoot>
    <tbody>
     <#list results.tracks as track>
     <tr>
         <th>1</th>
         <td>${track.artist}</td>
         <td>${track.name}</td>
         <td><a href="/remove${track.id}"><i class="fas fa-times"></i></a></td>
     </tr>
     <#else>
                    <p>No Tracks</p>
     </#list>

    </tbody>
</table>