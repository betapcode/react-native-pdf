//
//  react-native-pdf
//  
//
//  Created by Wonday on 17/4/21.
//  Copyright (c) wonday.org All rights reserved.
//

import * as React from 'react';
import * as ReactNative from 'react-native';

interface Props {
  style?: ReactNative.ViewStyle,
  source: object,
  page?: number,
  scale?: number,
  horizontal?: boolean,
  spacing?: number,
  password?: string,
  activityIndicator?: any,
  onLoadComplete?: (pageCount: number) => void,
  onPageChanged?: (page: number, pageCount: number) => void,
  onError?: ()=> void,
}

declare class Pdf extends React.Component<Props, any> {}

export default Pdf;
