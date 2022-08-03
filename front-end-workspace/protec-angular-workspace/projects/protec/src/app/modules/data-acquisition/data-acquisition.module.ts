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
import { DieDetailsComponent } from './die-details/die-details.component';
import { AccidentDateComponent } from './accident-date/accident-date.component';
import { TermsOfUseComponent } from './terms-of-use/terms-of-use.component';
import { AGBComponent } from './AGB/AGB.component';
import { MoreInjuriesComponent } from './more-injuries/more-injuries.component';
import { WeAreSorryComponent } from "./we-are-sorry/we-are-sorry.component";
import { YourDataComponent } from "./your-data/your-data.component";
import { ProspectsOfSuccess } from "./prospects-of-success/prospects-of-success.component";
import { RequestSendComponent } from "./request-send/request-send.component";
import { CalendarModule } from "primeng/calendar";
import { IllnessComponent } from "./illness/illness.component";
import { Illness2Component } from "./illness-2/illness-2.component";
import { PrivateAccidentComponent } from "./private-accident/private-accident.component";
import { DetailsFigureComponent } from './details-figure/details-figure.component';
import { AdditionalInsuranceComponent } from './additional-insurance/additional-insurance.component';
import { DetailsIntroComponent } from './details-intro/details-intro.component';

@NgModule({
  declarations: [
    IllnessComponent,
    Illness2Component,
    YourDataComponent,
    ProspectsOfSuccess,
    RequestSendComponent,
    WizardComponent,
    CookieGuideLineComponent,
    ImpressumComponent,
    BeginComponent,
    AccidentComponent,
    AccidentTypeComponent,
    LegalDataPolicyComponent,
    DieDetailsComponent,
    AccidentDateComponent,
    TermsOfUseComponent,
    AGBComponent,
    MoreInjuriesComponent,
    WeAreSorryComponent,
    AGBComponent,
    PrivateAccidentComponent,
    DetailsFigureComponent,
    AdditionalInsuranceComponent,
    DetailsIntroComponent
  ],
  imports: [
    CalendarModule,
    DataAcquisitionRoutingModule,
    CommonModule,
    SharedModule
  ]
})

export class DataAcquisitionModule { }
