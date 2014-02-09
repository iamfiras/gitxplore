# git[x]plore

In the repository details page we have three modules : **Readme** which displays the readme file if exists, **Contributions** which is a tree of the last 100 commits, and **Timeline** which is a representation of these 100 commits on a timeline.

The **Readme** module calls a github service once, the **Contributions** and **Timeline** modules call together the same github service once. So we have in total two github service calls.

In this branch I implemented this "details" service in a way that it will start loading the page and these modules as soon as one of them is ready, so it will not wait until all the data is ready to start sending it to the client.

It's basically an implementation of Yevgeniy Brikman [@brikis98](https://twitter.com/brikis98) presentation at ping-conf 2014 [Building composable, streaming, testable Play apps](http://www.ping-conf.com/#yevgeniybrikman). Check it out for more details.

## Screenshots

**Initial**: Waiting 1 second until the data is ready and starts streaming to the client, then the browser build the Dom, then it starts downloading requested files (css, js, fonts), and finally rendering the page.

![screenshot](https://raw.github.com/fbessadok/gitxplore/stream/demo/details.gitxplore.png)

**Optimised**: Receiving the head of the html page and some of the body in less than 200ms, it can now start building the (incomplete) DOM, downloading resources and even rendering the page before the Contributions module finishes its calculation. When it will finish it will send the builded html that will be inserted (thanks to a one-line js script) inside the right node in the DOM.

![screenshot](https://raw.github.com/fbessadok/gitxplore/stream/demo/stream.gitxplore.png)