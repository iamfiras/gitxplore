var App = window.App = {};


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

FormModel = Backbone.View.extend({
  el: "#repoForm",
  events: {
    "submit": "submit"
  },
  load: function(query) {
    $("#query").val(query);
    this.model.update(query);
  },
  submit: function(e) {
    e.preventDefault();
    App.router.navigateTo($("#query").val());
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

var WorkspaceRouter = Backbone.Router.extend({
  routes: {
    "search/:query": "search"
  },
  initialize: function() {
    Backbone.history.start({pushState: true});
  },
  search: function(query) {
    App.formView.load(query);
  },
  navigateTo: function(query) {
    this.navigate("search/"+query);
    this.search(query);
  }
});

// Move to 'jQuery' section
App.repoList = new RepoList();
App.tableView = new TableView({model: App.repoList});
App.formView = new FormModel({model: App.repoList});
App.router = new WorkspaceRouter();