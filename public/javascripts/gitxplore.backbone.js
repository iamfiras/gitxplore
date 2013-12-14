FormModel = Backbone.View.extend({
  el: "#repoForm",
  events: {
    "submit": "submit"
  },
  submit: function(e) {
    e.preventDefault();
    this.model.update($("#query").val());
  }
});

RepoModel = Backbone.Model.extend({
  defaults: {
    fullname: "",
    stars: 0
  }
});

RepoList = Backbone.Collection.extend({
  model: RepoModel,
  urlBase: "/search.json?q=",
  msgs: [$("#repolist"), $("#empty"), $("#fail"), $("#loading")],
  hide: function(e) {
    e.hide();
  },
  hideAll: function(e) {
    _.each(this.msgs, this.hide);
  },
  onRequest: function() {
    this.hideAll();
    $("#loading").show();
  },
  onSync: function() {
    this.hideAll();
    this.length > 0 ? $("#repolist").show() : $("#empty").show();
  },
  onError: function(model) {
    model.hideAll();
    $("#fail").show();
  },
  initialize: function() {
    this.on("request", this.onRequest);
    this.on("sync", this.onSync);
  },
  update: function(q) {
    this.url = this.urlBase + q;
    this.fetch({error: this.onError});
  }
});

TableView = Backbone.View.extend({
  el: "#repolist tbody",
  template: _.template($("#repoTemplate").html()),
  initialize: function() {
    this.listenTo(this.model, "all", this.render);
  },
  render: function() {
    this.$el.html(this.template({repos: this.model.models}));
    return this;
  },
  setModel: function(newModel) {
    this.model = newModel;
    this.render();
  }
});


var repoList = new RepoList();
var table = new TableView({model: repoList});
var form = new FormModel({model: repoList});