function display(element, isDisplayed) {
  isDisplayed ? element.show() : element.hide();
}

function toogle(results, loading, fail) {
  display($("#results"), results);
  display($("#loading"), loading);
  display($("#fail"), fail);
}

function submitSearch(e) {
  e.preventDefault();
  toogle(false, true, false);
  $.get("/results?q=" + $("#query").val(),
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

$("#repoForm").submit(submitSearch);