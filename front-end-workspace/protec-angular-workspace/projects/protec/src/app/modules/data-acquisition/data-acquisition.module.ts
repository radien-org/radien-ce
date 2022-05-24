import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../shared/shared.module';
import { DataAcquisitionRoutingModule } from './data-acquisition-routing.module';

import { WizardComponent } from './wizard/wizard.component';
import { CookieGuideLineComponent } from './cookie-guide-line/cookie-guide-line.component';


@NgModule({
  declarations: [
    WizardComponent,
    CookieGuideLineComponent,
  ],
  imports: [
    DataAcquisitionRoutingModule,
    CommonModule,
    SharedModule
  ]
})
export class DataAcquisitionModule { }
