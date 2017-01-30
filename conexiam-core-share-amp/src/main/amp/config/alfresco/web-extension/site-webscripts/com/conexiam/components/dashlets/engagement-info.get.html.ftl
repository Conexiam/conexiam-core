<#assign el=args.htmlid?js_string>

<script type="text/javascript">//<![CDATA[
    (function() {
       new Alfresco.widget.DashletResizer("${el}", "${instance.object.id}");
       new Alfresco.widget.DashletTitleBarActions("${args.htmlid?html}").setOptions(
       {
          actions:
          [
             {
                cssClass: "help",
                bubbleOnClick:
                {
                   message: "${msg("dashlet.help")?js_string}"
                },
                tooltip: "${msg("dashlet.help.tooltip")?js_string}"
             }
          ]
       });
    })();
    //]]></script>
<div class="dashlet engagementinfo">
    <div class="title">
        ${title}
    </div>
    <div class="body scrollablePanel"<#if args.height??> style="height: ${args.height}px;"</#if> >
        <div id="engagementinfo_item">
            <#if result?exists>
                Client Group: ${result.clientGroup!'Not set'} <br />
                Project Group: ${result.projectGroup!'Not set'} <br />
                Engagement Lead: ${result.engagementLead!'Not set'} <br />
                Engagement End Date: ${result.engagementEndDate!'Not set'} <br />
                Retention End Date: ${result.retentionEndDate!'Not set'} <br />
            <#else>
                This site has no engagement information.
            </#if>
        </div>
    </div>
</div>