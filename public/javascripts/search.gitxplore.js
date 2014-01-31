function submitSearch(e) {
  e.preventDefault();
  $.get('/results?q=' + $("#query").val(),
    function(data) {
      if (data.length > 0) {
        $('#results').html(data);
      }
    }
  ).fail(function() {
    $('#results').append("hop");
  });
}

$("#repoForm").submit(submitSearch);