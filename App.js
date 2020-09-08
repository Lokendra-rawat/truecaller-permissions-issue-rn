import { StatusBar } from 'expo-status-bar';
import React, { useEffect } from 'react';
import { StyleSheet, Text, View, Button, NativeModules, TextInput, Alert } from 'react-native';
const { TruecallerAuthModule } = NativeModules;

export default class App extends React.Component {

  state = {
    TRUE_STATUS: 'DEFAULT',
    phone: '9752009574',
    loading: false,
  }

  getProfile = async () => {
    try {
      const value = await TruecallerAuthModule.authenticate();
      this.setState({
        TRUE_STATUS: value.status,
      })
    } catch (e) {
      Alert.alert('error', e.message);
    }
  }

  verifyUser = async () => {
    try {
      if(!this.state.phone.length === 10) return;
      this.setState({
        loading: true,
      })
      const value = await TruecallerAuthModule.verifyNonTrueCaller(this.state.phone);
      this.setState({
        TRUE_STATUS: value.status,
        loading: false,
      })
    } catch (e) {
      Alert.alert('error', e.message);
    }
  }

  render() {
    const { TRUE_STATUS, phone } = this.state;
    return (
      <View style={styles.container}>
        <Text style={{
          fontSize: 24,
          marginBottom: 42,
        }}>True Status: {TRUE_STATUS}</Text>
        {TRUE_STATUS === 'DEFAULT' && <>
          <Button onPress={this.getProfile} title="LOGIN WITH TC" />
        </>}
        {TRUE_STATUS === 'VERIFICATION_REQUIRED' && <>
          <TextInput
            autoFocus
            style={{
              borderWidth: 1,
              padding: 8,
              margin: 24,
            }}
            maxLength={10}
            value={phone}
            onChange={(no) => {
              this.setState({
                phone: no,
              })
            }} />
          <Button disabled={this.state.loading} onPress={this.verifyUser} title="VERIFY NO." />
        </>}
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
