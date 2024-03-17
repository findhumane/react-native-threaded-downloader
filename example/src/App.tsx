import * as React from 'react';

import { Button, SafeAreaView, ScrollView, StyleSheet, TextInput, View, Text } from 'react-native';
import { performThreadedDownload } from 'react-native-threaded-downloader';

const BUTTON_TEXT = 'Perform HTTP GET';
const TIMEOUT_SECONDS = 60;

function processError(e: any) {
  let result = e.toString();
  result = result.replace(/Error: /g, '');
  return 'Error: ' + result;
}

export default function App() {
  const [url, setUrl] = React.useState<string>('https://example.com/');
  const [timeoutSeconds, setTimeoutSeconds] = React.useState<Number | undefined>();
  const [output, setOutput] = React.useState<string>("Enter a URL above and press '" + BUTTON_TEXT + "'");

  function onSubmit() {
    setOutput('Executing ' + url + ' with a timeout of ' + timeoutSeconds + ' seconds...');

    performThreadedDownload(url, timeoutSeconds ?? TIMEOUT_SECONDS)
      .then((response) => setOutput(response))
      .catch((e) => setOutput(processError(e)));
  }

  return (
    <SafeAreaView style={styles.container}>
      <TextInput
        style={styles.input}
        onChangeText={setUrl}
        value={url}
        placeholder="URL"
        clearButtonMode="always"
        inputMode="url"
        autoCapitalize="none"
        keyboardType="url"
        autoCorrect={false}
        autoComplete="url"
      />
      <TextInput
        style={styles.input}
        onChangeText={(val) => setTimeoutSeconds(parseFloat(val))}
        value={timeoutSeconds?.toString()}
        placeholder="Timeout (seconds)"
        clearButtonMode="always"
        inputMode="decimal"
        autoCapitalize="none"
        keyboardType="numeric"
        autoCorrect={false}
      />
      <View style={styles.buttonWrapper}>
        <Button title={BUTTON_TEXT} onPress={onSubmit} />
      </View>
      <ScrollView style={styles.outputContainer}>
        <Text>{output}</Text>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  input: {
    height: 40,
    marginVertical: 10,
    borderWidth: 1,
    padding: 10,
    width: '75%',
  },
  outputContainer: {
    borderWidth: 1,
    padding: 10,
    width: '90%',
  },
  buttonWrapper: {
    marginVertical: 10,
  },
});
