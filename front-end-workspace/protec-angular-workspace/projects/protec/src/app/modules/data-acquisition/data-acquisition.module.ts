import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../shared/shared.module';
import { DataAcquisitionRoutingModule } from './data-acquisition-routing.module';

import { WizardComponent } from './wizard/wizard.component';
import { CookieGuideLineComponent } from './cookie-guide-line/cookie-guide-line.component';
import { ImpressumComponent } from './impressum/impressum.component';
import { BeginComponent } from './begin/begin.component';
import { AccidentComponent } from './accident/accident.component';
import { AccidentTypeComponent } from './accident-type/accident-type.component';
import { LegalDataPolicyComponent } from './legal-data-policy/legal-data-policy.component';
import { AGBComponent } from './AGB/AGB.component';
import { DieDetailsComponent } from './die-details/die-details.component';
import { AccidentDateComponent } from './accident-date/accident-date.component';


@NgModule({
  declarations: [
    WizardComponent,
    CookieGuideLineComponent,
    ImpressumComponent,
    BeginComponent,
    AccidentComponent,
    AccidentTypeComponent,
    LegalDataPolicyComponent,
    DieDetailsComponent,
    AccidentDateComponent,
    AGBComponent
  ],
  imports: [
    DataAcquisitionRoutingModule,
    CommonModule,
    SharedModule
  ]
})


export class DataAcquisitionModule { }
