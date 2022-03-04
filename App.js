/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {useState} from 'react';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  NativeModules,
  useColorScheme,
  TextInput,
  Button,
} from 'react-native';

import {Colors} from 'react-native/Libraries/NewAppScreen';
const {RNTuya} = NativeModules;
RNTuya.initializeTuya().then(a => {
  console.log(a);
});
const App = () => {
  const isDarkMode = useColorScheme() === 'dark';
  const [email, setEmail] = useState('shreyansh.acc.sites@gmail.com');
  const [code, setCode] = useState('');
  const [countryCode, setCountryCode] = useState('+91');
  const [password, setPassword] = useState('Password');
  const [houseName, setHouseName] = useState('CameraHouse');
  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      <ScrollView
        contentInsetAdjustmentBehavior="automatic"
        style={backgroundStyle}>
        <TextInput value={email} onChangeText={setEmail} />
        <TextInput value={code} onChangeText={setCode} />
        <TextInput value={countryCode} onChangeText={setCountryCode} />
        <TextInput value={password} onChangeText={setPassword} />
        <Button
          title={'Get Code'}
          onPress={() => {
            RNTuya.getTuyaVerificationCode(countryCode, email).then(
              verifiationResult => {
                console.log({verifiationResult});
              },
            );
          }}
        />
        <Button
          title={'Register'}
          onPress={() => {
            RNTuya.registerTuyaUser(countryCode, email, password, code).then(
              registerResult => {
                console.log({registerResult});
              },
            );
          }}
        />
        <Button
          title={'Login'}
          onPress={() => {
            RNTuya.loginTuyaUser(countryCode, email, password).then(
              loginResult => {
                console.log({loginResult});
              },
            );
          }}
        />
        <TextInput value={houseName} onChangeText={setHouseName} />
        <Button
          title={'Create Home'}
          onPress={() => {
            RNTuya.createTuyaHome(houseName).then(createHouseResult => {
              console.log({createHouseResult});
            });
          }}
        />
        <Button
          title={'Get Homes List'}
          onPress={() => {
            RNTuya.getTuyaHomesList().then(houseListResult => {
              console.log(JSON.stringify({houseListResult}, null, 2));
            });
          }}
        />
      </ScrollView>
    </SafeAreaView>
  );
};

export default App;
