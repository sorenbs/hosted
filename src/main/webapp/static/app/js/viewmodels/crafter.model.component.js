Crafter.model.Component = function(initContent) {
	var self = this;
	self.content = ko.observable(initContent);
	self.content.subscribe(function (e) { console.log(e); })
}