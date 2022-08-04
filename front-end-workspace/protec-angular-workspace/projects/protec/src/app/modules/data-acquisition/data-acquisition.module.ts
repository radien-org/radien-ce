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
import { MultiSelectModule } from 'primeng/multiselect';
import { IllnessComponent } from "./illness/illness.component";
import { Illness2Component } from "./illness-2/illness-2.component";
import { PrivateAccidentComponent } from "./private-accident/private-accident.component";
import { DetailsFigureComponent } from './details-figure/details-figure.component';
import { AdditionalInsuranceComponent } from './additional-insurance/additional-insurance.component';
import { DetailsIntroComponent } from './details-intro/details-intro.component';
import { PersonalDataInputComponent } from "./personal-data-input/personal-data-input.component";
import { PersonalDataInputComponentTwo } from "./personal-data-input-2/personal-data-input-2.component";
import { WorkAccidentComponent } from './work-accident/work-accident.component';
import { PartBodyFigureComponent } from './part-body-figure/part-body-figure.component';
import { SummaryComponent } from './summary/summary.component';
import { ChanceOfSuccessComponent } from './chance-of-success/chance-of-success.component';
import { PersonalDataInformationComponent } from './personal-data-information/personal-data-information.component';
import { PersonalDataSummaryComponent } from './personal-data-summary/personal-data-summary.component';
import { AppreciationComponent } from './appreciation/appreciation.component';
import { PreSummaryComponent } from './pre-summary/pre-summary.component';
import { RequestQuoteComponent } from "./request-quote/request-quote.component";

@NgModule({
  declarations: [
    BeginComponent,
    PersonalDataInputComponent,
    PersonalDataInputComponentTwo,
    IllnessComponent,
    Illness2Component,
    YourDataComponent,
    ProspectsOfSuccess,
    RequestSendComponent,
    WizardComponent,
    CookieGuideLineComponent,
    ImpressumComponent,
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
    DetailsIntroComponent,
    WorkAccidentComponent,
    PartBodyFigureComponent,
    SummaryComponent,
    ChanceOfSuccessComponent,
    PersonalDataInformationComponent,
    PersonalDataSummaryComponent,
    AppreciationComponent,
    PreSummaryComponent,
    RequestQuoteComponent
  ],
  imports: [
    CalendarModule,
    MultiSelectModule,
    DataAcquisitionRoutingModule,
    CommonModule,
    SharedModule
  ]
})

export class DataAcquisitionModule { }
