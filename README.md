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

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
