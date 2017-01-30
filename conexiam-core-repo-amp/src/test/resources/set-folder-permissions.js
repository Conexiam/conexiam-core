var site = siteService.getSite("paid-engagement-project-3");
var docLib = site.getContainer("documentLibrary");
var folders = docLib.getChildren();
for (var i = 0; i < folders.length; i++) {
	var folder = folders[i];
	if (folder.hasAspect("cxm:aclTemplatable")) {
		var aclTemplate = folder.properties['cxm:defaultAclTemplate'];
		if (aclTemplate != undefined) {
			aclTemplates.apply(aclTemplate, folder);
			folder.properties['cxm:aclTemplate'] = aclTemplate;
			folder.save();
		}
	}
}
