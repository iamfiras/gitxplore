function clear() {
  $('#norepofound').hide();
  $('#fail').hide();
  $('table').empty();
}

function search(query) {
  $.get('/search.json',
    {'q':query},
    function(data) {
      clear();
      
      if (data.length > 0) {
        for (repo in data) {
          $('table').append(
            $('<tr>')
            .append($('<td>').append(data[repo].fullname + ' <a href="' + data[repo].url + '"><i class="icon-small icon-external-link"></i></a>'))
            .append($('<td class="content-right">').append(data[repo].stars + ' <i class="icon-star"></i>'))
          );
        }
      } else {
        clear();
        $('#norepofound').show();
      }
    }
  ).fail(function() {
    clear();
    $('#fail').show();
  });
}