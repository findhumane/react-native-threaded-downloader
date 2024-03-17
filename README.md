# react-native-threaded-downloader

Perform HTTP requests on separate native asynchronous threads and then call back to a promise.

## Installation

```sh
npm install react-native-threaded-downloader
```

## Usage

```js
import { performThreadedDownload } from 'react-native-threaded-downloader';

// First argument is the URL and second argument is the timeout in seconds
performThreadedDownload("https://example.com/", 60)
  .then((response) => console.log(response))
  .catch((e) => console.dir(e));
```

See [the example](example/src/App.tsx) for details.

## How it works

When `performThreadedDownload` is called:

* [On iOS](ios/ThreadedDownloader.mm), a task is executed on a [dispatch queue](https://developer.apple.com/documentation/dispatch/1453030-dispatch_queue_create) of type `DISPATCH_QUEUE_CONCURRENT`.
* [On Android](android/src/main/java/com/threadeddownloader/ThreadedDownloaderModule.java), a [`Runnable`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Runnable.html) is executed on a thread pool of up to 10 native threads.

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
