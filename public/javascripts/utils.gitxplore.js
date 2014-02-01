function buildTitle(query) {
  return "Search " + query + " | GitXplore";
}

function getUrlVariable(variable) {
  tabvars = window.location.search.substring(1).split("&");
  for (var i = 0; i < tabvars.length; i++) {
    pair = tabvars[i].split("=");
    if (pair[0] == variable) {
      return pair[1];
    }
  }
  return false;
}

function display(element, isDisplayed) {
  isDisplayed ? element.show() : element.hide();
}

function toogle(results, loading, fail) {
  display($("#results"), results);
  display($("#loading"), loading);
  display($("#fail"), fail);
}