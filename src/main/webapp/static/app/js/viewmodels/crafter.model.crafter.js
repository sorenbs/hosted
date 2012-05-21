Crafter.model.crafter = function () {
	self = this;
	self.componentListViewModel = new Crafter.model.ComponentList();
	self.playGame = function () {
		_.each(self.componentListViewModel.components(), function (x) {
			alert(x.content());
		});
	}
}