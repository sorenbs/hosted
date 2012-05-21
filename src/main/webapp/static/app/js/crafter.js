function l(l) { console.log(l); }


Crafter.viewModel = new Crafter.model.crafter();

Crafty.E = Crafty.e;
Crafty.e = function (components) {
	console.log(components);
	var entity = Crafty.E(components);
	entity.bind("Remove", function () { Crafter.e.pop(entity); });
	Crafter.e.push(entity);
	return entity;
};