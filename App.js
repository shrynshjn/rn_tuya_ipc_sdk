/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {useState, useRef} from 'react';
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
  findNodeHandle,
  UIManager,
  PermissionsAndroid,
} from 'react-native';
import QRCode from 'react-native-qrcode-svg';
import {RNTuyaCameraPlayer} from './NativeComponents';
import {Colors} from 'react-native/Libraries/NewAppScreen';
const {RNTuya} = NativeModules;
const defaultWS = 'Graphketing'; //'Aliste Automation 2.4G';
const defaultWP = '9910016426' //'9873382165';
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
  const [ws, setWS] = useState(defaultWS);
  const [wp, setWP] = useState(defaultWP);
  const [deviceId, setDeviceId] = useState('');
  const [play, setPlay] = useState(false);
  const [speak, setSpeak] = useState(false);
  const [listen, setListen] = useState(false);
  const [init, setInit] = useState(false);
  const [cameras, setCameras] = useState([]);
  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };
  const cameraRef = useRef();
  PermissionsAndroid.requestMultiple([
    'android.permission.READ_EXTERNAL_STORAGE',
    'android.permission.WRITE_EXTERNAL_STORAGE',
  ])
    .then(a => {
      console.log(a);
    })
    .catch(a => {
      console.log(a);
    });
  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      <ScrollView
        contentInsetAdjustmentBehavior="automatic"
        style={backgroundStyle}>
        <TextInput
          value={email}
          onChangeText={setEmail}
          placeholder={'Email'}
        />
        <TextInput value={code} onChangeText={setCode} placeholder={'Code'} />
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
          title={'Set wifi qr url'}
          onPress={() => {
            setQrUrl(
              JSON.stringify({
                p: wp,
                s: ws,
                t: qrToken,
              }),
            );
          }}
        />
        <Button
          title={'Start AP Config'}
          onPress={() => {
            RNTuya.startTuyaAPDevicePairing(ws, wp, qrToken).then(
              startAPResult => {
                console.log(JSON.stringify({startAPResult}, null, 2));
              },
            );
          }}
        />
        <Button
          title={'Stop AP Config'}
          onPress={() => {
            RNTuya.stopTuyaAPDevicePairing().then(stopApResult => {
              console.log(JSON.stringify({stopApResult}, null, 2));
            });
          }}
        />
        <Button
          title={'Get Wifi QR URL'}
          onPress={() => {
            console.log('Get wifi qr url');
            RNTuya.getTuyaWifiQRUrl(ws, wp, qrToken).then(qrUrlResult => {
              console.log(JSON.stringify({qrUrlResult}, null, 2));
              if (qrUrlResult.success) {
                setQrUrl(qrUrlResult.qrUrl);
              }
            });
          }}
        />
        <Button
          title={'Get Devices List'}
          onPress={() => {
            RNTuya.getTuyaDevicesList(homeId).then(cameraListResult => {
              console.log(JSON.stringify({cameraListResult}, null, 2));
              setCameras(cameraListResult.devices);
              setInit(true);
            });
          }}
        />
        {qrUrl !== 'qrUrl' && (
          <View
            style={{
              marginVertical: 100,
              marginHorizontal: (Dimensions.get('window').width - 300) / 2,
            }}>
            <QRCode value={qrUrl} size={300} />
          </View>
        )}
        {cameras.map((camera, index) => (
          <View style={{marginVertical: 15}}>
            <View
              style={{
                width: Dimensions.get('window').width,
                height: (Dimensions.get('window').width * 1080) / 1920,
                backgroundColor: 'black',
                flex: 1,
              }}>
              <RNTuyaCameraPlayer
                ref={index === 0 ? cameraRef : undefined}
                deviceId={camera.deviceId}
                style={{backgroundColor: 'white', flex: 1}}
                //initialized={init}
                speak={speak}
                play={play}
                listen={listen}
                // onStatusChanged={({nativeEvent}) => {
                //   console.log('onStatusChanged', nativeEvent);
                // }}
                // onSaveSnapComplete={({nativeEvent}) => {
                //   console.log('onSaveSnapComplete', nativeEvent);
                // }}
              />
            </View>
            <Button
              title={!speak ? 'Speak' : 'Stop speak'}
              onPress={() => {
                setSpeak(!speak);
              }}
            />
            <Button
              title={!listen ? 'Stop Listen' : 'Stop Listen'}
              onPress={() => {
                setListen(!listen);
              }}
            />
            <Button
              title={!play ? 'Play' : 'Pause'}
              onPress={() => {
                setPlay(!play);
              }}
            />
            <Button
              title="Save Screenshot"
              onPress={() => {
                console.log('node handle', findNodeHandle(cameraRef.current));
                UIManager.dispatchViewManagerCommand(
                  findNodeHandle(cameraRef.current),
                  '0',
                  [findNodeHandle(cameraRef.current)],
                );
              }}
            />
          </View>
        ))}
      </ScrollView>
    </SafeAreaView>
  );
};

export default App;
