/* global widgetUtils */
var siteService = widgetUtils.findObject(model.jsonModel, "id", "SITE_SERVICE");
if (siteService && siteService.config)
{
   siteService.config.additionalSitePresets = [
     { value: "paid-engagement", label: msg.get("title.paidEngagement") }
   ];
}
