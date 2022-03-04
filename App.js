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
  View,
  Dimensions,
} from 'react-native';
import QRCode from 'react-native-qrcode-svg';

import {Colors} from 'react-native/Libraries/NewAppScreen';
import useWindowDimensions from 'react-native/Libraries/Utilities/useWindowDimensions';
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
  const homeId = '5576231'; //CameraHouse;
  const [qrToken, setQRToken] = useState('');
  const [qrUrl, setQrUrl] = useState('qrUrl');
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
        <Button
          title={'Get Wifi QR Token'}
          onPress={() => {
            console.log('Get Wifi WR Tokrn');
            RNTuya.getTuyaWifiQRToken(homeId).then(qrTokenResult => {
              console.log(JSON.stringify({qrTokenResult}, null, 2));
              if (qrTokenResult.success) {
                setQRToken(qrTokenResult.token);
              }
            });
          }}
        />
        <Button
          title={'Get Wifi QR URL'}
          onPress={() => {
            console.log('Get wifi qr url');
            RNTuya.getTuyaWifiQRUrl(
              'Aliste Automation 2.4G',
              '9873382165',
              qrToken,
            ).then(qrUrlResult => {
              console.log(JSON.stringify({qrUrlResult}, null, 2));
              if (qrUrlResult.success) {
                setQrUrl(qrUrlResult.qrUrl);
              }
            });
          }}
        />
        <Button
          title={'Get Cameras List'}
          onPress={() => {
            RNTuya.getTuyaCamerasList(homeId).then(cameraListResult => {
              console.log(JSON.stringify({cameraListResult}, null, 2));
            });
          }}
        />
        <View
          style={{
            marginVertical: 100,
            marginHorizontal: (Dimensions.get('window').width - 300) / 2,
          }}>
          <QRCode value={qrUrl} size={300} />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

export default App;
