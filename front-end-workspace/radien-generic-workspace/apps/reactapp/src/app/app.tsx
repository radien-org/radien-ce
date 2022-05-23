import * as React from 'react';
import { Component } from 'react';

import './app.css';

export class App extends Component {
  render() {
    const title = 'reactapp';
    return (
      <div>
        <div style={{ textAlign: 'center' }}>
          <happynrwl-greeting title={title}/>
        </div>
        <p>
          This is a React app running.
        </p>
      </div>
    );
  }
}
