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
    this.show();
  },
  finishContributors: function(contributors) {
    this.contributors = contributors;
    this.show();
  },
  show: function() {
    if (!$.isEmptyObject(this.commits) && !$.isEmptyObject(this.contributors)) {
      var that = this;
      var tree = Array();
      _.each(this.contributors, function(contributor) {
         var contribCommits = _.filter(that.commits, function(commit) { return commit.committer == contributor.login; });
         var innerArray = Array();
         innerArray.push(contributor.login + " (" + (contribCommits.length * 100 / that.commits.length).toFixed(2)  + "% - " + contribCommits.length + " commits)");
         innerArray.push(contribCommits);
         tree.push(innerArray);
      });

      this.repoHistory = _.sortBy(tree, function(arr) { return 1 / arr[1].length; });

      App.mainView.updateModel(this);
    }
  }
});

CommitModel = Backbone.Model.extend({
  defaults: {
    message: "",
    sha: "",
    url: "",
    date: "",
    committer: ""
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
    _.each(this.models, function(commitModel) {
      commitModel.attributes.date = $.datepicker.formatDate('yy-mm-dd', new Date(Date.parse(commitModel.attributes.date)));
    });
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
  },
  updateModel: function(model) {
    App.listView.setModel(model);
    App.timelineView.setModel(model);
  }
});

ListView = Backbone.View.extend({
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

TimelineView = Backbone.View.extend({
  el: "#timeline",
  render: function() {
    var events = _.map(this.model.commits, function(commit) {
      return {dates: [new Date(Date.parse(commit.date))], title: commit.committer + ": " + commit.message};
    });
    App.timeline = new Chronoline(document.getElementById("timeline"), events, {animated: true, sections: []});
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