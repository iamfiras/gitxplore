function submitSearch(e) {
  e.preventDefault();
  query = $("#query").val()
  gitxploreHistory(query);
  querying(query);
}

function querying(query) {
  toogle(false, true, false);
  $.get("/results?q=" + query,
    function(data) {
      if (data.length > 0) {
        $("#results").html(data);
        toogle(true, false, false);
      }
    }
  ).fail(function() {
    toogle(false, false, true);
  });
}

function gitxploreHistory(query) {
  pageTitle = buildTitle(query);
  history.pushState({query: query}, pageTitle, "search?q=" + query);
  document.title = pageTitle;
}

window.onpopstate = function(e) {
  query = getUrlVariable("q");
  if (query) {
    $("#query").val(query);
    document.title = buildTitle(query);
    querying(query);
  } else {
    $("#query").val("");
    document.title = "GitXplore";
    toogle(false, false, false);
  }
};

$("#repoForm").submit(submitSearch);