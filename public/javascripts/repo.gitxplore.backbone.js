var App = window.App = {};

RepoModel = Backbone.Model.extend({
  defaults: {
    repoHistory: {}
  },
  update: function(repofullname) {
    this.commits = {};
    this.contributors = {};
    App.commitList.update(repofullname);
    App.contributorList.update(repofullname);
  },
  finishCommits: function(commits) {
    this.commits = commits;
    this.load();
  },
  finishContributors: function(contributors) {
    this.contributors = contributors;
    this.load();
  },
  load: function() {
    if (this.commits != {} && this.contributors != {}) {
      this.repoHistory = {};
      var that = this;
      _.each(this.contributors, function(contributor) {
         var contribCommits = _.filter(that.commits, function(commit) { return commit.commiter == contributor.login; });
         that.repoHistory[contributor.login + " (" + (contribCommits.length * 100 / that.commits.length).toFixed(2)  + "% - " + contribCommits.length + " commits / " + that.commits.length + ")"] = contribCommits
      });
      App.tableView.setModel(this);
    }
  }
});

CommitModel = Backbone.Model.extend({
  defaults: {
    sha: "",
    url: "",
    date: "",
    commiter: ""
  }
});

CommitList = Backbone.Collection.extend({
  model: CommitModel,
  urlBase: "/commits.json?repofullname=",
  update: function(repofullname) {
    this.url = this.urlBase + repofullname;
    this.fetch();
  },
  initialize: function() {
    this.on("sync", this.onSync);
  },
  onSync: function() {
    App.repoModel.finishCommits(_.map(this.models, function(m) { return m.attributes; }));
  }
});

ContributorModel = Backbone.Model.extend({
  defaults: {
    login: ""
  }
});

ContributorList = Backbone.Collection.extend({
  model: ContributorModel,
  urlBase: "/contributors.json?repofullname=",
  update: function(repofullname) {
    this.url = this.urlBase + repofullname;
    this.fetch();
  },
  initialize: function() {
    this.on("sync", this.onSync);
  },
  onSync: function() {
    App.repoModel.finishContributors(_.map(this.models, function(m) { return m.attributes; }));
  }
});

MainView = Backbone.View.extend({
  el: "#reponame",
  load: function(repofullname) {
    this.$el.html(repofullname);
    App.repoModel.update(repofullname);
  }
});

TableView = Backbone.View.extend({
  el: "#historyList",
  template: _.template($("#historyTemplate").html()),
  initialize: function() {
    this.listenTo(this.model, "all", this.render);
  },
  render: function() {
    this.$el.html(this.template({repoHistory: this.model.repoHistory}));
    return this;
  },
  setModel: function(model) {
    this.model = model;
    this.render();
  }
});

var WorkspaceRouter = Backbone.Router.extend({
  routes: {
    "repo/:author/:reponame": "loadRepo"
  },
  initialize: function() {
    Backbone.history.start({pushState: true});
  },
  loadRepo: function(author, reponame) {
    App.mainView.load(author + "/" + reponame);
  }
});

// Move to 'jQuery' section
App.repoModel = new RepoModel()
App.commitList = new CommitList();
App.contributorList = new ContributorList();
App.mainView = new MainView();
App.tableView = new TableView({model: App.repoModel});
App.router = new WorkspaceRouter();