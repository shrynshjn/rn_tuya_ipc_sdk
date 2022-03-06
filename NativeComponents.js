import React, {Component} from 'react';
import {requireNativeComponent} from 'react-native';

export class RNTuyaCameraPlayer extends Component {
  render() {
    return <NativeTuyaCameraPlayer {...this.props} />;
  }
}

const NativeTuyaCameraPlayer = requireNativeComponent(
  'TuyaCameraPlayer',
  RNTuyaCameraPlayer,
);
